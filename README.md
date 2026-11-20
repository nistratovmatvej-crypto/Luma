# Luma
Luma is an offline-first Android shell for a customizable team of AI agents. Each agent has its own role, personality, color, icon shape, autonomy level and local chat history. Provider connection is OpenAI-compatible and configurable.

## Build
Run `gradle assembleDebug` with Android SDK installed. GitHub Actions builds the APK on push and uploads `Luma-debug`.

## Current capabilities
- Native Android WebView shell and local UI assets (no CDN)
- Customizable agents and local persistence
- AI chat through a configurable OpenAI-compatible endpoint/model/API key
- Per-agent autonomy mode (confirm, guided, automatic within scope) injected into the prompt
- Settings for interface, privacy, drafts, motion, notifications, and advanced behavior
- Multi-file picker via Android document picker
- Honest capability boundaries: no external action tools are integrated yet; the agent cannot claim to execute them.
