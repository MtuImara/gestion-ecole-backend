/**
 * Module de gestion des parents
 */

class ParentsModule {
    constructor() {
        this.parents = [];
        this.currentParent = null;
    }

    init() {
        console.log('[Parents Module] Initialisation...');
        this.loadParents();
        this.setupInterface();
    }

    setupInterface() {
        const header = document.querySelector('.header-actions');
        if (header && !document.getElementById('parentActions')) {
            const actions = document.createElement('div');
            actions.id = 'parentActions';
            actions.innerHTML = `
                <button onclick="parentsModule.showAddForm()" style="
                    padding: 8px 16px;
                    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">➕ Nouveau parent</button>
                
                <button onclick="parentsModule.sendMessage()" style="
                    padding: 8px 16px;
                    background: #28a745;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">📧 Message groupé</button>
            `;
            header.insertBefore(actions, header.firstChild);
        }

        this.createDashboard();
    }

    createDashboard() {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent || document.getElementById('parentsDashboard')) return;

        const dashboard = document.createElement('div');
        dashboard.id = 'parentsDashboard';
        dashboard.innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0;">
                <div style="background: white; padding: 15px; border-radius: 10px; text-align: center;">
                    <div style="font-size: 36px;">👨‍👩‍👧‍👦</div>
                    <div style="font-size: 24px; font-weight: bold; color: #2c3e50;" id="totalParents">0</div>
                    <div style="color: #7f8c8d; font-size: 14px;">Total Parents</div>
                </div>
                
                <div style="background: white; padding: 15px; border-radius: 10px; text-align: center;">
                    <div style="font-size: 36px;">✅</div>
                    <div style="font-size: 24px; font-weight: bold; color: #27ae60;" id="parentsActifs">0</div>
                    <div style="color: #7f8c8d; font-size: 14px;">Actifs</div>
                </div>
                
                <div style="background: white; padding: 15px; border-radius: 10px; text-align: center;">
                    <div style="font-size: 36px;">👶</div>
                    <div style="font-size: 24px; font-weight: bold; color: #3498db;" id="moyenneEnfants">0</div>
                    <div style="color: #7f8c8d; font-size: 14px;">Moy. enfants/parent</div>
                </div>
                
                <div style="background: white; padding: 15px; border-radius: 10px; text-align: center;">
                    <div style="font-size: 36px;">📱</div>
                    <div style="font-size: 24px; font-weight: bold; color: #9b59b6;" id="parentsConnectes">0</div>
                    <div style="color: #7f8c8d; font-size: 14px;">Connectés (app)</div>
                </div>
            </div>
        `;

        const header = document.querySelector('.header');
        if (header) {
            header.parentNode.insertBefore(dashboard, header.nextSibling);
        }
    }

    async loadParents() {
        // Données simulées
        this.parents = [
            { 
                id: 1, 
                nom: 'Dupont', 
                prenom: 'Jean-Pierre', 
                telephone: '+257 79 123 456',
                email: 'jp.dupont@email.com',
                profession: 'Ingénieur',
                enfants: ['Dupont Marie (6A)', 'Dupont Pierre (4ème)'],
                statut: 'ACTIF',
                solde: -50000
            },
            { 
                id: 2, 
                nom: 'Martin', 
                prenom: 'Sophie', 
                telephone: '+257 79 234 567',
                email: 's.martin@email.com',
                profession: 'Médecin',
                enfants: ['Martin Lucas (5A)'],
                statut: 'ACTIF',
                solde: 0
            },
            { 
                id: 3, 
                nom: 'Bernard', 
                prenom: 'Paul', 
                telephone: '+257 79 345 678',
                email: 'p.bernard@email.com',
                profession: 'Commerçant',
                enfants: ['Bernard Emma (6B)', 'Bernard Louis (3ème)'],
                statut: 'ACTIF',
                solde: 150000
            }
        ];

        this.displayParents();
        this.updateDashboard();
    }

    displayParents() {
        const container = document.getElementById('parentsTable') || this.createTable();
        if (!container) return;

        container.innerHTML = `
            <table style="width: 100%; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden;">
                <thead>
                    <tr style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white;">
                        <th style="padding: 12px;">Parent</th>
                        <th style="padding: 12px;">Contact</th>
                        <th style="padding: 12px;">Enfants</th>
                        <th style="padding: 12px;">Solde</th>
                        <th style="padding: 12px;">Statut</th>
                        <th style="padding: 12px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${this.parents.map(parent => `
                        <tr>
                            <td style="padding: 12px;">
                                <div style="font-weight: 600;">${parent.nom} ${parent.prenom}</div>
                                <div style="color: #7f8c8d; font-size: 12px;">${parent.profession}</div>
                            </td>
                            <td style="padding: 12px;">
                                <div>${parent.telephone}</div>
                                <div style="color: #7f8c8d; font-size: 12px;">${parent.email}</div>
                            </td>
                            <td style="padding: 12px;">
                                ${parent.enfants.map(e => `<div style="margin: 2px 0;">• ${e}</div>`).join('')}
                            </td>
                            <td style="padding: 12px;">
                                <span style="
                                    font-weight: bold;
                                    color: ${parent.solde < 0 ? '#e74c3c' : parent.solde > 0 ? '#f39c12' : '#27ae60'};
                                ">${this.formatMoney(parent.solde)}</span>
                            </td>
                            <td style="padding: 12px;">
                                <span style="
                                    padding: 4px 8px;
                                    border-radius: 12px;
                                    background: ${parent.statut === 'ACTIF' ? '#d4edda' : '#f8d7da'};
                                    color: ${parent.statut === 'ACTIF' ? '#155724' : '#721c24'};
                                    font-size: 12px;
                                ">${parent.statut}</span>
                            </td>
                            <td style="padding: 12px;">
                                <button onclick="parentsModule.showDetails(${parent.id})" style="padding: 4px 8px; background: #17a2b8; color: white; border: none; border-radius: 3px; cursor: pointer; margin: 2px;">👁️</button>
                                <button onclick="parentsModule.sendSMS(${parent.id})" style="padding: 4px 8px; background: #28a745; color: white; border: none; border-radius: 3px; cursor: pointer; margin: 2px;">📱</button>
                                <button onclick="parentsModule.showPayments(${parent.id})" style="padding: 4px 8px; background: #f39c12; color: white; border: none; border-radius: 3px; cursor: pointer; margin: 2px;">💰</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    createTable() {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent) return null;

        const table = document.createElement('div');
        table.id = 'parentsTable';
        table.style.marginTop = '20px';
        mainContent.appendChild(table);
        return table;
    }

    updateDashboard() {
        document.getElementById('totalParents').textContent = this.parents.length;
        document.getElementById('parentsActifs').textContent = this.parents.filter(p => p.statut === 'ACTIF').length;
        
        const totalEnfants = this.parents.reduce((sum, p) => sum + p.enfants.length, 0);
        document.getElementById('moyenneEnfants').textContent = (totalEnfants / this.parents.length).toFixed(1);
        
        document.getElementById('parentsConnectes').textContent = Math.floor(this.parents.length * 0.6); // Simulé
    }

    showAddForm() {
        alert('Formulaire d\'ajout de parent');
    }

    sendMessage() {
        alert('Envoi de message groupé aux parents');
    }

    showDetails(id) {
        const parent = this.parents.find(p => p.id === id);
        if (parent) {
            alert(`Détails de ${parent.nom} ${parent.prenom}`);
        }
    }

    sendSMS(id) {
        const parent = this.parents.find(p => p.id === id);
        if (parent) {
            alert(`Envoi SMS à ${parent.telephone}`);
        }
    }

    showPayments(id) {
        const parent = this.parents.find(p => p.id === id);
        if (parent) {
            alert(`Historique des paiements de ${parent.nom} ${parent.prenom}`);
        }
    }

    formatMoney(amount) {
        return new Intl.NumberFormat('fr-FR').format(amount) + ' BIF';
    }
}

const parentsModule = new ParentsModule();
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('parents')) {
        parentsModule.init();
    }
});
