# CI / CD

## Active workflows

- `.github/workflows/ci.yml`  
  Runs on push / PR to `main` & `develop`.  
  Builds debug APK, attempts release AAB, runs unit tests, uploads artifacts.

- `.github/workflows/build-apk.yml`  
  Self-contained build of **this** repo only.  
  Triggered on relevant path changes or manually.

- `.github/workflows/release.yml`  
  Manual (workflow_dispatch) signed AAB build.  
  Uses GitHub secrets when present (`ANDROID_KEYSTORE_BASE64`, etc.).  
  Does **not** auto-upload to Play Store yet.

## Secrets (optional, for signed release)

| Secret                    | Required for signed build |
|---------------------------|---------------------------|
| `ANDROID_KEYSTORE_BASE64` | Yes                       |
| `KEYSTORE_PASSWORD`       | Yes                       |
| `KEY_ALIAS`               | Yes                       |
| `KEY_PASSWORD`            | Yes                       |
| `GOOGLE_PLAY_JSON_KEY`    | Only for Play upload      |

## Planned / next (skeleton only)

- Detekt gate in CI
- Coverage report upload
- Fastlane `internal` lane wired to secrets
