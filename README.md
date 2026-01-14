# Viber 🐱

A modern GitHub client for Android, optimized for vibe coding and GitHub Copilot integration.

## Features

✨ **Vibe Mode** - Enhanced UI theme for the ultimate coding experience  
🐱 **Cat Mode** - Cats and confetti celebrations on every success!  
🔐 **GitHub OAuth** - Secure authentication with GitHub  
📦 **Repository Browser** - View and manage your repositories  
🐛 **Issue Tracking** - Browse and manage issues  
🔀 **Pull Requests** - Review and manage pull requests  
🤖 **Copilot Integration** - Interface for GitHub Copilot workflows  
👤 **Profile Management** - View your GitHub profile and settings  

## Setup

### Prerequisites

- Android Studio Arctic Fox or newer
- Android SDK 24 or higher
- GitHub OAuth App credentials

### Configuration

1. Create a GitHub OAuth App:
   - Go to GitHub Settings → Developer settings → OAuth Apps → New OAuth App
   - Set Authorization callback URL to: `viber://oauth/callback`
   - Note your Client ID and Client Secret

2. Add your GitHub credentials to `gradle.properties`:
   ```properties
   GITHUB_CLIENT_ID=your_client_id_here
   ```

3. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

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

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! This app is designed to make GitHub workflow more fun and productive.

---

Made with 💜 by vibecoding-inc  
GIB GAS! 🚀
