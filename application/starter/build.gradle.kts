plugins {
    application
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("financial.starter.ApplicationKt")
}

dependencies {
    implementation(project(":application:shared"))
    implementation(project(":application:account"))
    implementation(project(":application:bookkeeping"))

    compileOnly(libs.kafka.clients)
    compileOnly(libs.kafka.streams)

    runtimeOnly(libs.slf4j.simple)
    runtimeOnly(libs.postgresql)
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    exclude(
        "librocksdbjni-*musl*",
        "librocksdbjni-osx*",
        "librocksdbjni-win*",
        "librocksdbjni-linux32*",
        "librocksdbjni-linux-ppc64le*",
        "librocksdbjni-linux-riscv64*",
        "librocksdbjni-linux-s390x*"
    )
}
