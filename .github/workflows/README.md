# GitHub Actions Workflows

This directory contains automated CI/CD workflows for the Viber Android app.

## Workflows

### `build.yml` - Android CI
Automatically builds and tests the app on every PR and push to main.

### `release.yml` - Android Release
Automatically builds and attaches APK/AAB files to GitHub releases.

## Setup

See [CI_CD.md](../../CI_CD.md) in the repository root for complete setup instructions and required secrets.

## Quick Start

1. Add `GITHUB_CLIENT_ID` secret to repository settings
2. (Optional) Add signing secrets for release builds
3. Create a PR - CI will automatically run
4. Create a release - APK/AAB will be automatically attached
