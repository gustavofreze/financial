import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

group = "financial"

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

val ktlint: Configuration = configurations.create("ktlint")

val kotlinSources = listOf("*.gradle.kts", "application/*/build.gradle.kts", "application/*/src/**/*.kt")

val lintedFiles = files(
    fileTree(rootDir) { include("*.gradle.kts", ".editorconfig") },
    fileTree("application") {
        include("*/build.gradle.kts", "*/src/**/*.kt")
        exclude("*/build/**")
    }
)

dependencies {
    ktlint(libs.ktlint.cli) {
        attributes { attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL)) }
    }
}

tasks.register<JavaExec>("ktlintCheck") {
    group = "verification"
    description = "Checks every Kotlin source and build script against the ktlint rules of .editorconfig."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--sun-misc-unsafe-memory-access=allow")
    val report = layout.buildDirectory.file("reports/ktlint/check.xml")
    inputs.files(lintedFiles).withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.file(report)
    args(listOf("--reporter=plain", "--reporter=checkstyle,output=${report.get().asFile}") + kotlinSources)
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fixes every Kotlin source and build script that ktlint can format."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--sun-misc-unsafe-memory-access=allow")
    inputs.files(lintedFiles).withPathSensitivity(PathSensitivity.RELATIVE)
    args(listOf("--format") + kotlinSources)
}

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

val pinnedLibraries = versionCatalog.libraryAliases
    .map { alias -> versionCatalog.findLibrary(alias).get().get() }
    .filter { library -> library.versionConstraint.requiredVersion.isNotEmpty() }
    .map { library -> Triple(library.module.group, library.module.name, library.versionConstraint.requiredVersion) }

val pinnedPlugins = versionCatalog.pluginAliases
    .map { alias -> versionCatalog.findPlugin(alias).get().get() }
    .map { plugin -> Triple(plugin.pluginId, "${plugin.pluginId}.gradle.plugin", plugin.version.requiredVersion) }

tasks.register("showOutdated") {
    group = "help"
    description = "Lists every dependency and plugin of the version catalog whose latest stable release is newer."
    val libraries = pinnedLibraries
    val plugins = pinnedPlugins
    doLast {
        val client = java.net.http.HttpClient.newHttpClient()
        val release = Regex("""^\d+(\.\d+)*""")
        val qualifier = { version: String -> version.replace(release, "") }
        val segments = { version: String -> Regex("""\d+""").findAll(version).map { it.value.toLong() }.toList() }
        val newest = Comparator<String> { left, right ->
            segments(left).zip(segments(right)).map { (first, second) -> first.compareTo(second) }
                .firstOrNull { comparison -> comparison != 0 } ?: segments(left).size.compareTo(segments(right).size)
        }
        val repositories = libraries.map { library ->
            val confluent = library.first.startsWith("io.confluent") || library.third.endsWith("-ccs")
            library to if (confluent) "https://packages.confluent.io/maven" else "https://repo.maven.apache.org/maven2"
        } + plugins.map { plugin -> plugin to "https://plugins.gradle.org/m2" }
        repositories.forEach { (coordinate, repository) ->
            val (group, name, current) = coordinate
            val url = "$repository/${group.replace('.', '/')}/$name/maven-metadata.xml"
            val request = java.net.http.HttpRequest.newBuilder(java.net.URI.create(url)).build()
            val metadata = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).body()
            val latest = Regex("<version>([^<]+)</version>").findAll(metadata)
                .map { match -> match.groupValues[1] }
                .filter { version -> release.containsMatchIn(version) && qualifier(version) == qualifier(current) }
                .maxWithOrNull(newest)
            if (latest != null && latest != current) {
                println("$group:$name $current -> $latest")
            }
        }
    }
}

val catalog = libs

val compositionRoot = ":application:starter"

val applicationModules = subprojects.filter { it.parent != rootProject }

configure(applicationModules) {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    configure<KotlinJvmProjectExtension> {
        jvmToolchain(27)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_26)
            allWarningsAsErrors.set(true)
            freeCompilerArgs.add("-Xjdk-release=26")
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(26)
    }

    dependencies {
        "testImplementation"(platform(catalog.junit.bom))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testImplementation"(kotlin("test"))
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        filter { isFailOnNoMatchingTests = false }
        testLogging {
            events("failed")
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

configure(applicationModules.filter { it.path != compositionRoot }) {
    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = catalog.versions.jacoco.get()
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        reports.xml.required.set(true)
    }

    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        dependsOn("test")
        violationRules {
            rule {
                listOf("LINE", "BRANCH", "INSTRUCTION", "METHOD").forEach { metric ->
                    limit {
                        counter = metric
                        minimum = "1.0".toBigDecimal()
                    }
                }
            }
        }
    }

    val pitest = configurations.create("pitest")
    val testRuntimeClasspath = the<SourceSetContainer>()["test"].runtimeClasspath

    dependencies {
        "pitest"(catalog.pitest.command.line)
        "pitest"(catalog.pitest.junit5.plugin)
    }

    tasks.register<JavaExec>("pitest") {
        group = "verification"
        description = "Runs PIT over the module and fails below a mutation score of 100."
        dependsOn("testClasses")
        classpath = pitest + testRuntimeClasspath
        mainClass.set("org.pitest.mutationtest.commandline.MutationCoverageReport")
        val sources = file("src/main/kotlin")
        val reports = layout.buildDirectory.dir("reports/pitest").get().asFile
        inputs.dir(sources).withPathSensitivity(PathSensitivity.RELATIVE)
        outputs.dir(reports)
        args(
            "--targetClasses=financial.*",
            "--targetTests=financial.*",
            "--sourceDirs=$sources",
            "--mutableCodePaths=${layout.buildDirectory.dir("classes/kotlin/main").get().asFile}",
            "--reportDir=$reports",
            "--outputFormats=HTML,XML",
            "--timestampedReports=false",
            "--threads=2",
            "--mutationThreshold=100",
            "--avoidCallsTo=kotlin.jvm.internal"
        )
    }
}
