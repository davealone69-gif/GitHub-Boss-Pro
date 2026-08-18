# GitHub-Boss-Pro

Clean production-ready multi-module Android project for **Total GitHub AI control**.

This is the official clean home for the Github Boss app.

---

## Status (2026-08-18)

- Multi-module skeleton (`:app` + `:libs`) + buildSrc + version catalog ✅
- Convention plugins apply ProjectConfig (compile/min/target SDK, Java 17) ✅
- Modern Compose (Kotlin 2.0 Compose plugin) ✅
- Working PAT login + repository list UI (already present) ✅
- CI builds **this** repo (APK + AAB) ✅
- Secure signing placeholders (secrets stay out of VCS) ✅
- Fastlane skeleton ✅
- Detekt config ✅
- Release workflow stub (manual, secrets-based) ✅
- **Feature migration frozen** — nothing new added without explicit approval

## Quick start

```bash
# Restore wrapper jar if missing (one-time)
# curl -fsSL -o gradle/wrapper/gradle-wrapper.jar \
#   https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar

./gradlew assembleDebug
./gradlew bundleRelease
```

## Required PAT scopes

- `repo`
- `workflow`
- `read:user`
- `notifications`

Generate at: https://github.com/settings/tokens

## Project structure

```
GitHub-Boss-Pro/
├── app/                  # Main application module
├── libs/                 # Shared library module
├── buildSrc/             # Convention plugins + ProjectConfig
├── gradle/               # Version catalog + wrapper
├── fastlane/             # Play Store / build lanes
├── config/detekt/        # Static analysis config
├── .github/workflows/    # CI + release
└── docs/                 # Documentation
```

## Talk to Grok

Just say you’re working on GitHub-Boss-Pro and the context is already loaded.

---

Made with help from Grok
