# Setup Guide for Viber Android App

## Prerequisites

Before you begin, ensure you have:

- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **JDK**: Java 17 or higher
- **Android SDK**: API Level 24 (Android 7.0) or higher
- **GitHub Account**: For OAuth authentication

## Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/vibecoding-inc/Viber.git
cd Viber
```

### 2. Create a GitHub OAuth App

To enable authentication, you need to register an OAuth application with GitHub:

1. Go to https://github.com/settings/developers
2. Click **"New OAuth App"**
3. Fill in the application details:
   - **Application name**: `Viber` (or your preferred name)
   - **Homepage URL**: `https://github.com/vibecoding-inc/Viber`
   - **Authorization callback URL**: `viber://oauth/callback`
   - **Application description**: Optional
4. Click **"Register application"**
5. Note your **Client ID** and generate a **Client Secret**

⚠️ **Security Note**: Keep your Client Secret secure. Never commit it to version control.

### 3. Configure the App

Add your GitHub OAuth credentials to the project:

1. Open `gradle.properties` in the project root
2. Add your GitHub Client ID:
   ```properties
   GITHUB_CLIENT_ID=your_actual_client_id_here
   ```

> **Note**: For production apps, the client secret should be handled server-side. This demo shows the OAuth flow structure.

### 4. Build the Project

#### Using Android Studio:

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Build → Make Project** (or Ctrl+F9)

#### Using Command Line:

```bash
# On Unix/macOS:
./gradlew assembleDebug

# On Windows:
gradlew.bat assembleDebug
```

### 5. Run the App

#### On an Emulator:

1. Create an AVD (Android Virtual Device) in Android Studio
   - Recommended: Pixel 5, API Level 33
2. Click **Run → Run 'app'** (or Shift+F10)

#### On a Physical Device:

1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect your device via USB
4. Select your device from the device dropdown
5. Click **Run**

## Project Structure

```
Viber/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/vibecoding/viber/
│   │       │   ├── data/              # Data layer
│   │       │   │   ├── local/         # DataStore preferences
│   │       │   │   ├── model/         # Data models
│   │       │   │   ├── remote/        # API services
│   │       │   │   └── repository/    # Repositories
│   │       │   ├── di/                # Dependency injection
│   │       │   ├── ui/                # UI layer
│   │       │   │   ├── auth/          # Authentication screens
│   │       │   │   ├── components/    # Reusable components
│   │       │   │   ├── copilot/       # Copilot integration
│   │       │   │   ├── home/          # Main navigation
│   │       │   │   ├── issues/        # Issues screens
│   │       │   │   ├── profile/       # Profile & settings
│   │       │   │   ├── pullrequests/  # PR screens
│   │       │   │   ├── repositories/  # Repository browser
│   │       │   │   └── theme/         # App theming
│   │       │   └── ViberApplication.kt
│   │       ├── res/                   # Resources
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── CAT_MODE.md
```

## Key Technologies

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Async**: Coroutines + Flow
- **Local Storage**: DataStore
- **Image Loading**: Coil
- **Material Design**: Material 3

## Features Overview

### 🔐 Authentication
- GitHub OAuth 2.0 flow
- Secure token storage using DataStore
- Custom Chrome Tabs for OAuth

### 📦 Repository Browser
- List user repositories
- Search functionality
- Sort by stars, forks, updates
- View repository details

### 🐛 Issues
- View assigned issues
- Filter by state (open/closed)
- Label display
- Quick navigation to GitHub

### 🔀 Pull Requests
- Browse pull requests
- View PR status
- Merge indicators
- Draft PR support

### 🤖 Copilot Integration
- Interface optimized for GitHub Copilot
- Vibe coding enhancements
- Quick access to AI features

### 👤 Profile
- View GitHub profile
- Repository/follower counts
- Vibe Mode toggle ✨
- **Cat Mode toggle 🐱**
- Sign out

## Special Features

### Vibe Mode ✨
A special theme optimized for coding:
- Purple/pink gradient colors
- Enhanced visual feedback
- Optimized for focus

### Cat Mode 🐱
The fun mode with:
- Animated cats on success
- Colorful confetti celebrations
- Warm orange/brown theme
- Triggers on:
  - Successful login
  - Repository loads
  - Issue views
  - PR interactions
  - Copilot usage

See [CAT_MODE.md](CAT_MODE.md) for detailed documentation.

## Testing

### Manual Testing Checklist

- [ ] Launch app
- [ ] Sign in with GitHub
- [ ] View repositories list
- [ ] Click on a repository
- [ ] Navigate to Issues tab
- [ ] Navigate to Pull Requests tab
- [ ] Navigate to Copilot tab
- [ ] Navigate to Profile tab
- [ ] Toggle Vibe Mode
- [ ] Toggle Cat Mode
- [ ] Verify confetti appears
- [ ] Sign out

### Testing Cat Mode

1. Sign in to the app
2. Go to Profile tab
3. Enable Cat Mode toggle
4. Navigate to Repositories
5. Click on any repository
6. **Expected**: Cats and confetti should appear! 🐱🎊

## Troubleshooting

### Build Issues

**Problem**: Gradle sync fails
- **Solution**: Check internet connection and Gradle version compatibility

**Problem**: Missing dependencies
- **Solution**: Run `./gradlew clean build`

**Problem**: JDK version error
- **Solution**: Ensure JDK 17 is installed and configured in Android Studio

### Runtime Issues

**Problem**: OAuth callback doesn't work
- **Solution**: 
  1. Verify redirect URI in GitHub OAuth app settings
  2. Check AndroidManifest.xml intent-filter
  3. Ensure GITHUB_CLIENT_ID is set correctly

**Problem**: Network errors
- **Solution**: 
  1. Check internet connection
  2. Verify API token is valid
  3. Check GitHub API rate limits

**Problem**: Cat mode not showing
- **Solution**:
  1. Ensure toggle is enabled in Profile
  2. Check that celebrations are triggered
  3. Verify device supports animations

## Development Tips

### Adding New Features

1. Create models in `data/model/`
2. Add API endpoints in `data/remote/`
3. Create repository in `data/repository/`
4. Build ViewModel in `ui/yourfeature/`
5. Design Compose UI in `ui/yourfeature/`
6. Add navigation in `HomeScreen.kt`

### Debugging

- Enable verbose logging in `NetworkModule.kt`
- Use Android Studio's Logcat
- Check network calls in OkHttp interceptor
- Use Compose Preview for UI debugging

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Use Compose best practices

## Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [GitHub REST API v3](https://docs.github.com/en/rest)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

## License

This project is licensed under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

**Ready to vibe code? GIB GAS! 🚀**

For Cat Mode documentation, see [CAT_MODE.md](CAT_MODE.md)
