# Detekt

Static analysis for Kotlin sources in `:app` and `:libs`.

## Local

```bash
./gradlew detekt --no-daemon
```

Config: `config/detekt/detekt.yml`

## CI

The main `CI` workflow runs `detekt` before the debug APK build.
Failures are currently non-blocking (`|| true`) so phone-first builds stay green while rules are tuned.

Tighten later by removing `|| true` once the baseline is clean.
