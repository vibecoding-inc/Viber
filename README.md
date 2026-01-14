# Viber

A lightweight notification service for prompt completion events. Viber sends notifications immediately as soon as a prompt finishes processing in your application.

## Features

- 🚀 **Immediate Notifications**: Notifications are sent as soon as a prompt completes
- 🔔 **Event-Driven**: Simple listener-based architecture
- 🛡️ **Error Handling**: Gracefully handles errors in notification listeners
- 🧪 **Well Tested**: Comprehensive test coverage
- 📦 **Zero Dependencies**: No external dependencies required

## Installation

```bash
npm install
```

## Usage

### Basic Example

```javascript
const { PromptNotificationService } = require('./src');

// Create a notification service
const notificationService = new PromptNotificationService();

// Add a listener for notifications
notificationService.addListener((notification) => {
  console.log('Prompt completed!', notification);
});

// When a prompt completes, send notification immediately
notificationService.notifyPromptComplete({
  id: 'prompt-123',
  text: 'What is the capital of France?',
  response: 'The capital of France is Paris.'
});
```

### Running the Example

```bash
node example.js
```

## API Reference

### `PromptNotificationService`

#### `addListener(callback)`
Register a function to be called when a prompt completes.
- **callback**: Function that receives notification data

#### `removeListener(callback)`
Remove a previously registered listener.
- **callback**: The function to remove

#### `notifyPromptComplete(promptData)`
Send notification immediately when a prompt completes.
- **promptData**: Object containing:
  - `id`: Unique identifier for the prompt
  - `text`: The prompt text
  - `response`: The response generated

Returns a notification object with:
- `timestamp`: ISO timestamp of completion
- `promptId`: The prompt identifier
- `promptText`: The prompt text
- `response`: The response
- `status`: Always 'completed'

#### `clearListeners()`
Remove all registered listeners.

## Testing

Run the test suite:

```bash
npm test
```

## License

MIT