ALTER TABLE account.troubleshoot
    ALTER COLUMN payload TYPE TEXT;

COMMENT ON COLUMN account.troubleshoot.payload IS '[CONFIDENTIAL] The payload of the event as the policy received it, kept as text so a malformed one is recorded too (e.g., {"id":"4fc06d14-0a42-41af-8d78-710b2a60947d"}).';
