* [Financial transaction registering](#financial-transaction-registering)
* [Balance adjusting](#balance-adjusting)
* [Failures solving](#failures-solving)

## Financial transaction registering

#### Registers a financial transaction in bookkeeping and emits the event that the account context reacts to.

The command creates a `Transaction` aggregate and appends the `TransactionRegistered` event to the
`bookkeeping.transactions` event store. Nothing else is written: the balances change only when the account context
consumes the event, after the command returns.

**Console** `make register-financial-transaction`

### Command

The command takes no input. Its values are fixed, except for the amount, drawn at random on each run.

| Field           |    Type    | Description                   | Value                                 |
|:----------------|:----------:|:------------------------------|:--------------------------------------|
| `amount`        | BigDecimal | Amount of the transaction.    | Random whole value from 10 to 10000.  |
| `currency`      |   String   | Currency of the amount.       | `BRL`                                 |
| `debitAccount`  |   String   | Account the amount leaves.    | `2.1.1.001`                           |
| `creditAccount` |   String   | Account the amount goes into. | `2.2.1.001`                           |

### Result

- **Log**: `Transaction with value <amount> has been registered`.

- **Event**: `TransactionRegistered`, revision 1, sequence 1, appended to `bookkeeping.transactions` with its
  `occurred_on` in UTC, and published by change data capture on the topic
  `financial.accounting.bookkeeping.transaction-registered`.

  **Payload**:
  ```json
  {
      "id": "4fc06d14-0a42-41af-8d78-710b2a60947d",
      "amount": {
          "value": 100,
          "currency": "BRL"
      },
      "status": 1,
      "debitAccount": "2.1.1.001",
      "creditAccount": "2.2.1.001"
  }
  ```

## Balance adjusting

#### Moves the amount of a registered transaction from the debit account to the credit account.

The `AdjustBalancePolicy` of the account context reacts to each `TransactionRegistered` consumed from Kafka with the
command `AdjustBalance`. The amount is subtracted from the debit account and added to the credit account, even when
that leaves the debit account at zero or negative. Both balances change in one database transaction, so a transfer is
never half applied, and the transaction id is recorded in `account.adjustments`, so an event delivered twice adjusts the
balances only once. The command is not exposed to the console.

**Trigger** `TransactionRegistered` on `financial.accounting.bookkeeping.transaction-registered`

### Command

| Field           |    Type    | Description                      | Constraints                                    |
|:----------------|:----------:|:---------------------------------|:-----------------------------------------------|
| `amount`        | BigDecimal | Amount to move.                  | Taken from the event.                          |
| `currency`      |   String   | Currency of the amount.          | Same currency as both accounts.                |
| `debitAccount`  |   String   | Number of the account to debit.  | Must exist in `account.balances`.              |
| `creditAccount` |   String   | Number of the account to credit. | Must exist in `account.balances`.              |
| `transactionId` |   String   | Identifier of the transaction.   | The `id` of the `TransactionRegistered` event. |

### Result

- **Success**: both rows of `account.balances` are updated with the new balance and `updated_at`, and the transaction
  id is inserted in `account.adjustments`, in one database transaction.

- **Redelivery**: when the transaction id is already in `account.adjustments`, nothing changes.

- **Concurrent change**: when either balance changed after it was read (its `version` no longer matches), neither
  balance changes and the transaction id is not stored. The failure is stored like any other below, and a replay
  adjusts from the current balances.

- **Failure**: the policy does not stop the stream. The event, the policy and the error are stored in
  `account.troubleshoot`, from which [Failures solving](#failures-solving) runs the policy again.

  Typical errors:

  ```
  Account with number <number> not found
  ```

  ```
  Currencies cannot be different
  ```

  ```
  Balance of account <number> changed during the adjustment
  ```

## Failures solving

#### Runs again the policies whose events failed, from the troubleshooting queue.

The command reads up to 100 entries of `account.troubleshoot` not yet resent, oldest first, and runs each event again
through the policy that failed on it. An entry is marked as resent only when the policy succeeds. When it fails again,
the entry stays pending with the new error, so the next run retries it. The initial data seeds one failed
`TransactionRegistered` of BRL 100.00 from `2.1.1.001` to `2.2.1.001`, so the command has something to solve on a fresh
environment.

**Console** `make solve-failures`

### Command

| Field   | Type | Description                               | Value |
|:--------|:----:|:------------------------------------------|:------|
| `limit` | Int  | Maximum number of entries read in a run.  | `100` |

### Result

- **Log**: `Number of processed failures: <count>`.

- **Effect**: each entry whose policy succeeds gets `resent = 1` and `updated_at` set, and its policy runs as in
  [Balance adjusting](#balance-adjusting). Each entry whose policy fails again keeps `resent = 0` and gets the new
  `error` and `updated_at`.
