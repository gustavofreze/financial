package mock

import org.apache.avro.Schema

object SchemaMock {

    private val schema = """
        {         
          "name": "%s",
          "type": "record",
          "fields": [
            {
              "name": "code",
              "type": "string"
            },
            {
              "name": "payload",
              "type": {
                "connect.name": "io.debezium.data.Json",
                "connect.version": 1,
                "type": "string"
              }
            },
            {
              "name": "revision",
              "type": "int"
            },
            {
              "name": "sequence",
              "type": "int"
            },
            {
              "name": "event_name",
              "type": "string"
            },
            {
              "name": "occurred_on",
              "type": {
                "connect.name": "io.debezium.time.ZonedTimestamp",
                "connect.version": 1,
                "type": "string"
              }
            },
            {
              "name": "aggregate_id",
              "type": "string"
            },
            {
              "name": "aggregate_type",
              "type": "string"
            }
          ]          
        }
    """.trimIndent()

    fun get(name: String): Schema {
        return Schema.Parser().parse(schema.format(name))
    }
}