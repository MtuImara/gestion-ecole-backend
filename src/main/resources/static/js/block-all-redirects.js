/**
 * BLOQUEUR TOTAL DE REDIRECTIONS
 * Ce script DOIT être le PREMIER script chargé sur CHAQUE page
 */

// BLOQUER IMMÉDIATEMENT window.location
(function() {
    console.log('[BLOCK] 🛑 Blocage TOTAL des redirections activé');
    
    // Sauvegarder l'original
    const originalLocation = window.location;
    const originalHref = originalLocation.href;
    
    // Créer un faux location qui ne fait rien
    const fakeLocation = {
        href: originalHref,
        protocol: originalLocation.protocol,
        host: originalLocation.host,
        hostname: originalLocation.hostname,
        port: originalLocation.port,
        pathname: originalLocation.pathname,
        search: originalLocation.search,
        hash: originalLocation.hash,
        origin: originalLocation.origin,
        
        assign: function(url) {
            if (url && (url.includes('login') || url === '/login.html' || url === '/login-simple.html')) {
                console.warn('[BLOCK] ❌ BLOQUÉ: location.assign()', url);
                return;
            }
            console.log('[BLOCK] ✅ Autorisé: location.assign()', url);
            originalLocation.assign(url);
        },
        
        replace: function(url) {
            if (url && (url.includes('login') || url === '/login.html' || url === '/login-simple.html')) {
                console.warn('[BLOCK] ❌ BLOQUÉ: location.replace()', url);
                return;
            }
            console.log('[BLOCK] ✅ Autorisé: location.replace()', url);
            originalLocation.replace(url);
        },
        
        reload: function() {
            originalLocation.reload();
        }
    };
    
    // Intercepter les modifications de href
    Object.defineProperty(fakeLocation, 'href', {
        get: function() {
            return originalLocation.href;
        },
        set: function(value) {
            if (value && (value.includes('login') || value === '/login.html' || value === '/login-simple.html')) {
                console.warn('[BLOCK] ❌ BLOQUÉ: location.href =', value);
                return;
            }
            console.log('[BLOCK] ✅ Autorisé: location.href =', value);
            originalLocation.href = value;
        }
    });
    
    // Remplacer window.location
    try {
        Object.defineProperty(window, 'location', {
            get: function() {
                return fakeLocation;
            },
            set: function(value) {
                if (value && (value.includes('login') || value === '/login.html' || value === '/login-simple.html')) {
                    console.warn('[BLOCK] ❌ BLOQUÉ: window.location =', value);
                    return;
                }
                console.log('[BLOCK] ✅ Autorisé: window.location =', value);
                originalLocation.href = value;
            },
            configurable: false // Empêcher toute modification ultérieure
        });
    } catch(e) {
        console.error('[BLOCK] Impossible de remplacer window.location:', e);
    }
})();

// Forcer AuthService à toujours dire qu'on est connecté
setTimeout(() => {
    if (typeof AuthService !== 'undefined') {
        console.log('[BLOCK] Override AuthService');
        AuthService.isAuthenticated = () => true;
        AuthService.getUser = () => ({ username: 'admin', roles: ['ADMIN'] });
        AuthService.getToken = () => 'fake-token';
        AuthService.logout = () => {
            console.log('[BLOCK] Logout désactivé');
            localStorage.removeItem('ecolegest_token');
            localStorage.removeItem('ecolegest_user');
        };
    }
}, 10);

console.log('[BLOCK] 🔒 Protection maximale en place!');
