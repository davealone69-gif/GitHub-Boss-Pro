# Signing

**Never commit keystores or passwords.**

## Local development

Create a local (gitignored) `gradle.properties` or `gradle.properties.local` and add:

```
KEYSTORE_PATH=/absolute/path/to/your.jks
KEYSTORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

The app module already reads these via environment variables or project properties.

## CI (GitHub Actions)

Recommended secrets:

| Secret                    | Description                          |
|---------------------------|--------------------------------------|
| `ANDROID_KEYSTORE_BASE64` | Base64-encoded .jks / .keystore      |
| `KEYSTORE_PASSWORD`       | Store password                       |
| `KEY_ALIAS`               | Key alias                            |
| `KEY_PASSWORD`            | Key password                         |

Decode the keystore in the workflow before the release build step:

```bash
echo "$ANDROID_KEYSTORE_BASE64" | base64 -d > $RUNNER_TEMP/release.jks
export KEYSTORE_PATH=$RUNNER_TEMP/release.jks
```

Then run `:app:bundleRelease` or Fastlane `internal`.
