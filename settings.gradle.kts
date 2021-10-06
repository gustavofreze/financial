rootProject.name = "financial"

include(":application:shared")
include(":application:account")
include(":application:starter")
include(":application:bookkeeping")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            val kotlinVersion: String by settings
            val shadowVersion: String by settings
            val testLoggerVersion: String by settings

            when (requested.id.id) {
                "com.adarshr.test-logger" -> useVersion(testLoggerVersion)
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "com.github.johnrengelman.shadow" -> useVersion(shadowVersion)
            }
        }
    }
}