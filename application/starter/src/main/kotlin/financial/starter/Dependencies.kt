package financial.starter

import financial.accounting.account.application.handlers.BalanceAdjustingHandler
import financial.accounting.account.application.handlers.FailuresSolvingHandler
import financial.accounting.account.driven.repository.DatabaseAccountBalances
import financial.accounting.account.driven.troubleshoot.DatabasePolicyFailures
import financial.accounting.account.driven.troubleshoot.FailureMessages
import financial.accounting.account.driven.troubleshoot.LoggedSolvingNotices
import financial.accounting.account.driver.console.SolveFailuresConsole
import financial.accounting.account.driver.policies.AdjustBalancePolicy
import financial.accounting.account.driver.policies.PolicyEventBus
import financial.accounting.account.query.balance.console.ShowBalanceConsole
import financial.accounting.account.query.balance.database.DatabaseBalanceFinding
import financial.accounting.bookkeeping.application.handlers.FinancialTransactionRegisteringHandler
import financial.accounting.bookkeeping.driven.log.LoggedRegistrationNotices
import financial.accounting.bookkeeping.driven.repository.EventStoreTransactions
import financial.accounting.bookkeeping.driver.console.RegisterFinancialTransactionConsole
import financial.shared.Environment
import financial.shared.adapters.console.ConsoleDriver
import financial.shared.adapters.database.DatabaseSettings
import financial.shared.adapters.database.JdbiDatabase
import financial.shared.adapters.json.JacksonJsonMapper
import financial.shared.adapters.kafka.KafkaConnectors
import financial.shared.adapters.kafka.KafkaSettings
import financial.shared.adapters.kafka.KafkaStreamsRunner
import financial.shared.adapters.kafka.KafkaTopics
import financial.shared.adapters.kafka.KafkaTopology
import financial.shared.adapters.log.Slf4jLogger
import java.time.Clock
import java.time.Duration

class Dependencies {

    private val clock = Clock.systemUTC()

    private val jsonMapper = JacksonJsonMapper()

    private val database by lazy {
        JdbiDatabase.from(
            settings = DatabaseSettings(
                user = Environment.get(variable = "POSTGRESQL_DATABASE_USER"),
                jdbcUrl = Environment.get(variable = "POSTGRESQL_DATABASE_JDBC_URL"),
                password = Environment.get(variable = "POSTGRESQL_DATABASE_PASSWORD")
            )
        )
    }

    private val kafkaSettings by lazy {
        KafkaSettings(
            topics = Environment.get(variable = "TOPICS").split(","),
            connectorUrl = Environment.get(variable = "KAFKA_CONNECT_URL"),
            applicationId = Environment.get(variable = "APPLICATION_ID"),
            connectorsPath = Environment.get(variable = "KAFKA_CONNECT_CONNECTORS_PATH"),
            bootstrapServers = Environment.get(variable = "BOOTSTRAP_SERVERS"),
            schemaRegistryUrl = Environment.get(variable = "SCHEMA_REGISTRY_URL")
        )
    }

    private val policyFailures by lazy {
        DatabasePolicyFailures(clock = clock, database = database, messages = FailureMessages())
    }

    private val policies by lazy {
        listOf(
            AdjustBalancePolicy(
                failures = policyFailures,
                adjusting = BalanceAdjustingHandler(
                    clock = clock,
                    balances = DatabaseAccountBalances(database = database)
                ),
                jsonMapper = jsonMapper
            )
        )
    }

    val consoleDriver by lazy {
        ConsoleDriver(
            commands = listOf(
                RegisterFinancialTransactionConsole(
                    registering = FinancialTransactionRegisteringHandler(
                        clock = clock,
                        notices = LoggedRegistrationNotices(
                            logger = Slf4jLogger(owner = LoggedRegistrationNotices::class)
                        ),
                        transactions = EventStoreTransactions(database = database, jsonMapper = jsonMapper)
                    )
                ),
                ShowBalanceConsole(output = System.out, finding = DatabaseBalanceFinding(database = database)),
                SolveFailuresConsole(
                    solving = FailuresSolvingHandler(
                        notices = LoggedSolvingNotices(logger = Slf4jLogger(owner = LoggedSolvingNotices::class)),
                        failures = policyFailures,
                        policies = policies
                    )
                )
            )
        )
    }

    val platform by lazy {
        Platform(
            topics = KafkaTopics(admin = KafkaTopics.KAFKA_ADMIN, settings = kafkaSettings),
            streams = KafkaStreamsRunner(
                streams = KafkaStreamsRunner.KAFKA_STREAMS,
                settings = kafkaSettings,
                shutdown = KafkaStreamsRunner.RUNTIME_SHUTDOWN_HOOKS,
                topology = KafkaTopology(
                    topics = kafkaSettings.topics,
                    eventBus = PolicyEventBus(policies = policies),
                    schemaRegistryUrl = kafkaSettings.schemaRegistryUrl
                )
            ),
            connectors = KafkaConnectors(
                pause = KafkaConnectors.THREAD_PAUSE,
                logger = Slf4jLogger(owner = KafkaConnectors::class),
                settings = kafkaSettings,
                jsonMapper = jsonMapper,
                retryDelay = Duration.ofSeconds(3),
                maximumAttempts = 60
            )
        )
    }
}
