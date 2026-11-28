plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(libs.slf4j.api)
    implementation(libs.jdbi.core)
    implementation(libs.hikari)
    implementation(libs.avro)
    implementation(libs.kafka.clients)
    implementation(libs.kafka.streams)
    implementation(libs.kafka.streams.avro.serde)
    implementation(libs.jackson.module.kotlin)

    constraints {
        implementation(libs.guava)
    }

    testImplementation(libs.kafka.streams.test.utils)
    testRuntimeOnly(libs.slf4j.simple)

    testFixturesImplementation(libs.h2)
    testFixturesImplementation(libs.jdbi.core)
}
