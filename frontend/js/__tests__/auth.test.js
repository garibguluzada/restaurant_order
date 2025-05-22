// Mock localStorage
const localStorageMock = (() => {
  let store = {};
  return {
    getItem: jest.fn(key => store[key]),
    setItem: jest.fn((key, value) => {
      store[key] = value;
    }),
    clear: jest.fn(() => {
      store = {};
    }),
    removeItem: jest.fn(key => {
      delete store[key];
    })
  };
})();

Object.defineProperty(window, 'localStorage', { value: localStorageMock });

// Import the functions we want to test
require('../auth.js');

describe('Authentication Utilities', () => {
  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();
    // Clear all mocks
    jest.clearAllMocks();
  });

  describe('checkAuth', () => {
    test('should return false when no JWT is present', () => {
      expect(window.checkAuth()).toBe(false);
      expect(localStorage.getItem).toHaveBeenCalledWith('jwt');
    });

    test('should return true when JWT is present', () => {
      localStorage.setItem('jwt', 'dummy.jwt.token');
      expect(window.checkAuth()).toBe(true);
      expect(localStorage.getItem).toHaveBeenCalledWith('jwt');
    });
  });

  describe('getUserId', () => {
    test('should return null when no JWT is present', () => {
      expect(window.getUserId()).toBe(null);
      expect(localStorage.getItem).toHaveBeenCalledWith('jwt');
    });

    test('should return null for invalid JWT format', () => {
      localStorage.setItem('jwt', 'invalid-jwt');
      expect(window.getUserId()).toBe(null);
    });

    test('should return user ID from valid JWT', () => {
      // Create a valid JWT payload with id: 123
      const payload = btoa(JSON.stringify({ id: 123 }));
      const jwt = `header.${payload}.signature`;
      localStorage.setItem('jwt', jwt);
      
      expect(window.getUserId()).toBe(123);
      expect(localStorage.getItem).toHaveBeenCalledWith('jwt');
    });

    test('should handle JWT parsing error gracefully', () => {
      // Create an invalid base64 payload
      const jwt = 'header.invalid-base64.signature';
      localStorage.setItem('jwt', jwt);
      
      // Mock console.error to prevent test output pollution
      const originalConsoleError = console.error;
      console.error = jest.fn();
      
      expect(window.getUserId()).toBe(null);
      expect(console.error).toHaveBeenCalled();
      
      // Restore console.error
      console.error = originalConsoleError;
    });
  });
}); 