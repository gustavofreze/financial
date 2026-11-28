package financial.accounting.account.query.balance.database

object Queries {

    val FIND_ALL = """
        SELECT number, balance
        FROM account.balances
        ORDER BY id;
    """.trimIndent()
}
