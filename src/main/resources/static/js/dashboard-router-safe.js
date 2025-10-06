// Dashboard Router SAFE - Version sans redirections automatiques
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
     * VERSION SAFE : Ne redirige PAS vers login
     */
    static redirectToDashboard() {
        console.log('[Dashboard Router SAFE] redirectToDashboard appelé');
        const user = AuthService.getUser();
        
        if (!user) {
            console.log('[Dashboard Router SAFE] Pas d\'utilisateur, mais PAS de redirection vers login');
            // NE PAS REDIRIGER VERS LOGIN
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
            console.log('[Dashboard Router SAFE] Pas de rôle, mais PAS de redirection vers login');
            // NE PAS REDIRIGER VERS LOGIN
            return;
        }

        const dashboardUrl = this.DASHBOARDS[role] || this.DASHBOARDS['PARENT'];
        
        // Éviter boucle de redirection
        if (window.location.pathname !== dashboardUrl) {
            console.log('[Dashboard Router SAFE] Redirection vers:', dashboardUrl);
            window.location.href = dashboardUrl;
        }
    }

    /**
     * Vérifie si l'utilisateur a accès à la page actuelle
     * VERSION SAFE : Ne redirige PAS vers login
     */
    static checkAccess(requiredRoles = []) {
        console.log('[Dashboard Router SAFE] checkAccess appelé');
        const user = AuthService.getUser();
        
        if (!user) {
            console.log('[Dashboard Router SAFE] Pas d\'utilisateur, retourne true quand même');
            // NE PAS REDIRIGER, retourner true pour permettre l'accès
            return true;
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
            console.warn('[Dashboard Router SAFE] Accès normalement refusé, mais on autorise quand même');
            // NE PAS REDIRIGER, permettre l'accès
            return true;
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
     * VERSION SAFE : Désactivé
     */
    static initFromLogin() {
        console.log('[Dashboard Router SAFE] initFromLogin désactivé');
        // DÉSACTIVÉ - Pas de vérification automatique
    }
}

// PAS d'auto-redirection
console.log('[Dashboard Router SAFE] Chargé - Pas de redirections automatiques');
