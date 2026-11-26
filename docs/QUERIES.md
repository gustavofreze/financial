* [Show balances](#show-balances)

## Show balances

#### Shows the balance of every account, with its sign as a status.

The query reads `account.balances` and prints one line per account. A negative balance is printed in parentheses, as
in accounting notation. Both seeded accounts start at BRL 100.00.

**Console** `make show-balance`

### Query

The query takes no input.

### Result

| Column    | Description                                                     |
|:----------|:----------------------------------------------------------------|
| `Account` | Number of the account.                                          |
| `Balance` | Balance with two decimals, in parentheses when negative.        |
| `Status`  | `POSITIVE`, `ZERO` or `NEGATIVE`, from the sign of the balance. |

```
+--------------+--------------+--------------+
| Account      | Balance      | Status       |
+--------------+--------------+--------------+
| 2.1.1.001    | 100.00       | POSITIVE     |
| 2.2.1.001    | 100.00       | POSITIVE     |
+--------------+--------------+--------------+
```
