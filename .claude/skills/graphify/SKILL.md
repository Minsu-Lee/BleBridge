---
name: graphify
description: This skill should be used when the user invokes "/graphify", asks to "build or update the graphify graph", or requests a graphify query, path, or explanation. Delegates the complete workflow to Codex to avoid consuming the Claude Code context window.
allowed-tools: Bash
argument-hint: "[path | query | flags]"
---

# Graphify via Codex

Delegate the entire graphify request to Codex. Do not read the Codex graphify
skill, reproduce its pipeline, inspect its references, or perform graph extraction
inside Claude Code.

Run this command once from the project root:

```bash
bash .claude/skills/graphify/scripts/delegate-to-codex.sh "$ARGUMENTS"
```

Wait for completion and return the command output to the user. If the command
fails, report its exit status and error output without attempting the graphify
pipeline in Claude Code.
