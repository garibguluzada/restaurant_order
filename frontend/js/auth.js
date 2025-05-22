// Authentication utility functions
function checkAuth() {
    const jwt = localStorage.getItem('jwt');
    if (!jwt) {
        return false;
    }
    return true;
}

function getUserId() {
    const jwt = localStorage.getItem('jwt');
    if (!jwt) return null;
    
    try {
        const payload = JSON.parse(atob(jwt.split('.')[1]));
        return payload.id;
    } catch (error) {
        console.error('Error parsing JWT:', error);
        return null;
    }
}

// Export functions for use in other files
window.checkAuth = checkAuth;
window.getUserId = getUserId; 