# Agent rules

## Ownership
- I write the application code. Agents review, explain and may provide setup and configuration, but never write feature code unless I ask.

## Sessions
- Read the latest file in `sessions/` before starting new work. Read older ones if needed.
- Log each session's progress in `sessions/YYYY-MM-DD.md`. These files are never committed.

## Git
- Never commit or push without my permission.
- Keep commits small.
- Commit messages are a single line, no body or bullet list (e.g. "refactor: read and write offline drafts through the repositories").
- Never move changes between staged and unstaged without my permission.
- Create a stash as a backup when needed.
- Never mention Claude or any AI agent in commits: no AI names in messages and no "Co-Authored-By: Claude" or similar trailers.

## Secrets
- Never commit `sessions/` or credentials.
- Never put credentials, tokens, keys, personal emails or private URLs in `sessions/` or in any committed file.

## Code
- Do not add comments to the code.
- No magic numbers: use named constants.
- No hardcoded colors (e.g. `#E36E1C`): add new colors to the theme and use them from there.

## Files and tools
- Do not modify `agents.md`, `claude.md` or `todo.md` without my permission.
- When using browser automation (claude-in-chrome), never change anything sensitive, such as Figma files.
