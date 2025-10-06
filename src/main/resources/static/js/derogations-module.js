/**
 * Module de gestion des dérogations et demandes spéciales
 */

class DerogationsModule {
    constructor() {
        this.derogations = [];
        this.types = ['ABSENCE', 'RETARD', 'SORTIE', 'DISPENSE', 'AUTRE'];
        this.statuts = ['EN_ATTENTE', 'APPROUVEE', 'REJETEE'];
    }

    init() {
        console.log('[Dérogations Module] Initialisation...');
        this.loadDerogations();
        this.setupInterface();
    }

    setupInterface() {
        this.createDashboard();
        this.addActionButtons();
    }

    addActionButtons() {
        const header = document.querySelector('.header-actions');
        if (header && !document.getElementById('derogationActions')) {
            const actions = document.createElement('div');
            actions.id = 'derogationActions';
            actions.innerHTML = `
                <button onclick="derogationsModule.showNewRequest()" style="
                    padding: 8px 16px;
                    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">📝 Nouvelle demande</button>
                
                <button onclick="derogationsModule.showPending()" style="
                    padding: 8px 16px;
                    background: #f39c12;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">⏳ En attente (${this.getPendingCount()})</button>
            `;
            header.insertBefore(actions, header.firstChild);
        }
    }

    createDashboard() {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent || document.getElementById('derogationsDashboard')) return;

        const dashboard = document.createElement('div');
        dashboard.id = 'derogationsDashboard';
        dashboard.innerHTML = `
            <div style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); color: white; padding: 20px; border-radius: 15px; margin: 20px 0;">
                <h3 style="margin: 0 0 20px 0;">📋 Centre de Dérogations</h3>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px;">
                    <div style="background: rgba(255,255,255,0.2); padding: 15px; border-radius: 10px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;" id="totalDerogations">0</div>
                        <div style="font-size: 14px;">Total demandes</div>
                    </div>
                    <div style="background: rgba(255,255,255,0.2); padding: 15px; border-radius: 10px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;" id="derogationsAttente">0</div>
                        <div style="font-size: 14px;">En attente</div>
                    </div>
                    <div style="background: rgba(255,255,255,0.2); padding: 15px; border-radius: 10px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;" id="derogationsApprouvees">0</div>
                        <div style="font-size: 14px;">Approuvées</div>
                    </div>
                    <div style="background: rgba(255,255,255,0.2); padding: 15px; border-radius: 10px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;" id="derogationsRejetees">0</div>
                        <div style="font-size: 14px;">Rejetées</div>
                    </div>
                </div>
            </div>

            <!-- Filtres rapides -->
            <div style="background: white; padding: 15px; border-radius: 10px; margin-bottom: 20px;">
                <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                    <button onclick="derogationsModule.filterByType('TOUS')" style="padding: 8px 16px; background: #667eea; color: white; border: none; border-radius: 5px; cursor: pointer;">Tous</button>
                    <button onclick="derogationsModule.filterByType('ABSENCE')" style="padding: 8px 16px; background: #e74c3c; color: white; border: none; border-radius: 5px; cursor: pointer;">Absences</button>
                    <button onclick="derogationsModule.filterByType('RETARD')" style="padding: 8px 16px; background: #f39c12; color: white; border: none; border-radius: 5px; cursor: pointer;">Retards</button>
                    <button onclick="derogationsModule.filterByType('SORTIE')" style="padding: 8px 16px; background: #3498db; color: white; border: none; border-radius: 5px; cursor: pointer;">Sorties</button>
                    <button onclick="derogationsModule.filterByType('DISPENSE')" style="padding: 8px 16px; background: #9b59b6; color: white; border: none; border-radius: 5px; cursor: pointer;">Dispenses</button>
                </div>
            </div>
        `;

        const header = document.querySelector('.header');
        if (header) {
            header.parentNode.insertBefore(dashboard, header.nextSibling);
        }
    }

    async loadDerogations() {
        // Données simulées
        this.derogations = [
            {
                id: 1,
                type: 'ABSENCE',
                eleve: 'Dupont Marie (6A)',
                parent: 'Dupont Jean-Pierre',
                dateDebut: '2024-10-07',
                dateFin: '2024-10-08',
                motif: 'Maladie - Fièvre',
                justificatif: 'certificat_medical.pdf',
                statut: 'EN_ATTENTE',
                dateCreation: '2024-10-06'
            },
            {
                id: 2,
                type: 'SORTIE',
                eleve: 'Martin Lucas (5A)',
                parent: 'Martin Sophie',
                dateDebut: '2024-10-10',
                dateFin: '2024-10-10',
                motif: 'Rendez-vous médical à 14h',
                justificatif: null,
                statut: 'APPROUVEE',
                dateCreation: '2024-10-05',
                approuvePar: 'Direction',
                dateApprobation: '2024-10-05'
            },
            {
                id: 3,
                type: 'DISPENSE',
                eleve: 'Bernard Emma (6B)',
                parent: 'Bernard Paul',
                dateDebut: '2024-10-01',
                dateFin: '2024-10-31',
                motif: 'Dispense de sport - Entorse',
                justificatif: 'certificat_dispense.pdf',
                statut: 'APPROUVEE',
                dateCreation: '2024-09-30',
                approuvePar: 'Infirmerie',
                dateApprobation: '2024-10-01'
            }
        ];

        this.displayDerogations();
        this.updateDashboard();
    }

    displayDerogations(filteredDerogations = null) {
        const container = document.getElementById('derogationsTable') || this.createTable();
        if (!container) return;

        const derogations = filteredDerogations || this.derogations;

        container.innerHTML = `
            <div style="background: white; border-radius: 10px; overflow: hidden;">
                ${derogations.map(d => `
                    <div style="border-bottom: 1px solid #f0f0f0; padding: 15px; display: flex; justify-content: space-between; align-items: center;">
                        <div style="flex: 1;">
                            <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 10px;">
                                <span style="
                                    padding: 4px 8px;
                                    border-radius: 5px;
                                    background: ${this.getTypeColor(d.type)};
                                    color: white;
                                    font-size: 12px;
                                    font-weight: bold;
                                ">${d.type}</span>
                                <span style="font-weight: 600; color: #2c3e50;">${d.eleve}</span>
                                <span style="color: #7f8c8d; font-size: 12px;">par ${d.parent}</span>
                            </div>
                            <div style="color: #666; font-size: 14px; margin-bottom: 5px;">
                                📅 ${new Date(d.dateDebut).toLocaleDateString('fr-FR')} ${d.dateFin !== d.dateDebut ? `au ${new Date(d.dateFin).toLocaleDateString('fr-FR')}` : ''}
                            </div>
                            <div style="color: #666; font-size: 14px;">
                                💬 ${d.motif}
                            </div>
                            ${d.justificatif ? `<div style="color: #3498db; font-size: 12px; margin-top: 5px;">📎 ${d.justificatif}</div>` : ''}
                        </div>
                        
                        <div style="display: flex; align-items: center; gap: 10px;">
                            <span style="
                                padding: 6px 12px;
                                border-radius: 20px;
                                background: ${this.getStatutColor(d.statut)};
                                color: white;
                                font-size: 12px;
                                font-weight: bold;
                            ">${this.getStatutLabel(d.statut)}</span>
                            
                            ${d.statut === 'EN_ATTENTE' ? `
                                <button onclick="derogationsModule.approve(${d.id})" style="
                                    padding: 6px 12px;
                                    background: #27ae60;
                                    color: white;
                                    border: none;
                                    border-radius: 5px;
                                    cursor: pointer;
                                ">✅ Approuver</button>
                                <button onclick="derogationsModule.reject(${d.id})" style="
                                    padding: 6px 12px;
                                    background: #e74c3c;
                                    color: white;
                                    border: none;
                                    border-radius: 5px;
                                    cursor: pointer;
                                ">❌ Rejeter</button>
                            ` : ''}
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    }

    createTable() {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent) return null;

        const table = document.createElement('div');
        table.id = 'derogationsTable';
        table.style.marginTop = '20px';
        mainContent.appendChild(table);
        return table;
    }

    updateDashboard() {
        document.getElementById('totalDerogations').textContent = this.derogations.length;
        document.getElementById('derogationsAttente').textContent = this.derogations.filter(d => d.statut === 'EN_ATTENTE').length;
        document.getElementById('derogationsApprouvees').textContent = this.derogations.filter(d => d.statut === 'APPROUVEE').length;
        document.getElementById('derogationsRejetees').textContent = this.derogations.filter(d => d.statut === 'REJETEE').length;
    }

    showNewRequest() {
        const modal = document.createElement('div');
        modal.id = 'derogationModal';
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
            <div style="background: white; padding: 30px; border-radius: 15px; width: 500px; max-height: 90vh; overflow-y: auto;">
                <h2>📝 Nouvelle demande de dérogation</h2>
                <form id="derogationForm">
                    <select required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                        <option value="">Type de demande</option>
                        <option value="ABSENCE">Absence</option>
                        <option value="RETARD">Retard</option>
                        <option value="SORTIE">Sortie anticipée</option>
                        <option value="DISPENSE">Dispense</option>
                        <option value="AUTRE">Autre</option>
                    </select>
                    
                    <select required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                        <option value="">Élève concerné</option>
                        <option value="1">Dupont Marie (6A)</option>
                        <option value="2">Martin Lucas (5A)</option>
                        <option value="3">Bernard Emma (6B)</option>
                    </select>
                    
                    <input type="date" placeholder="Date début" required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    <input type="date" placeholder="Date fin" style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    
                    <textarea placeholder="Motif de la demande" required rows="4" style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;"></textarea>
                    
                    <input type="file" style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    
                    <div style="display: flex; gap: 10px; margin-top: 20px;">
                        <button type="button" onclick="document.getElementById('derogationModal').remove()" style="flex: 1; padding: 10px; background: #6c757d; color: white; border: none; border-radius: 5px; cursor: pointer;">Annuler</button>
                        <button type="submit" style="flex: 1; padding: 10px; background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); color: white; border: none; border-radius: 5px; cursor: pointer;">Soumettre</button>
                    </div>
                </form>
            </div>
        `;

        document.body.appendChild(modal);
    }

    approve(id) {
        const derogation = this.derogations.find(d => d.id === id);
        if (derogation) {
            derogation.statut = 'APPROUVEE';
            derogation.approuvePar = 'Direction';
            derogation.dateApprobation = new Date().toISOString().split('T')[0];
            this.displayDerogations();
            this.updateDashboard();
            this.showNotification('Dérogation approuvée', 'success');
        }
    }

    reject(id) {
        if (confirm('Êtes-vous sûr de rejeter cette demande ?')) {
            const derogation = this.derogations.find(d => d.id === id);
            if (derogation) {
                derogation.statut = 'REJETEE';
                this.displayDerogations();
                this.updateDashboard();
                this.showNotification('Dérogation rejetée', 'warning');
            }
        }
    }

    filterByType(type) {
        if (type === 'TOUS') {
            this.displayDerogations();
        } else {
            const filtered = this.derogations.filter(d => d.type === type);
            this.displayDerogations(filtered);
        }
    }

    showPending() {
        const pending = this.derogations.filter(d => d.statut === 'EN_ATTENTE');
        this.displayDerogations(pending);
    }

    getPendingCount() {
        return this.derogations.filter(d => d.statut === 'EN_ATTENTE').length;
    }

    getTypeColor(type) {
        const colors = {
            'ABSENCE': '#e74c3c',
            'RETARD': '#f39c12',
            'SORTIE': '#3498db',
            'DISPENSE': '#9b59b6',
            'AUTRE': '#95a5a6'
        };
        return colors[type] || '#95a5a6';
    }

    getStatutColor(statut) {
        const colors = {
            'EN_ATTENTE': '#f39c12',
            'APPROUVEE': '#27ae60',
            'REJETEE': '#e74c3c'
        };
        return colors[statut] || '#95a5a6';
    }

    getStatutLabel(statut) {
        const labels = {
            'EN_ATTENTE': 'En attente',
            'APPROUVEE': 'Approuvée',
            'REJETEE': 'Rejetée'
        };
        return labels[statut] || statut;
    }

    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            background: ${type === 'success' ? '#27ae60' : type === 'warning' ? '#f39c12' : '#3498db'};
            color: white;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.2);
            z-index: 10001;
        `;
        notification.textContent = message;
        document.body.appendChild(notification);
        setTimeout(() => notification.remove(), 3000);
    }
}

const derogationsModule = new DerogationsModule();
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('derogations')) {
        derogationsModule.init();
    }
});
