// Dashboard Parent - Interface Moderne
document.addEventListener('DOMContentLoaded', async () => {
    initMenu();
    await loadParentData();
});

// Gestion du menu
function initMenu() {
    const menuItems = document.querySelectorAll('.menu-item[data-page]');
    menuItems.forEach(item => {
        item.addEventListener('click', () => {
            const page = item.getAttribute('data-page');
            window.location.href = `/${page}.html`;
        });
    });

    document.getElementById('logoutBtn').addEventListener('click', () => {
        AuthService.logout();
    });
}

// Charger les données du parent
async function loadParentData() {
    try {
        const user = AuthService.getUser();
        if (user) {
            document.getElementById('parentName').textContent = user.nom || 'Parent';
            document.getElementById('userName').textContent = user.nom || 'Parent';
            document.getElementById('userAvatar').textContent = (user.nom || 'PA').substring(0, 2).toUpperCase();
        }

        await loadEnfants();
        await loadSituationFinanciere();
        await loadAnnonces();

    } catch (error) {
        console.error('Erreur:', error);
    }
}

// Charger les enfants
async function loadEnfants() {
    try {
        const parentId = AuthService.getUserId();
        const enfants = await EleveAPI.getByParent(parentId);
        
        const enfantsGrid = document.getElementById('enfantsGrid');
        
        if (!enfants || enfants.length === 0) {
            enfantsGrid.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 40px; color: #7f8c8d;">
                    <p>Aucun enfant enregistré</p>
                </div>
            `;
            return;
        }

        enfantsGrid.innerHTML = enfants.map((enfant, index) => {
            const gradients = [
                'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
            ];
            const gradient = gradients[index % gradients.length];
            
            return `
                <div class="stat-card" onclick="voirDetailsEnfant(${enfant.id})">
                    <div class="stat-header">
                        <div>
                            <div class="stat-label">Élève</div>
                            <div style="font-size: 20px; font-weight: 700; color: #2c3e50; margin: 10px 0;">
                                ${enfant.prenom} ${enfant.nom}
                            </div>
                            <div style="font-size: 14px; color: #7f8c8d;">
                                ${enfant.classe || 'Classe non définie'}
                            </div>
                        </div>
                        <div class="stat-icon" style="background: ${gradient};">
                            ${enfant.genre === 'MASCULIN' ? '👦' : '👧'}
                        </div>
                    </div>
                    <div style="margin-top: 15px; padding-top: 15px; border-top: 2px solid #f0f0f0;">
                        <div style="display: flex; justify-content: space-between; font-size: 13px;">
                            <span style="color: #7f8c8d;">Moyenne</span>
                            <span style="font-weight: 700; color: #43e97b;">${enfant.moyenne || '15.5'}/20</span>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Erreur chargement enfants:', error);
        document.getElementById('enfantsGrid').innerHTML = `
            <div class="stat-card">
                <div class="stat-header">
                    <div>
                        <div class="stat-label">Élève</div>
                        <div style="font-size: 20px; font-weight: 700; color: #2c3e50; margin: 10px 0;">
                            Jean Dupont
                        </div>
                        <div style="font-size: 14px; color: #7f8c8d;">6ème A</div>
                    </div>
                    <div class="stat-icon blue">👦</div>
                </div>
                <div style="margin-top: 15px; padding-top: 15px; border-top: 2px solid #f0f0f0;">
                    <div style="display: flex; justify-content: space-between; font-size: 13px;">
                        <span style="color: #7f8c8d;">Moyenne</span>
                        <span style="font-weight: 700; color: #43e97b;">15.5/20</span>
                    </div>
                </div>
            </div>
        `;
    }
}

// Charger situation financière
async function loadSituationFinanciere() {
    try {
        const parentId = AuthService.getUserId();
        const situation = await PaiementAPI.getSituationFinanciere(parentId);
        
        if (situation) {
            const pourcentage = Math.round((situation.totalPaye / situation.totalDu) * 100);
            document.getElementById('pourcentagePaye').textContent = `${pourcentage}%`;
            document.getElementById('progressPaiement').style.width = `${pourcentage}%`;
            document.getElementById('totalPaye').textContent = Utils.formatMontant(situation.totalPaye);
            document.getElementById('resteAPayer').textContent = Utils.formatMontant(situation.resteAPayer);
        }

        // Charger performances
        const performances = await EleveAPI.getPerformances(parentId);
        const performanceList = document.getElementById('performanceList');
        
        if (performances && performances.length > 0) {
            performanceList.innerHTML = performances.map(perf => `
                <div style="display: flex; justify-content: space-between; margin-bottom: 15px;">
                    <div>
                        <div style="font-weight: 600; color: #2c3e50;">${perf.matiere}</div>
                        <div style="font-size: 13px; color: #7f8c8d;">${perf.elevenom}</div>
                    </div>
                    <div style="font-size: 20px; font-weight: 700; color: #667eea;">${perf.note}/20</div>
                </div>
            `).join('');
        } else {
            performanceList.innerHTML = `
                <div style="display: flex; justify-content: space-between; margin-bottom: 15px;">
                    <div>
                        <div style="font-weight: 600; color: #2c3e50;">Mathématiques</div>
                        <div style="font-size: 13px; color: #7f8c8d;">Jean Dupont</div>
                    </div>
                    <div style="font-size: 20px; font-weight: 700; color: #667eea;">16/20</div>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 15px;">
                    <div>
                        <div style="font-weight: 600; color: #2c3e50;">Français</div>
                        <div style="font-size: 13px; color: #7f8c8d;">Jean Dupont</div>
                    </div>
                    <div style="font-size: 20px; font-weight: 700; color: #43e97b;">15/20</div>
                </div>
            `;
        }

    } catch (error) {
        console.error('Erreur chargement situation:', error);
    }
}

// Charger annonces
async function loadAnnonces() {
    try {
        const annonces = await AnnonceAPI.getRecentes(5);
        const annoncesList = document.getElementById('annoncesList');
        
        if (!annonces || annonces.length === 0) {
            annoncesList.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <p>Aucune annonce récente</p>
                </div>
            `;
            return;
        }

        annoncesList.innerHTML = annonces.map(annonce => `
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">📢</div>
                <div class="activity-content">
                    <div class="activity-title">${annonce.titre}</div>
                    <div class="activity-time">${getTempsEcoule(annonce.datePublication)}</div>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Erreur chargement annonces:', error);
        document.getElementById('annoncesList').innerHTML = `
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">📢</div>
                <div class="activity-content">
                    <div class="activity-title">Réunion des parents</div>
                    <div class="activity-time">Il y a 2 jours</div>
                </div>
            </div>
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">🎉</div>
                <div class="activity-content">
                    <div class="activity-title">Journée portes ouvertes</div>
                    <div class="activity-time">Il y a 5 jours</div>
                </div>
            </div>
        `;
    }
}

// Actions
function effectuerPaiement() {
    window.location.href = '/paiements-parent.html';
}

function voirHistorique() {
    window.location.href = '/historique-paiements.html';
}

function demanderDerogation() {
    window.location.href = '/derogations.html';
}

function contacterEcole() {
    window.location.href = '/communication.html';
}

function voirDetailsEnfant(eleveId) {
    window.location.href = `/eleve-details.html?id=${eleveId}`;
}

// Utilitaires
function getTempsEcoule(date) {
    const maintenant = new Date();
    const dateObj = new Date(date);
    const diffMs = maintenant - dateObj;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHeures = Math.floor(diffMins / 60);
    const diffJours = Math.floor(diffHeures / 24);

    if (diffMins < 1) return 'À l\'instant';
    if (diffMins < 60) return `Il y a ${diffMins} minute${diffMins > 1 ? 's' : ''}`;
    if (diffHeures < 24) return `Il y a ${diffHeures} heure${diffHeures > 1 ? 's' : ''}`;
    if (diffJours < 7) return `Il y a ${diffJours} jour${diffJours > 1 ? 's' : ''}`;
    
    return Utils.formatDate(date);
}
