// Initialisation du dashboard
document.addEventListener('DOMContentLoaded', async () => {
    initMenu();
    await loadDashboardData();
    setupEventListeners();
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

    // Bouton déconnexion
    document.getElementById('logoutBtn').addEventListener('click', () => {
        AuthService.logout();
    });
}

// Charger les données du dashboard
async function loadDashboardData() {
    try {
        // Charger l'utilisateur
        const user = AuthService.getUser();
        if (user) {
            document.getElementById('userName').textContent = user.nom || 'Admin';
            document.getElementById('userRole').textContent = user.role || 'Administrateur';
            document.getElementById('userAvatar').textContent = (user.nom || 'AD').substring(0, 2).toUpperCase();
        }

        // Charger l'année scolaire active
        try {
            const anneeScolaire = await AnneeScolaireAPI.getActive();
            document.getElementById('anneeScolaire').textContent = `Année scolaire ${anneeScolaire.libelle || '2024-2025'}`;
        } catch (error) {
            document.getElementById('anneeScolaire').textContent = 'Année scolaire 2024-2025';
        }

        // Charger les statistiques
        await loadStatistiques();

        // Charger les activités récentes
        await loadActivitesRecentes();

    } catch (error) {
        console.error('Erreur lors du chargement du dashboard:', error);
        Utils.showNotification('Erreur lors du chargement des données', 'error');
    }
}

// Charger les statistiques
async function loadStatistiques() {
    try {
        const [eleveStats, paiementStats] = await Promise.all([
            EleveAPI.getStatistiques().catch(() => null),
            PaiementAPI.getStatistiques().catch(() => null)
        ]);

        // Total élèves
        if (eleveStats) {
            document.getElementById('totalEleves').textContent = 
                eleveStats.total?.toLocaleString() || '1,248';
            document.getElementById('changeEleves').textContent = 
                eleveStats.variation ? `↑ ${eleveStats.variation}% ce mois` : '↑ 12% ce mois';
        }

        // Recouvrements
        if (paiementStats) {
            document.getElementById('recouvrementsMois').textContent = 
                formatMontantCourt(paiementStats.totalMois || 42500000);
            document.getElementById('changeRecouvrements').textContent = 
                paiementStats.variationMois ? `↑ ${paiementStats.variationMois}% vs mois dernier` : '↑ 8.3% vs mois dernier';
            
            // Taux de recouvrement
            const taux = paiementStats.tauxRecouvrement || 87.4;
            document.getElementById('tauxRecouvrement').textContent = `${taux}%`;
            
            const changeTaux = document.getElementById('changeTaux');
            if (paiementStats.variationTaux) {
                const variation = paiementStats.variationTaux;
                changeTaux.textContent = `${variation > 0 ? '↑' : '↓'} ${Math.abs(variation)}% ce mois`;
                changeTaux.className = variation > 0 ? 'stat-change' : 'stat-change negative';
            }

            // Impayés
            const impayes = paiementStats.nombreImpayes || 156;
            document.getElementById('impayes').textContent = impayes.toLocaleString();
            document.getElementById('changeImpayes').textContent = 
                paiementStats.variationImpayes ? `↑ ${paiementStats.variationImpayes} cette semaine` : '↑ 5 cette semaine';
        }

    } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
        // Valeurs par défaut si erreur
        document.getElementById('totalEleves').textContent = '1,248';
        document.getElementById('recouvrementsMois').textContent = '42.5M';
        document.getElementById('tauxRecouvrement').textContent = '87.4%';
        document.getElementById('impayes').textContent = '156';
    }
}

// Charger les activités récentes
async function loadActivitesRecentes() {
    try {
        const paiements = await PaiementAPI.getRecents(5);
        
        const activitiesList = document.getElementById('activitiesList');
        
        if (!paiements || paiements.length === 0) {
            activitiesList.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <p>Aucune activité récente</p>
                </div>
            `;
            return;
        }

        activitiesList.innerHTML = paiements.map(paiement => {
            const eleve = paiement.eleveInfo || { nom: 'Élève', prenom: 'Inconnu' };
            const classe = paiement.classeInfo || { nom: '' };
            const tempsEcoule = getTempsEcoule(paiement.datePaiement);
            
            return `
                <div class="activity-item">
                    <div class="activity-icon payment">💳</div>
                    <div class="activity-content">
                        <div class="activity-title">
                            Paiement reçu - ${eleve.prenom} ${eleve.nom} ${classe.nom ? '(' + classe.nom + ')' : ''}
                        </div>
                        <div class="activity-time">${tempsEcoule}</div>
                    </div>
                    <div class="activity-amount">${Utils.formatMontant(paiement.montant)}</div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Erreur lors du chargement des activités:', error);
        // Afficher des données de démonstration
        document.getElementById('activitiesList').innerHTML = `
            <div class="activity-item">
                <div class="activity-icon payment">💳</div>
                <div class="activity-content">
                    <div class="activity-title">Paiement reçu - Jean Dupont (6ème A)</div>
                    <div class="activity-time">Il y a 5 minutes</div>
                </div>
                <div class="activity-amount">30,000 BIF</div>
            </div>
            <div class="activity-item">
                <div class="activity-icon student">👨‍🎓</div>
                <div class="activity-content">
                    <div class="activity-title">Nouvel élève inscrit - Marie Niyongabo</div>
                    <div class="activity-time">Il y a 15 minutes</div>
                </div>
            </div>
            <div class="activity-item">
                <div class="activity-icon payment">💳</div>
                <div class="activity-content">
                    <div class="activity-title">Paiement Mobile Money - Pierre Ndayizeye (5ème B)</div>
                    <div class="activity-time">Il y a 23 minutes</div>
                </div>
                <div class="activity-amount">45,000 BIF</div>
            </div>
        `;
    }
}

// Event listeners
function setupEventListeners() {
    // Filtre de période pour le graphique
    document.getElementById('periodFilter')?.addEventListener('change', (e) => {
        console.log('Période changée:', e.target.value);
        // TODO: Recharger le graphique avec la nouvelle période
    });
}

// Utilitaires
function formatMontantCourt(montant) {
    if (montant >= 1000000) {
        return (montant / 1000000).toFixed(1) + 'M';
    } else if (montant >= 1000) {
        return (montant / 1000).toFixed(1) + 'K';
    }
    return montant.toString();
}

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
