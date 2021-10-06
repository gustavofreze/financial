package unit.financial.shared.adapters.console.command.resolver

import financial.shared.adapters.console.command.resolver.CommandHandlerResolver
import financial.shared.adapters.console.command.resolver.CommandResolver
import financial.shared.adapters.console.command.resolver.exception.CommandHandlerNotFoundException
import mock.ContainerFactoryForTest
import mock.command.CommandWithoutHandler
import mock.command.CreateTransaction
import mock.command.CreateTransactionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CommandHandlerResolverTest {

    private val command = CreateTransaction()
    private lateinit var commandResolver: CommandResolver

    @BeforeEach
    fun `Before each`() {
        commandResolver = CommandHandlerResolver(container = ContainerFactoryForTest)
    }

    @Test
    fun `GIVEN that I have a command and its CommandHandler, WHEN the resolve method is called, THEN it resolves to the command handler`() {
        val actual = commandResolver.resolve(command)

        assertEquals(CreateTransactionHandler::class.simpleName, actual::class.simpleName)
    }

    @Test
    fun `GIVEN I have a command and I don't have its CommandHandler, WHEN the resolve method is called, THEN return an error`() {
        val path = "unit.mock.command"

        assertThrows<CommandHandlerNotFoundException>("No CommandHandler found in path ${path}.") {
            commandResolver.resolve(CommandWithoutHandler())
        }
    }
}