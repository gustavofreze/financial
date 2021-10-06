package financial.starter

import financial.shared.adapters.console.command.CommandConsoleDriver
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    val platform = Dependencies.get<Platform>(Platform::class.qualifiedName!!)
    val consoleDriver = Dependencies.get<CommandConsoleDriver>(CommandConsoleDriver::class.qualifiedName!!)

    when (args.firstOrNull()) {
        "useCase" -> consoleDriver.drive(args)
        "platform" -> platform.run()
        else -> error("Invalid argument for execution")
    }
}