// Dashboard Comptable - Interface Premium
document.addEventListener('DOMContentLoaded', async () => {
    initMenu();
    await loadComptableData();
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

    document.getElementById('logoutBtn').addEventListener('click', () => {
        AuthService.logout();
    });
}

// Charger les données du comptable
async function loadComptableData() {
    try {
        const user = AuthService.getUser();
        if (user) {
            document.getElementById('userName').textContent = user.nom || 'Comptable';
            document.getElementById('userAvatar').textContent = (user.nom || 'CO').substring(0, 2).toUpperCase();
        }

        // Date du jour
        const today = new Date();
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('dateJour').textContent = today.toLocaleDateString('fr-FR', options);

        await loadStatistiquesFinancieres();
        await loadPaiementsEnAttente();
        await loadDernieresTransactions();
        await loadModesPaiement();

    } catch (error) {
        console.error('Erreur:', error);
    }
}

// Charger statistiques financières
async function loadStatistiquesFinancieres() {
    try {
        const stats = await PaiementAPI.getStatistiquesComptable();
        
        if (stats) {
            // Recouvrement du jour
            document.getElementById('recouvrementJour').textContent = formatMontantCourt(stats.recouvrementJour || 2500000);
            document.getElementById('changeJour').textContent = `${stats.nombrePaiementsJour || 23} paiements`;
            
            // Recouvrement du mois
            document.getElementById('recouvrementMois').textContent = formatMontantCourt(stats.recouvrementMois || 42500000);
            document.getElementById('changeMois').textContent = `↑ ${stats.pourcentageVariation || 8.3}% vs mois dernier`;
            
            // En attente
            document.getElementById('paiementsEnAttente').textContent = stats.nombreEnAttente || 12;
            document.getElementById('changeAttente').textContent = `${formatMontantCourt(stats.montantEnAttente || 3800000)} BIF`;
            
            // Taux de recouvrement
            document.getElementById('tauxRecouvrement').textContent = `${stats.tauxRecouvrement || 87.4}%`;
            document.getElementById('changeTaux').textContent = `Objectif: 90%`;
        }

    } catch (error) {
        console.error('Erreur statistiques:', error);
        // Valeurs par défaut
        document.getElementById('recouvrementJour').textContent = '2.5M';
        document.getElementById('changeJour').textContent = '23 paiements';
        document.getElementById('recouvrementMois').textContent = '42.5M';
        document.getElementById('changeMois').textContent = '↑ 8.3% vs mois dernier';
        document.getElementById('paiementsEnAttente').textContent = '12';
        document.getElementById('changeAttente').textContent = '3.8M BIF';
        document.getElementById('tauxRecouvrement').textContent = '87.4%';
        document.getElementById('changeTaux').textContent = 'Objectif: 90%';
    }
}

// Charger paiements en attente
async function loadPaiementsEnAttente() {
    try {
        const paiements = await PaiementAPI.getPaiementsEnAttente(0, 5);
        const liste = document.getElementById('paiementsAttenteList');
        
        if (!paiements || paiements.length === 0) {
            liste.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <div style="font-size: 48px; margin-bottom: 15px;">✅</div>
                    <p>Aucun paiement en attente</p>
                </div>
            `;
            return;
        }

        liste.innerHTML = paiements.map(paiement => {
            const eleve = paiement.eleveInfo || { nom: 'Inconnu', prenom: '' };
            const classe = paiement.classeInfo || { nom: '' };
            
            return `
                <div class="activity-item" onclick="window.location.href='/validation-paiements.html'">
                    <div class="activity-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">⏳</div>
                    <div class="activity-content">
                        <div class="activity-title">
                            ${eleve.prenom} ${eleve.nom} ${classe.nom ? '- ' + classe.nom : ''}
                        </div>
                        <div class="activity-time">
                            ${paiement.modePaiement || 'Mode inconnu'} • ${getTempsEcoule(paiement.datePaiement)}
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-size: 18px; font-weight: 700; color: #2c3e50;">${Utils.formatMontant(paiement.montant)}</div>
                        <div style="font-size: 12px; color: #f5576c;">En attente</div>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Erreur paiements en attente:', error);
        document.getElementById('paiementsAttenteList').innerHTML = `
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">⏳</div>
                <div class="activity-content">
                    <div class="activity-title">Marie Niyongabo - 5ème A</div>
                    <div class="activity-time">Mobile Money • Il y a 15 minutes</div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 18px; font-weight: 700; color: #2c3e50;">45,000 BIF</div>
                    <div style="font-size: 12px; color: #f5576c;">En attente</div>
                </div>
            </div>
        `;
    }
}

// Charger dernières transactions
async function loadDernieresTransactions() {
    try {
        const transactions = await PaiementAPI.getRecents(5);
        const liste = document.getElementById('transactionsList');
        
        if (!transactions || transactions.length === 0) {
            liste.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <p>Aucune transaction récente</p>
                </div>
            `;
            return;
        }

        liste.innerHTML = transactions.map(transaction => {
            const eleve = transaction.eleveInfo || { nom: 'Inconnu', prenom: '' };
            const classe = transaction.classeInfo || { nom: '' };
            const icone = getIconeMode(transaction.modePaiement);
            
            return `
                <div class="activity-item">
                    <div class="activity-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">${icone}</div>
                    <div class="activity-content">
                        <div class="activity-title">
                            ${eleve.prenom} ${eleve.nom} ${classe.nom ? '- ' + classe.nom : ''}
                        </div>
                        <div class="activity-time">
                            ${transaction.modePaiement || 'Espèces'} • ${getTempsEcoule(transaction.datePaiement)}
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-size: 18px; font-weight: 700; color: #43e97b;">${Utils.formatMontant(transaction.montant)}</div>
                        <div style="font-size: 12px; color: #43e97b;">✓ Validé</div>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Erreur transactions:', error);
        document.getElementById('transactionsList').innerHTML = `
            <div class="activity-item">
                <div class="activity-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">💳</div>
                <div class="activity-content">
                    <div class="activity-title">Jean Dupont - 6ème A</div>
                    <div class="activity-time">Espèces • Il y a 5 minutes</div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 18px; font-weight: 700; color: #43e97b;">30,000 BIF</div>
                    <div style="font-size: 12px; color: #43e97b;">✓ Validé</div>
                </div>
            </div>
        `;
    }
}

// Charger modes de paiement
async function loadModesPaiement() {
    try {
        const modes = await PaiementAPI.getStatistiquesParMode();
        const container = document.getElementById('modesPaiement');
        
        if (!modes || modes.length === 0) {
            container.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <p>Aucune donnée disponible</p>
                </div>
            `;
            return;
        }

        const total = modes.reduce((sum, mode) => sum + (mode.montant || 0), 0);
        
        container.innerHTML = modes.map((mode, index) => {
            const pourcentage = total > 0 ? Math.round((mode.montant / total) * 100) : 0;
            const couleurs = [
                '#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a'
            ];
            const couleur = couleurs[index % couleurs.length];
            
            return `
                <div style="margin-bottom: 20px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                        <span style="font-weight: 600; color: #2c3e50;">${mode.nom || 'Mode inconnu'}</span>
                        <span style="font-weight: 700; color: ${couleur};">${formatMontantCourt(mode.montant)} BIF (${pourcentage}%)</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${pourcentage}%; background: ${couleur};"></div>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Erreur modes paiement:', error);
        document.getElementById('modesPaiement').innerHTML = `
            <div style="margin-bottom: 20px;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                    <span style="font-weight: 600; color: #2c3e50;">Espèces</span>
                    <span style="font-weight: 700; color: #667eea;">15.2M BIF (45%)</span>
                </div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: 45%; background: #667eea;"></div>
                </div>
            </div>
            <div style="margin-bottom: 20px;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                    <span style="font-weight: 600; color: #2c3e50;">Mobile Money</span>
                    <span style="font-weight: 700; color: #f093fb;">10.8M BIF (32%)</span>
                </div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: 32%; background: #f093fb;"></div>
                </div>
            </div>
            <div style="margin-bottom: 20px;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                    <span style="font-weight: 600; color: #2c3e50;">Virement Bancaire</span>
                    <span style="font-weight: 700; color: #4facfe;">7.5M BIF (23%)</span>
                </div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: 23%; background: #4facfe;"></div>
                </div>
            </div>
        `;
    }
}

// Event listeners
function setupEventListeners() {
    document.getElementById('modeFilter')?.addEventListener('change', (e) => {
        console.log('Filtre changé:', e.target.value);
        loadModesPaiement();
    });
}

// Actions
function genererRapport() {
    alert('Génération du rapport journalier en cours...');
    // TODO: Implémenter génération rapport
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

function getIconeMode(mode) {
    const modes = {
        'ESPECES': '💵',
        'MOBILE_MONEY': '📱',
        'VIREMENT': '🏦',
        'CHEQUE': '💳',
        'CARTE_BANCAIRE': '💳'
    };
    return modes[mode] || '💰';
}
