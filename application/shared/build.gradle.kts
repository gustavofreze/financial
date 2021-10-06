dependencies {
    implementation("io.insert-koin:koin-core:${property("koinVersion")}")

    implementation("com.github.kittinunf.fuel:fuel:${property("fuelVersion")}")
    implementation("com.github.kittinunf.fuel:fuel-jackson:${property("fuelVersion")}")

    implementation("com.fasterxml.jackson.core:jackson-core:${property("jacksonVersion")}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${property("jacksonVersion")}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${property("jacksonVersion")}")

    implementation("org.apache.kafka:kafka-streams:${property("kafkaStreamsVersion")}")
    implementation("org.apache.kafka:kafka-clients:${property("kafkaClientVersion")}")
    implementation("io.confluent:kafka-avro-serializer:${property("kafkaVersion")}")
    implementation("io.confluent:kafka-streams-avro-serde:${property("kafkaVersion")}")

    implementation("org.slf4j:slf4j-simple:${property("slf4jVersion")}")
    implementation("io.github.microutils:kotlin-logging:${property("loggingVersion")}")
}