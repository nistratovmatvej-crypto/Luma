# Luma Video Editor
Stage-one foundation of an offline-first, native Android video editor. **Android UI is Kotlin + Jetpack Compose (no WebView).**

## Structure
- `android/app` app entry and editor feature
- `core/ui` shared Compose theme
- `web` separate React + Vite workspace
- Native project navigation, local project list, media picker, preview/timeline scaffold, theme setting, export placeholder

## Build
GitHub Actions runs `gradle assembleDebug` and uploads `Luma-debug`. For local build, install Android SDK and run Gradle 8.11.1.

## Stage-one limits
Timeline and preview are UI scaffolds, not yet a non-linear editing engine. Media picker is wired to document URIs, but clips are not decoded/played in preview. Export creates a local text placeholder; no video render/encode in this milestone. No AI, account, ads, subscription, or network requirement for core UI.
