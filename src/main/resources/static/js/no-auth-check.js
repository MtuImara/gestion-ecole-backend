/**
 * Module qui DÉSACTIVE toute vérification d'authentification automatique
 * Utilisé pour empêcher les redirections intempestives
 */

console.log('[No Auth Check] Module chargé - AUCUNE vérification automatique');

// Override de toute fonction qui pourrait faire une vérification
if (typeof AuthService !== 'undefined') {
    console.log('[No Auth Check] Override AuthService.isAuthenticated');
    const originalIsAuthenticated = AuthService.isAuthenticated;
    AuthService.isAuthenticated = function() {
        // Toujours retourner true pour éviter les redirections
        console.log('[No Auth Check] isAuthenticated appelé - retourne toujours true');
        return true;
    };
}

// Désactiver auth-check.js s'il est chargé
if (typeof window.AuthCheck !== 'undefined') {
    console.log('[No Auth Check] Désactivation de AuthCheck');
    window.AuthCheck.check = function() {
        console.log('[No Auth Check] AuthCheck.check désactivé');
        return true;
    };
}

// Fonction pour obtenir le token (sans vérification)
window.getAuthToken = function() {
    return localStorage.getItem('ecolegest_token');
};

// Fonction pour obtenir l'utilisateur (sans vérification)
window.getAuthUser = function() {
    const userStr = localStorage.getItem('ecolegest_user');
    if (userStr) {
        try {
            return JSON.parse(userStr);
        } catch (e) {
            return null;
        }
    }
    return null;
};

// Fonction de déconnexion simple
window.simpleLogout = function() {
    localStorage.removeItem('ecolegest_token');
    localStorage.removeItem('ecolegest_user');
    window.location.href = '/login-simple.html';
};

console.log('[No Auth Check] Vérifications automatiques DÉSACTIVÉES');
