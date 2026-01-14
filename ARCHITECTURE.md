# Architecture Documentation

## Overview

Viber follows Clean Architecture principles with MVVM (Model-View-ViewModel) pattern, providing clear separation of concerns and maintainability.

## Architecture Layers

### 1. Presentation Layer (UI)

**Location**: `ui/`

Implements the MVVM pattern using Jetpack Compose.

#### Components:
- **Views (Composables)**: UI components built with Jetpack Compose
- **ViewModels**: Business logic and state management
- **State**: Immutable UI states using StateFlow
- **Theme**: Material 3 theming with custom color schemes

#### Key Features:
- Declarative UI with Compose
- Reactive state management with Flow
- Navigation using Compose Navigation
- Material 3 design system

### 2. Domain Layer

**Location**: `data/repository/`

Contains business logic and use cases.

#### Components:
- **Repositories**: Abstract data operations
- **Result**: Sealed class for operation results

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### 3. Data Layer

**Location**: `data/`

Manages data from various sources.

#### Components:

##### Remote Data Source
- **GitHubApiService**: REST API endpoints
- **GitHubAuthService**: OAuth endpoints
- **Retrofit**: HTTP client
- **OkHttp**: Network interceptors

##### Local Data Source
- **PreferencesManager**: DataStore for preferences
- **Token storage**: Secure access token management

##### Models
- **User**: GitHub user data
- **Repository**: Repository information
- **Issue**: Issue details
- **PullRequest**: PR information
- **Label**: Issue/PR labels

### 4. Dependency Injection

**Location**: `di/`

Uses Hilt for dependency injection.

#### Modules:
- **NetworkModule**: Provides network components
  - Retrofit instances (API & Auth)
  - OkHttp client with interceptors
  - API services

## Data Flow

```
User Action
    ↓
Composable (UI)
    ↓
ViewModel
    ↓
Repository
    ↓
API Service / Local Storage
    ↓
Network / DataStore
    ↓
Result
    ↓
ViewModel (updates StateFlow)
    ↓
Composable (recomposes)
```

## Key Design Patterns

### 1. Repository Pattern
Abstracts data sources from the business logic.

```kotlin
@Singleton
class GitHubRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val preferencesManager: PreferencesManager
) {
    suspend fun getUserRepositories(): Result<List<Repository>>
}
```

### 2. Dependency Injection
Hilt provides dependencies throughout the app.

```kotlin
@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel()
```

### 3. State Management
StateFlow for reactive UI updates.

```kotlin
private val _repositories = MutableStateFlow<List<Repository>>(emptyList())
val repositories: StateFlow<List<Repository>> = _repositories.asStateFlow()
```

### 4. Observer Pattern
Flow/StateFlow for reactive programming.

```kotlin
LaunchedEffect(Unit) {
    viewModel.repositories.collect { repos ->
        // UI updates automatically
    }
}
```

## Authentication Flow

```
1. User clicks "Sign in with GitHub"
   ↓
2. AuthViewModel.startAuth()
   ↓
3. Opens Chrome Custom Tab with GitHub OAuth URL
   ↓
4. User authorizes on GitHub
   ↓
5. Redirect to viber://oauth/callback?code=...
   ↓
6. OAuthCallbackActivity receives code
   ↓
7. AuthViewModel.handleAuthCallback(code)
   ↓
8. AuthRepository.handleAuthCode(code)
   ↓
9. Exchange code for access token
   ↓
10. Store token in DataStore
    ↓
11. Navigate to HomeScreen
```

## Network Architecture

### Interceptors

1. **Auth Interceptor**
   - Adds Bearer token to requests
   - Adds GitHub API headers

2. **Logging Interceptor**
   - Logs requests and responses
   - Useful for debugging

### Error Handling

```kotlin
try {
    val response = apiService.getUserRepositories()
    if (response.isSuccessful && response.body() != null) {
        Result.Success(response.body()!!)
    } else {
        Result.Error("Failed: ${response.message()}")
    }
} catch (e: Exception) {
    Result.Error("Network error: ${e.localizedMessage}")
}
```

## State Management Strategy

### ViewModel State

Each ViewModel manages:
- **Data State**: The actual data (repositories, issues, etc.)
- **Loading State**: Boolean for loading indicators
- **Error State**: Error messages

```kotlin
private val _repositories = MutableStateFlow<List<Repository>>(emptyList())
private val _isLoading = MutableStateFlow(false)
private val _error = MutableStateFlow<String?>(null)
```

### UI State

Composables observe state and react:

```kotlin
val repositories by viewModel.repositories.collectAsState()
val isLoading by viewModel.isLoading.collectAsState()

when {
    isLoading -> LoadingIndicator()
    repositories.isEmpty() -> EmptyState()
    else -> RepositoryList(repositories)
}
```

## Navigation Architecture

### Navigation Graph

```
AuthScreen (if not authenticated)
    ↓
HomeScreen (navigation host)
    ├── RepositoriesScreen
    ├── IssuesScreen
    ├── PullRequestsScreen
    ├── CopilotScreen
    └── ProfileScreen
```

### Navigation Implementation

```kotlin
NavHost(navController, startDestination = "repositories") {
    composable("repositories") { RepositoriesScreen() }
    composable("issues") { IssuesScreen() }
    composable("pullrequests") { PullRequestsScreen() }
    composable("copilot") { CopilotScreen() }
    composable("profile") { ProfileScreen() }
}
```

## Theme Architecture

### Color Schemes

Three main themes:
1. **Default Dark Theme**: GitHub-inspired colors
2. **Vibe Mode**: Purple/pink gradient
3. **Cat Mode**: Orange/brown warm tones

### Dynamic Theming

```kotlin
@Composable
fun ViberTheme(
    vibeMode: Boolean = false,
    catMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        catMode -> CatModeColorScheme
        vibeMode -> VibeModeColorScheme
        else -> DarkColorScheme
    }
    
    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

## Cat Mode Architecture

### Components

1. **ConfettiAnimation**: Particle system for confetti
2. **CatEmoji**: Animated cat drawing
3. **CatModeOverlay**: Multiple floating cats

### State Management

```
PreferencesManager
    ↓ (Flow)
HomeViewModel.catMode
    ↓ (StateFlow)
HomeScreen (applies theme & overlays)
```

### Celebration Trigger Flow

```
User Action (success)
    ↓
onSuccess() callback
    ↓
HomeViewModel.triggerCelebration()
    ↓
showCelebration state = true
    ↓
ConfettiAnimation & CatModeOverlay appear
    ↓
After 3 seconds
    ↓
showCelebration state = false
```

## Security Considerations

### Token Storage
- Access tokens stored in encrypted DataStore
- Never exposed in logs
- Cleared on sign out

### API Security
- HTTPS only
- Bearer token authentication
- No client secret in app (server-side in production)

### OAuth Flow
- State parameter for CSRF protection
- Custom URI scheme for callback
- Chrome Custom Tabs for secure auth

## Performance Optimizations

### Lazy Loading
- LazyColumn for lists
- Pagination support in API

### Image Loading
- Coil for efficient image loading
- Caching enabled by default

### Coroutines
- Structured concurrency
- viewModelScope for automatic cancellation
- Proper error handling

## Testing Strategy

### Unit Tests
- ViewModel logic
- Repository operations
- Data transformations

### Integration Tests
- API service calls
- Database operations
- Navigation flows

### UI Tests
- Compose UI testing
- User interaction flows
- State changes

## Future Enhancements

### Planned Features
- [ ] Offline support with Room database
- [ ] Push notifications
- [ ] Code editor integration
- [ ] Advanced search
- [ ] Repository statistics
- [ ] Contribution graphs

### Technical Improvements
- [ ] GraphQL support
- [ ] Modularization
- [ ] Compose multiplatform
- [ ] CI/CD pipeline
- [ ] Automated testing

## Dependencies

### Core
- Kotlin 1.9.10
- Compose BOM 2023.10.01
- Hilt 2.48.1

### Networking
- Retrofit 2.9.0
- OkHttp 4.12.0
- Gson converter

### UI
- Material 3
- Coil 2.5.0
- Navigation Compose 2.7.5

### Storage
- DataStore 1.0.0

### Testing
- JUnit 4.13.2
- Espresso 3.5.1
- Compose UI Test

## Best Practices

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **Single Source of Truth**: State flows from ViewModel to UI
3. **Reactive Programming**: Use Flow for async operations
4. **Immutability**: State is immutable and updated via copies
5. **Error Handling**: Comprehensive error handling at all layers
6. **Resource Management**: Proper lifecycle awareness
7. **Code Organization**: Clear package structure

## References

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Jetpack Compose Best Practices](https://developer.android.com/jetpack/compose/architecture)
- [Kotlin Coroutines Best Practices](https://kotlinlang.org/docs/coroutines-guide.html)

---

This architecture ensures the app is:
- ✅ Maintainable
- ✅ Testable
- ✅ Scalable
- ✅ Performant
- 🐱 Fun with Cat Mode!
