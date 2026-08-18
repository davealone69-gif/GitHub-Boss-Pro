# CI / CD

## Active workflows

- `.github/workflows/ci.yml`  
  Runs on push / PR to `main` & `develop`.  
  Builds debug APK, attempts release AAB, runs unit tests, uploads artifacts.

- `.github/workflows/build-apk.yml`  
  Self-contained build of **this** repo only (no longer depends on the old GitHub-Boss repo).  
  Triggered on relevant path changes or manually.

## Planned / next

- Signed release workflow using GitHub secrets + AAB upload
- Fastlane `internal` lane for Play Store internal testing track
- Detekt + lint gate
- Coverage report upload
