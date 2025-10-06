/**
 * Système d'authentification simplifié
 * Remplace complètement l'ancien système
 */

// Configuration
const AUTH_CONFIG = {
    TOKEN_KEY: 'ecolegest_token',
    USER_KEY: 'ecolegest_user',
    LOGIN_PAGE: '/login-simple.html'
};

// Fonction pour vérifier si on est sur une page publique
function isPublicPage() {
    const publicPages = [
        'login.html',
        'login-simple.html',
        'register.html',
        'test-final.html',
        'test-auth.html',
        'test-simple-login.html',
        'test-login.html',
        'diagnostic-login.html',
        'verify-cache-integration.html',
        'fix-all-pages.html'
    ];
    
    const currentPage = window.location.pathname.split('/').pop();
    return publicPages.includes(currentPage);
}

// Fonction pour obtenir le token
function getToken() {
    return localStorage.getItem(AUTH_CONFIG.TOKEN_KEY);
}

// Fonction pour obtenir l'utilisateur
function getUser() {
    const userStr = localStorage.getItem(AUTH_CONFIG.USER_KEY);
    if (userStr) {
        try {
            return JSON.parse(userStr);
        } catch (e) {
            console.error('Erreur parsing user:', e);
            return null;
        }
    }
    return null;
}

// Fonction pour vérifier l'authentification
function checkAuth() {
    // Si c'est une page publique, pas de vérification
    if (isPublicPage()) {
        console.log('[Auth Simple] Page publique, pas de vérification');
        return true;
    }
    
    const token = getToken();
    const user = getUser();
    
    console.log('[Auth Simple] Vérification auth:');
    console.log('  - Token:', token ? 'Présent' : 'Absent');
    console.log('  - User:', user ? user.username : 'Absent');
    
    if (!token || !user) {
        console.log('[Auth Simple] Non authentifié, redirection nécessaire');
        return false;
    }
    
    console.log('[Auth Simple] ✅ Authentifié:', user.username);
    return true;
}

// Fonction de déconnexion
function logout() {
    console.log('[Auth Simple] Déconnexion...');
    localStorage.removeItem(AUTH_CONFIG.TOKEN_KEY);
    localStorage.removeItem(AUTH_CONFIG.USER_KEY);
    window.location.href = AUTH_CONFIG.LOGIN_PAGE;
}

// NE PAS faire de vérification automatique au chargement
// Laisser chaque page décider quand vérifier
console.log('[Auth Simple] Module chargé - Pas de vérification automatique');

// Exporter les fonctions
window.AuthSimple = {
    getToken: getToken,
    getUser: getUser,
    checkAuth: checkAuth,
    logout: logout,
    isPublicPage: isPublicPage
};
