package mock.command

import financial.shared.adapters.console.command.resolver.CommandHandler

class CreateTransactionHandler : CommandHandler<CreateTransaction> {

    override fun handle(command: CreateTransaction) {}
}