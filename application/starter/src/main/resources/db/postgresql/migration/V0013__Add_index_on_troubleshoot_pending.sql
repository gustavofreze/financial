CREATE INDEX idx_troubleshoot_created_at ON account.troubleshoot (created_at) WHERE resent = 0;
