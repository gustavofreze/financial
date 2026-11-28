package financial.shared

import financial.shared.application.IntegrationEvent

data class AmountReceived(val amount: Int) : IntegrationEvent
