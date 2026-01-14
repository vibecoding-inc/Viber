class PromptNotificationService {
  constructor() {
    this.listeners = [];
  }

  /**
   * Register a notification listener
   * @param {Function} callback - Function to call when prompt completes
   */
  addListener(callback) {
    if (typeof callback !== 'function') {
      throw new Error('Listener must be a function');
    }
    this.listeners.push(callback);
  }

  /**
   * Remove a notification listener
   * @param {Function} callback - Function to remove
   */
  removeListener(callback) {
    const index = this.listeners.indexOf(callback);
    if (index > -1) {
      this.listeners.splice(index, 1);
    }
  }

  /**
   * Send notification immediately when a prompt completes
   * @param {Object} promptData - Data about the completed prompt
   */
  notifyPromptComplete(promptData) {
    const notification = {
      timestamp: new Date().toISOString(),
      promptId: promptData.id,
      promptText: promptData.text,
      response: promptData.response,
      status: 'completed'
    };

    // Send notifications to all listeners immediately
    this.listeners.forEach(listener => {
      try {
        listener(notification);
      } catch (error) {
        console.error('Error in notification listener:', error);
      }
    });

    return notification;
  }

  /**
   * Clear all listeners
   */
  clearListeners() {
    this.listeners = [];
  }
}

module.exports = PromptNotificationService;
