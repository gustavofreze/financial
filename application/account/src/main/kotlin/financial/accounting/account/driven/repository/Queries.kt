package financial.accounting.account.driven.repository

object Queries {

    val FIND = """
        SELECT *
        FROM account.balances
        WHERE number = :number;
        """.trimIndent()

    val UPDATE = """
        UPDATE account.balances
        SET balance = :balance, updated_at = :updated_at
        WHERE id = :id;
        """.trimIndent()
}