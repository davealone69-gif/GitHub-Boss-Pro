# Detekt

Static analysis for Kotlin sources under `app/src` and `libs/src`.

## Config

- Rules: `config/detekt/detekt.yml`
- Root plugin: `build.gradle.kts` (detekt + formatting plugin)
- Reports: HTML / XML / TXT / MD under `build/reports/detekt/`

## Local

```bash
./gradlew detekt
```

## CI

`.github/workflows/ci.yml` runs `detekt` and uploads `detekt-reports` artifact.

Currently `ignoreFailures = true` so issues are **reported without failing the build** (baseline phase).

### Harden later

In root `build.gradle.kts`:

```kotlin
ignoreFailures = false
```

And lower `maxIssues` in `detekt.yml` once the report is clean.
