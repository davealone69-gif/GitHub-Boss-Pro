# GitHub-Boss-Pro

**Phone-first tool** to make GitHub + Termux easier on Android.

Control repos, issues, workflows, and notifications from your phone without needing a laptop.

---

## Status (2026-08-19)

- Multi-module skeleton (`:app` + `:libs`) + buildSrc + version catalog ✅
- Convention plugins + ProjectConfig (SDK levels, Java 17) ✅ applied
- Modern Compose (Kotlin 2.0) ✅
- PAT login + repos / issues / workflows / notifications ✅
- Builder (template + free Gemini) + Help / Termux coach ✅
- CI builds this repo (APK + AAB) + detekt ✅
- Secure signing placeholders (secrets out of VCS) ✅
- Fastlane skeleton ✅
- Feature migration from old `GitHub-Boss` ✅ (avatar intentionally not ported)

## Purpose

| This app **is** | This app is **not** |
|-----------------|---------------------|
| GitHub control from Android | Avatar / AI character app |
| Termux-friendly workflow helper | Aura / Mandela product |
| Clean production Android project | Desktop replacement |

## Quick start

```bash
# One-time: restore wrapper jar if missing
# curl -fsSL -o gradle/wrapper/gradle-wrapper.jar \
#   https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar

./gradlew assembleDebug
./gradlew bundleRelease
./gradlew detekt
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
├── GEMINI-FREE.md        # Free Gemini key guide
├── TERMUX-COMMANDS.md    # Phone/Termux build commands
└── docs/                 # Documentation
```

## Docs

- [Signing](docs/SIGNING.md)
- [CI / CD](docs/CI.md)
- [Detekt](docs/DETEKT.md)
- [Gradle Wrapper](docs/WRAPPER.md)
- [Free Gemini](GEMINI-FREE.md)
- [Termux commands](TERMUX-COMMANDS.md)

---

Made with help from Grok
