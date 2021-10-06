package mock

import financial.shared.di.Container
import io.mockk.mockk
import mock.command.CreateTransactionHandler
import org.koin.core.KoinApplication
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.reflect.KClass

object ContainerFactoryForTest : Container {

    private val application: KoinApplication by lazy { koinApplication { modules(module) } }

    override fun <T> get(className: String): T = application.koin.get(Class.forName(className).kotlin)

    override fun has(className: String): Boolean { TODO("Not yet implemented") }

    private val module = module {
        single { mockk<CreateTransactionHandler>(relaxed = true) } bind CreateTransactionHandler::class
    }
}