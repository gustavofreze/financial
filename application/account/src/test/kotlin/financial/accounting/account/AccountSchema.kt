package financial.accounting.account

import financial.accounting.account.application.domain.models.AccountBalance
import financial.shared.adapters.database.Database
import financial.shared.adapters.database.RowMapper
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object AccountSchema {

    val statements = arrayOf(
        "CREATE SCHEMA account",
        """
        CREATE TABLE account.balances
        (
            id         SERIAL                   NOT NULL,
            number     VARCHAR(255)             NOT NULL,
            balance    NUMERIC(15, 2)           NOT NULL,
            currency   VARCHAR(3)               NOT NULL,
            updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
            version    BIGINT                   NOT NULL DEFAULT 0,
            CONSTRAINT balances_pk PRIMARY KEY (id),
            CONSTRAINT unq_balances_number UNIQUE (number)
        )
        """.trimIndent(),
        """
        CREATE TABLE account.adjustments
        (
            transaction_id VARCHAR(36)              NOT NULL,
            adjusted_at    TIMESTAMP WITH TIME ZONE NOT NULL,
            CONSTRAINT adjustments_pk PRIMARY KEY (transaction_id)
        )
        """.trimIndent(),
        """
        CREATE TABLE account.troubleshoot
        (
            id          VARCHAR(36)              NOT NULL,
            error       TEXT                     NOT NULL,
            resent      SMALLINT                 NOT NULL DEFAULT 0,
            payload     TEXT                     NOT NULL,
            created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at  TIMESTAMP WITH TIME ZONE,
            event_name  VARCHAR(255)             NOT NULL,
            policy_name VARCHAR(255)             NOT NULL,
            CONSTRAINT troubleshoot_pk PRIMARY KEY (id),
            CONSTRAINT chk_troubleshoot_resent CHECK (resent IN (0, 1))
        )
        """.trimIndent()
    )

    fun balance(account: AccountBalance): String = """
        INSERT INTO account.balances (id, number, balance, currency, updated_at)
        VALUES (${account.id.value}, '${account.number.value}', ${account.balance.value},
                '${account.balance.currency.name}', TIMESTAMP WITH TIME ZONE '${account.updatedAt.format(TIMESTAMP)}')
    """.trimIndent()

    fun failure(id: String, createdAt: OffsetDateTime): String = """
        INSERT INTO account.troubleshoot (id, error, payload, created_at, event_name, policy_name)
        VALUES ('$id', 'Error', '{}', TIMESTAMP WITH TIME ZONE '${createdAt.format(TIMESTAMP)}',
                'TransactionRegistered', 'AdjustBalancePolicy')
    """.trimIndent()

    fun adjustments(database: Database): List<String> = database.fetchAll(
        sql = "SELECT transaction_id FROM account.adjustments ORDER BY transaction_id",
        mapper = RowMapper { row -> row.text(column = "transaction_id") },
        parameters = emptyMap()
    )

    fun storedFailures(database: Database): List<StoredPolicyFailure> = database.fetchAll(
        sql = "SELECT error, resent, payload, event_name, policy_name FROM account.troubleshoot ORDER BY created_at",
        mapper = RowMapper { row ->
            StoredPolicyFailure(
                error = row.text(column = "error"),
                resent = row.integer(column = "resent"),
                payload = row.text(column = "payload"),
                eventName = row.text(column = "event_name"),
                policyName = row.text(column = "policy_name")
            )
        },
        parameters = emptyMap()
    )

    private val TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx")
}
