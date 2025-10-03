/**
 * Cache Buster - Gestion automatique du cache
 * Empêche les problèmes de cache navigateur
 */

// Version de l'application (change à chaque déploiement)
const APP_VERSION = '1.0.' + Date.now();

// Configuration du cache
const CacheManager = {
    
    // Initialisation au chargement de la page
    init: function() {
        console.log('Cache Manager initialisé - Version:', APP_VERSION);
        
        // Ajouter les meta tags pour désactiver le cache
        this.addNoCacheMetaTags();
        
        // Vérifier et nettoyer le cache si nécessaire
        this.checkAndClearCache();
        
        // Ajouter versioning aux ressources existantes
        this.updateResourceVersions();
        
        // Intercepter les requêtes fetch pour ajouter no-cache
        this.interceptFetch();
    },
    
    // Ajouter les meta tags no-cache
    addNoCacheMetaTags: function() {
        const metaTags = [
            { 'http-equiv': 'Cache-Control', 'content': 'no-cache, no-store, must-revalidate' },
            { 'http-equiv': 'Pragma', 'content': 'no-cache' },
            { 'http-equiv': 'Expires', 'content': '0' },
            { 'name': 'app-version', 'content': APP_VERSION }
        ];
        
        metaTags.forEach(tag => {
            const meta = document.createElement('meta');
            Object.keys(tag).forEach(key => {
                meta.setAttribute(key, tag[key]);
            });
            document.head.appendChild(meta);
        });
    },
    
    // Vérifier et nettoyer le cache si version différente
    checkAndClearCache: function() {
        const storedVersion = localStorage.getItem('app-version');
        
        if (storedVersion !== APP_VERSION) {
            console.log('Nouvelle version détectée. Nettoyage du cache...');
            
            // Nettoyer le localStorage (sauf auth)
            const authToken = localStorage.getItem('token');
            const user = localStorage.getItem('user');
            
            localStorage.clear();
            
            // Restaurer auth si existait
            if (authToken) localStorage.setItem('token', authToken);
            if (user) localStorage.setItem('user', user);
            
            // Sauvegarder nouvelle version
            localStorage.setItem('app-version', APP_VERSION);
            
            // Nettoyer les caches du navigateur si possible
            if ('caches' in window) {
                caches.keys().then(names => {
                    names.forEach(name => {
                        caches.delete(name);
                    });
                });
            }
            
            // Forcer rechargement si première visite avec nouvelle version
            if (storedVersion && !sessionStorage.getItem('version-reloaded')) {
                sessionStorage.setItem('version-reloaded', 'true');
                location.reload(true);
            }
        }
    },
    
    // Mettre à jour les versions des ressources existantes
    updateResourceVersions: function() {
        // Mettre à jour les liens CSS
        document.querySelectorAll('link[rel="stylesheet"]').forEach(link => {
            if (!link.href.includes('?v=')) {
                link.href = link.href + '?v=' + APP_VERSION;
            }
        });
        
        // Mettre à jour les scripts
        document.querySelectorAll('script[src]').forEach(script => {
            if (!script.src.includes('?v=') && !script.src.includes('cache-buster.js')) {
                const newScript = document.createElement('script');
                newScript.src = script.src + '?v=' + APP_VERSION;
                newScript.async = script.async;
                newScript.defer = script.defer;
                script.parentNode.replaceChild(newScript, script);
            }
        });
    },
    
    // Intercepter fetch pour ajouter headers no-cache
    interceptFetch: function() {
        const originalFetch = window.fetch;
        
        window.fetch = function(...args) {
            let [resource, config] = args;
            
            // Ajouter headers no-cache pour les requêtes API
            if (typeof resource === 'string' && resource.includes('/api/')) {
                config = config || {};
                config.headers = config.headers || {};
                config.headers['Cache-Control'] = 'no-cache';
                config.headers['Pragma'] = 'no-cache';
            }
            
            return originalFetch(resource, config);
        };
    },
    
    // Fonction pour charger un script avec versioning
    loadScript: function(src, callback) {
        const script = document.createElement('script');
        script.src = `${src}?v=${APP_VERSION}`;
        script.onload = callback;
        document.head.appendChild(script);
        return script;
    },
    
    // Fonction pour charger un CSS avec versioning
    loadCSS: function(href) {
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = `${href}?v=${APP_VERSION}`;
        document.head.appendChild(link);
        return link;
    },
    
    // Forcer le rechargement complet
    forceReload: function() {
        localStorage.setItem('force-reload', 'true');
        location.reload(true);
    }
};

// Auto-initialisation
document.addEventListener('DOMContentLoaded', function() {
    CacheManager.init();
});

// Export pour utilisation
window.CacheManager = CacheManager;
window.APP_VERSION = APP_VERSION;
