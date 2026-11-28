package financial.accounting.account.driven.repository

object Queries {

    val FIND = """
        SELECT id, number, balance, currency, version, updated_at
        FROM account.balances
        WHERE number = :number;
    """.trimIndent()

    val REGISTER_ADJUSTMENT = """
        INSERT INTO account.adjustments (transaction_id, adjusted_at)
        VALUES (:transaction_id, :adjusted_at)
        ON CONFLICT DO NOTHING;
    """.trimIndent()

    val UPDATE = """
        UPDATE account.balances
        SET balance = :balance, updated_at = :updated_at, version = version + 1
        WHERE id = :id AND version = :version;
    """.trimIndent()
}
