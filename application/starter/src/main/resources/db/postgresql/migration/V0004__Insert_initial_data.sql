INSERT INTO account.balances (id, number, balance, currency, updated_at)
VALUES (1, '2.1.1.001', 100.00, 'BRL', NOW()),
       (2, '2.2.1.001', 100.00, 'BRL', NOW());

INSERT INTO account.troubleshoot (id, error, resent, payload, created_at, updated_at, event_name, policy_name)
VALUES ('e8985137-26d4-4286-8b46-4fd86c306c4c', 'Error', 0, '{"id":"4fc06d14-0a42-41af-8d78-710b2a60947d","amount":{"value":100.00,"currency":"BRL"},"status":1,"debitAccount":"2.1.1.001","creditAccount":"2.2.1.001"}', NOW(), null, 'financial.accounting.account.driver.policies.events.TransactionRegistered', 'financial.accounting.account.driver.policies.AdjustBalancePolicy');
