# CI/CD Configuration

This repository includes automated CI/CD workflows using GitHub Actions for building and releasing the Viber Android app.

## Workflows

### 1. Android CI (`build.yml`)

**Triggers:**
- Pull requests to `main` branch
- Pushes to `main` branch

**What it does:**
1. Sets up Java 17 and Android SDK
2. Grants execute permissions to gradlew
3. Creates local.properties with Android SDK path
4. Builds debug APK (`./gradlew assembleDebug`)
5. Runs lint checks (`./gradlew lintDebug`)
6. Runs unit tests (`./gradlew testDebugUnitTest`)
7. Uploads build artifacts (APK and lint results)

**Artifacts:**
- `debug-apk`: Debug APK files (retained for 7 days)
- `lint-results`: Lint analysis results (retained for 7 days)

### 2. Android Release (`release.yml`)

**Triggers:**
- GitHub release is created or published

**What it does:**
1. Sets up Java 17 and Android SDK
2. Decodes signing keystore (if provided)
3. Builds signed/unsigned release APK
4. Builds signed/unsigned release AAB (Android App Bundle)
5. Renames artifacts with version tag
6. Attaches APK and AAB to GitHub release
7. Uploads artifacts for download

**Artifacts:**
- `release-artifacts`: Release APK and AAB files (retained for 30 days)

## Required Secrets

Configure these secrets in your GitHub repository settings (`Settings` → `Secrets and variables` → `Actions`):

### Essential Secrets

#### `OAUTH_CLIENT_ID` (Required)
- **Description**: GitHub OAuth application client ID
- **Used in**: Both build and release workflows
- **How to get**: Create a GitHub OAuth app at https://github.com/settings/developers
- **Example**: `Iv1.1234567890abcdef`
- **Note**: Named `OAUTH_CLIENT_ID` instead of `GITHUB_CLIENT_ID` due to GitHub Actions naming limitations

#### `OAUTH_CLIENT_SECRET` (Required)
- **Description**: GitHub OAuth application client secret
- **Used in**: Both build and release workflows
- **How to get**: Generated when you create a GitHub OAuth app at https://github.com/settings/developers
- **Example**: `1234567890abcdef1234567890abcdef12345678`
- **Security**: Keep this secret secure! Never expose it in logs or commit it to the repository.

### GitHub App Secrets (Optional - for advanced API access)

Use these if you want to use GitHub App authentication for advanced features like Copilot APIs.

#### `GITHUB_APP_ID` (Optional)
- **Description**: GitHub App ID
- **Used in**: Both build and release workflows
- **How to get**: Create a GitHub App at https://github.com/settings/apps
- **Example**: `123456`

#### `GITHUB_APP_PRIVATE_KEY` (Optional)
- **Description**: GitHub App private key in PEM format
- **Used in**: Both build and release workflows
- **How to get**: Generate a private key in your GitHub App settings
- **Security**: This is a sensitive key! Keep it secure and never commit it to the repository.

#### `GITHUB_APP_INSTALLATION_ID` (Optional)
- **Description**: GitHub App installation ID for your organization/user
- **Used in**: Both build and release workflows
- **How to get**: Install your GitHub App on your account and get the installation ID from the URL
- **Example**: `12345678`

### Release Signing Secrets (Optional but recommended for production)

#### `KEYSTORE_BASE64` (Optional)
- **Description**: Base64-encoded Android keystore file
- **Used in**: Release workflow only
- **How to create**:
  ```bash
  # Generate keystore
  keytool -genkey -v -keystore release.keystore -alias viber -keyalg RSA -keysize 2048 -validity 10000
  
  # Encode to base64
  base64 -i release.keystore | pbcopy  # macOS
  base64 -w 0 release.keystore         # Linux
  ```
- **Note**: Keep this secret secure! Never commit the keystore to the repository.

#### `KEYSTORE_PASSWORD` (Optional)
- **Description**: Password for the keystore file
- **Used in**: Release workflow only
- **Example**: The password you set when generating the keystore

#### `KEY_ALIAS` (Optional)
- **Description**: Alias name for the signing key
- **Used in**: Release workflow only
- **Example**: `viber` (or whatever you specified when creating the keystore)

#### `KEY_PASSWORD` (Optional)
- **Description**: Password for the specific key in the keystore
- **Used in**: Release workflow only
- **Example**: Often the same as KEYSTORE_PASSWORD

### Built-in Secrets

#### `GITHUB_TOKEN`
- **Description**: Automatically provided by GitHub Actions
- **Used in**: Release workflow to upload artifacts to releases
- **No setup required**: This is automatically available in all workflows

## Setting Up Secrets

### Step 1: Navigate to Secrets Settings
1. Go to your repository on GitHub
2. Click `Settings` tab
3. Click `Secrets and variables` → `Actions` in the left sidebar
4. Click `New repository secret` button

### Step 2: Add Required Secrets

Add each secret one by one:

1. **OAUTH_CLIENT_ID**
   - Name: `OAUTH_CLIENT_ID`
   - Value: Your GitHub OAuth app client ID
   - Click `Add secret`

2. **OAUTH_CLIENT_SECRET**
   - Name: `OAUTH_CLIENT_SECRET`
   - Value: Your GitHub OAuth app client secret
   - Click `Add secret`

3. **KEYSTORE_BASE64** (for releases)
   - Name: `KEYSTORE_BASE64`
   - Value: Base64-encoded keystore file content
   - Click `Add secret`

3. **KEYSTORE_PASSWORD** (for releases)
   - Name: `KEYSTORE_PASSWORD`
   - Value: Your keystore password
   - Click `Add secret`

4. **KEY_ALIAS** (for releases)
   - Name: `KEY_ALIAS`
   - Value: Your key alias
   - Click `Add secret`

5. **KEY_PASSWORD** (for releases)
   - Name: `KEY_PASSWORD`
   - Value: Your key password
   - Click `Add secret`

## Usage

### Running CI Builds

CI builds run automatically on:
- Every pull request to `main`
- Every push to `main`

You can monitor the build status in the "Actions" tab of your repository.

### Creating a Release

To trigger a release build:

1. **Create a new release on GitHub:**
   ```bash
   # Using GitHub CLI
   gh release create v1.0.0 --title "Version 1.0.0" --notes "Release notes here"
   
   # Or via GitHub web interface:
   # Go to Releases → Draft a new release
   ```

2. **The workflow will automatically:**
   - Build signed release APK (if keystore is configured)
   - Build signed release AAB (if keystore is configured)
   - Attach both files to the release
   - Make them available for download

3. **Download the artifacts:**
   - Go to the release page
   - Download the APK or AAB file
   - The files will be named: `Viber-v1.0.0-app-release.apk` and `Viber-v1.0.0-app-release.aab`

### Manual Workflow Triggers

You can also manually trigger workflows from the Actions tab:

1. Go to `Actions` tab
2. Select the workflow you want to run
3. Click `Run workflow` button
4. Select the branch and click `Run workflow`

## Troubleshooting

### Build Fails with "OAUTH_CLIENT_ID not set"

**Solution**: Add the `OAUTH_CLIENT_ID` secret to your repository settings.

### Release Builds are Unsigned

**Symptoms**: APK installs as unsigned, shows "Install blocked" on some devices.

**Solution**: Add all four signing secrets:
- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

### Keystore Decoding Fails

**Symptoms**: Build fails during "Decode keystore" step.

**Solution**: Ensure your `KEYSTORE_BASE64` secret is properly encoded:
```bash
# Re-encode your keystore
base64 -w 0 release.keystore > keystore.txt
# Copy the contents of keystore.txt to the secret
```

### Gradle Build Fails

**Symptoms**: Build fails during `assembleDebug` or `assembleRelease`.

**Common causes:**
1. **Gradle version mismatch**: Ensure Gradle 8.5 is specified in `gradle-wrapper.properties`
2. **JDK version**: Workflow uses JDK 17, ensure your build.gradle.kts is compatible
3. **Missing dependencies**: Check that all dependencies in build.gradle.kts are available

### Tests Fail

**Note**: Tests are set to `continue-on-error: true`, so they won't fail the build. Check the test results in the workflow logs.

## Workflow Features

### Caching
- Gradle dependencies are cached to speed up builds
- Cache key is based on Gradle wrapper and build files

### Artifacts
- Debug builds retain artifacts for 7 days
- Release builds retain artifacts for 30 days
- All artifacts are downloadable from the Actions tab

### Error Handling
- Lint and tests continue even if they fail
- Build must succeed for artifacts to be uploaded
- Release workflow handles both signed and unsigned builds gracefully

## Security Best Practices

1. **Never commit secrets**: Use GitHub Secrets for all sensitive data
2. **Rotate keys regularly**: Update your keystore and secrets periodically
3. **Limit secret access**: Only repository administrators should have access to secrets
4. **Use different keystores**: Use separate keystores for debug and release builds
5. **Monitor workflow runs**: Regularly check the Actions tab for any suspicious activity

## Advanced Configuration

### Changing Build Variants

To build different variants, modify the workflows:

```yaml
# Build all variants
- name: Build all variants
  run: ./gradlew assembleDebug assembleRelease
```

### Adding Code Quality Checks

Add additional checks to the CI workflow:

```yaml
- name: Run Detekt
  run: ./gradlew detekt

- name: Run ktlint
  run: ./gradlew ktlintCheck
```

### Customizing Artifact Names

Modify the rename step in `release.yml`:

```yaml
- name: Rename artifacts
  run: |
    cd app/build/outputs/apk/release
    for file in *.apk; do
      mv "$file" "MyApp-${{ github.event.release.tag_name }}.apk"
    done
```

## Continuous Deployment

To automatically deploy to Google Play Store, add this step to the release workflow:

```yaml
- name: Upload to Play Store
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.SERVICE_ACCOUNT_JSON }}
    packageName: com.vibecoding.viber
    releaseFiles: app/build/outputs/bundle/release/*.aab
    track: production
```

**Required**: Add `SERVICE_ACCOUNT_JSON` secret with Google Play Console service account credentials.

## Support

For issues with the CI/CD workflows:
1. Check the workflow logs in the Actions tab
2. Verify all required secrets are set correctly
3. Ensure your local build works: `./gradlew assembleDebug`
4. Check GitHub Actions status page for service outages

---

**CI/CD Status**: ✅ Configured and ready to use!
