// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});

// Mock window.scrollTo
window.scrollTo = jest.fn();

// Mock window.alert
window.alert = jest.fn();

// Mock window.confirm
window.confirm = jest.fn();

// Mock window.location
delete window.location;
window.location = {
  href: '',
  assign: jest.fn(),
  replace: jest.fn(),
  reload: jest.fn(),
};

// Suppress console errors during tests
const originalConsoleError = console.error;
console.error = (...args) => {
  if (typeof args[0] === 'string' && args[0].includes('Error parsing JWT')) {
    return;
  }
  originalConsoleError(...args);
}; 