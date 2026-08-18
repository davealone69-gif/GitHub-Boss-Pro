# Gradle Wrapper

The `gradle-wrapper.jar` is currently missing from the repository (binary).

## One-time restore (local or CI)

```bash
mkdir -p gradle/wrapper
curl -fsSL -o gradle/wrapper/gradle-wrapper.jar \
  https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar
chmod +x gradlew
```

Then commit the jar if desired, or keep the CI fallback that installs Gradle 8.9 when the jar is absent.
