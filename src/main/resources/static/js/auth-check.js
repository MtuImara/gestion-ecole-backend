/**
 * Système de vérification d'authentification unifié
 * Ce fichier remplace la vérification dans api.js
 */

// Configuration
const TOKEN_KEY = 'ecolegest_token';
const USER_KEY = 'ecolegest_user';

// Pages publiques qui ne nécessitent pas d'authentification
const PUBLIC_PAGES = [
    '/login.html',
    '/login-simple.html',
    '/register.html',
    '/test-final.html',
    '/test-auth.html',
    '/test-simple-login.html',
    '/test-login.html',
    '/diagnostic-login.html',
    '/verify-cache-integration.html'
];

// Fonction pour obtenir le token
function getAuthToken() {
    return localStorage.getItem(TOKEN_KEY);
}

// Fonction pour obtenir l'utilisateur
function getAuthUser() {
    const userStr = localStorage.getItem(USER_KEY);
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

// Fonction pour vérifier si l'utilisateur est authentifié
function isAuthenticated() {
    const token = getAuthToken();
    const user = getAuthUser();
    
    console.log('[Auth Check] Token présent:', !!token);
    console.log('[Auth Check] User présent:', !!user);
    
    return !!(token && user);
}

// Fonction pour obtenir le dashboard selon le rôle
function getDashboardUrl(user) {
    if (!user || !user.roles || user.roles.length === 0) {
        return '/dashboard-fixed.html';
    }
    
    const role = user.roles[0];
    
    switch(role) {
        case 'ADMIN':
        case 'DIRECTEUR':
            return '/dashboard-fixed.html';
        case 'COMPTABLE':
            return '/dashboard-comptable.html';
        case 'ENSEIGNANT':
            return '/dashboard-enseignant.html';
        case 'PARENT':
            return '/dashboard-parent.html';
        default:
            return '/dashboard-fixed.html';
    }
}

// Fonction de déconnexion
function logout() {
    console.log('[Auth Check] Déconnexion...');
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = '/login-simple.html';
}

// Vérification au chargement de la page
function checkPageAuthentication() {
    const currentPath = window.location.pathname;
    console.log('[Auth Check] Page actuelle:', currentPath);
    
    // Si c'est une page publique, pas de vérification
    if (PUBLIC_PAGES.includes(currentPath)) {
        console.log('[Auth Check] Page publique, pas de vérification nécessaire');
        return;
    }
    
    // Pour les pages protégées, vérifier l'authentification
    if (!isAuthenticated()) {
        console.log('[Auth Check] Non authentifié! Redirection vers login...');
        
        // Afficher un message avant de rediriger
        const messageDiv = document.createElement('div');
        messageDiv.style.cssText = `
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: #f8d7da;
            color: #721c24;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            z-index: 10000;
            font-family: Arial, sans-serif;
        `;
        messageDiv.innerHTML = `
            <h3 style="margin-top: 0;">⚠️ Session expirée</h3>
            <p>Vous devez vous reconnecter.</p>
            <p style="font-size: 12px; margin-bottom: 0;">Redirection dans 2 secondes...</p>
        `;
        document.body.appendChild(messageDiv);
        
        // Rediriger après 2 secondes
        setTimeout(() => {
            window.location.href = '/login-simple.html';
        }, 2000);
    } else {
        const user = getAuthUser();
        console.log('[Auth Check] ✅ Authentifié:', user ? user.username : 'Unknown');
        
        // Afficher un indicateur de connexion
        showAuthIndicator(user);
    }
}

// Afficher un indicateur visuel de connexion
function showAuthIndicator(user) {
    // Ne pas afficher sur les pages de test
    if (window.location.pathname.includes('test') || window.location.pathname.includes('diagnostic')) {
        return;
    }
    
    const indicator = document.createElement('div');
    indicator.id = 'auth-indicator';
    indicator.style.cssText = `
        position: fixed;
        top: 10px;
        right: 10px;
        background: rgba(40, 167, 69, 0.9);
        color: white;
        padding: 8px 15px;
        border-radius: 20px;
        font-size: 12px;
        font-family: Arial, sans-serif;
        z-index: 9999;
        display: flex;
        align-items: center;
        gap: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
    `;
    
    indicator.innerHTML = `
        <span style="font-size: 16px;">✅</span>
        <span>Connecté: <strong>${user ? user.username : 'Unknown'}</strong></span>
        ${user && user.roles ? `<span style="opacity: 0.8;">(${user.roles[0]})</span>` : ''}
    `;
    
    document.body.appendChild(indicator);
    
    // Faire disparaître après 5 secondes
    setTimeout(() => {
        if (indicator.parentNode) {
            indicator.style.transition = 'opacity 0.5s';
            indicator.style.opacity = '0';
            setTimeout(() => {
                if (indicator.parentNode) {
                    indicator.remove();
                }
            }, 500);
        }
    }, 5000);
}

// Lancer la vérification quand le DOM est chargé
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', checkPageAuthentication);
} else {
    // DOM déjà chargé
    checkPageAuthentication();
}

// Exporter les fonctions pour utilisation dans d'autres scripts
window.AuthCheck = {
    getToken: getAuthToken,
    getUser: getAuthUser,
    isAuthenticated: isAuthenticated,
    getDashboardUrl: getDashboardUrl,
    logout: logout,
    check: checkPageAuthentication
};

console.log('[Auth Check] Module chargé et prêt');
