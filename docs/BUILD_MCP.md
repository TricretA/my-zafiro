# MCP implementation

## Status

The branch combines the existing MCP settings and OAuth implementation with:

- Encrypted Android Keystore-backed OAuth/header secret storage.
- OAuth and static-header records stored outside backup exports.
- Lazy refresh with a 60-second expiry skew and single-flight mutexes.
- `invalid_grant` marked as `NEEDS_REAUTH`.
- MCP `mcpServers` JSON import parsing and environment placeholder resolution.
- A shipped MCP/skills/workspaces catalog.

## Safety

- MCP write and destructive tools default through the existing unattended approval pattern.
- Money-moving connectors are marked approve-only in the catalog.
- Secrets are stripped from MCP backup partitions; re-authentication is required after restore.
- Skill installation remains GitHub-only and pinned-SHA oriented.

## Verification

- Android build and device/live-server behavior remain UNVERIFIED until CI and a physical phone test.
