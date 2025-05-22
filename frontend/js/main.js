const scrollToTopButton = document.getElementById("scrollToTopButton");
const menuOverLay = document.getElementById("menuOverlay");
const navLinks = document.getElementById("navLinks");

// Show the selected menu category and hide other categories

document.addEventListener("DOMContentLoaded", function () {
  // Show all categories by default
  document.querySelectorAll(".menu-category").forEach(category => {
    category.classList.add("active");
  });

  let categoryBtns = document.querySelectorAll(".category-button");
  categoryBtns.forEach(function (button) {
    button.addEventListener("click", function () {
      let category = button.dataset.category;
      console.log(category);
      let categories = document.querySelectorAll(".menu-category");
      console.log(categories);
      categories.forEach(function (category) {
        category.classList.remove("active");
      });
      let selectedCategory = document.querySelector(
        '.menu-category[data-category="' + category + '"]'
      );
      console.log(selectedCategory);
      selectedCategory.classList.add("active");
    });
  });
});

// Scroll target in the navbar

document.addEventListener("DOMContentLoaded", function () {
  //   debugger;
  var hashLinks = document.querySelectorAll('a[href^="#"]');
  console.log(hashLinks);
  hashLinks.forEach(function (link) {
    link.addEventListener("click", function (e) {
      e.preventDefault();
      console.log(e);
      var targetId = link.getAttribute("href").substring(1);
      console.log(targetId);
      document.getElementById(targetId).scrollIntoView({
        behavior: "smooth",
      });
    });
  });
});

// POPUP


document.addEventListener("DOMContentLoaded", () => {
  const mealPopup = document.getElementById("mealPopup");
  const popupClose = document.getElementById("popupClose");
  const popupMealName = document.getElementById("popupMealName");
  const additionalProductsDiv = document.getElementById("additionalProducts");
  const addToCartBtn = document.getElementById("addToCartBtn");
  const quantityDisplay = document.getElementById("quantity");
  const decreaseQuantityBtn = document.getElementById("decreaseQuantity");
  const increaseQuantityBtn = document.getElementById("increaseQuantity");

  let currentMealId = null;
  let allProducts = [];
  let currentQuantity = 1;

  // Quantity control functions
  function updateQuantity(change) {
    currentQuantity = Math.max(1, currentQuantity + change);
    quantityDisplay.textContent = currentQuantity;
  }

  decreaseQuantityBtn.addEventListener("click", () => updateQuantity(-1));
  increaseQuantityBtn.addEventListener("click", () => updateQuantity(1));

  // Reset quantity when opening popup
  function resetQuantity() {
    currentQuantity = 1;
    quantityDisplay.textContent = currentQuantity;
  }

  // Fetch all additional products once and keep in memory
  fetch("http://localhost:8080/products")
    .then((res) => res.json())
    .then((data) => {
      allProducts = data;
      console.log("Fetched products:", allProducts);
    })
    .catch(error => {
      console.error("Error fetching products:", error);
    });

  fetch("http://localhost:8080/menu")
    .then((res) => res.json())
    .then((meals) => {
      console.log("Fetched meals:", meals);
      meals.forEach((meal) => {
        const li = document.createElement("li");
        li.innerHTML = `
          <div class="meal-card" data-mealid="${meal.mealid}" data-name="${meal.mealname}">
            <h4>${meal.mealname} - <span>$${meal.price.toFixed(2)}</span></h4>
            <p>${meal.mealcontent}</p>
            <img src="${meal.pictureofmeal}" alt="${meal.mealname}" />
          </div>`;
        
        const categoryListId = getCategoryListId(meal.category);
        const categoryList = document.getElementById(categoryListId);
        
        if (categoryList) {
          categoryList.appendChild(li);
          
          // Attach click event listener to the meal card
          const mealCard = li.querySelector(".meal-card");
          mealCard.addEventListener("click", function() {
            console.log("Meal card clicked:", meal);
            currentMealId = meal.mealid;
            popupMealName.textContent = meal.mealname;
            resetQuantity();

            // Filter additional products for this meal
            const relatedProducts = allProducts.filter(
              (prod) => parseInt(prod.mealid) === parseInt(currentMealId)
            );
            console.log("Related products for meal", currentMealId, ":", relatedProducts);

            // Render checkboxes
            additionalProductsDiv.innerHTML = "";
            
            // Add meal description
            const descriptionDiv = document.createElement("div");
            descriptionDiv.className = "meal-description";
            descriptionDiv.textContent = meal.mealcontent || "No description available";
            additionalProductsDiv.appendChild(descriptionDiv);
            
            if (relatedProducts.length > 0) {
              const productsContainer = document.createElement("div");
              productsContainer.className = "additional-products-container";
              
              // Add additional products
              const productsTitle = document.createElement("h3");
              productsTitle.textContent = "Additional Products:";
              productsContainer.appendChild(productsTitle);
              
              relatedProducts.forEach((product) => {
                const label = document.createElement("label");
                label.className = "product-checkbox";
                label.innerHTML = `
                  <input type="checkbox" value="${product.prodId}" data-price="${product.price}">
                  <span>${product.products} (+$${product.price.toFixed(2)})</span>
                `;
                productsContainer.appendChild(label);
              });
              
              additionalProductsDiv.appendChild(productsContainer);
            } else {
              const noProductsMsg = document.createElement("p");
              noProductsMsg.textContent = "No additional products available";
              additionalProductsDiv.appendChild(noProductsMsg);
            }

            mealPopup.style.display = "flex";
          });
        } else {
          console.warn(`Target list not found for category: ${categoryListId}`);
        }
      });
    })
    .catch(error => {
      console.error("Error fetching menu:", error);
    });

  function getCategoryListId(category) {
    switch (category.toLowerCase()) {
      case "appetizer":
      case "appetizers":
        return "appetizersList";
      case "main course":
      case "maincourse":
        return "mainCourseList";
      case "dessert":
        return "dessertList";
      case "beverage":
      case "beverages":
        return "beverageList";
      default:
        return "";
    }
  }

  popupClose.addEventListener("click", () => {
    mealPopup.style.display = "none";
  });

  addToCartBtn.addEventListener("click", () => {
    // Check authentication before adding to cart
    if (!checkAuth()) {
        const notification = document.getElementById('notification');
        const notificationMessage = document.getElementById('notification-message');
        notificationMessage.textContent = '401 Unauthorized - Please login to add items to cart';
        notification.classList.remove('hide');
        notification.classList.add('show');
        
        // Hide notification after 3 seconds
        setTimeout(() => {
            notification.classList.remove('show');
            notification.classList.add('hide');
        }, 3000);
        return;
    }

    const selected = Array.from(additionalProductsDiv.querySelectorAll("input:checked"))
        .map(input => ({
            name: input.nextElementSibling.textContent.split(' (+')[0],
            price: parseFloat(input.dataset.price)
        }));

    // Get the current meal details
    const mealName = popupMealName.textContent;
    const mealPrice = parseFloat(document.querySelector(`.meal-card[data-mealid="${currentMealId}"] span`).textContent.replace('$', ''));

    // Calculate total price
    const additionalTotal = selected.reduce((sum, item) => sum + item.price, 0);
    const totalPrice = (mealPrice + additionalTotal) * currentQuantity;

    // Create cart item object
    const cartItem = {
        main: {
            name: mealName,
            price: mealPrice
        },
        additional: selected,
        quantity: currentQuantity,
        totalPrice: totalPrice
    };

    // Get existing cart from localStorage or initialize empty array
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    
    // Add new item to cart
    cart.push(cartItem);
    
    // Save updated cart back to localStorage
    localStorage.setItem('cart', JSON.stringify(cart));

    console.log("Added to cart:", cartItem);
    console.log("Current cart:", cart);

    // Show notification
    const notification = document.getElementById('notification');
    const notificationMessage = document.getElementById('notification-message');
    notificationMessage.textContent = `${currentQuantity}x ${mealName} added to cart`;
    
    // Show notification
    notification.classList.remove('hide');
    notification.classList.add('show');
    
    // Hide notification after 3 seconds
    setTimeout(() => {
        notification.classList.remove('show');
        notification.classList.add('hide');
    }, 3000);

    mealPopup.style.display = "none";
  });
});

window.addEventListener("scroll", () => {
  // show the button
  if (
    document.body.scrollTop > 200 ||
    document.documentElement.scrollTop > 200
  ) {
    scrollToTopButton.style.display = "block";
    scrollToTopButton.addEventListener("click", () => {
      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    });
  } else {
    scrollToTopButton.style.display = "none";
  }
});

// Function to clear the inputs in contact section
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("contactForm");
  if (form) {
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      document.getElementById("name").value = "";
      document.getElementById("email").value = "";
      document.getElementById("subject").value = "";
      document.getElementById("message").value = "";
    });
  }
});

// Toggle menu icon and show the list of menu
document.addEventListener("DOMContentLoaded", () => {
  const menuToggle = document.getElementById("menuToggle");
  menuToggle.addEventListener("click", () => {
    if (
      navLinks.style.display === "flex" &&
      menuOverLay.style.display === "flex"
    ) {
      navLinks.style.display = "none";
      menuOverLay.style.display = "none";

      // calling the function of closing toggle
      // overlayClick();
    } else {
      navLinks.style.display = "flex";
      menuOverLay.style.display = "flex";
    }
  });
});

// Fuction close menu toggle (when you press the overlay it will close the menu)
document.addEventListener("DOMContentLoaded", () => {
  menuOverLay.addEventListener("click", () => {
    navLinks.style.display = "none";
    menuOverLay.style.display = "none";
  });
});
// Another way to apply close menu and overlay
// function overlayClick() {
//   menuOverLay.addEventListener("click", () => {
//     navLinks.style.display = "none";
//     menuOverLay.style.display = "none";
//   });
// }

// Filter function
document.addEventListener("DOMContentLoaded", function () {
  const menuSearchInput = document.getElementById("menuSearch");
  const searchIcon = document.querySelector(".search-icon");

  // Add event listeners for input and icon click
  if (menuSearchInput && searchIcon) {
    menuSearchInput.addEventListener("input", filterMenuItems);
    searchIcon.addEventListener("click", function () {
      filterMenuItems();
      menuSearchInput.value = "";
    });
  }

  function filterMenuItems() {
    const searchTerm = menuSearchInput.value.toLowerCase().trim();
    console.log("Search term:", searchTerm);
    
    const mealCards = document.querySelectorAll(".meal-card");
    console.log("Number of meal cards:", mealCards.length);

    // First, hide all categories
    document.querySelectorAll(".menu-category").forEach(category => {
      category.style.display = "none";
    });

    if (searchTerm === "") {
      // If search is empty, show all meals and categories
      document.querySelectorAll(".menu-category").forEach(category => {
        category.style.display = "block";
      });
      mealCards.forEach((card) => {
        const listItem = card.closest("li");
        if (listItem) {
          listItem.style.display = "block";
        }
      });
      return;
    }

    // Track which categories have visible meals
    const visibleCategories = new Set();

    mealCards.forEach((card) => {
      const mealName = card.querySelector("h4").textContent.toLowerCase();
      const mealDescription = card.querySelector("p").textContent.toLowerCase();
      
      console.log("Meal name:", mealName);
      console.log("Meal description:", mealDescription);

      const listItem = card.closest("li");
      if (listItem) {
        if (mealName.includes(searchTerm) || mealDescription.includes(searchTerm)) {
          listItem.style.display = "block";
          // Add the parent category to visible categories
          const category = listItem.closest(".menu-category");
          if (category) {
            visibleCategories.add(category);
          }
        } else {
          listItem.style.display = "none";
        }
      }
    });

    // Show only categories that have visible meals
    visibleCategories.forEach(category => {
      category.style.display = "block";
    });
  }
});

// Function to route to menu
function navigateToMenu() {
  window.location.href = "index.html#menu";
}

document.querySelectorAll(".menuNav").forEach((nav) => {
  nav.addEventListener("click", function () {
    navigateToMenu();
  });
});
