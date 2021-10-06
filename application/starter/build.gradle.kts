plugins {
    application
    id("com.github.johnrengelman.shadow")
}

application {
    mainClass.set("financial.starter.ApplicationKt")
}

dependencies {
    implementation(project(":application:shared"))
    implementation(project(":application:account"))
    implementation(project(":application:bookkeeping"))

    implementation("io.insert-koin:koin-core:${property("koinVersion")}")
}