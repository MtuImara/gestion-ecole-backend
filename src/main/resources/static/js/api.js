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
            
            const response = await fetch('/auth/login', {
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
    },
    
    async generateMatricule() {
        return apiCall('/eleves/generate-matricule');
    },
    
    async filter(classeId, anneeScolaireId, statut, page = 0, size = 10) {
        let url = `/eleves/filter?page=${page}&size=${size}`;
        if (classeId) url += `&classeId=${classeId}`;
        if (anneeScolaireId) url += `&anneeScolaireId=${anneeScolaireId}`;
        if (statut) url += `&statut=${statut}`;
        return apiCall(url);
    },
    
    // Méthodes pour le dashboard parent
    async getByParent(parentId) {
        return apiCall(`/eleves/parent/${parentId}`);
    },
    
    async getPerformances(parentId) {
        return apiCall(`/eleves/parent/${parentId}/performances`);
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
    },
    
    async telechargerRecu(paiementId) {
        const response = await fetch(`${API_BASE_URL}/paiements/${paiementId}/recu`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${AuthService.getToken()}`
            }
        });
        if (!response.ok) throw new Error('Erreur téléchargement');
        return await response.blob();
    },
    
    async validerPaiement(paiementId) {
        return apiCall(`/paiements/${paiementId}/valider`, {
            method: 'POST'
        });
    },
    
    async annulerPaiement(paiementId) {
        return apiCall(`/paiements/${paiementId}/annuler`, {
            method: 'POST'
        });
    },
    
    async getPaiementsEnAttente(page = 0, size = 20) {
        return apiCall(`/paiements/en-attente?page=${page}&size=${size}`);
    },
    
    // Méthodes pour dashboards
    async getSituationFinanciere(parentId) {
        return apiCall(`/paiements/parent/${parentId}/situation`);
    },
    
    async getStatistiquesComptable() {
        return apiCall('/paiements/statistiques-comptable');
    },
    
    async getStatistiquesParMode() {
        return apiCall('/paiements/statistiques-par-mode');
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
    },
    
    async update(id, derogation) {
        return apiCall(`/derogations/${id}`, {
            method: 'PUT',
            body: JSON.stringify(derogation)
        });
    },
    
    async delete(id) {
        return apiCall(`/derogations/${id}`, {
            method: 'DELETE'
        });
    },
    
    async getByEleve(eleveId) {
        return apiCall(`/derogations/eleve/${eleveId}`);
    },
    
    async getByParent(parentId) {
        return apiCall(`/derogations/parent/${parentId}`);
    },
    
    async getByStatut(statut, page = 0, size = 20) {
        return apiCall(`/derogations/statut/${statut}?page=${page}&size=${size}`);
    },
    
    async approuver(id, decidePar) {
        return apiCall(`/derogations/${id}/approuver?decidePar=${encodeURIComponent(decidePar)}`, {
            method: 'POST'
        });
    },
    
    async rejeter(id, decidePar, motifRejet) {
        return apiCall(`/derogations/${id}/rejeter?decidePar=${encodeURIComponent(decidePar)}&motifRejet=${encodeURIComponent(motifRejet)}`, {
            method: 'POST'
        });
    },
    
    async generateNumero() {
        return apiCall('/derogations/generate-numero');
    },
    
    async countParStatut() {
        return apiCall('/derogations/count-par-statut');
    }
};

// API de Gestion des Utilisateurs
const UserManagementAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/users?page=${page}&size=${size}`);
    },
    
    async getById(id) {
        return apiCall(`/users/${id}`);
    },
    
    async create(user) {
        return apiCall('/users', {
            method: 'POST',
            body: JSON.stringify(user)
        });
    },
    
    async update(id, user) {
        return apiCall(`/users/${id}`, {
            method: 'PUT',
            body: JSON.stringify(user)
        });
    },
    
    async delete(id) {
        return apiCall(`/users/${id}`, {
            method: 'DELETE'
        });
    },
    
    async activate(id) {
        return apiCall(`/users/${id}/activate`, {
            method: 'PUT'
        });
    },
    
    async deactivate(id) {
        return apiCall(`/users/${id}/deactivate`, {
            method: 'PUT'
        });
    },
    
    async changePassword(id, newPassword) {
        return apiCall(`/users/${id}/change-password`, {
            method: 'PUT',
            body: JSON.stringify({ newPassword })
        });
    },
    
    async changeRole(id, role) {
        return apiCall(`/users/${id}/change-role`, {
            method: 'PUT',
            body: JSON.stringify({ role })
        });
    },
    
    async search(query, page = 0, size = 10) {
        return apiCall(`/users/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
    },
    
    async getByRole(role, page = 0, size = 10) {
        return apiCall(`/users/by-role/${role}?page=${page}&size=${size}`);
    },
    
    async getStatistics() {
        return apiCall('/users/statistics');
    }
};

// API des Classes
const ClasseAPI = {
    async getAll(page = 0, size = 10) {
        return apiCall(`/classes?page=${page}&size=${size}`);
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
    
    async search(query) {
        return apiCall(`/classes/search?query=${encodeURIComponent(query)}`);
    },
    
    async getEleves(id) {
        return apiCall(`/classes/${id}/eleves`);
    }
};

// API des Niveaux
const NiveauAPI = {
    async getAll() {
        return apiCall('/niveaux');
    },
    
    async getById(id) {
        return apiCall(`/niveaux/${id}`);
    }
};

// API de Communication
const CommunicationAPI = {
    // Messages
    async getMessagesRecus(page = 0, size = 20) {
        return apiCall(`/communication/messages/recus?page=${page}&size=${size}`);
    },
    
    async getMessagesEnvoyes(page = 0, size = 20) {
        return apiCall(`/communication/messages/envoyes?page=${page}&size=${size}`);
    },
    
    async getMessagesNonLus(page = 0, size = 20) {
        return apiCall(`/communication/messages/non-lus?page=${page}&size=${size}`);
    },
    
    async getMessage(id) {
        return apiCall(`/communication/messages/${id}`);
    },
    
    async envoyerMessage(messageData, files = null) {
        const formData = new FormData();
        formData.append('message', new Blob([JSON.stringify(messageData)], { type: 'application/json' }));
        if (files) {
            files.forEach(file => formData.append('files', file));
        }
        return apiCall('/communication/messages', {
            method: 'POST',
            body: formData,
            headers: {} // Let browser set Content-Type for FormData
        });
    },
    
    async marquerMessageCommeLu(id) {
        return apiCall(`/communication/messages/${id}/lire`, { method: 'PUT' });
    },
    
    async archiverMessage(id) {
        return apiCall(`/communication/messages/${id}/archiver`, { method: 'PUT' });
    },
    
    async supprimerMessage(id) {
        return apiCall(`/communication/messages/${id}`, { method: 'DELETE' });
    },
    
    async countMessagesNonLus() {
        return apiCall('/communication/messages/count-non-lus');
    },
    
    // Notifications
    async getNotifications(page = 0, size = 20) {
        return apiCall(`/communication/notifications?page=${page}&size=${size}`);
    },
    
    async getNotificationsNonLues(page = 0, size = 20) {
        return apiCall(`/communication/notifications/non-lues?page=${page}&size=${size}`);
    },
    
    async getNotification(id) {
        return apiCall(`/communication/notifications/${id}`);
    },
    
    async marquerNotificationCommeLue(id) {
        return apiCall(`/communication/notifications/${id}/lire`, { method: 'PUT' });
    },
    
    async marquerToutesNotificationsLues() {
        return apiCall('/communication/notifications/lire-toutes', { method: 'PUT' });
    },
    
    async countNotificationsNonLues() {
        return apiCall('/communication/notifications/count-non-lues');
    },
    
    // Annonces
    async getAnnoncesActives(page = 0, size = 20) {
        return apiCall(`/communication/annonces?page=${page}&size=${size}`);
    },
    
    async getAnnoncesEpinglees(page = 0, size = 20) {
        return apiCall(`/communication/annonces/epinglees?page=${page}&size=${size}`);
    },
    
    async getAnnonce(id) {
        return apiCall(`/communication/annonces/${id}`);
    },
    
    async creerAnnonce(annonceData, files = null) {
        const formData = new FormData();
        formData.append('annonce', new Blob([JSON.stringify(annonceData)], { type: 'application/json' }));
        if (files) {
            files.forEach(file => formData.append('files', file));
        }
        return apiCall('/communication/annonces', {
            method: 'POST',
            body: formData,
            headers: {}
        });
    },
    
    async modifierAnnonce(id, annonceData) {
        return apiCall(`/communication/annonces/${id}`, {
            method: 'PUT',
            body: JSON.stringify(annonceData)
        });
    },
    
    async epinglerAnnonce(id) {
        return apiCall(`/communication/annonces/${id}/epingler`, { method: 'PUT' });
    },
    
    async supprimerAnnonce(id) {
        return apiCall(`/communication/annonces/${id}`, { method: 'DELETE' });
    },
    
    async countAnnoncesActives() {
        return apiCall('/communication/annonces/count');
    },
    
    async searchAnnonces(recherche, page = 0, size = 20) {
        return apiCall(`/communication/annonces/search?recherche=${encodeURIComponent(recherche)}&page=${page}&size=${size}`);
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
    },
    
    // Méthodes pour le dashboard enseignant
    async getStatistiques(enseignantId) {
        return apiCall(`/enseignants/${enseignantId}/statistiques`);
    },
    
    async getClasses(enseignantId) {
        return apiCall(`/enseignants/${enseignantId}/classes`);
    },
    
    async getPerformances(enseignantId) {
        return apiCall(`/enseignants/${enseignantId}/performances`);
    },
    
    async getElevesDifficulte(enseignantId) {
        return apiCall(`/enseignants/${enseignantId}/eleves-difficulte`);
    },
    
    async getProchainsCours(enseignantId) {
        return apiCall(`/enseignants/${enseignantId}/prochains-cours`);
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

// DÉSACTIVÉ - Utiliser auth-check.js à la place
// La vérification d'authentification a été déplacée dans auth-check.js
// pour éviter les problèmes de redirection en boucle
/*
document.addEventListener('DOMContentLoaded', () => {
    // Code désactivé - voir auth-check.js
});
*/
