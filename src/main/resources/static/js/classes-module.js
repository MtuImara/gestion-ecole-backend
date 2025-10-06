/**
 * Module de gestion des classes
 */

class ClassesModule {
    constructor() {
        this.classes = [];
        this.currentClasse = null;
    }

    init() {
        console.log('[Classes Module] Initialisation...');
        this.loadClasses();
        this.setupInterface();
    }

    setupInterface() {
        const header = document.querySelector('.header-actions');
        if (header && !document.getElementById('classeActions')) {
            const actions = document.createElement('div');
            actions.id = 'classeActions';
            actions.innerHTML = `
                <button onclick="classesModule.showAddForm()" style="
                    padding: 8px 16px;
                    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">➕ Nouvelle classe</button>
                
                <button onclick="classesModule.showStats()" style="
                    padding: 8px 16px;
                    background: #17a2b8;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">📊 Statistiques</button>
            `;
            header.insertBefore(actions, header.firstChild);
        }
    }

    async loadClasses() {
        try {
            // Données simulées
            this.classes = [
                { id: 1, nom: '6ème A', niveau: '6ème', effectifMax: 30, effectifActuel: 25, professeurPrincipal: 'M. Dupont', salle: 'A101' },
                { id: 2, nom: '6ème B', niveau: '6ème', effectifMax: 30, effectifActuel: 23, professeurPrincipal: 'Mme Martin', salle: 'A102' },
                { id: 3, nom: '5ème A', niveau: '5ème', effectifMax: 30, effectifActuel: 28, professeurPrincipal: 'M. Bernard', salle: 'B201' },
                { id: 4, nom: '5ème B', niveau: '5ème', effectifMax: 30, effectifActuel: 26, professeurPrincipal: 'Mme Laurent', salle: 'B202' },
                { id: 5, nom: '4ème', niveau: '4ème', effectifMax: 35, effectifActuel: 30, professeurPrincipal: 'M. Moreau', salle: 'C301' },
                { id: 6, nom: '3ème', niveau: '3ème', effectifMax: 35, effectifActuel: 24, professeurPrincipal: 'Mme Simon', salle: 'C302' }
            ];
            this.displayClasses();
        } catch (error) {
            console.error('[Classes] Erreur:', error);
        }
    }

    displayClasses() {
        const container = document.getElementById('classesContainer') || this.createContainer();
        if (!container) return;

        container.innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; margin: 20px 0;">
                ${this.classes.map(classe => `
                    <div style="
                        background: white;
                        border-radius: 15px;
                        padding: 20px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                        cursor: pointer;
                        transition: all 0.3s;
                    " onmouseover="this.style.transform='translateY(-5px)'" onmouseout="this.style.transform='translateY(0)'">
                        <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 15px;">
                            <div>
                                <h3 style="margin: 0; color: #2c3e50;">${classe.nom}</h3>
                                <p style="color: #7f8c8d; margin: 5px 0;">Salle ${classe.salle}</p>
                            </div>
                            <div style="
                                background: ${classe.effectifActuel < classe.effectifMax ? 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' : 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'};
                                color: white;
                                padding: 8px 12px;
                                border-radius: 20px;
                                font-weight: bold;
                            ">${classe.effectifActuel}/${classe.effectifMax}</div>
                        </div>
                        
                        <div style="margin-bottom: 15px;">
                            <div style="background: #f0f0f0; border-radius: 10px; height: 8px; overflow: hidden;">
                                <div style="
                                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                    height: 100%;
                                    width: ${(classe.effectifActuel / classe.effectifMax * 100)}%;
                                    transition: width 0.3s;
                                "></div>
                            </div>
                            <p style="color: #7f8c8d; font-size: 12px; margin-top: 5px;">
                                Taux de remplissage: ${Math.round(classe.effectifActuel / classe.effectifMax * 100)}%
                            </p>
                        </div>
                        
                        <div style="font-size: 14px; color: #666;">
                            <p>👨‍🏫 Prof principal: <strong>${classe.professeurPrincipal}</strong></p>
                            <p>📚 Niveau: <strong>${classe.niveau}</strong></p>
                        </div>
                        
                        <div style="margin-top: 15px; display: flex; gap: 10px;">
                            <button onclick="event.stopPropagation(); classesModule.showEleves(${classe.id})" style="
                                flex: 1;
                                padding: 8px;
                                background: #667eea;
                                color: white;
                                border: none;
                                border-radius: 5px;
                                cursor: pointer;
                            ">👥 Élèves</button>
                            <button onclick="event.stopPropagation(); classesModule.editClasse(${classe.id})" style="
                                flex: 1;
                                padding: 8px;
                                background: #f39c12;
                                color: white;
                                border: none;
                                border-radius: 5px;
                                cursor: pointer;
                            ">✏️ Modifier</button>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    }

    createContainer() {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent) return null;

        const container = document.createElement('div');
        container.id = 'classesContainer';
        mainContent.appendChild(container);
        return container;
    }

    showAddForm() {
        const modal = document.createElement('div');
        modal.id = 'classeModal';
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
            <div style="background: white; padding: 30px; border-radius: 15px; width: 500px;">
                <h2>➕ Nouvelle Classe</h2>
                <form id="classeForm">
                    <input type="text" placeholder="Nom de la classe" required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    <input type="text" placeholder="Niveau" required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    <input type="number" placeholder="Effectif maximum" required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    <input type="text" placeholder="Salle" required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    <input type="text" placeholder="Professeur principal" required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px;">
                    
                    <div style="display: flex; gap: 10px; margin-top: 20px;">
                        <button type="button" onclick="document.getElementById('classeModal').remove()" style="flex: 1; padding: 10px; background: #6c757d; color: white; border: none; border-radius: 5px; cursor: pointer;">Annuler</button>
                        <button type="submit" style="flex: 1; padding: 10px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; border: none; border-radius: 5px; cursor: pointer;">Créer</button>
                    </div>
                </form>
            </div>
        `;

        document.body.appendChild(modal);
    }

    showEleves(classeId) {
        const classe = this.classes.find(c => c.id === classeId);
        if (classe) {
            alert(`Affichage des ${classe.effectifActuel} élèves de la classe ${classe.nom}`);
        }
    }

    editClasse(id) {
        const classe = this.classes.find(c => c.id === id);
        if (classe) {
            alert(`Modification de la classe ${classe.nom}`);
        }
    }

    showStats() {
        const totalEleves = this.classes.reduce((sum, c) => sum + c.effectifActuel, 0);
        const capaciteMax = this.classes.reduce((sum, c) => sum + c.effectifMax, 0);
        const tauxGlobal = Math.round(totalEleves / capaciteMax * 100);
        
        alert(`📊 Statistiques des classes:
        - Nombre de classes: ${this.classes.length}
        - Total élèves: ${totalEleves}
        - Capacité maximale: ${capaciteMax}
        - Taux de remplissage global: ${tauxGlobal}%`);
    }
}

const classesModule = new ClassesModule();
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('classes')) {
        classesModule.init();
    }
});
