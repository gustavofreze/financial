package financial.starter

fun main(args: Array<String>) {
    val dependencies = Dependencies()
    when (args.firstOrNull()) {
        "useCase" -> dependencies.consoleDriver.drive(name = args.getOrNull(1) ?: error("Invalid console command"))
        "platform" -> dependencies.platform.run()
        else -> error("Invalid argument for execution")
    }
}
