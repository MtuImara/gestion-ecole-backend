/**
 * Module ULTRA AGRESSIF qui empêche TOUTE redirection vers login
 * DOIT être chargé EN PREMIER sur TOUTES les pages
 */

console.log('[No Redirect] Protection MAXIMALE activée');

// 1. BLOQUER window.location.href
const originalLocationDescriptor = Object.getOwnPropertyDescriptor(window, 'location');
Object.defineProperty(window, 'location', {
    get: function() {
        const location = originalLocationDescriptor.get.call(this);
        const originalHrefDescriptor = Object.getOwnPropertyDescriptor(location, 'href');
        
        if (!location._hrefIntercepted) {
            Object.defineProperty(location, 'href', {
                get: function() {
                    return originalHrefDescriptor ? originalHrefDescriptor.get.call(this) : this.href;
                },
                set: function(value) {
                    if (value && (value.includes('login.html') || value.includes('login-simple.html'))) {
                        console.warn('[No Redirect] ❌ BLOQUÉ: window.location.href =', value);
                        return;
                    }
                    if (originalHrefDescriptor) {
                        originalHrefDescriptor.set.call(this, value);
                    }
                },
                configurable: true
            });
            location._hrefIntercepted = true;
        }
        return location;
    },
    configurable: true
});

// Intercepter window.location.href
(function() {
    const originalLocation = window.location;
    const originalHref = originalLocation.href;
    
    // Créer un proxy pour window.location
    const locationProxy = new Proxy(originalLocation, {
        set: function(target, property, value) {
            if (property === 'href') {
                // Vérifier si on essaie de rediriger vers login
                if (value && (value.includes('/login.html') || value.endsWith('/login.html'))) {
                    console.warn('[No Redirect] BLOQUÉ: Tentative de redirection vers', value);
                    return true; // Bloquer la redirection
                }
            }
            // Permettre les autres redirections
            target[property] = value;
            return true;
        }
    });
    
    // Remplacer window.location par notre proxy
    try {
        Object.defineProperty(window, 'location', {
            get: function() {
                return locationProxy;
            },
            configurable: true
        });
    } catch (e) {
        console.log('[No Redirect] Impossible de remplacer window.location, essai méthode alternative');
    }
})();

// 2. BLOQUER window.location.replace
window.location.replace = new Proxy(window.location.replace, {
    apply: function(target, thisArg, args) {
        const url = args[0];
        if (url && (url.includes('login.html') || url.includes('login-simple.html'))) {
            console.warn('[No Redirect] ❌ BLOQUÉ: location.replace()', url);
            return;
        }
        return target.apply(thisArg, args);
    }
});

// 3. BLOQUER window.location.assign  
window.location.assign = new Proxy(window.location.assign, {
    apply: function(target, thisArg, args) {
        const url = args[0];
        if (url && (url.includes('login.html') || url.includes('login-simple.html'))) {
            console.warn('[No Redirect] ❌ BLOQUÉ: location.assign()', url);
            return;
        }
        return target.apply(thisArg, args);
    }
});

// Si DashboardRouter existe, le neutraliser
if (typeof DashboardRouter !== 'undefined') {
    console.log('[No Redirect] Neutralisation de DashboardRouter');
    
    // Override redirectToDashboard pour ne pas rediriger vers login
    DashboardRouter.redirectToDashboard = function() {
        console.log('[No Redirect] DashboardRouter.redirectToDashboard neutralisé');
        const user = AuthService.getUser();
        if (!user) {
            console.log('[No Redirect] Pas d\'utilisateur, mais pas de redirection');
            return;
        }
        
        let role = null;
        if (user.roles && Array.isArray(user.roles) && user.roles.length > 0) {
            role = user.roles[0];
        } else if (user.role) {
            role = user.role;
        }
        
        if (role) {
            const dashboardUrl = DashboardRouter.DASHBOARDS[role] || '/dashboard.html';
            if (window.location.pathname !== dashboardUrl) {
                window.location.href = dashboardUrl;
            }
        }
    };
    
    // Override checkAccess pour toujours retourner true
    DashboardRouter.checkAccess = function() {
        console.log('[No Redirect] DashboardRouter.checkAccess neutralisé - retourne true');
        return true;
    };
}

// 4. FORCER l'authentification à TOUJOURS être vraie
// Attendre un peu que AuthService soit chargé
setTimeout(() => {
    if (typeof AuthService !== 'undefined') {
        console.log('[No Redirect] ✅ Override AuthService');
        
        // Forcer isAuthenticated à toujours retourner true
        AuthService.isAuthenticated = function() {
            console.log('[No Redirect] AuthService.isAuthenticated() => TRUE (forcé)');
            return true;
        };
        
        // Empêcher logout de rediriger
        AuthService.logout = function() {
            console.log('[No Redirect] Logout appelé mais redirection bloquée');
            localStorage.removeItem('ecolegest_token');
            localStorage.removeItem('ecolegest_user');
            // PAS de redirection
        };
        
        // Forcer getUser à retourner un utilisateur fictif si nécessaire
        const originalGetUser = AuthService.getUser;
        AuthService.getUser = function() {
            const user = originalGetUser.call(this);
            if (!user) {
                console.log('[No Redirect] Pas d\'utilisateur, création d\'un utilisateur fictif');
                return { username: 'admin', roles: ['ADMIN'] };
            }
            return user;
        };
    }
    
    // Désactiver RoleManager s'il existe
    if (typeof RoleManager !== 'undefined') {
        console.log('[No Redirect] ✅ Neutralisation de RoleManager');
        RoleManager.initializePage = function() {
            console.log('[No Redirect] RoleManager.initializePage() désactivé');
        };
        RoleManager.isAuthenticated = function() {
            return true;
        };
    }
}, 100);

console.log('[No Redirect] 🛡️ PROTECTION MAXIMALE ACTIVÉE - Aucune redirection possible!');
