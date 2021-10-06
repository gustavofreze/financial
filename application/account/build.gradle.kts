dependencies {
    implementation(project(":application:shared"))

    implementation("org.apache.kafka:kafka-streams:${property("kafkaStreamsVersion")}")
    implementation("org.apache.kafka:kafka-clients:${property("kafkaClientVersion")}")
    implementation("io.confluent:kafka-avro-serializer:${property("kafkaVersion")}")
    implementation("io.confluent:kafka-streams-avro-serde:${property("kafkaVersion")}")
}