ALTER TABLE account.troubleshoot
    ADD CONSTRAINT chk_troubleshoot_resent CHECK (resent IN (0, 1));
