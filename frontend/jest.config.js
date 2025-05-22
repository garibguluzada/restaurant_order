module.exports = {
  testEnvironment: 'jsdom',
  setupFiles: ['./js/__tests__/setup.js'],
  moduleFileExtensions: ['js', 'json'],
  testMatch: ['**/__tests__/**/*.test.js'],
  transform: {},
  verbose: true,
  testTimeout: 10000,
  collectCoverage: true,
  coverageDirectory: 'coverage',
  coverageReporters: ['text', 'lcov'],
  collectCoverageFrom: [
    'js/**/*.js',
    '!js/__tests__/**',
    '!js/__mocks__/**'
  ]
}; 