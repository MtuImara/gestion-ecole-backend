/**
 * Navigation simple sans vérification d'authentification
 */

console.log('[Navigation] Mode simple activé - Pas de vérification d\'authentification');

// Fonction pour naviguer vers une page
function navigateTo(page) {
    const url = `/${page}.html`;
    console.log('[Navigation] Navigation vers:', url);
    
    // Navigation directe sans aucune vérification
    window.location.pathname = url;
}

// Attacher les événements de navigation au chargement
document.addEventListener('DOMContentLoaded', function() {
    console.log('[Navigation] Initialisation de la navigation simple');
    
    // Gérer tous les boutons de menu
    const menuItems = document.querySelectorAll('.menu-item[onclick*="location.href"]');
    menuItems.forEach(item => {
        // Extraire la page de l'onclick
        const onclick = item.getAttribute('onclick');
        if (onclick) {
            const match = onclick.match(/\/([^.]+)\.html/);
            if (match) {
                const page = match[1];
                
                // Remplacer l'onclick par notre fonction
                item.removeAttribute('onclick');
                item.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    console.log('[Navigation] Clic sur menu:', page);
                    navigateTo(page);
                });
            }
        }
    });
    
    // Gérer les menus avec data-page
    const dataPageItems = document.querySelectorAll('.menu-item[data-page]');
    dataPageItems.forEach(item => {
        const page = item.getAttribute('data-page');
        item.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log('[Navigation] Clic sur menu (data-page):', page);
            navigateTo(page);
        });
    });
    
    console.log('[Navigation] Navigation simple prête');
});

// Exposer globalement
window.SimpleNavigation = {
    navigateTo: navigateTo
};
