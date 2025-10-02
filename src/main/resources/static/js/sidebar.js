// Composant Sidebar réutilisable
function createSidebar(activePage) {
    const menuItems = [
        { icon: '📊', text: 'Dashboard', page: 'dashboard.html' },
        { icon: '🎓', text: 'Élèves', page: 'eleves.html' },
        { icon: '🔥', text: 'Paiements', page: 'paiements.html' },
        { icon: '🏫', text: 'Classes', page: 'classes.html' },
        { icon: '👨‍👩‍👧‍👦', text: 'Parents', page: 'parents.html' },
        { icon: '👨‍🏫', text: 'Enseignants', page: 'enseignants.html' },
        { icon: '📄', text: 'Factures', page: 'factures.html' },
        { icon: '🎁', text: 'Dérogations', page: 'derogations.html' },
        { icon: '📊', text: 'Rapports', page: 'rapports.html' },
        { icon: '⚙️', text: 'Paramètres', page: 'parametres.html' }
    ];

    const menuHTML = menuItems.map(item => `
        <div class="menu-item ${activePage === item.page ? 'active' : ''}" onclick="window.location.href='/${item.page}'">
            <span class="menu-icon">${item.icon}</span>
            <span class="menu-text">${item.text}</span>
        </div>
    `).join('');

    return `
        <div class="sidebar">
            <div class="logo" onclick="window.location.href='/dashboard.html'">
                <div class="logo-icon">🎓</div>
                <span class="logo-text">EcoleGest</span>
            </div>
            <div class="menu">
                ${menuHTML}
                <div class="menu-item" onclick="AuthService.logout()">
                    <span class="menu-icon">🚪</span>
                    <span class="menu-text">Déconnexion</span>
                </div>
            </div>
        </div>
    `;
}

// Charger le sidebar dans la page
document.addEventListener('DOMContentLoaded', () => {
    const sidebarContainer = document.getElementById('sidebar-container');
    if (sidebarContainer) {
        const currentPage = window.location.pathname.split('/').pop();
        sidebarContainer.innerHTML = createSidebar(currentPage);
    }
});
