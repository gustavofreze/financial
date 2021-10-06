package financial.accounting.bookkeeping.driven.repository

object Queries {

    val INSERT = """
        INSERT INTO bookkeeping.transactions (code, payload, revision, sequence, event_name, occurred_on, aggregate_id, aggregate_type)
        VALUES (:code, :payload, :revision, :sequence, :event_name, :occurred_on, :aggregate_id, :aggregate_type);
        """.trimIndent()
}