// Configuration de l'API
const API_BASE_URL = '';  // Pas de context-path
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
    
    getUserId() {
        const user = this.getUser();
        return user ? user.id : null;
    },
    
    setUser(user) {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    },
    
    isAuthenticated() {
        return !!this.getToken();
    },
    
    async login(username, password) {
        try {
            console.log('Tentative de connexion pour:', username);
            
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });
            
            console.log('Response status:', response.status);
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('Erreur login:', errorText);
                throw new Error('Identifiants invalides');
            }
            
            const data = await response.json();
            console.log('Login response:', data);
            
            // Vérifier et sauvegarder le token
            if (data.accessToken) {
                this.setToken(data.accessToken);
                console.log('Token sauvegardé');
            } else {
                console.error('Pas de accessToken dans la réponse');
            }
            
            // Sauvegarder l'utilisateur
            if (data.user) {
                this.setUser(data.user);
                console.log('User sauvegardé:', data.user);
            } else {
                console.error('Pas de user dans la réponse');
            }
            
            return data;
        } catch (error) {
            console.error('Erreur de connexion:', error);
            throw error;
        }
    },
    
    logout() {
        this.removeToken();
        // DÉSACTIVÉ - Ne pas rediriger vers login
        console.log('[API] Logout appelé mais redirection désactivée');
        // window.location.href = '/login.html';
    }
};

// Fonction générique pour les appels API
async function apiCall(endpoint, options = {}) {
    const token = AuthService.getToken();
    
    // TEMPORAIRE: Ne pas envoyer le token car les APIs sont en permitAll()
    const defaultHeaders = {
        'Content-Type': 'application/json'
        // ...(token && { 'Authorization': `Bearer ${token}` })
    };
    
    // Pour debug: toujours envoyer le token s'il existe, mais ne pas échouer s'il n'y en a pas
    if (token) {
        defaultHeaders['Authorization'] = `Bearer ${token}`;
        console.log('[API] Token inclus dans la requête');
    } else {
        console.log('[API] Pas de token - Mode sans authentification');
    }
    
    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers
        }
    };
    
    try {
        console.log(`[API] Appel: ${endpoint}`, { token: token ? 'Présent' : 'Absent' });
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        
        if (response.status === 401) {
            console.error('[API] Erreur 401 - Non authentifié');
            // TEMPORAIRE: Ne pas lancer d'erreur car les APIs sont en permitAll()
            // AuthService.logout();
            // throw new Error('Session expirée. Veuillez vous reconnecter.');
            console.warn('[API] Ignorer erreur 401 - APIs en mode permitAll()');
        }
        
        if (!response.ok) {
            let errorMessage = `Erreur HTTP ${response.status}`;
            try {
                const error = await response.json();
                errorMessage = error.message || error.error || errorMessage;
            } catch (e) {
                // Si le parsing JSON échoue, garder le message par défaut
            }
            throw new Error(errorMessage);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        
        // Si ce n'est pas du JSON, retourner un objet vide plutôt que la réponse brute
        console.warn('Réponse non-JSON reçue');
        return {};
    } catch (error) {
        console.error('Erreur API:', error);
        throw error;
    }
}

// API des Élèves
const EleveAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/eleves?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/eleves/${id}`);
    },
    
    async create(eleve) {
        return apiCall('/api/eleves', {
            method: 'POST',
            body: JSON.stringify(eleve)
        });
    },
    
    async update(id, eleve) {
        return apiCall(`/api/eleves/${id}`, {
            method: 'PUT',
            body: JSON.stringify(eleve)
        });
    },
    
    async delete(id) {
        return apiCall(`/api/eleves/${id}`, {
            method: 'DELETE'
        });
    },
    
    async search(query) {
        return apiCall(`/api/eleves/search?query=${encodeURIComponent(query)}`);
    },
    
    async getStatistiques() {
        return apiCall('/api/eleves/statistiques');
    },
    
    async getByClasse(classeId) {
        return apiCall(`/api/eleves/classe/${classeId}`);
    },
    
    async getByParent(parentId) {
        return apiCall(`/api/eleves/parent/${parentId}`);
    }
};

// API des Parents
const ParentAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/parents?page=${page}&size=${size}`);
    },
    
    async getAllNoPagination() {
        return apiCall('/api/parents/all');
    },
    
    async getById(id) {
        return apiCall(`/api/parents/${id}`);
    },
    
    async create(parent) {
        return apiCall('/api/parents', {
            method: 'POST',
            body: JSON.stringify(parent)
        });
    },
    
    async update(id, parent) {
        return apiCall(`/api/parents/${id}`, {
            method: 'PUT',
            body: JSON.stringify(parent)
        });
    },
    
    async delete(id) {
        return apiCall(`/api/parents/${id}`, {
            method: 'DELETE'
        });
    },
    
    async search(query) {
        return apiCall(`/api/parents/search?search=${encodeURIComponent(query)}`);
    },
    
    async getByType(typeParent) {
        return apiCall(`/api/parents/type/${typeParent}`);
    },
    
    async getByEleveId(eleveId) {
        return apiCall(`/api/parents/eleve/${eleveId}`);
    },
    
    async addEnfant(parentId, eleveId) {
        return apiCall(`/api/parents/${parentId}/enfants/${eleveId}`, {
            method: 'POST'
        });
    },
    
    async removeEnfant(parentId, eleveId) {
        return apiCall(`/api/parents/${parentId}/enfants/${eleveId}`, {
            method: 'DELETE'
        });
    },
    
    async checkEmailExists(email) {
        return apiCall(`/api/parents/exists/email/${encodeURIComponent(email)}`);
    },
    
    async checkCinExists(cin) {
        return apiCall(`/api/parents/exists/cin/${encodeURIComponent(cin)}`);
    },
    
    async getCount() {
        return apiCall('/api/parents/count');
    },
    
    async generateNumero() {
        return apiCall('/api/parents/generate-numero');
    },
    
    async getSituationFinanciere(parentId) {
        return apiCall(`/api/parents/${parentId}/situation-financiere`);
    }
};

// API des Paiements
const PaiementAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/paiements?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/paiements/${id}`);
    },
    
    async create(paiement) {
        return apiCall('/api/paiements', {
            method: 'POST',
            body: JSON.stringify(paiement)
        });
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/api/paiements/eleve/${eleveId}`);
    },
    
    async getStatistiques() {
        return apiCall('/api/paiements/statistiques');
    },
    
    async getRecents(limit = 10) {
        return apiCall(`/api/paiements/recents?limit=${limit}`);
    },
    
    async telechargerRecu(paiementId) {
        const response = await fetch(`${API_BASE_URL}/api/paiements/${paiementId}/recu`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${AuthService.getToken()}`
            }
        });
        if (!response.ok) throw new Error('Erreur téléchargement');
        return await response.blob();
    },
    
    async validerPaiement(paiementId) {
        return apiCall(`/api/paiements/${paiementId}/valider`, {
            method: 'POST'
        });
    },
    
    async annulerPaiement(paiementId) {
        return apiCall(`/api/paiements/${paiementId}/annuler`, {
            method: 'POST'
        });
    },
    
    async getPaiementsEnAttente(page = 0, size = 20) {
        return apiCall(`/api/paiements/en-attente?page=${page}&size=${size}`);
    },
    
    // Méthodes pour dashboards
    async getSituationFinanciere(parentId) {
        return apiCall(`/api/paiements/parent/${parentId}/situation`);
    },
    
    async getStatistiquesComptable() {
        return apiCall('/api/paiements/statistiques-comptable');
    },
    
    async getStatistiquesParMode() {
        return apiCall('/api/paiements/statistiques-par-mode');
    }
};

// API des Dérogations
const DerogationAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/derogations?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/derogations/${id}`);
    },
    
    async create(derogation) {
        return apiCall('/api/derogations', {
            method: 'POST',
            body: JSON.stringify(derogation)
        });
    },
    
    async update(id, derogation) {
        return apiCall(`/api/derogations/${id}`, {
            method: 'PUT',
            body: JSON.stringify(derogation)
        });
    },
    
    async delete(id) {
        return apiCall(`/api/derogations/${id}`, {
            method: 'DELETE'
        });
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/api/derogations/eleve/${eleveId}`);
    },
    
    async getByParent(parentId) {
        return apiCall(`/api/derogations/parent/${parentId}`);
    },
    
    async getByStatut(statut, page = 0, size = 20) {
        return apiCall(`/api/derogations/statut/${statut}?page=${page}&size=${size}`);
    },
    
    async approuver(id, decidePar) {
        return apiCall(`/api/derogations/${id}/approuver?decidePar=${encodeURIComponent(decidePar)}`, {
            method: 'POST'
        });
    },
    
    async rejeter(id, decidePar, motifRejet) {
        return apiCall(`/api/derogations/${id}/rejeter?decidePar=${encodeURIComponent(decidePar)}&motifRejet=${encodeURIComponent(motifRejet)}`, {
            method: 'POST'
        });
    },
    
    async generateNumero() {
        return apiCall('/api/derogations/generate-numero');
    },
    
    async countParStatut() {
        return apiCall('/api/derogations/count-par-statut');
    }
};

// API de Gestion des Utilisateurs
const UserManagementAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/users?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/users/${id}`);
    },
    
    async create(user) {
        return apiCall('/api/users', {
            method: 'POST',
            body: JSON.stringify(user)
        });
    },
    
    async update(id, user) {
        return apiCall(`/api/users/${id}`, {
            method: 'PUT',
            body: JSON.stringify(user)
        });
    },
    
    async delete(id) {
        return apiCall(`/api/users/${id}`, {
            method: 'DELETE'
        });
    },
    
    async activate(id) {
        return apiCall(`/api/users/${id}/activate`, {
            method: 'PUT'
        });
    },
    
    async deactivate(id) {
        return apiCall(`/api/users/${id}/deactivate`, {
            method: 'PUT'
        });
    },
    
    async changePassword(id, newPassword) {
        return apiCall(`/api/users/${id}/change-password`, {
            method: 'PUT',
            body: JSON.stringify({ newPassword })
        });
    },
    
    async changeRole(id, role) {
        return apiCall(`/api/users/${id}/change-role`, {
            method: 'PUT',
            body: JSON.stringify({ role })
        });
    },
    
    async search(query, page = 0, size = 10) {
        return apiCall(`/api/users/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
    },
    
    async getByRole(role, page = 0, size = 10) {
        return apiCall(`/api/users/by-role/${role}?page=${page}&size=${size}`);
    },
    
    async getStatistics() {
        return apiCall('/api/users/statistics');
    }
};

// API des Classes
const ClasseAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/classes?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/classes/${id}`);
    },
    
    async create(classe) {
        return apiCall('/api/classes', {
            method: 'POST',
            body: JSON.stringify(classe)
        });
    },
    
    async update(id, classe) {
        return apiCall(`/api/classes/${id}`, {
            method: 'PUT',
            body: JSON.stringify(classe)
        });
    },
    
    async delete(id) {
        return apiCall(`/api/classes/${id}`, {
            method: 'DELETE'
        });
    },
    
    async search(query) {
        return apiCall(`/api/classes/search?query=${encodeURIComponent(query)}`);
    },
    
    async getEleves(id) {
        return apiCall(`/api/classes/${id}/eleves`);
    }
};

// API des Niveaux
const NiveauAPI = {
    async getAll() {
        return apiCall('/api/niveaux');
    },
    
    async getById(id) {
        return apiCall(`/api/niveaux/${id}`);
    }
};

// API de Communication
const CommunicationAPI = {
    // Messages
    async getMessagesRecus(page = 0, size = 20) {
        return apiCall(`/api/communication/messages/recus?page=${page}&size=${size}`);
    },
    
    async getMessagesEnvoyes(page = 0, size = 20) {
        return apiCall(`/api/communication/messages/envoyes?page=${page}&size=${size}`);
    },
    
    async getMessagesNonLus(page = 0, size = 20) {
        return apiCall(`/api/communication/messages/non-lus?page=${page}&size=${size}`);
    },
    
    async getMessage(id) {
        return apiCall(`/api/communication/messages/${id}`);
    },
    
    async envoyerMessage(messageData, files = null) {
        const formData = new FormData();
        formData.append('message', new Blob([JSON.stringify(messageData)], { type: 'application/json' }));
        if (files) {
            files.forEach(file => formData.append('files', file));
        }
        return apiCall('/api/communication/messages', {
            method: 'POST',
            body: formData,
            headers: {} // Let browser set Content-Type for FormData
        });
    },
    
    async marquerMessageCommeLu(id) {
        return apiCall(`/api/communication/messages/${id}/lire`, { method: 'PUT' });
    },
    
    async archiverMessage(id) {
        return apiCall(`/api/communication/messages/${id}/archiver`, { method: 'PUT' });
    },
    
    async supprimerMessage(id) {
        return apiCall(`/api/communication/messages/${id}`, { method: 'DELETE' });
    },
    
    async countMessagesNonLus() {
        return apiCall('/api/communication/messages/count-non-lus');
    },
    
    // Notifications
    async getNotifications(page = 0, size = 20) {
        return apiCall(`/api/communication/notifications?page=${page}&size=${size}`);
    },
    
    async getNotificationsNonLues(page = 0, size = 20) {
        return apiCall(`/api/communication/notifications/non-lues?page=${page}&size=${size}`);
    },
    
    async getNotification(id) {
        return apiCall(`/api/communication/notifications/${id}`);
    },
    
    async marquerNotificationCommeLue(id) {
        return apiCall(`/api/communication/notifications/${id}/lire`, { method: 'PUT' });
    },
    
    async marquerToutesNotificationsLues() {
        return apiCall('/api/communication/notifications/lire-toutes', { method: 'PUT' });
    },
    
    async countNotificationsNonLues() {
        return apiCall('/api/communication/notifications/count-non-lues');
    },
    
    // Annonces
    async getAnnoncesActives(page = 0, size = 20) {
        return apiCall(`/api/communication/annonces?page=${page}&size=${size}`);
    },
    
    async getAnnoncesEpinglees(page = 0, size = 20) {
        return apiCall(`/api/communication/annonces/epinglees?page=${page}&size=${size}`);
    },
    
    async getAnnonce(id) {
        return apiCall(`/api/communication/annonces/${id}`);
    },
    
    async creerAnnonce(annonceData, files = null) {
        const formData = new FormData();
        formData.append('annonce', new Blob([JSON.stringify(annonceData)], { type: 'application/json' }));
        if (files) {
            files.forEach(file => formData.append('files', file));
        }
        return apiCall('/api/communication/annonces', {
            method: 'POST',
            body: formData,
            headers: {}
        });
    },
    
    async modifierAnnonce(id, annonceData) {
        return apiCall(`/api/communication/annonces/${id}`, {
            method: 'PUT',
            body: JSON.stringify(annonceData)
        });
    },
    
    async epinglerAnnonce(id) {
        return apiCall(`/api/communication/annonces/${id}/epingler`, { method: 'PUT' });
    },
    
    async supprimerAnnonce(id) {
        return apiCall(`/api/communication/annonces/${id}`, { method: 'DELETE' });
    },
    
    async countAnnoncesActives() {
        return apiCall('/api/communication/annonces/count');
    },
    
    async searchAnnonces(recherche, page = 0, size = 20) {
        return apiCall(`/api/communication/annonces/search?recherche=${encodeURIComponent(recherche)}&page=${page}&size=${size}`);
    }
};

// API des Factures
const FactureAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/factures?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/factures/${id}`);
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/api/factures/eleve/${eleveId}`);
    },
    
    async getImpayees() {
        return apiCall('/api/factures/impayees');
    }
};

// API des Années Scolaires
const AnneeScolaireAPI = {
    async getAll() {
        return apiCall('/api/annees-scolaires');
    },
    
    async getActive() {
        return apiCall('/api/annees-scolaires/active');
    },
    
    async getById(id) {
        return apiCall(`/api/annees-scolaires/${id}`);
    }
};

// API du Dashboard
const DashboardAPI = {
    async getStatistiques() {
        return apiCall('/api/dashboard/statistiques');
    },
    
    async getActivitesRecentes() {
        return apiCall('/api/dashboard/activites-recentes');
    }
};

// API des Enseignants
const EnseignantAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/enseignants?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/enseignants/${id}`);
    },
    
    async create(enseignant) {
        return apiCall('/api/enseignants', {
            method: 'POST',
            body: JSON.stringify(enseignant)
        });
    },
    
    async update(id, enseignant) {
        return apiCall(`/api/enseignants/${id}`, {
            method: 'PUT',
            body: JSON.stringify(enseignant)
        });
    },
    
    async delete(id) {
        return apiCall(`/api/enseignants/${id}`, {
            method: 'DELETE'
        });
    },
    
    // Méthodes pour le dashboard enseignant
    async getStatistiques(enseignantId) {
        return apiCall(`/api/enseignants/${enseignantId}/statistiques`);
    },
    
    async getClasses(enseignantId) {
        return apiCall(`/api/enseignants/${enseignantId}/classes`);
    },
    
    async getPerformances(enseignantId) {
        return apiCall(`/api/enseignants/${enseignantId}/performances`);
    },
    
    async getElevesDifficulte(enseignantId) {
        return apiCall(`/api/enseignants/${enseignantId}/eleves-difficulte`);
    },
    
    async getProchainsCours(enseignantId) {
        return apiCall(`/api/enseignants/${enseignantId}/prochains-cours`);
    }
};

// API des Bourses
const BourseAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/bourses?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/bourses/${id}`);
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/api/bourses/eleve/${eleveId}`);
    }
};

// API des Messages
const MessageAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/messages?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/messages/${id}`);
    },
    
    async send(message) {
        return apiCall('/api/messages', {
            method: 'POST',
            body: JSON.stringify(message)
        });
    },
    
    async getConversations() {
        return apiCall('/api/messages/conversations');
    }
};

// API des Notifications  
const NotificationAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/notifications?page=${page}&size=${size}`);
    },
    
    async getUnread() {
        return apiCall('/api/notifications/unread');
    },
    
    async markAsRead(id) {
        return apiCall(`/api/notifications/${id}/read`, {
            method: 'PUT'
        });
    },
    
    async markAllAsRead() {
        return apiCall('/api/notifications/mark-all-read', {
            method: 'PUT'
        });
    }
};

// API des Annonces
const AnnonceAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/api/annonces?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/api/annonces/${id}`);
    },
    
    async create(annonce) {
        return apiCall('/api/annonces', {
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

// Exposer les APIs globalement
window.AuthService = AuthService;
window.EleveAPI = EleveAPI;
window.ClasseAPI = ClasseAPI;
window.PaiementAPI = PaiementAPI;
window.UserAPI = UserAPI;
window.UIHelper = UIHelper;

// DÉSACTIVÉ - Utiliser auth-check.js à la place
// La vérification d'authentification a été déplacée dans auth-check.js
// pour éviter les problèmes de redirection en boucle
/*
document.addEventListener('DOMContentLoaded', () => {
    // Code désactivé - voir auth-check.js
});
*/
