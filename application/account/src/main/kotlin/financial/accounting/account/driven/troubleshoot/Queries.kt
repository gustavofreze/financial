package financial.accounting.account.driven.troubleshoot

object Queries {

    val INSERT = """
        INSERT INTO account.troubleshoot (id, error, payload, event_name, policy_name)
        VALUES (:id, :error, :payload, :event_name, :policy_name);
    """.trimIndent()

    val PENDING = """
        SELECT id, payload, policy_name
        FROM account.troubleshoot
        WHERE resent = 0
        ORDER BY created_at
        LIMIT :limit;
    """.trimIndent()

    val RESOLVE = """
        UPDATE account.troubleshoot
        SET resent = 1, updated_at = :updated_at
        WHERE id = :id;
    """.trimIndent()

    val RETAIN = """
        UPDATE account.troubleshoot
        SET error = :error, updated_at = :updated_at
        WHERE id = :id;
    """.trimIndent()
}
