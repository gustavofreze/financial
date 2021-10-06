import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "financial"

plugins {
    kotlin("jvm")
    id("com.adarshr.test-logger")
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://packages.confluent.io/maven")
    }
}

subprojects {
    apply(plugin = "com.adarshr.test-logger")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:${property("kotlinVersion")}")
        implementation("org.jetbrains.kotlin:kotlin-reflect:${property("kotlinVersion")}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("coroutinesVersion")}")

        implementation("org.jdbi:jdbi3-core:${property("jdbiVersion")}")
        implementation("org.jdbi:jdbi3-kotlin:${property("jdbiVersion")}")
        implementation("org.postgresql:postgresql:${property("postgresqlVersion")}")

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("jupiterVersion")}")
        testImplementation("io.mockk:mockk:${property("mockkVersion")}")
        testImplementation("com.h2database:h2:${property("h2Version")}")
        testImplementation("org.junit.jupiter:junit-jupiter-api:${property("jupiterVersion")}")
        testImplementation("org.junit.jupiter:junit-jupiter-params:${property("jupiterVersion")}")
        testImplementation(kotlin("test"))
    }

    tasks.withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }

    tasks.withType<Test> {
        useJUnitPlatform { excludeTags = setOf("Integration") }
    }

    tasks.register<Test>("allTests") {
        outputs.upToDateWhen { false }
        useJUnitPlatform { }
    }

    tasks.register<Test>("unitTest") {
        outputs.upToDateWhen { false }
        useJUnitPlatform { excludeTags = setOf("Integration") }
    }

    tasks.register<Test>("integrationTest") {
        outputs.upToDateWhen { false }
        useJUnitPlatform { includeTags = setOf("Integration") }
    }
}