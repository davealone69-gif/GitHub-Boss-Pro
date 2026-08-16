# Signing

Never commit keystores or passwords.

## Local development
Add to your local `gradle.properties` (this file is gitignored):

```
STORE_FILE=/path/to/your.jks
STORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

## CI (GitHub Actions)
Add these secrets:
- ANDROID_KEYSTORE_BASE64
- STORE_PASSWORD
- KEY_ALIAS
- KEY_PASSWORD
