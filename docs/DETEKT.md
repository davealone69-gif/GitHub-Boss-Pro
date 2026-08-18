# Detekt

Config lives at `config/detekt/detekt.yml`.

## Local run (once detekt is applied in build)

```bash
./gradlew detekt
```

## Status

- Config file present ✅
- Plugin declared in version catalog ✅
- Not yet applied as a hard CI gate (to keep skeleton green while features are frozen)

When ready, add to root or module build files and enable the gate in `ci.yml`.
