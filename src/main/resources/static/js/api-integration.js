/**
 * Module d'intégration API pour connecter le frontend aux endpoints backend
 * Gère toutes les communications avec les services REST
 */

class APIIntegration {
    constructor() {
        this.baseURL = '/api';
        this.token = localStorage.getItem('ecolegest_token');
    }

    // Headers par défaut pour toutes les requêtes
    getHeaders() {
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.token}`
        };
    }

    // Gestion des erreurs
    async handleResponse(response) {
        if (!response.ok) {
            let errorMessage = `Erreur HTTP: ${response.status}`;
            try {
                const error = await response.json();
                console.error('[API] Détails de l\'erreur:', error);
                errorMessage = error.message || error.error || errorMessage;
            } catch (e) {
                console.error('[API] Impossible de parser l\'erreur:', e);
            }
            throw new Error(errorMessage);
        }
        return response.json();
    }

    // ========== ÉLÈVES ==========
    
    async getEleves() {
        try {
            const response = await fetch(`${this.baseURL}/eleves`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération élèves:', error);
            throw error;
        }
    }

    async getEleveById(id) {
        try {
            const response = await fetch(`${this.baseURL}/eleves/${id}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération élève:', error);
            throw error;
        }
    }

    async createEleve(eleveData) {
        try {
            console.log('[API] Données envoyées pour création élève:', eleveData);
            const response = await fetch(`${this.baseURL}/eleves`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(eleveData)
            });
            const result = await this.handleResponse(response);
            console.log('[API] Élève créé avec succès:', result);
            return result;
        } catch (error) {
            console.error('[API] Erreur création élève:', error);
            throw error;
        }
    }

    async updateEleve(id, eleveData) {
        try {
            const response = await fetch(`${this.baseURL}/eleves/${id}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                body: JSON.stringify(eleveData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur mise à jour élève:', error);
            throw error;
        }
    }

    async deleteEleve(id) {
        try {
            const response = await fetch(`${this.baseURL}/eleves/${id}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });
            if (!response.ok) {
                throw new Error(`Erreur suppression: ${response.status}`);
            }
            return true;
        } catch (error) {
            console.error('[API] Erreur suppression élève:', error);
            throw error;
        }
    }

    async searchEleves(query) {
        try {
            const response = await fetch(`${this.baseURL}/eleves/search?q=${encodeURIComponent(query)}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur recherche élèves:', error);
            throw error;
        }
    }

    // ========== CLASSES ==========
    
    async getClasses() {
        try {
            const response = await fetch(`${this.baseURL}/classes`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération classes:', error);
            throw error;
        }
    }

    async getClasseById(id) {
        try {
            const response = await fetch(`${this.baseURL}/classes/${id}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération classe:', error);
            throw error;
        }
    }

    async createClasse(classeData) {
        try {
            const response = await fetch(`${this.baseURL}/classes`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(classeData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur création classe:', error);
            throw error;
        }
    }

    async updateClasse(id, classeData) {
        try {
            const response = await fetch(`${this.baseURL}/classes/${id}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                body: JSON.stringify(classeData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur mise à jour classe:', error);
            throw error;
        }
    }

    async deleteClasse(id) {
        try {
            const response = await fetch(`${this.baseURL}/classes/${id}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });
            if (!response.ok) {
                throw new Error(`Erreur suppression: ${response.status}`);
            }
            return true;
        } catch (error) {
            console.error('[API] Erreur suppression classe:', error);
            throw error;
        }
    }

    async getElevesByClasse(classeId) {
        try {
            const response = await fetch(`${this.baseURL}/classes/${classeId}/eleves`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération élèves de la classe:', error);
            throw error;
        }
    }

    // ========== PARENTS ==========
    
    async getParents() {
        try {
            const response = await fetch(`${this.baseURL}/parents`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération parents:', error);
            throw error;
        }
    }

    async getParentById(id) {
        try {
            const response = await fetch(`${this.baseURL}/parents/${id}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération parent:', error);
            throw error;
        }
    }

    async createParent(parentData) {
        try {
            const response = await fetch(`${this.baseURL}/parents`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(parentData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur création parent:', error);
            throw error;
        }
    }

    async updateParent(id, parentData) {
        try {
            const response = await fetch(`${this.baseURL}/parents/${id}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                body: JSON.stringify(parentData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur mise à jour parent:', error);
            throw error;
        }
    }

    async deleteParent(id) {
        try {
            const response = await fetch(`${this.baseURL}/parents/${id}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });
            if (!response.ok) {
                throw new Error(`Erreur suppression: ${response.status}`);
            }
            return true;
        } catch (error) {
            console.error('[API] Erreur suppression parent:', error);
            throw error;
        }
    }

    async getEnfantsByParent(parentId) {
        try {
            const response = await fetch(`${this.baseURL}/parents/${parentId}/enfants`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération enfants:', error);
            throw error;
        }
    }

    // ========== PAIEMENTS ==========
    
    async getPaiements() {
        try {
            const response = await fetch(`${this.baseURL}/paiements`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération paiements:', error);
            throw error;
        }
    }

    async getPaiementById(id) {
        try {
            const response = await fetch(`${this.baseURL}/paiements/${id}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération paiement:', error);
            throw error;
        }
    }

    async createPaiement(paiementData) {
        try {
            const response = await fetch(`${this.baseURL}/paiements`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(paiementData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur création paiement:', error);
            throw error;
        }
    }

    async updatePaiement(id, paiementData) {
        try {
            const response = await fetch(`${this.baseURL}/paiements/${id}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                body: JSON.stringify(paiementData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur mise à jour paiement:', error);
            throw error;
        }
    }

    async annulerPaiement(id) {
        try {
            const response = await fetch(`${this.baseURL}/paiements/${id}/annuler`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur annulation paiement:', error);
            throw error;
        }
    }

    async getPaiementsByEleve(eleveId) {
        try {
            const response = await fetch(`${this.baseURL}/paiements/eleve/${eleveId}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération paiements élève:', error);
            throw error;
        }
    }

    async getPaiementStats() {
        try {
            const response = await fetch(`${this.baseURL}/paiements/stats`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération stats paiements:', error);
            throw error;
        }
    }

    async genererRecu(paiementId) {
        try {
            const response = await fetch(`${this.baseURL}/paiements/${paiementId}/recu`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur génération reçu:', error);
            throw error;
        }
    }

    // ========== ENSEIGNANTS ==========
    
    async getEnseignants() {
        try {
            const response = await fetch(`${this.baseURL}/enseignants`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération enseignants:', error);
            throw error;
        }
    }

    async getEnseignantById(id) {
        try {
            const response = await fetch(`${this.baseURL}/enseignants/${id}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération enseignant:', error);
            throw error;
        }
    }

    async createEnseignant(enseignantData) {
        try {
            const response = await fetch(`${this.baseURL}/enseignants`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(enseignantData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur création enseignant:', error);
            throw error;
        }
    }

    async updateEnseignant(id, enseignantData) {
        try {
            const response = await fetch(`${this.baseURL}/enseignants/${id}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                body: JSON.stringify(enseignantData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur mise à jour enseignant:', error);
            throw error;
        }
    }

    async deleteEnseignant(id) {
        try {
            const response = await fetch(`${this.baseURL}/enseignants/${id}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });
            if (!response.ok) {
                throw new Error(`Erreur suppression: ${response.status}`);
            }
            return true;
        } catch (error) {
            console.error('[API] Erreur suppression enseignant:', error);
            throw error;
        }
    }

    // ========== DÉROGATIONS ==========
    
    async getDerogations() {
        try {
            const response = await fetch(`${this.baseURL}/derogations`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération dérogations:', error);
            throw error;
        }
    }

    async getDerogationById(id) {
        try {
            const response = await fetch(`${this.baseURL}/derogations/${id}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération dérogation:', error);
            throw error;
        }
    }

    async createDerogation(derogationData) {
        try {
            const response = await fetch(`${this.baseURL}/derogations`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(derogationData)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur création dérogation:', error);
            throw error;
        }
    }

    async approuverDerogation(id) {
        try {
            const response = await fetch(`${this.baseURL}/derogations/${id}/approuver`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur approbation dérogation:', error);
            throw error;
        }
    }

    async rejeterDerogation(id, motif) {
        try {
            const response = await fetch(`${this.baseURL}/derogations/${id}/rejeter`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify({ motif })
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur rejet dérogation:', error);
            throw error;
        }
    }

    // ========== DASHBOARD & STATISTIQUES ==========
    
    async getDashboardStats() {
        try {
            const response = await fetch(`${this.baseURL}/dashboard/stats`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération stats dashboard:', error);
            throw error;
        }
    }

    async getActivitesRecentes() {
        try {
            const response = await fetch(`${this.baseURL}/dashboard/activites`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération activités:', error);
            throw error;
        }
    }

    // ========== NOTIFICATIONS ==========
    
    async getNotifications() {
        try {
            const response = await fetch(`${this.baseURL}/notifications`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur récupération notifications:', error);
            throw error;
        }
    }

    async markNotificationAsRead(id) {
        try {
            const response = await fetch(`${this.baseURL}/notifications/${id}/read`, {
                method: 'POST',
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur marquage notification:', error);
            throw error;
        }
    }

    // ========== UPLOAD DE FICHIERS ==========
    
    async uploadFile(file, type = 'document') {
        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('type', type);

            const response = await fetch(`${this.baseURL}/upload`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.token}`
                    // Ne pas définir Content-Type, le navigateur le fera automatiquement pour FormData
                },
                body: formData
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur upload fichier:', error);
            throw error;
        }
    }

    async uploadPhoto(file, entityType, entityId) {
        try {
            const formData = new FormData();
            formData.append('photo', file);

            const response = await fetch(`${this.baseURL}/${entityType}/${entityId}/photo`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.token}`
                },
                body: formData
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur upload photo:', error);
            throw error;
        }
    }

    // ========== RECHERCHE GLOBALE ==========
    
    async searchGlobal(query) {
        try {
            const response = await fetch(`${this.baseURL}/search?q=${encodeURIComponent(query)}`, {
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('[API] Erreur recherche globale:', error);
            throw error;
        }
    }

    // ========== EXPORT ==========
    
    async exportData(type, format = 'excel') {
        try {
            const response = await fetch(`${this.baseURL}/export/${type}?format=${format}`, {
                headers: this.getHeaders()
            });
            
            if (!response.ok) {
                throw new Error(`Erreur export: ${response.status}`);
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `export_${type}_${new Date().toISOString().split('T')[0]}.${format === 'excel' ? 'xlsx' : format}`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            
            return true;
        } catch (error) {
            console.error('[API] Erreur export:', error);
            throw error;
        }
    }
}

// Instance globale
const API = new APIIntegration();

// Exposer globalement
window.API = API;
