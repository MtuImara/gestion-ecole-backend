// Dashboard Enseignant - Interface Premium
document.addEventListener('DOMContentLoaded', async () => {
    initMenu();
    await loadEnseignantData();
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

// Charger les données de l'enseignant
async function loadEnseignantData() {
    try {
        const user = AuthService.getUser();
        if (user) {
            document.getElementById('enseignantName').textContent = user.nom || 'Enseignant';
            document.getElementById('userName').textContent = user.nom || 'Enseignant';
            document.getElementById('userAvatar').textContent = (user.nom || 'EN').substring(0, 2).toUpperCase();
            document.getElementById('matiere').textContent = user.matiere || 'Enseignant';
        }

        // Date du jour
        const today = new Date();
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('dateJour').textContent = today.toLocaleDateString('fr-FR', options);

        await loadStatistiques();
        await loadClasses();
        await loadPerformanceClasses();
        await loadElevesDifficulte();
        await loadProchainsCours();

    } catch (error) {
        console.error('Erreur:', error);
    }
}

// Charger statistiques
async function loadStatistiques() {
    try {
        const enseignantId = AuthService.getUserId();
        const stats = await EnseignantAPI.getStatistiques(enseignantId);
        
        if (stats) {
            document.getElementById('nombreClasses').textContent = stats.nombreClasses || 4;
            document.getElementById('changeClasses').textContent = `${stats.nombreHeures || 18} heures/semaine`;
            
            document.getElementById('totalEleves').textContent = stats.totalEleves || 120;
            document.getElementById('changeEleves').textContent = `${stats.nombreEleves || 30} élèves/classe`;
            
            document.getElementById('moyenneGenerale').textContent = `${stats.moyenneGenerale || 14.5}/20`;
            document.getElementById('changeMoyenne').textContent = stats.tendance || '↑ 0.5 ce mois';
            
            document.getElementById('tauxPresence').textContent = `${stats.tauxPresence || 92}%`;
        }

    } catch (error) {
        console.error('Erreur statistiques:', error);
        document.getElementById('nombreClasses').textContent = '4';
        document.getElementById('changeClasses').textContent = '18 heures/semaine';
        document.getElementById('totalEleves').textContent = '120';
        document.getElementById('changeEleves').textContent = '30 élèves/classe';
        document.getElementById('moyenneGenerale').textContent = '14.5/20';
        document.getElementById('changeMoyenne').textContent = '↑ 0.5 ce mois';
        document.getElementById('tauxPresence').textContent = '92%';
    }
}

// Charger classes
async function loadClasses() {
    try {
        const enseignantId = AuthService.getUserId();
        const classes = await EnseignantAPI.getClasses(enseignantId);
        
        const classesGrid = document.getElementById('classesGrid');
        const classeFilter = document.getElementById('classeFilter');
        
        if (!classes || classes.length === 0) {
            classesGrid.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 40px; color: #7f8c8d;">
                    <p>Aucune classe assignée</p>
                </div>
            `;
            return;
        }

        const gradients = [
            'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
            'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
            'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
        ];

        classesGrid.innerHTML = classes.map((classe, index) => {
            const gradient = gradients[index % gradients.length];
            
            return `
                <div class="stat-card" onclick="window.location.href='/classe-details.html?id=${classe.id}'">
                    <div class="stat-header">
                        <div>
                            <div class="stat-label">Classe</div>
                            <div style="font-size: 24px; font-weight: 700; color: #2c3e50; margin: 10px 0;">
                                ${classe.nom}
                            </div>
                            <div style="font-size: 14px; color: #7f8c8d;">
                                ${classe.nombreEleves || 30} élèves
                            </div>
                        </div>
                        <div class="stat-icon" style="background: ${gradient};">📚</div>
                    </div>
                    <div style="margin-top: 15px; padding-top: 15px; border-top: 2px solid #f0f0f0;">
                        <div style="display: flex; justify-content: space-between; font-size: 13px;">
                            <span style="color: #7f8c8d;">Moyenne</span>
                            <span style="font-weight: 700; color: #667eea;">${classe.moyenne || '14.5'}/20</span>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        // Remplir filtre
        classeFilter.innerHTML = '<option value="all">Toutes les classes</option>' +
            classes.map(c => `<option value="${c.id}">${c.nom}</option>`).join('');

    } catch (error) {
        console.error('Erreur classes:', error);
        document.getElementById('classesGrid').innerHTML = `
            <div class="stat-card">
                <div class="stat-header">
                    <div>
                        <div class="stat-label">Classe</div>
                        <div style="font-size: 24px; font-weight: 700; color: #2c3e50; margin: 10px 0;">6ème A</div>
                        <div style="font-size: 14px; color: #7f8c8d;">30 élèves</div>
                    </div>
                    <div class="stat-icon blue">📚</div>
                </div>
                <div style="margin-top: 15px; padding-top: 15px; border-top: 2px solid #f0f0f0;">
                    <div style="display: flex; justify-content: space-between; font-size: 13px;">
                        <span style="color: #7f8c8d;">Moyenne</span>
                        <span style="font-weight: 700; color: #667eea;">14.5/20</span>
                    </div>
                </div>
            </div>
        `;
    }
}

// Charger performance des classes
async function loadPerformanceClasses() {
    try {
        const enseignantId = AuthService.getUserId();
        const performances = await EnseignantAPI.getPerformances(enseignantId);
        
        const container = document.getElementById('performanceChart');
        
        if (!performances || performances.length === 0) {
            container.innerHTML = `<p style="text-align: center; color: #7f8c8d;">Aucune donnée disponible</p>`;
            return;
        }

        container.innerHTML = performances.map((perf, index) => {
            const couleurs = ['#667eea', '#f093fb', '#4facfe', '#43e97b'];
            const couleur = couleurs[index % couleurs.length];
            const pourcentage = Math.round((perf.moyenne / 20) * 100);
            
            return `
                <div style="margin-bottom: 20px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                        <span style="font-weight: 600; color: #2c3e50;">${perf.classe}</span>
                        <span style="font-weight: 700; color: ${couleur};">${perf.moyenne}/20</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${pourcentage}%; background: ${couleur};"></div>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Erreur performances:', error);
        document.getElementById('performanceChart').innerHTML = `
            <div style="margin-bottom: 20px;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                    <span style="font-weight: 600; color: #2c3e50;">6ème A</span>
                    <span style="font-weight: 700; color: #667eea;">14.5/20</span>
                </div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: 72%; background: #667eea;"></div>
                </div>
            </div>
        `;
    }
}

// Charger élèves en difficulté
async function loadElevesDifficulte() {
    try {
        const enseignantId = AuthService.getUserId();
        const eleves = await EnseignantAPI.getElevesDifficulte(enseignantId);
        
        const liste = document.getElementById('elevesDifficulteList');
        
        if (!eleves || eleves.length === 0) {
            liste.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <div style="font-size: 48px; margin-bottom: 15px;">✅</div>
                    <p>Tous les élèves sont à jour!</p>
                </div>
            `;
            return;
        }

        liste.innerHTML = eleves.map(eleve => `
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">⚠️</div>
                <div class="activity-content">
                    <div class="activity-title">${eleve.prenom} ${eleve.nom} - ${eleve.classe}</div>
                    <div class="activity-time">Moyenne: ${eleve.moyenne}/20 • ${eleve.motif}</div>
                </div>
                <button class="btn-secondary" onclick="contacterParent(${eleve.id})">Contacter</button>
            </div>
        `).join('');

    } catch (error) {
        console.error('Erreur élèves difficulté:', error);
        document.getElementById('elevesDifficulteList').innerHTML = `
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">⚠️</div>
                <div class="activity-content">
                    <div class="activity-title">Pierre Ndayizeye - 6ème A</div>
                    <div class="activity-time">Moyenne: 8.5/20 • Absences répétées</div>
                </div>
                <button class="btn-secondary" onclick="contacterParent(1)">Contacter</button>
            </div>
        `;
    }
}

// Charger prochains cours
async function loadProchainsCours() {
    try {
        const enseignantId = AuthService.getUserId();
        const cours = await EnseignantAPI.getProchainsCours(enseignantId);
        
        const liste = document.getElementById('prochainsCoursList');
        
        if (!cours || cours.length === 0) {
            liste.innerHTML = `<p style="text-align: center; padding: 40px; color: #7f8c8d;">Aucun cours programmé</p>`;
            return;
        }

        liste.innerHTML = cours.map((c, index) => {
            const icones = ['📚', '🔬', '🌍', '📐', '💻'];
            const icone = icones[index % icones.length];
            
            return `
                <div class="activity-item">
                    <div class="activity-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">${icone}</div>
                    <div class="activity-content">
                        <div class="activity-title">${c.matiere} - ${c.classe}</div>
                        <div class="activity-time">${c.horaire} • Salle ${c.salle}</div>
                    </div>
                    <div style="font-size: 13px; color: #7f8c8d;">${c.jour}</div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Erreur cours:', error);
        document.getElementById('prochainsCoursList').innerHTML = `
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">📚</div>
                <div class="activity-content">
                    <div class="activity-title">Mathématiques - 6ème A</div>
                    <div class="activity-time">08h00 - 10h00 • Salle 101</div>
                </div>
                <div style="font-size: 13px; color: #7f8c8d;">Demain</div>
            </div>
        `;
    }
}

// Actions
function saisirNotes() {
    window.location.href = '/notes.html';
}

function marquerPresences() {
    window.location.href = '/presences.html';
}

function consulterEmploiTemps() {
    window.location.href = '/emploi-temps.html';
}

function envoyerMessage() {
    window.location.href = '/communication.html';
}

function contacterParent(eleveId) {
    window.location.href = `/communication.html?eleve=${eleveId}`;
}
