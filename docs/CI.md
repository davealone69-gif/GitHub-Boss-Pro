# CI / CD

## Current workflow
- `.github/workflows/ci.yml` runs on push and pull requests to `main` / `develop`
- Builds `assembleDebug`
- Runs unit tests

## Planned
- Release workflow with signing + AAB upload
- Fastlane for Play Store internal track
