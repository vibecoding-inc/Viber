# Viber 🐱

A modern GitHub client for Android, optimized for vibe coding and GitHub Copilot integration.

## Features

✨ **Vibe Mode** - Enhanced UI theme for the ultimate coding experience  
🐱 **Cat Mode** - Cats and confetti celebrations on every success!  
🔐 **GitHub Device Flow Auth** - Secure authentication without exposing secrets  
📦 **Repository Browser** - View and manage your repositories  
🐛 **Issue Tracking** - Browse and manage issues  
🔀 **Pull Requests** - Review and manage pull requests  
🤖 **Copilot Integration** - Interface for GitHub Copilot workflows  
👤 **Profile Management** - View your GitHub profile and settings  

## Setup

### Prerequisites

- Android Studio Arctic Fox or newer
- Android SDK 24 or higher
- A GitHub account

### GitHub App Configuration

**IMPORTANT**: This app now uses **GitHub Device Flow** for secure authentication. This means:
- ✅ No need to manage client secrets
- ✅ Secure for mobile apps
- ✅ Works with both GitHub OAuth Apps and GitHub Apps
- ✅ Better security for B2B/SaaS deployments

#### Option 1: GitHub OAuth App (Recommended for personal use)

1. Create a GitHub OAuth App:
   - Go to GitHub Settings → Developer settings → OAuth Apps → New OAuth App
   - Application name: `Viber` (or your preferred name)
   - Homepage URL: `https://github.com/vibecoding-inc/Viber`
   - Authorization callback URL: `viber://oauth/callback` (optional, legacy fallback)
   - Note your Client ID
   - **DO NOT** add your Client Secret to the app (it's not needed for Device Flow!)

2. Add your GitHub Client ID to `gradle.properties`:
   ```properties
   OAUTH_CLIENT_ID=your_client_id_here
   ```

#### Option 2: GitHub App (Recommended for B2B/SaaS)

1. Create a GitHub App:
   - Go to GitHub Settings → Developer settings → GitHub Apps → New GitHub App
   - GitHub App name: `Viber` (or your preferred name)
   - Homepage URL: `https://github.com/vibecoding-inc/Viber`
   - **Check**: "Device Flow" in "Identifying and authorizing users"
   - Set required permissions:
     - Repository permissions: Read & Write for repos you want to access
     - Organization permissions: Read for organization access
     - User permissions: Read for user profile
   - Note your Client ID

2. Add your GitHub App Client ID to `gradle.properties`:
   ```properties
   OAUTH_CLIENT_ID=your_client_id_here
   ```

### Build and Run

```bash
./gradlew assembleDebug
```

Or open in Android Studio and click Run.

## Authentication Flow

The app uses **GitHub Device Flow** authentication:

1. User clicks "Sign in with GitHub"
2. App requests a device code from GitHub
3. User is shown a verification code
4. Browser opens to GitHub's device activation page
5. User enters the code on GitHub and authorizes
6. App polls GitHub for the access token
7. Once authorized, user is signed in!

**Why Device Flow?**
- No client secret needed (secure for mobile)
- Works offline during authorization
- Better user experience
- Industry standard for CLI and mobile apps

## Architecture

- **MVVM Architecture** - Clean separation of concerns
- **Jetpack Compose** - Modern declarative UI
- **Hilt** - Dependency injection
- **Retrofit** - Network calls
- **DataStore** - Local data persistence
- **Coroutines & Flow** - Asynchronous programming

## Cat Mode 🐱

Enable Cat Mode in your profile settings to get:
- Animated cats floating around the screen
- Colorful confetti on successful actions
- Special cat-themed color scheme
- Celebrations when you:
  - Successfully authenticate
  - Load repositories
  - View issues
  - Interact with pull requests
  - Use Copilot features

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Hilt (Dependency Injection)
- Retrofit (Networking)
- Coil (Image Loading)
- DataStore (Preferences)
- GitHub REST API v3

## CI/CD

This project includes automated CI/CD workflows using GitHub Actions:
- **Automated builds** on every PR
- **Automated releases** with signed APK/AAB attachments
- See [CI_CD.md](CI_CD.md) for setup instructions and required secrets

## Documentation

- [SETUP.md](SETUP.md) - Detailed setup and build instructions
- [CAT_MODE.md](CAT_MODE.md) - Cat Mode feature documentation
- [ARCHITECTURE.md](ARCHITECTURE.md) - Technical architecture guide
- [CI_CD.md](CI_CD.md) - CI/CD configuration and secrets setup

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! This app is designed to make GitHub workflow more fun and productive.

---

Made with 💜 by vibecoding-inc  
GIB GAS! 🚀
