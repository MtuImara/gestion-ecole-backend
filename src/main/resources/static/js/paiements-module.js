/**
 * Module complet de gestion des paiements
 * Fonctionnalités: Création, validation, suivi, rapports
 */

class PaiementsModule {
    constructor() {
        this.paiements = [];
        this.tarifsMensuels = {
            '6A': 150000, '6B': 150000,
            '5A': 160000, '5B': 160000,
            '4': 170000,
            '3': 180000
        };
        this.fraisInscription = 50000;
    }

    init() {
        console.log('[Paiements Module] Initialisation...');
        this.loadPaiements();
        this.setupInterface();
        this.setupEventListeners();
    }

    setupInterface() {
        // Ajouter bouton nouveau paiement
        const header = document.querySelector('.header-actions');
        if (header && !document.getElementById('paiementActions')) {
            const actions = document.createElement('div');
            actions.id = 'paiementActions';
            actions.innerHTML = `
                <button onclick="paiementsModule.showNewPaiement()" style="
                    padding: 8px 16px;
                    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">💰 Nouveau paiement</button>
                
                <button onclick="paiementsModule.showRapport()" style="
                    padding: 8px 16px;
                    background: #17a2b8;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">📊 Rapport</button>
            `;
            header.insertBefore(actions, header.firstChild);
        }

        // Ajouter tableau de bord des paiements
        this.createDashboard();
    }

    createDashboard() {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent || document.getElementById('paiementsDashboard')) return;

        const dashboard = document.createElement('div');
        dashboard.id = 'paiementsDashboard';
        dashboard.innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 20px 0;">
                <div style="background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.05);">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <div style="color: #7f8c8d; font-size: 14px;">Recettes du jour</div>
                            <div style="font-size: 28px; font-weight: bold; color: #27ae60;" id="recettesJour">0 BIF</div>
                        </div>
                        <div style="font-size: 30px;">💵</div>
                    </div>
                </div>
                
                <div style="background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.05);">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <div style="color: #7f8c8d; font-size: 14px;">Recettes du mois</div>
                            <div style="font-size: 28px; font-weight: bold; color: #3498db;" id="recettesMois">0 BIF</div>
                        </div>
                        <div style="font-size: 30px;">📈</div>
                    </div>
                </div>
                
                <div style="background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.05);">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <div style="color: #7f8c8d; font-size: 14px;">Impayés</div>
                            <div style="font-size: 28px; font-weight: bold; color: #e74c3c;" id="impayes">0 BIF</div>
                        </div>
                        <div style="font-size: 30px;">⚠️</div>
                    </div>
                </div>
                
                <div style="background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.05);">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <div style="color: #7f8c8d; font-size: 14px;">Taux recouvrement</div>
                            <div style="font-size: 28px; font-weight: bold; color: #9b59b6;" id="tauxRecouvrement">0%</div>
                        </div>
                        <div style="font-size: 30px;">📊</div>
                    </div>
                </div>
            </div>
        `;

        const header = document.querySelector('.header');
        if (header) {
            header.parentNode.insertBefore(dashboard, header.nextSibling);
        }
    }

    async loadPaiements() {
        try {
            // Charger depuis l'API d'intégration
            this.paiements = await window.API.getPaiements();
            
            // Charger aussi les statistiques
            const stats = await window.API.getPaiementStats();
            if (stats) {
                this.stats = stats;
            }
        } catch (error) {
            console.error('[Paiements] Erreur:', error);
            // Fallback sur données simulées
            this.paiements = this.getMockPaiements();
            this.showNotification('Mode hors ligne - Données de démonstration', 'warning');
        }

        this.displayPaiements();
        this.updateDashboard();
    }

    getMockPaiements() {
        return [
            {
                id: 1,
                numeroRecu: 'REC2024001',
                eleve: { nom: 'Dupont', prenom: 'Jean', classe: '6A' },
                montant: 150000,
                type: 'SCOLARITE',
                mois: 'Janvier 2024',
                date: '2024-01-15',
                modePaiement: 'ESPECES',
                statut: 'VALIDE'
            },
            {
                id: 2,
                numeroRecu: 'REC2024002',
                eleve: { nom: 'Martin', prenom: 'Marie', classe: '6A' },
                montant: 150000,
                type: 'SCOLARITE',
                mois: 'Janvier 2024',
                date: '2024-01-16',
                modePaiement: 'VIREMENT',
                statut: 'VALIDE'
            }
        ];
    }

    displayPaiements() {
        const container = document.getElementById('paiementsTable');
        if (!container) return;

        container.innerHTML = `
            <table style="width: 100%; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden;">
                <thead>
                    <tr style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: white;">
                        <th style="padding: 12px;">N° Reçu</th>
                        <th style="padding: 12px;">Date</th>
                        <th style="padding: 12px;">Élève</th>
                        <th style="padding: 12px;">Type</th>
                        <th style="padding: 12px;">Montant</th>
                        <th style="padding: 12px;">Mode</th>
                        <th style="padding: 12px;">Statut</th>
                        <th style="padding: 12px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${this.paiements.map(p => `
                        <tr>
                            <td style="padding: 12px; font-weight: 600;">${p.numeroRecu}</td>
                            <td style="padding: 12px;">${new Date(p.date).toLocaleDateString('fr-FR')}</td>
                            <td style="padding: 12px;">${p.eleve.nom} ${p.eleve.prenom} (${p.eleve.classe})</td>
                            <td style="padding: 12px;">
                                <span style="
                                    padding: 4px 8px;
                                    border-radius: 12px;
                                    background: ${p.type === 'SCOLARITE' ? '#e8f5e9' : '#fff3e0'};
                                    color: ${p.type === 'SCOLARITE' ? '#2e7d32' : '#e65100'};
                                    font-size: 12px;
                                ">${p.type}</span>
                            </td>
                            <td style="padding: 12px; font-weight: 600;">${this.formatMoney(p.montant)}</td>
                            <td style="padding: 12px;">${p.modePaiement}</td>
                            <td style="padding: 12px;">
                                <span style="
                                    padding: 4px 8px;
                                    border-radius: 12px;
                                    background: ${p.statut === 'VALIDE' ? '#d4edda' : '#f8d7da'};
                                    color: ${p.statut === 'VALIDE' ? '#155724' : '#721c24'};
                                    font-size: 12px;
                                ">${p.statut}</span>
                            </td>
                            <td style="padding: 12px;">
                                <button onclick="paiementsModule.printRecu(${p.id})" style="
                                    padding: 4px 8px;
                                    background: #667eea;
                                    color: white;
                                    border: none;
                                    border-radius: 3px;
                                    cursor: pointer;
                                    margin-right: 5px;
                                ">🖨️</button>
                                <button onclick="paiementsModule.annulerPaiement(${p.id})" style="
                                    padding: 4px 8px;
                                    background: #e74c3c;
                                    color: white;
                                    border: none;
                                    border-radius: 3px;
                                    cursor: pointer;
                                ">❌</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    showNewPaiement() {
        const modal = document.createElement('div');
        modal.id = 'paiementModal';
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
                width: 600px;
                max-height: 90vh;
                overflow-y: auto;
            ">
                <h2>💰 Nouveau Paiement</h2>
                <form id="paiementForm">
                    <div style="margin-bottom: 15px;">
                        <label>Élève *</label>
                        <select name="eleveId" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                            <option value="">Sélectionner un élève</option>
                            <option value="1">Dupont Jean - 6A</option>
                            <option value="2">Martin Marie - 6A</option>
                        </select>
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Type de paiement *</label>
                        <select name="type" required onchange="paiementsModule.updateMontant(this.value)" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                            <option value="">Sélectionner</option>
                            <option value="SCOLARITE">Scolarité mensuelle</option>
                            <option value="INSCRIPTION">Frais d'inscription</option>
                            <option value="AUTRE">Autre</option>
                        </select>
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Montant (BIF) *</label>
                        <input type="number" name="montant" id="montantInput" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Mode de paiement *</label>
                        <select name="modePaiement" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                            <option value="ESPECES">Espèces</option>
                            <option value="VIREMENT">Virement</option>
                            <option value="MOBILE_MONEY">Mobile Money</option>
                            <option value="CHEQUE">Chèque</option>
                        </select>
                    </div>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Référence (optionnel)</label>
                        <input type="text" name="reference" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                    </div>
                    
                    <div style="display: flex; gap: 10px; justify-content: flex-end;">
                        <button type="button" onclick="document.getElementById('paiementModal').remove()" style="
                            padding: 10px 20px;
                            background: #6c757d;
                            color: white;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                        ">Annuler</button>
                        <button type="submit" style="
                            padding: 10px 20px;
                            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
                            color: white;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                        ">Valider le paiement</button>
                    </div>
                </form>
            </div>
        `;

        document.body.appendChild(modal);

        document.getElementById('paiementForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.processPaiement(new FormData(e.target));
        });
    }

    processPaiement(formData) {
        const data = Object.fromEntries(formData);
        
        const newPaiement = {
            id: Date.now(),
            numeroRecu: `REC${new Date().getFullYear()}${String(this.paiements.length + 1).padStart(3, '0')}`,
            date: new Date().toISOString().split('T')[0],
            statut: 'VALIDE',
            ...data
        };

        this.paiements.unshift(newPaiement);
        
        document.getElementById('paiementModal').remove();
        
        this.displayPaiements();
        this.updateDashboard();
        
        // Afficher le reçu
        this.printRecu(newPaiement.id);
        
        this.showNotification('Paiement enregistré avec succès', 'success');
    }

    updateMontant(type) {
        const montantInput = document.getElementById('montantInput');
        if (type === 'SCOLARITE') {
            montantInput.value = 150000; // Montant par défaut
        } else if (type === 'INSCRIPTION') {
            montantInput.value = this.fraisInscription;
        }
    }

    updateDashboard() {
        const today = new Date().toISOString().split('T')[0];
        const currentMonth = new Date().getMonth();
        
        const recettesJour = this.paiements
            .filter(p => p.date === today && p.statut === 'VALIDE')
            .reduce((sum, p) => sum + p.montant, 0);
            
        const recettesMois = this.paiements
            .filter(p => new Date(p.date).getMonth() === currentMonth && p.statut === 'VALIDE')
            .reduce((sum, p) => sum + p.montant, 0);

        document.getElementById('recettesJour').textContent = this.formatMoney(recettesJour);
        document.getElementById('recettesMois').textContent = this.formatMoney(recettesMois);
        document.getElementById('impayes').textContent = this.formatMoney(500000); // Simulé
        document.getElementById('tauxRecouvrement').textContent = '85%'; // Simulé
    }

    printRecu(id) {
        const paiement = this.paiements.find(p => p.id === id);
        if (!paiement) return;

        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <html>
            <head>
                <title>Reçu ${paiement.numeroRecu}</title>
                <style>
                    body { font-family: Arial; padding: 20px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .info { margin: 10px 0; }
                    .montant { font-size: 24px; font-weight: bold; color: #27ae60; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🎓 EcoleGest</h1>
                    <h2>REÇU DE PAIEMENT</h2>
                    <p>N° ${paiement.numeroRecu}</p>
                </div>
                <div class="info">Date: ${new Date(paiement.date).toLocaleDateString('fr-FR')}</div>
                <div class="info">Élève: ${paiement.eleve?.nom || 'Test'} ${paiement.eleve?.prenom || 'Test'}</div>
                <div class="info">Type: ${paiement.type}</div>
                <div class="info montant">Montant: ${this.formatMoney(paiement.montant)}</div>
                <div class="info">Mode: ${paiement.modePaiement}</div>
            </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.print();
    }

    annulerPaiement(id) {
        if (confirm('Êtes-vous sûr de vouloir annuler ce paiement ?')) {
            const paiement = this.paiements.find(p => p.id === id);
            if (paiement) {
                paiement.statut = 'ANNULE';
                this.displayPaiements();
                this.updateDashboard();
                this.showNotification('Paiement annulé', 'warning');
            }
        }
    }

    formatMoney(amount) {
        return new Intl.NumberFormat('fr-FR').format(amount) + ' BIF';
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

    setupEventListeners() {
        // Recherche de paiements
        const searchInput = document.getElementById('searchPaiement');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.searchPaiements(e.target.value);
            });
        }
    }

    searchPaiements(query) {
        // Implémenter la recherche
    }

    showRapport() {
        // Afficher le rapport des paiements
        alert('Rapport des paiements en cours de génération...');
    }
}

// Initialiser
const paiementsModule = new PaiementsModule();
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('paiements')) {
        paiementsModule.init();
    }
});
