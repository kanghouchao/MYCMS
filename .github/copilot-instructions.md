# Copilot / AI Assistant Instructions for MYCMS

**This project uses Spec Kit for specification-driven development.**  
Before implementing any new feature or making major changes, ensure this constitution is followed: `./specify/memory/constitution.md`.

---

## MUST follow these guidelines

- **Use MCP where possible.** Prefer integrating AI assistants with the Model Context Protocol (MCP) so the assistant can access repository context and tooling in a controlled way.
- **Security note:** MCP integrations provide powerful capabilities; prefer read-only or limited access connectors when possible and avoid exposing secrets or credentials to agents.
- **Respect the constitution.** All code changes must comply with the project constitution in `./specify/memory/constitution.md`. PRs will be reviewed for compliance.
