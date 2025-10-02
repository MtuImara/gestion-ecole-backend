// Cache Buster - Ajoute version aux URLs
const APP_VERSION = Date.now(); // Utilise timestamp pour forcer rechargement

// Fonction pour charger un script avec versioning
function loadScriptWithVersion(src) {
    const script = document.createElement('script');
    script.src = `${src}?v=${APP_VERSION}`;
    document.head.appendChild(script);
}

// Fonction pour charger un CSS avec versioning
function loadCSSWithVersion(href) {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = `${href}?v=${APP_VERSION}`;
    document.head.appendChild(link);
}

// Désactiver le cache du navigateur
if (window.performance && window.performance.navigation.type === 1) {
    console.log('Page rechargée - Cache nettoyé');
}
