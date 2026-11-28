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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        exclusiveContent {
            forRepository { maven(url = "https://packages.confluent.io/maven") }
            filter {
                includeModuleByRegex("io\\.confluent.*", "(?!logredactor).*")
                includeVersionByRegex("org\\.apache\\.kafka", ".*", ".*-ccs")
            }
        }
    }
}
