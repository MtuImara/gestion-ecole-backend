/**
 * Module de gestion CRUD des élèves
 */

// Gestionnaire principal des élèves
const ElevesManager = {
    eleves: [],
    currentEleve: null,
    
    // Initialiser
    init() {
        this.loadEleves();
        this.setupEventListeners();
        this.addActionButtons();
    },
    
    // Ajouter les boutons d'action
    addActionButtons() {
        const header = document.querySelector('.header-actions');
        if (header && !document.getElementById('eleveActions')) {
            const actions = document.createElement('div');
            actions.id = 'eleveActions';
            actions.innerHTML = `
                <button onclick="ElevesManager.showAddForm()" style="
                    padding: 8px 16px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">➕ Nouvel élève</button>
            `;
            header.insertBefore(actions, header.firstChild);
        }
    },
    
    // Charger les élèves depuis l'API
    async loadEleves() {
        try {
            // Utiliser l'API d'intégration
            this.eleves = await window.API.getEleves();
            this.displayEleves();
        } catch (error) {
            console.error('Erreur chargement élèves:', error);
            // Fallback sur données de test si l'API n'est pas disponible
            this.eleves = this.getMockData();
            this.displayEleves();
            this.showNotification('Mode hors ligne - Données de démonstration', 'warning');
        }
    },
    
    // Données de test
    getMockData() {
        return [
            { id: 1, matricule: 'ELV001', nom: 'Dupont', prenom: 'Jean', classe: '6A', genre: 'M', email: 'jean.dupont@ecole.com', statut: 'ACTIF' },
            { id: 2, matricule: 'ELV002', nom: 'Martin', prenom: 'Marie', classe: '6A', genre: 'F', email: 'marie.martin@ecole.com', statut: 'ACTIF' },
            { id: 3, matricule: 'ELV003', nom: 'Bernard', prenom: 'Pierre', classe: '5A', genre: 'M', email: 'pierre.bernard@ecole.com', statut: 'ACTIF' }
        ];
    },
    
    // Afficher les élèves
    displayEleves() {
        const container = document.getElementById('elevesTable');
        if (!container) return;
        
        container.innerHTML = `
            <table style="width: 100%; border-collapse: collapse;">
                <thead>
                    <tr style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                        <th style="padding: 12px;">Matricule</th>
                        <th style="padding: 12px;">Nom</th>
                        <th style="padding: 12px;">Prénom</th>
                        <th style="padding: 12px;">Classe</th>
                        <th style="padding: 12px;">Email</th>
                        <th style="padding: 12px;">Statut</th>
                        <th style="padding: 12px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${this.eleves.map(eleve => `
                        <tr>
                            <td style="padding: 12px;">${eleve.matricule}</td>
                            <td style="padding: 12px;">${eleve.nom}</td>
                            <td style="padding: 12px;">${eleve.prenom}</td>
                            <td style="padding: 12px;">${eleve.classe}</td>
                            <td style="padding: 12px;">${eleve.email}</td>
                            <td style="padding: 12px;">
                                <span style="
                                    padding: 4px 8px;
                                    border-radius: 12px;
                                    background: ${eleve.statut === 'ACTIF' ? '#d4edda' : '#f8d7da'};
                                    color: ${eleve.statut === 'ACTIF' ? '#155724' : '#721c24'};
                                ">${eleve.statut}</span>
                            </td>
                            <td style="padding: 12px;">
                                <button onclick="ElevesManager.editEleve(${eleve.id})" style="
                                    padding: 4px 8px;
                                    background: #667eea;
                                    color: white;
                                    border: none;
                                    border-radius: 3px;
                                    cursor: pointer;
                                    margin-right: 5px;
                                ">✏️</button>
                                <button onclick="ElevesManager.deleteEleve(${eleve.id})" style="
                                    padding: 4px 8px;
                                    background: #e74c3c;
                                    color: white;
                                    border: none;
                                    border-radius: 3px;
                                    cursor: pointer;
                                ">🗑️</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    },
    
    // Afficher le formulaire d'ajout
    showAddForm() {
        this.showForm();
    },
    
    // Modifier un élève
    editEleve(id) {
        const eleve = this.eleves.find(e => e.id === id);
        if (eleve) {
            this.currentEleve = eleve;
            this.showForm(eleve);
        }
    },
    
    // Afficher le formulaire
    showForm(eleve = null) {
        const modal = document.createElement('div');
        modal.id = 'eleveFormModal';
        modal.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
        `;
        
        modal.innerHTML = `
            <div style="
                background: white;
                padding: 30px;
                border-radius: 15px;
                width: 500px;
                max-height: 90vh;
                overflow-y: auto;
            ">
                <h2>${eleve ? 'Modifier' : 'Ajouter'} un élève</h2>
                <form id="eleveForm">
                    <div style="margin-bottom: 15px;">
                        <label>Matricule *</label>
                        <input type="text" name="matricule" value="${eleve?.matricule || ''}" required style="
                            width: 100%;
                            padding: 8px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                        ">
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Nom *</label>
                        <input type="text" name="nom" value="${eleve?.nom || ''}" required style="
                            width: 100%;
                            padding: 8px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                        ">
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Prénom *</label>
                        <input type="text" name="prenom" value="${eleve?.prenom || ''}" required style="
                            width: 100%;
                            padding: 8px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                        ">
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Classe *</label>
                        <select name="classe" required style="
                            width: 100%;
                            padding: 8px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                        ">
                            <option value="6A" ${eleve?.classe === '6A' ? 'selected' : ''}>6ème A</option>
                            <option value="6B" ${eleve?.classe === '6B' ? 'selected' : ''}>6ème B</option>
                            <option value="5A" ${eleve?.classe === '5A' ? 'selected' : ''}>5ème A</option>
                            <option value="5B" ${eleve?.classe === '5B' ? 'selected' : ''}>5ème B</option>
                            <option value="4" ${eleve?.classe === '4' ? 'selected' : ''}>4ème</option>
                            <option value="3" ${eleve?.classe === '3' ? 'selected' : ''}>3ème</option>
                        </select>
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Email</label>
                        <input type="email" name="email" value="${eleve?.email || ''}" style="
                            width: 100%;
                            padding: 8px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                        ">
                    </div>
                    
                    <div style="display: flex; gap: 10px; justify-content: flex-end;">
                        <button type="button" onclick="document.getElementById('eleveFormModal').remove()" style="
                            padding: 10px 20px;
                            background: #6c757d;
                            color: white;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                        ">Annuler</button>
                        <button type="submit" style="
                            padding: 10px 20px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                        ">Enregistrer</button>
                    </div>
                </form>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // Gérer la soumission
        document.getElementById('eleveForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveEleve(new FormData(e.target), eleve);
        });
    },
    
    // Sauvegarder un élève via l'API
    async saveEleve(formData, existingEleve) {
        const data = Object.fromEntries(formData);
        
        try {
            if (existingEleve) {
                // Mise à jour via API
                const updatedEleve = await window.API.updateEleve(existingEleve.id, data);
                const index = this.eleves.findIndex(e => e.id === existingEleve.id);
                if (index !== -1) {
                    this.eleves[index] = updatedEleve;
                }
            } else {
                // Création via API
                const newEleve = await window.API.createEleve({
                    ...data,
                    statut: 'ACTIF'
                });
                this.eleves.push(newEleve);
            }
            
            // Fermer la modal
            document.getElementById('eleveFormModal').remove();
            
            // Rafraîchir l'affichage
            this.displayEleves();
            
            // Notification
            this.showNotification(existingEleve ? 'Élève modifié avec succès' : 'Élève ajouté avec succès', 'success');
        } catch (error) {
            console.error('Erreur sauvegarde élève:', error);
            this.showNotification('Erreur lors de la sauvegarde', 'error');
        }
    },
    
    // Supprimer un élève
    deleteEleve(id) {
        if (confirm('Êtes-vous sûr de vouloir supprimer cet élève ?')) {
            this.eleves = this.eleves.filter(e => e.id !== id);
            this.displayEleves();
            this.showNotification('Élève supprimé avec succès', 'success');
        }
    },
    
    // Afficher une notification
    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            background: ${type === 'success' ? '#28a745' : '#17a2b8'};
            color: white;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.2);
            z-index: 10001;
            animation: slideIn 0.3s ease;
        `;
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.remove();
        }, 3000);
    },
    
    // Configurer les événements
    setupEventListeners() {
        // Recherche
        const searchInput = document.getElementById('searchEleve');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.searchEleves(e.target.value);
            });
        }
    },
    
    // Rechercher des élèves
    searchEleves(query) {
        const filtered = this.eleves.filter(eleve => 
            eleve.nom.toLowerCase().includes(query.toLowerCase()) ||
            eleve.prenom.toLowerCase().includes(query.toLowerCase()) ||
            eleve.matricule.toLowerCase().includes(query.toLowerCase())
        );
        
        // Afficher les résultats filtrés
        this.displayEleves(filtered);
    }
};

// Initialiser au chargement
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('eleves')) {
        ElevesManager.init();
    }
});
