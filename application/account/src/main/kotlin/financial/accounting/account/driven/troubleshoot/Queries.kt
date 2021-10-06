package financial.accounting.account.driven.troubleshoot

object Queries {

    val LIST = """
        SELECT id, error, payload, event_name, policy_name
        FROM account.troubleshoot
        WHERE resent = 0
        LIMIT :limit;
        """.trimIndent()

    val INSERT = """
        INSERT INTO account.troubleshoot (id, error, payload, event_name, policy_name)
        VALUES (:id, :error, :payload, :event_name, :policy_name);
        """.trimIndent()

    val DELETE = """
        UPDATE account.troubleshoot
        SET resent = 1, updated_at = :updated_at
        WHERE id = :id;
        """.trimIndent()
}