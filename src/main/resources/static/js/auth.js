// Gestion de l'authentification
(function() {
    'use strict';
    
    // Vérifier si l'utilisateur est connecté
    function checkAuth() {
        const token = localStorage.getItem('ecolegest_token');
        const user = localStorage.getItem('ecolegest_user');
        
        // Vérification de l'authentification ACTIVÉE
        if (!token || !user) {
            // Rediriger vers la page de connexion si pas de token
            if (!window.location.pathname.includes('login.html') && 
                !window.location.pathname.includes('index.html')) {
                console.log('[Auth] Pas de token, redirection vers login');
                window.location.href = '/login.html';
                return false;
            }
        }
        
        console.log('[Auth] Utilisateur authentifié');
        return true;
    }
    
    // Obtenir le token
    function getToken() {
        return localStorage.getItem('ecolegest_token');
    }
    
    // Obtenir les informations de l'utilisateur
    function getUser() {
        const userStr = localStorage.getItem('ecolegest_user');
        if (userStr) {
            try {
                return JSON.parse(userStr);
            } catch (e) {
                console.error('Erreur lors du parsing des données utilisateur:', e);
                return null;
            }
        }
        return null;
    }
    
    // Vérifier si l'utilisateur a un rôle spécifique
    function hasRole(role) {
        const user = getUser();
        if (user && user.roles) {
            return user.roles.some(r => r === role || r === 'ROLE_' + role);
        }
        return false;
    }
    
    // Vérifier si l'utilisateur a l'un des rôles spécifiés
    function hasAnyRole(...roles) {
        const user = getUser();
        if (user && user.roles) {
            return roles.some(role => 
                user.roles.some(r => r === role || r === 'ROLE_' + role)
            );
        }
        return false;
    }
    
    // Se déconnecter
    function logout() {
        localStorage.removeItem('ecolegest_token');
        localStorage.removeItem('ecolegest_user');
        window.location.href = '/login.html';
    }
    
    // Ajouter le token aux headers de toutes les requêtes fetch
    const originalFetch = window.fetch;
    window.fetch = function(...args) {
        let [resource, config] = args;
        
        // Si c'est une requête vers l'API
        if (typeof resource === 'string' && resource.includes('/api/')) {
            const token = getToken();
            
            if (token) {
                config = config || {};
                config.headers = config.headers || {};
                config.headers['Authorization'] = `Bearer ${token}`;
            }
        }
        
        return originalFetch(resource, config)
            .then(response => {
                // Si 401 ou 403, rediriger vers login
                if (response.status === 401 || response.status === 403) {
                    if (!window.location.pathname.includes('login.html')) {
                        logout();
                    }
                }
                return response;
            });
    };
    
    // Exposer les fonctions globalement
    window.auth = {
        checkAuth,
        getToken,
        getUser,
        hasRole,
        hasAnyRole,
        logout
    };
    
    // Vérifier l'authentification au chargement de la page
    document.addEventListener('DOMContentLoaded', function() {
        // Ne pas vérifier l'auth sur la page de login
        if (!window.location.pathname.includes('login.html')) {
            // Vérification activée
            checkAuth();
            
            // Afficher les informations de l'utilisateur si disponibles
            const user = getUser();
            if (user) {
                const userNameElements = document.querySelectorAll('.user-name');
                userNameElements.forEach(el => {
                    el.textContent = user.nom || user.username || 'Utilisateur';
                });
                
                const userRoleElements = document.querySelectorAll('.user-role');
                userRoleElements.forEach(el => {
                    el.textContent = user.roles ? user.roles[0].replace('ROLE_', '') : 'Utilisateur';
                });
            }
        }
    });
    
    // Gérer les boutons de déconnexion
    document.addEventListener('DOMContentLoaded', function() {
        const logoutButtons = document.querySelectorAll('.logout-btn, [onclick*="logout"]');
        logoutButtons.forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                logout();
            });
        });
    });
    
})();
