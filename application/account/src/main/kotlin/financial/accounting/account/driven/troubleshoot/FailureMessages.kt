package financial.accounting.account.driven.troubleshoot

import financial.accounting.account.application.domain.exceptions.InvalidAccountId
import financial.accounting.account.application.exceptions.AccountBalanceNotFound
import financial.accounting.account.application.exceptions.AccountBalancesUnavailable
import financial.accounting.account.application.exceptions.BalanceAdjustmentConflict
import financial.accounting.account.application.exceptions.PolicyNotFound
import financial.shared.domain.exceptions.CurrencyMismatch
import financial.shared.domain.exceptions.UnsupportedCurrency

class FailureMessages {

    fun describe(failure: Exception): String = when (failure) {
        is PolicyNotFound -> "Policy ${failure.policyName} not found"
        is CurrencyMismatch -> "Currencies cannot be different"
        is InvalidAccountId -> "Account id ${failure.value} is not valid"
        is UnsupportedCurrency -> "Currency ${failure.value} is not supported"
        is AccountBalanceNotFound -> "Account with number ${failure.number.value} not found"
        is BalanceAdjustmentConflict -> "Balance of account ${failure.number.value} changed during the adjustment"
        is AccountBalancesUnavailable -> "Account balances are unavailable\n${failure.cause.stackTraceToString()}"
        else -> failure.stackTraceToString()
    }
}
