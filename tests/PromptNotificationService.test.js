const PromptNotificationService = require('../src/PromptNotificationService');

describe('PromptNotificationService', () => {
  let service;

  beforeEach(() => {
    service = new PromptNotificationService();
  });

  afterEach(() => {
    service.clearListeners();
  });

  describe('addListener', () => {
    test('should add a listener function', () => {
      const listener = jest.fn();
      service.addListener(listener);
      expect(service.listeners).toContain(listener);
    });

    test('should throw error if listener is not a function', () => {
      expect(() => service.addListener('not a function')).toThrow('Listener must be a function');
    });
  });

  describe('removeListener', () => {
    test('should remove a listener function', () => {
      const listener = jest.fn();
      service.addListener(listener);
      service.removeListener(listener);
      expect(service.listeners).not.toContain(listener);
    });

    test('should do nothing if listener does not exist', () => {
      const listener = jest.fn();
      service.removeListener(listener);
      expect(service.listeners.length).toBe(0);
    });
  });

  describe('notifyPromptComplete', () => {
    test('should send notification immediately to all listeners', () => {
      const listener1 = jest.fn();
      const listener2 = jest.fn();
      
      service.addListener(listener1);
      service.addListener(listener2);

      const promptData = {
        id: 'test-123',
        text: 'Test prompt',
        response: 'Test response'
      };

      service.notifyPromptComplete(promptData);

      expect(listener1).toHaveBeenCalledTimes(1);
      expect(listener2).toHaveBeenCalledTimes(1);
    });

    test('should include correct data in notification', () => {
      const listener = jest.fn();
      service.addListener(listener);

      const promptData = {
        id: 'test-456',
        text: 'What is AI?',
        response: 'AI stands for Artificial Intelligence'
      };

      const notification = service.notifyPromptComplete(promptData);

      expect(notification).toMatchObject({
        promptId: 'test-456',
        promptText: 'What is AI?',
        response: 'AI stands for Artificial Intelligence',
        status: 'completed'
      });
      expect(notification.timestamp).toBeDefined();
    });

    test('should handle errors in listeners gracefully', () => {
      const errorListener = jest.fn(() => {
        throw new Error('Listener error');
      });
      const goodListener = jest.fn();

      service.addListener(errorListener);
      service.addListener(goodListener);

      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();

      service.notifyPromptComplete({
        id: 'test-789',
        text: 'Test',
        response: 'Response'
      });

      expect(errorListener).toHaveBeenCalled();
      expect(goodListener).toHaveBeenCalled();
      expect(consoleErrorSpy).toHaveBeenCalled();

      consoleErrorSpy.mockRestore();
    });
  });

  describe('clearListeners', () => {
    test('should remove all listeners', () => {
      service.addListener(jest.fn());
      service.addListener(jest.fn());
      service.addListener(jest.fn());

      expect(service.listeners.length).toBe(3);

      service.clearListeners();

      expect(service.listeners.length).toBe(0);
    });
  });
});
