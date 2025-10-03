// Dashboard Router - Redirection intelligente par rôle
class DashboardRouter {
    
    static DASHBOARDS = {
        'ADMIN': '/dashboard.html',
        'DIRECTEUR': '/dashboard.html',
        'COMPTABLE': '/dashboard-comptable.html',
        'ENSEIGNANT': '/dashboard-enseignant.html',
        'PARENT': '/dashboard-parent.html',
        'ELEVE': '/dashboard-eleve.html'
    };

    /**
     * Redirige vers le dashboard approprié selon le rôle de l'utilisateur
     */
    static redirectToDashboard() {
        const user = AuthService.getUser();
        
        if (!user) {
            window.location.href = '/login.html';
            return;
        }

        // Les rôles sont dans un tableau
        let role = null;
        if (user.roles && Array.isArray(user.roles) && user.roles.length > 0) {
            role = user.roles[0];
        } else if (user.role) {
            role = user.role;
        }
        
        console.log('User roles:', user.roles);
        console.log('Selected role:', role);
        
        if (!role) {
            console.error('Aucun rôle trouvé pour l\'utilisateur', user);
            window.location.href = '/login.html';
            return;
        }

        const dashboardUrl = this.DASHBOARDS[role] || this.DASHBOARDS['PARENT'];
        
        // Éviter boucle de redirection
        if (window.location.pathname !== dashboardUrl) {
            window.location.href = dashboardUrl;
        }
    }

    /**
     * Vérifie si l'utilisateur a accès à la page actuelle
     */
    static checkAccess(requiredRoles = []) {
        const user = AuthService.getUser();
        
        if (!user) {
            window.location.href = '/login.html';
            return false;
        }

        if (requiredRoles.length === 0) {
            return true;
        }

        // Les rôles sont dans un tableau
        let userRole = null;
        if (user.roles && Array.isArray(user.roles) && user.roles.length > 0) {
            userRole = user.roles[0];
        } else if (user.role) {
            userRole = user.role;
        }
        
        if (!requiredRoles.includes(userRole)) {
            console.warn('Accès refusé à cette page');
            this.redirectToDashboard();
            return false;
        }

        return true;
    }

    /**
     * Obtient l'URL du dashboard pour un rôle spécifique
     */
    static getDashboardUrl(role) {
        return this.DASHBOARDS[role] || this.DASHBOARDS['PARENT'];
    }

    /**
     * Initialise le routage automatique depuis la page login
     */
    static initFromLogin() {
        // Attendre que l'utilisateur se connecte
        const checkLogin = setInterval(() => {
            const user = AuthService.getUser();
            if (user) {
                clearInterval(checkLogin);
                this.redirectToDashboard();
            }
        }, 500);
    }
}

// Auto-redirection si déjà connecté
if (window.location.pathname === '/login.html' || window.location.pathname === '/') {
    const user = AuthService.getUser();
    if (user) {
        DashboardRouter.redirectToDashboard();
    }
}
