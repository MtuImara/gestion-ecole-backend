// Service de gestion des rôles utilisateur
const RoleManager = {
    
    // Décoder le JWT pour extraire les informations
    decodeToken(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (error) {
            console.error('Erreur décodage token:', error);
            return null;
        }
    },
    
    // Obtenir les informations utilisateur depuis le token
    getUserInfo() {
        const token = AuthService.getToken();
        if (!token) return null;
        return this.decodeToken(token);
    },
    
    // Obtenir le rôle de l'utilisateur
    getUserRole() {
        const userInfo = this.getUserInfo();
        if (!userInfo) return null;
        
        // Le token JWT contient souvent les roles dans 'authorities' ou 'roles'
        if (userInfo.authorities && userInfo.authorities.length > 0) {
            return userInfo.authorities[0]; // Premier rôle
        }
        if (userInfo.role) return userInfo.role;
        return null;
    },
    
    // Obtenir tous les rôles
    getAllRoles() {
        const userInfo = this.getUserInfo();
        if (!userInfo) return [];
        return userInfo.authorities || [userInfo.role] || [];
    },
    
    // Vérifications par rôle
    isAdmin() {
        return this.getAllRoles().includes('ADMIN') || this.getAllRoles().includes('ROLE_ADMIN');
    },
    
    isComptable() {
        return this.getAllRoles().includes('COMPTABLE') || this.getAllRoles().includes('ROLE_COMPTABLE');
    },
    
    isSecretaire() {
        return this.getAllRoles().includes('SECRETAIRE') || this.getAllRoles().includes('ROLE_SECRETAIRE');
    },
    
    isParent() {
        return this.getAllRoles().includes('PARENT') || this.getAllRoles().includes('ROLE_PARENT');
    },
    
    isEleve() {
        return this.getAllRoles().includes('ELEVE') || this.getAllRoles().includes('ROLE_ELEVE');
    },
    
    isEnseignant() {
        return this.getAllRoles().includes('ENSEIGNANT') || this.getAllRoles().includes('ROLE_ENSEIGNANT');
    },
    
    isDirecteur() {
        return this.getAllRoles().includes('DIRECTEUR') || this.getAllRoles().includes('ROLE_DIRECTEUR');
    },
    
    // Vérifier si l'utilisateur a l'un des rôles spécifiés
    hasAnyRole(...roles) {
        const userRoles = this.getAllRoles();
        return roles.some(role => 
            userRoles.includes(role) || userRoles.includes('ROLE_' + role)
        );
    },
    
    // Obtenir le nom de l'utilisateur
    getUsername() {
        const userInfo = this.getUserInfo();
        return userInfo ? (userInfo.sub || userInfo.username) : null;
    },
    
    // Obtenir l'ID de l'utilisateur
    getUserId() {
        const userInfo = this.getUserInfo();
        return userInfo ? userInfo.userId : null;
    },
    
    // Vérifier si l'utilisateur est authentifié
    isAuthenticated() {
        return AuthService.isAuthenticated();
    },
    
    // Masquer un élément selon les rôles autorisés
    hideIfNotRole(elementId, ...allowedRoles) {
        const element = document.getElementById(elementId);
        if (!element) return;
        
        if (!this.hasAnyRole(...allowedRoles)) {
            element.style.display = 'none';
        }
    },
    
    // Afficher un élément seulement si l'utilisateur a le rôle
    showIfRole(elementId, ...allowedRoles) {
        const element = document.getElementById(elementId);
        if (!element) return;
        
        if (this.hasAnyRole(...allowedRoles)) {
            element.style.display = '';
        } else {
            element.style.display = 'none';
        }
    },
    
    // Obtenir le dashboard approprié selon le rôle
    getDashboardUrl() {
        if (this.isAdmin() || this.isDirecteur()) return '/dashboard.html';
        if (this.isComptable() || this.isSecretaire()) return '/dashboard.html';
        if (this.isEnseignant()) return '/dashboard-enseignant.html';
        if (this.isParent()) return '/dashboard-parent.html';
        if (this.isEleve()) return '/dashboard-eleve.html';
        return '/dashboard.html';
    },
    
    // Obtenir les menus selon le rôle
    getMenuItems() {
        const baseMenu = [
            { icon: '📊', text: 'Dashboard', url: this.getDashboardUrl(), roles: ['ALL'] }
        ];
        
        const adminMenu = [
            { icon: '👨‍🎓', text: 'Élèves', url: '/eleves.html', roles: ['ADMIN', 'SECRETAIRE', 'DIRECTEUR'] },
            { icon: '🏫', text: 'Classes', url: '/classes.html', roles: ['ADMIN', 'SECRETAIRE', 'DIRECTEUR'] },
            { icon: '💰', text: 'Paiements', url: '/paiements.html', roles: ['ADMIN', 'COMPTABLE', 'PARENT', 'ELEVE'] },
            { icon: '✅', text: 'Validation', url: '/validation-paiements.html', roles: ['ADMIN', 'COMPTABLE', 'SECRETAIRE'] },
            { icon: '🧾', text: 'Reçus', url: '/recus.html', roles: ['ADMIN', 'COMPTABLE', 'PARENT', 'ELEVE'] },
            { icon: '🎁', text: 'Dérogations', url: '/derogations.html', roles: ['ADMIN', 'DIRECTEUR', 'PARENT'] },
            { icon: '💬', text: 'Communication', url: '/communication.html', roles: ['ALL'] }
        ];
        
        return [...baseMenu, ...adminMenu].filter(item => {
            if (item.roles.includes('ALL')) return true;
            return this.hasAnyRole(...item.roles);
        });
    },
    
    // Générer le menu HTML
    generateMenu() {
        const menuItems = this.getMenuItems();
        const currentPath = window.location.pathname;
        
        return menuItems.map(item => {
            const isActive = currentPath.includes(item.url) ? 'active' : '';
            return `
                <div class="menu-item ${isActive}" onclick="window.location.href='${item.url}'">
                    <span class="menu-icon">${item.icon}</span>
                    <span class="menu-text">${item.text}</span>
                </div>
            `;
        }).join('');
    },
    
    // Initialiser la gestion des rôles sur une page
    initializePage() {
        // Vérifier si l'utilisateur est authentifié
        if (!this.isAuthenticated()) {
            window.location.href = '/login.html';
            return;
        }
        
        // Afficher le nom de l'utilisateur
        const username = this.getUsername();
        const role = this.getUserRole();
        
        console.log('Utilisateur connecté:', username, 'Rôle:', role);
        
        // Injecter le menu dynamique si un conteneur existe
        const menuContainer = document.querySelector('.sidebar');
        if (menuContainer) {
            const logo = menuContainer.querySelector('.logo');
            const logoutBtn = menuContainer.querySelector('#logoutBtn');
            
            menuContainer.innerHTML = '';
            if (logo) menuContainer.appendChild(logo);
            menuContainer.innerHTML += this.generateMenu();
            if (logoutBtn) menuContainer.appendChild(logoutBtn);
        }
    }
};

// Initialiser automatiquement sur chaque page
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname !== '/login.html') {
        RoleManager.initializePage();
    }
});
