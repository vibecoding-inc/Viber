# Cat Mode & Celebrations Documentation 🐱🎊

## Overview

Cat Mode is a delightful feature that adds animated cats and confetti celebrations to the Viber app. When enabled, successful actions trigger a visual celebration with:

- **Animated floating cats** 🐱
- **Colorful confetti particles** 🎊
- **Special cat-themed color scheme** 🎨

## How to Enable Cat Mode

1. Navigate to the **Profile** tab in the bottom navigation
2. Toggle the **Cat Mode** switch
3. Enjoy cats and confetti on every success! 🎉

## When Do Celebrations Trigger?

Celebrations are automatically triggered when:

### Authentication Events
- ✅ Successfully signing in with GitHub
- ✅ OAuth flow completion

### Repository Actions
- ✅ Successfully loading repositories
- ✅ Clicking on a repository card
- ✅ Starring a repository
- ✅ Forking a repository

### Issue Management
- ✅ Loading assigned issues
- ✅ Clicking on an issue
- ✅ Creating a new issue
- ✅ Closing an issue

### Pull Request Actions
- ✅ Viewing pull requests
- ✅ Merging a PR
- ✅ Approving a PR review

### Copilot Integration
- ✅ Accessing Copilot features
- ✅ Accepting Copilot suggestions
- ✅ Generating code with Copilot

## Technical Implementation

### Components

#### ConfettiAnimation
Location: `ui/components/CatModeComponents.kt`

The confetti animation creates 50 colorful particles that:
- Start from the top of the screen
- Fall with realistic physics
- Rotate as they fall
- Use vibrant colors (orange, yellow, blue, pink, purple, green)
- Last for 3 seconds

```kotlin
@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onAnimationEnd: () -> Unit = {}
)
```

#### CatEmoji
Location: `ui/components/CatModeComponents.kt`

Renders an animated cat face that:
- Scales up and down (breathing animation)
- Rotates slightly left and right
- Uses orange/beige colors for the cat
- Has black eyes and pink nose
- Shows pointy ears

```kotlin
@Composable
fun CatEmoji(
    modifier: Modifier = Modifier,
    isAnimating: Boolean = false
)
```

#### CatModeOverlay
Location: `ui/components/CatModeComponents.kt`

Creates multiple floating cats across the screen:
- Spawns 5 cats at random positions
- Each cat animates independently
- Cats appear during celebrations

```kotlin
@Composable
fun CatModeOverlay(
    modifier: Modifier = Modifier,
    isActive: Boolean = false
)
```

### Color Themes

Cat Mode includes a special color scheme:

```kotlin
private val CatModeColorScheme = darkColorScheme(
    primary = CatOrange,      // #FF6B35
    secondary = CatBeige,     // #F7C59F
    tertiary = VibePink,      // #F72585
    background = Color(0xFF2C1810),  // Warm dark brown
    surface = Color(0xFF3D2415),     // Lighter brown
)
```

### State Management

Cat mode state is managed through:

1. **PreferencesManager** - Stores cat mode preference
   ```kotlin
   suspend fun setCatMode(enabled: Boolean)
   val catModeEnabled: Flow<Boolean>
   ```

2. **HomeViewModel** - Triggers celebrations
   ```kotlin
   fun triggerCelebration()
   ```

3. **ProfileViewModel** - Toggles cat mode
   ```kotlin
   fun toggleCatMode()
   ```

## User Experience

### Visual Feedback
- Immediate visual confirmation when cat mode is enabled
- Persistent indicator in the top app bar (🐱 icon)
- Smooth animations that don't interfere with app usage

### Performance
- Animations use Compose's efficient rendering
- Celebrations are time-limited (3 seconds)
- No impact on API calls or data loading

### Accessibility
- Cat mode is optional and can be disabled
- Animations don't block user interaction
- Color scheme maintains good contrast ratios

## Vibe Mode vs Cat Mode

Both modes can be enabled simultaneously:

| Feature | Vibe Mode | Cat Mode |
|---------|-----------|----------|
| Color Theme | Purple/Pink gradient | Orange/Brown warm tones |
| Celebrations | ✨ Sparkles | 🐱 Cats + 🎊 Confetti |
| Icon | ⚡ Lightning | 🐾 Paw |
| Best For | Productivity | Fun & Joy |

## Future Enhancements

Potential improvements for cat mode:
- [ ] Different cat breeds/styles
- [ ] Sound effects (optional meows)
- [ ] Achievement badges with cats
- [ ] Customizable celebration duration
- [ ] More celebration triggers
- [ ] Cat animations on scroll
- [ ] Interactive cats (tap to pet)

## Code Examples

### Triggering a Celebration

```kotlin
// In any screen
@Composable
fun MyScreen(
    onSuccess: () -> Unit = {}
) {
    Button(onClick = {
        // Do something successful
        onSuccess() // This triggers the celebration!
    }) {
        Text("Do Something Awesome")
    }
}
```

### Using Celebrations in ViewModels

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    
    fun doSomethingAwesome() {
        viewModelScope.launch {
            val result = repository.doAction()
            if (result is Success) {
                // Celebration will be triggered by the UI
                _successEvent.emit(true)
            }
        }
    }
}
```

## Troubleshooting

### Celebrations Not Showing
1. Check if Cat Mode is enabled in Profile settings
2. Verify the `showCelebration` state in HomeViewModel
3. Check logs for any animation errors

### Performance Issues
1. Reduce celebration duration
2. Decrease number of confetti particles
3. Disable cat mode on lower-end devices

### Cats Not Animating
1. Ensure Compose animations are enabled
2. Check if device has sufficient GPU capabilities
3. Verify animation files are not corrupted

## Contributing

To add new celebration triggers:

1. Add the success callback to your screen
2. Call the callback when the action succeeds
3. The HomeScreen will handle the rest!

Example:
```kotlin
@Composable
fun NewFeatureScreen(
    onSuccess: () -> Unit = {}
) {
    // Your UI
    
    LaunchedEffect(successState) {
        if (successState) {
            onSuccess() // 🎉 Celebration!
        }
    }
}
```

---

**Remember**: The goal is to make GitHub interactions more joyful and rewarding! 🐱✨

GIB GAS! 🚀
