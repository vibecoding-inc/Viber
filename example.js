const { PromptNotificationService } = require('./src');

// Create a notification service instance
const notificationService = new PromptNotificationService();

// Add a listener for prompt completion notifications
notificationService.addListener((notification) => {
  console.log('🔔 Prompt completed!');
  console.log('Timestamp:', notification.timestamp);
  console.log('Prompt ID:', notification.promptId);
  console.log('Prompt:', notification.promptText);
  console.log('Response:', notification.response);
  console.log('Status:', notification.status);
  console.log('---');
});

// Simulate prompt completions
console.log('Starting prompt processing...\n');

// Example 1: Simple prompt
setTimeout(() => {
  notificationService.notifyPromptComplete({
    id: 'prompt-001',
    text: 'What is the capital of France?',
    response: 'The capital of France is Paris.'
  });
}, 1000);

// Example 2: Code generation prompt
setTimeout(() => {
  notificationService.notifyPromptComplete({
    id: 'prompt-002',
    text: 'Write a function to reverse a string',
    response: 'function reverseString(str) { return str.split("").reverse().join(""); }'
  });
}, 2000);

// Example 3: Complex prompt
setTimeout(() => {
  notificationService.notifyPromptComplete({
    id: 'prompt-003',
    text: 'Explain quantum computing',
    response: 'Quantum computing uses quantum-mechanical phenomena to perform computation...'
  });
}, 3000);

console.log('Example running... notifications will appear as prompts complete.');
