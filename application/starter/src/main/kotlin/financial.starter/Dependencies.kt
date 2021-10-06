package financial.starter

import financial.accounting.account.command.adjust.AdjustBalance
import financial.accounting.account.command.adjust.AdjustBalanceHandler
import financial.accounting.account.command.solve.SolveFailures
import financial.accounting.account.command.solve.SolveFailuresHandler
import financial.accounting.account.core.model.AccountBalanceRepository
import financial.accounting.account.driven.repository.AccountBalanceMapper
import financial.accounting.account.driven.troubleshoot.FailureMapper
import financial.accounting.account.driven.troubleshoot.Persistence
import financial.accounting.account.driven.view.ConsoleBalance
import financial.accounting.account.driven.view.Viewer
import financial.accounting.account.driver.kafka.TransactionRegisteredMapper
import financial.accounting.account.driver.policies.AdjustBalancePolicy
import financial.accounting.account.query.BalanceDao
import financial.accounting.account.query.ShowBalance
import financial.accounting.account.query.ShowBalanceHandler
import financial.accounting.bookkeeping.command.register.RegisterFinancialTransaction
import financial.accounting.bookkeeping.command.register.RegisterFinancialTransactionHandler
import financial.accounting.bookkeeping.core.model.TransactionRepository
import financial.shared.adapters.console.command.Command
import financial.shared.adapters.console.command.CommandConsoleDriver
import financial.shared.adapters.console.command.bus.CommandBus
import financial.shared.adapters.console.command.bus.CommandDispatcher
import financial.shared.adapters.console.command.resolver.CommandHandler
import financial.shared.adapters.console.command.resolver.CommandHandlerResolver
import financial.shared.adapters.console.command.resolver.CommandResolver
import financial.shared.adapters.console.event.EventBus
import financial.shared.adapters.console.event.EventDispatcher
import financial.shared.databases.DatabaseConnection
import financial.shared.databases.DatabaseSettings
import financial.shared.databases.RelationalDatabase
import financial.shared.databases.RelationalDatabaseCapabilities
import financial.shared.databases.postgresql.PostgreSqlDatabase
import financial.shared.databases.postgresql.PostgreSqlDatabaseSettings
import financial.shared.di.Container
import financial.shared.eventsourcing.EventSourcing
import financial.shared.eventsourcing.EventSourcingCapabilities
import financial.shared.eventsourcing.EventSourcingRoot
import financial.shared.log.Logger
import financial.shared.log.LoggerFactory
import financial.shared.mapper.JsonMapper
import financial.shared.mapper.JsonMapperFactory
import financial.shared.policies.Failures
import financial.shared.policies.Policy
import financial.shared.streams.ConnectorFactory
import financial.shared.streams.StreamFactory
import financial.shared.streams.StreamSettings
import financial.shared.streams.TopicFactory
import financial.shared.streams.kafka.KafkaSettings
import financial.shared.streams.kafka.connector.KafkaConnectorFactory
import financial.shared.streams.kafka.stream.KafkaEventMapper
import financial.shared.streams.kafka.stream.KafkaStreamFactory
import financial.shared.streams.kafka.stream.KafkaTopologyFactory
import financial.shared.streams.kafka.topic.KafkaTopicFactory
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import org.jdbi.v3.core.Jdbi
import org.koin.core.KoinApplication
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.math.BigDecimal
import financial.accounting.account.driven.repository.AccountBalanceRepository as AccountBalanceRepositoryAdapter
import financial.accounting.bookkeeping.driven.repository.TransactionRepository as BookkeepingRepositoryAdapter

object Dependencies : Container {

    private val application: KoinApplication by lazy {
        koinApplication { modules(sharedModule, starterModule, accountModule, bookkeepingModule) }
    }

    override fun <T> get(className: String): T = application.koin.get(Class.forName(className).kotlin)

    override fun has(className: String): Boolean { TODO("Not yet implemented") }

    private inline fun <reified T> getList(): Set<T> = application.koin.getAll<T>().toSet()

    private val accountModule = module {
        /** command */
        single { SolveFailures() } bind Command::class
        single { AdjustBalance(get(), get(), get(), get()) } bind Command::class
        single { AdjustBalanceHandler(get()) } bind CommandHandler::class
        single { SolveFailuresHandler(get(), get(), getList()) } bind CommandHandler::class

        /** driven */
        single { Persistence(get(), get(), get()) } bind Failures::class
        single { FailureMapper(get()) } bind FailureMapper::class
        single { ConsoleBalance() } bind Viewer::class
        single { AccountBalanceMapper() } bind AccountBalanceMapper::class
        single { AccountBalanceRepositoryAdapter(get(), get()) } bind AccountBalanceRepository::class

        /** driver */
        single { AdjustBalancePolicy(get(), get()) } bind Policy::class
        single { TransactionRegisteredMapper(get(), get()) } bind KafkaEventMapper::class

        /** query */
        single { BalanceDao(get(), get()) } bind BalanceDao::class
        single { ShowBalance() } bind Command::class
        single { ShowBalanceHandler(get(), get()) } bind CommandHandler::class
    }

    private val bookkeepingModule = module {
        /** command */
        single { RegisterFinancialTransaction() } bind Command::class
        single { RegisterFinancialTransactionHandler(get(), get(), get()) } bind CommandHandler::class

        /** driven */
        single { BookkeepingRepositoryAdapter(get(), get()) } bind TransactionRepository::class
    }

    private val sharedModule = module {
        /** adapters */
        single { EventDispatcher(getList()) } bind EventBus::class
        single { CommandDispatcher(get()) } bind CommandBus::class
        single { CommandHandlerResolver(get()) } bind CommandResolver::class
        single { CommandConsoleDriver(getList(), get()) } bind CommandConsoleDriver::class

        /** databases */
        single { DatabaseConnection(get()) } bind DatabaseConnection::class
        single { get<DatabaseConnection>().jdbi } bind Jdbi::class
        single { PostgreSqlDatabaseSettings() } bind DatabaseSettings::class
        single { PostgreSqlDatabase(RelationalDatabaseCapabilities(get())) } bind RelationalDatabase::class

        /** event sourcing */
        factory { EventSourcingCapabilities<EventSourcingRoot<*>>() } bind EventSourcing::class

        /** log */
        single { LoggerFactory() } bind Logger::class

        /** mapper */
        single { JsonMapperFactory() } bind JsonMapper::class

        /** streams */
        single { KafkaSettings() } bind StreamSettings::class
        single { KafkaTopicFactory(get()) } bind TopicFactory::class
        single { KafkaStreamFactory(get(), get()) } bind StreamFactory::class
        single { KafkaTopologyFactory(getList(), get()) } bind KafkaTopologyFactory::class
        single { KafkaConnectorFactory(get(), get()) } bind ConnectorFactory::class

        /** value object */
        factory { Int.MAX_VALUE } bind Int::class
        factory { String() } bind String::class
        factory { Amount(BigDecimal.ZERO, Currency.BRL) } bind Amount::class
    }

    private val starterModule = module {
        single { this@Dependencies } bind Container::class
        single { Platform(get(), get(), get()) } bind Platform::class
    }
}