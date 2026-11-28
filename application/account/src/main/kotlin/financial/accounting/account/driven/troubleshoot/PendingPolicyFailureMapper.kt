package financial.accounting.account.driven.troubleshoot

import financial.accounting.account.application.ports.outbound.PendingPolicyFailure
import financial.shared.adapters.database.Row
import financial.shared.adapters.database.RowMapper

class PendingPolicyFailureMapper : RowMapper<PendingPolicyFailure> {

    override fun map(row: Row) = PendingPolicyFailure(
        id = row.text(column = "id"),
        payload = row.text(column = "payload"),
        policyName = row.text(column = "policy_name")
    )
}
