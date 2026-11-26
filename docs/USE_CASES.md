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

| Field           |  Type  | Description                   | Value                                   |
|:----------------|:------:|:------------------------------|:----------------------------------------|
| `amount`        | Amount | Amount of the transaction.    | Random whole value from 10 to 10000 BRL. |
| `debitAccount`  | String | Account the amount leaves.    | `2.1.1.001`                             |
| `creditAccount` | String | Account the amount goes into. | `2.2.1.001`                             |

### Result

- **Log**: `Transaction with value <amount> has been registered`.

- **Event**: `TransactionRegistered`, revision 1, appended to `bookkeeping.transactions` and published by change data
  capture on the topic `financial.accounting.bookkeeping.transaction-registered`.

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
that leaves the debit account at zero or negative. The command is not exposed to the console.

**Trigger** `TransactionRegistered` on `financial.accounting.bookkeeping.transaction-registered`

### Command

| Field           |  Type  | Description                          | Constraints                                   |
|:----------------|:------:|:-------------------------------------|:----------------------------------------------|
| `id`            | String | Identifier of the transaction.       | UUID of the `TransactionRegistered` event.    |
| `balance`       | Amount | Amount to move.                      | Same currency as both accounts.               |
| `debitAccount`  | String | Number of the account to debit.      | Must exist in `account.balances`.             |
| `creditAccount` | String | Number of the account to credit.     | Must exist in `account.balances`.             |

### Result

- **Success**: both rows of `account.balances` are updated with the new balance and `updated_at`.

- **Failure**: the policy does not stop the stream. The event, the policy and the stack trace are stored in
  `account.troubleshoot`, from which [Failures solving](#failures-solving) runs the policy again.

  Typical errors:

  ```
  Account with number <number> not found
  ```

  ```
  Currencies cannot be different
  ```

## Failures solving

#### Runs again the policies whose events failed, from the troubleshooting queue.

The command reads up to 100 entries of `account.troubleshoot` not yet resent, runs each event through the policy that
failed on it, and marks the entry as resent. A policy that fails again stores a new entry. The initial data seeds one
failed `TransactionRegistered` of BRL 100.00 from `2.1.1.001` to `2.2.1.001`, so the command has something to solve on
a fresh environment.

**Console** `make solve-failures`

### Command

| Field   | Type | Description                               | Value |
|:--------|:----:|:------------------------------------------|:------|
| `limit` | Int  | Maximum number of entries read in a run.  | `100` |

### Result

- **Log**: `Number of processed failures: <count>`.

- **Effect**: each processed entry gets `resent = 1` and `updated_at` set, and its policy runs as in
  [Balance adjusting](#balance-adjusting).
