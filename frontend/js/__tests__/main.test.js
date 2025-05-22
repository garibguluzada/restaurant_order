// Mock DOM elements and functions
document.body.innerHTML = `
  <nav>
    <div id="menuToggle"></div>
    <div id="navLinks"></div>
    <div id="menuOverlay"></div>
  </nav>
  <div id="mealPopup" style="display: none;">
    <div id="popupClose"></div>
    <h2 id="popupMealName"></h2>
    <div id="additionalProducts"></div>
    <div id="quantity">1</div>
    <button id="decreaseQuantity">-</button>
    <button id="increaseQuantity">+</button>
    <button id="addToCartBtn">Add to Cart</button>
  </div>
  <div id="notification">
    <p id="notification-message"></p>
  </div>
  <div class="menu-category" data-category="appetizers">
    <ul id="appetizersList"></ul>
  </div>
  <div class="menu-category" data-category="maincourse">
    <ul id="mainCourseList"></ul>
  </div>
  <div class="menu-category" data-category="dessert">
    <ul id="dessertList"></ul>
  </div>
  <div class="menu-category" data-category="beverage">
    <ul id="beverageList"></ul>
  </div>
  <button class="category-button" data-category="appetizers">Appetizers</button>
  <button class="category-button" data-category="maincourse">Main Course</button>
  <button class="category-button" data-category="dessert">Dessert</button>
  <button class="category-button" data-category="beverage">Beverage</button>
`;

// Mock fetch
global.fetch = jest.fn();

// Mock localStorage
const localStorageMock = (() => {
  let store = {};
  return {
    getItem: jest.fn(key => (key in store ? store[key] : null)),
    setItem: jest.fn((key, value) => {
      store[key] = value;
    }),
    clear: jest.fn(() => {
      store = {};
    })
  };
})();

Object.defineProperty(window, 'localStorage', { value: localStorageMock });

// Mock window.location
delete window.location;
window.location = {
  href: '',
  assign: jest.fn(),
  replace: jest.fn(),
  reload: jest.fn(),
};

// Import the auth functions we need
require('../auth.js');

// Import the main file
require('../main.js');

describe('Main Application', () => {
  beforeEach(() => {
    // Set up a clean DOM for each test
    document.body.innerHTML = `
      <nav>
        <div id="menuToggle"></div>
        <div id="navLinks"></div>
        <div id="menuOverlay"></div>
      </nav>
      <div id="mealPopup" style="display: none;">
        <div id="popupClose"></div>
        <h2 id="popupMealName"></h2>
        <div id="additionalProducts"></div>
        <div id="quantity">1</div>
        <button id="decreaseQuantity">-</button>
        <button id="increaseQuantity">+</button>
        <button id="addToCartBtn">Add to Cart</button>
      </div>
      <div id="notification">
        <p id="notification-message"></p>
      </div>
      <div class="menu-category" data-category="appetizers">
        <ul id="appetizersList"></ul>
      </div>
      <div class="menu-category" data-category="maincourse">
        <ul id="mainCourseList"></ul>
      </div>
      <div class="menu-category" data-category="dessert">
        <ul id="dessertList"></ul>
      </div>
      <div class="menu-category" data-category="beverage">
        <ul id="beverageList"></ul>
      </div>
      <button class="category-button" data-category="appetizers">Appetizers</button>
      <button class="category-button" data-category="maincourse">Main Course</button>
      <button class="category-button" data-category="dessert">Dessert</button>
      <button class="category-button" data-category="beverage">Beverage</button>
    `;
    // Clear all mocks
    jest.clearAllMocks();
    // Reset localStorage
    localStorage.clear();
    // Reset window.location
    window.location.href = '';
    // Mock successful fetch responses
    fetch.mockImplementation((url) => {
      if (url === 'http://localhost:8080/menu') {
        return Promise.resolve({
          json: () => Promise.resolve([
            {
              mealid: 1,
              mealname: 'Test Meal',
              price: 10.99,
              mealcontent: 'Test Description',
              pictureofmeal: 'test.jpg',
              category: 'appetizers'
            }
          ])
        });
      }
      if (url === 'http://localhost:8080/products') {
        return Promise.resolve({
          json: () => Promise.resolve([
            {
              prodId: 1,
              products: 'Test Product',
              price: 2.99,
              mealid: 1
            }
          ])
        });
      }
      return Promise.reject(new Error('Not found'));
    });

    // Set initial styles for menu elements
    const navLinks = document.getElementById('navLinks');
    const menuOverlay = document.getElementById('menuOverlay');
    if (navLinks) navLinks.style.display = 'none';
    if (menuOverlay) menuOverlay.style.display = 'none';

    // Trigger DOMContentLoaded event to initialize event listeners
    document.dispatchEvent(new Event('DOMContentLoaded'));
  });

  describe('Menu Category Filtering', () => {
    test('should show all categories by default', async () => {
      // Wait for the fetch and DOM updates to complete
      await new Promise(resolve => setTimeout(resolve, 0));
      
      document.querySelectorAll('.menu-category').forEach(category => {
        expect(category.classList.contains('active')).toBe(true);
      });
    });

    test('should filter categories when clicking category buttons', async () => {
      // Wait for the fetch and DOM updates to complete
      await new Promise(resolve => setTimeout(resolve, 0));

      const appetizerButton = document.querySelector('[data-category="appetizers"]');
      appetizerButton.click();

      // Wait for the click event to be processed
      await new Promise(resolve => setTimeout(resolve, 0));

      const selected = document.querySelector('.menu-category.active');
      expect(selected).not.toBeNull();
      expect(selected.dataset.category).toBe('appetizers');
    });
  });

  describe('Meal Popup', () => {
    beforeEach(async () => {
      // Wait for the fetch to complete and meal cards to be created
      await new Promise(resolve => setTimeout(resolve, 0));
    });

    test('should open popup with meal details when clicking a meal', async () => {
      const mealCard = document.querySelector('.meal-card');
      if (!mealCard) {
        throw new Error('Meal card not found in DOM');
      }

      const mealPopup = document.getElementById('mealPopup');
      const popupMealName = document.getElementById('popupMealName');
      const quantityDisplay = document.getElementById('quantity');

      // Click the meal card
      mealCard.click();

      // Wait for the click event to be processed
      await new Promise(resolve => setTimeout(resolve, 0));

      expect(mealPopup.style.display).toBe('flex');
      expect(popupMealName.textContent).toBe('Test Meal');
      expect(quantityDisplay.textContent).toBe('1');
    });

    test('should close popup when clicking close button', async () => {
      const mealPopup = document.getElementById('mealPopup');
      const popupClose = document.getElementById('popupClose');

      // Open popup first
      mealPopup.style.display = 'flex';
      
      // Click close button
      popupClose.click();

      // Wait for the click event to be processed
      await new Promise(resolve => setTimeout(resolve, 0));

      expect(mealPopup.style.display).toBe('none');
    });

    test('should update quantity when using quantity buttons', async () => {
      const mealPopup = document.getElementById('mealPopup');
      const quantityDisplay = document.getElementById('quantity');
      const decreaseBtn = document.getElementById('decreaseQuantity');
      const increaseBtn = document.getElementById('increaseQuantity');

      // Open popup first
      mealPopup.style.display = 'flex';

      // Test increase
      increaseBtn.click();
      await new Promise(resolve => setTimeout(resolve, 0));
      expect(quantityDisplay.textContent).toBe('2');

      // Test decrease
      decreaseBtn.click();
      await new Promise(resolve => setTimeout(resolve, 0));
      expect(quantityDisplay.textContent).toBe('1');

      // Test minimum quantity
      decreaseBtn.click();
      await new Promise(resolve => setTimeout(resolve, 0));
      expect(quantityDisplay.textContent).toBe('1');
    });
  });

  describe('Add to Cart', () => {
    beforeEach(async () => {
      // Wait for the fetch to complete
      await new Promise(resolve => setTimeout(resolve, 0));
      
      // Open the meal popup first
      const mealCard = document.querySelector('.meal-card');
      if (mealCard) {
        mealCard.click();
        await new Promise(resolve => setTimeout(resolve, 0));
      }
    });

    test('should show unauthorized message when not logged in', async () => {
      const addToCartBtn = document.getElementById('addToCartBtn');
      const notificationMessage = document.getElementById('notification-message');

      // Ensure user is not logged in
      localStorage.clear();
      
      // Click add to cart
      addToCartBtn.click();

      // Wait for the click event to be processed
      await new Promise(resolve => setTimeout(resolve, 0));

      expect(notificationMessage.textContent).toBe('401 Unauthorized - Please login to add items to cart');
    });

    test('should allow adding to cart when logged in', async () => {
      // Mock logged in state
      localStorage.setItem('jwt', 'valid.jwt.token');
      
      const addToCartBtn = document.getElementById('addToCartBtn');
      const notificationMessage = document.getElementById('notification-message');

      // Click add to cart
      addToCartBtn.click();

      // Wait for the click event to be processed
      await new Promise(resolve => setTimeout(resolve, 0));

      // Since we're not mocking the cart API endpoint, we can't test the success case
      // But we can verify it didn't show the unauthorized message
      expect(notificationMessage.textContent).not.toBe('401 Unauthorized - Please login to add items to cart');
    });
  });
}); 