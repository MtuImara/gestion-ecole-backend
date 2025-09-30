// Configuration de l'API
const API_BASE_URL = '/api';
const TOKEN_KEY = 'ecolegest_token';
const USER_KEY = 'ecolegest_user';

// Gestion du token
const AuthService = {
    getToken() {
        return localStorage.getItem(TOKEN_KEY);
    },
    
    setToken(token) {
        localStorage.setItem(TOKEN_KEY, token);
    },
    
    removeToken() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    },
    
    getUser() {
        const user = localStorage.getItem(USER_KEY);
        return user ? JSON.parse(user) : null;
    },
    
    setUser(user) {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    },
    
    isAuthenticated() {
        return !!this.getToken();
    },
    
    async login(username, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });
            
            if (!response.ok) {
                throw new Error('Identifiants invalides');
            }
            
            const data = await response.json();
            this.setToken(data.token);
            this.setUser(data.user);
            return data;
        } catch (error) {
            console.error('Erreur de connexion:', error);
            throw error;
        }
    },
    
    logout() {
        this.removeToken();
        window.location.href = '/login.html';
    }
};

// Fonction générique pour les appels API
async function apiCall(endpoint, options = {}) {
    const token = AuthService.getToken();
    
    const defaultHeaders = {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
    };
    
    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers
        }
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        
        if (response.status === 401) {
            AuthService.logout();
            return;
        }
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erreur API');
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        
        return response;
    } catch (error) {
        console.error('Erreur API:', error);
        throw error;
    }
}

// API des Élèves
const EleveAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/eleves?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/eleves/${id}`);
    },
    
    async create(eleve) {
        return apiCall('/eleves', {
            method: 'POST',
            body: JSON.stringify(eleve)
        });
    },
    
    async update(id, eleve) {
        return apiCall(`/eleves/${id}`, {
            method: 'PUT',
            body: JSON.stringify(eleve)
        });
    },
    
    async delete(id) {
        return apiCall(`/eleves/${id}`, {
            method: 'DELETE'
        });
    },
    
    async search(query) {
        return apiCall(`/eleves/search?query=${encodeURIComponent(query)}`);
    },
    
    async getStatistiques() {
        return apiCall('/eleves/statistiques');
    }
};

// API des Paiements
const PaiementAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/paiements?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/paiements/${id}`);
    },
    
    async create(paiement) {
        return apiCall('/paiements', {
            method: 'POST',
            body: JSON.stringify(paiement)
        });
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/paiements/eleve/${eleveId}`);
    },
    
    async getStatistiques() {
        return apiCall('/paiements/statistiques');
    },
    
    async getRecents(limit = 10) {
        return apiCall(`/paiements/recents?limit=${limit}`);
    }
};

// API des Classes
const ClasseAPI = {
    async getAll() {
        return apiCall('/classes');
    },
    
    async getById(id) {
        return apiCall(`/classes/${id}`);
    },
    
    async create(classe) {
        return apiCall('/classes', {
            method: 'POST',
            body: JSON.stringify(classe)
        });
    },
    
    async update(id, classe) {
        return apiCall(`/classes/${id}`, {
            method: 'PUT',
            body: JSON.stringify(classe)
        });
    },
    
    async delete(id) {
        return apiCall(`/classes/${id}`, {
            method: 'DELETE'
        });
    },
    
    async getEleves(id) {
        return apiCall(`/classes/${id}/eleves`);
    }
};

// API des Factures
const FactureAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/factures?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/factures/${id}`);
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/factures/eleve/${eleveId}`);
    },
    
    async getImpayees() {
        return apiCall('/factures/impayees');
    }
};

// API des Années Scolaires
const AnneeScolaireAPI = {
    async getAll() {
        return apiCall('/annees-scolaires');
    },
    
    async getActive() {
        return apiCall('/annees-scolaires/active');
    },
    
    async getById(id) {
        return apiCall(`/annees-scolaires/${id}`);
    }
};

// API du Dashboard
const DashboardAPI = {
    async getStatistiques() {
        return apiCall('/dashboard/statistiques');
    },
    
    async getActivitesRecentes() {
        return apiCall('/dashboard/activites-recentes');
    }
};

// API des Parents
const ParentAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/parents?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/parents/${id}`);
    },
    
    async create(parent) {
        return apiCall('/parents', {
            method: 'POST',
            body: JSON.stringify(parent)
        });
    },
    
    async update(id, parent) {
        return apiCall(`/parents/${id}`, {
            method: 'PUT',
            body: JSON.stringify(parent)
        });
    },
    
    async delete(id) {
        return apiCall(`/parents/${id}`, {
            method: 'DELETE'
        });
    }
};

// API des Enseignants
const EnseignantAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/enseignants?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/enseignants/${id}`);
    },
    
    async create(enseignant) {
        return apiCall('/enseignants', {
            method: 'POST',
            body: JSON.stringify(enseignant)
        });
    },
    
    async update(id, enseignant) {
        return apiCall(`/enseignants/${id}`, {
            method: 'PUT',
            body: JSON.stringify(enseignant)
        });
    },
    
    async delete(id) {
        return apiCall(`/enseignants/${id}`, {
            method: 'DELETE'
        });
    }
};

// API des Bourses
const BourseAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/bourses?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/bourses/${id}`);
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/bourses/eleve/${eleveId}`);
    }
};

// API des Dérogations
const DerogationAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/derogations?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/derogations/${id}`);
    },
    
    async create(derogation) {
        return apiCall('/derogations', {
            method: 'POST',
            body: JSON.stringify(derogation)
        });
    }
};

// API des Messages
const MessageAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/messages?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/messages/${id}`);
    },
    
    async send(message) {
        return apiCall('/messages', {
            method: 'POST',
            body: JSON.stringify(message)
        });
    },
    
    async getConversations() {
        return apiCall('/messages/conversations');
    }
};

// API des Notifications  
const NotificationAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/notifications?page=${page}&size=${size}`);
    },
    
    async getUnread() {
        return apiCall('/notifications/unread');
    },
    
    async markAsRead(id) {
        return apiCall(`/notifications/${id}/read`, {
            method: 'PUT'
        });
    },
    
    async markAllAsRead() {
        return apiCall('/notifications/mark-all-read', {
            method: 'PUT'
        });
    }
};

// API des Annonces
const AnnonceAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/annonces?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/annonces/${id}`);
    },
    
    async create(annonce) {
        return apiCall('/annonces', {
            method: 'POST',
            body: JSON.stringify(annonce)
        });
    },
    
    async getRecent(limit = 10) {
        return apiCall(`/annonces/recent?limit=${limit}`);
    }
};

// Utilitaires
const Utils = {
    formatMontant(montant) {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'BIF',
            minimumFractionDigits: 0
        }).format(montant);
    },
    
    formatDate(date) {
        return new Date(date).toLocaleDateString('fr-FR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },
    
    formatDateTime(date) {
        return new Date(date).toLocaleString('fr-FR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },
    
    showNotification(message, type = 'success') {
        // Créer une notification toast
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 10px;
            background: ${type === 'success' ? '#27ae60' : '#e74c3c'};
            color: white;
            font-weight: 500;
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
            z-index: 10000;
            animation: slideIn 0.3s ease;
        `;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    },
    
    showLoader() {
        const loader = document.createElement('div');
        loader.id = 'global-loader';
        loader.innerHTML = `
            <div style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 9999;">
                <div style="background: white; padding: 30px; border-radius: 15px; text-align: center;">
                    <div class="spinner"></div>
                    <p style="margin-top: 15px; color: #667eea; font-weight: 500;">Chargement...</p>
                </div>
            </div>
        `;
        document.body.appendChild(loader);
    },
    
    hideLoader() {
        const loader = document.getElementById('global-loader');
        if (loader) loader.remove();
    }
};

// Vérifier l'authentification au chargement
document.addEventListener('DOMContentLoaded', () => {
    const publicPages = ['/login.html', '/register.html'];
    const currentPage = window.location.pathname;
    
    if (!publicPages.includes(currentPage) && !AuthService.isAuthenticated()) {
        window.location.href = '/login.html';
    }
});
