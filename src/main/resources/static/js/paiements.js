let currentEleve = null;
let searchTimeout = null;

// Initialisation
document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    loadPaiementsRecents();
});

function setupEventListeners() {
    // Recherche élève avec debounce
    document.getElementById('searchEleve').addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        const query = e.target.value.trim();
        
        if (query.length < 2) {
            hideSearchResults();
            return;
        }
        
        searchTimeout = setTimeout(() => {
            searchEleves(query);
        }, 300);
    });

    // Modes de paiement
    document.querySelectorAll('.payment-method').forEach(method => {
        method.addEventListener('click', () => {
            document.querySelectorAll('.payment-method').forEach(m => m.classList.remove('active'));
            method.classList.add('active');
            method.querySelector('input[type="radio"]').checked = true;
            updateSummary();
        });
    });

    // Montant change
    document.getElementById('montant').addEventListener('input', updateSummary);

    // Boutons
    document.getElementById('cancelBtn').addEventListener('click', resetForm);
    document.getElementById('paiementForm').addEventListener('submit', handleSubmit);
    
    document.getElementById('logoutBtn').addEventListener('click', () => {
        AuthService.logout();
    });
}

// Recherche élèves
async function searchEleves(query) {
    try {
        const results = await EleveAPI.search(query);
        displaySearchResults(results);
    } catch (error) {
        console.error('Erreur recherche:', error);
        hideSearchResults();
    }
}

function displaySearchResults(results) {
    const searchResults = document.getElementById('searchResults');
    
    if (!results || results.length === 0) {
        searchResults.innerHTML = '<div style="padding: 15px; text-align: center; color: #7f8c8d;">Aucun élève trouvé</div>';
        searchResults.classList.add('show');
        return;
    }
    
    searchResults.innerHTML = results.map(eleve => `
        <div class="search-result-item" onclick="selectEleve(${eleve.id})">
            <div class="result-name">${eleve.prenom} ${eleve.nom}</div>
            <div class="result-meta">
                ${eleve.classeInfo?.nom || 'Classe non assignée'} • 
                Matricule: ${eleve.matricule || 'N/A'}
            </div>
        </div>
    `).join('');
    
    searchResults.classList.add('show');
}

function hideSearchResults() {
    document.getElementById('searchResults').classList.remove('show');
}

// Sélection élève
async function selectEleve(eleveId) {
    try {
        Utils.showLoader();
        
        const eleve = await EleveAPI.getById(eleveId);
        currentEleve = eleve;
        
        // Masquer les résultats
        hideSearchResults();
        document.getElementById('searchEleve').value = `${eleve.prenom} ${eleve.nom}`;
        
        // Afficher les infos élève
        document.getElementById('studentInfo').style.display = 'block';
        document.getElementById('eleveId').value = eleve.id;
        
        // Remplir les infos
        document.getElementById('studentName').textContent = `${eleve.prenom} ${eleve.nom}`;
        document.getElementById('studentClass').textContent = eleve.classeInfo?.nom || 'Non assigné';
        document.getElementById('studentMatricule').textContent = eleve.matricule || 'N/A';
        
        // Charger les factures et paiements
        await loadEleveFinances(eleveId);
        
        Utils.hideLoader();
        
    } catch (error) {
        console.error('Erreur sélection élève:', error);
        Utils.hideLoader();
        Utils.showNotification('Erreur lors du chargement des données de l\'élève', 'error');
    }
}

// Charger les finances de l'élève
async function loadEleveFinances(eleveId) {
    try {
        const [factures, paiements] = await Promise.all([
            FactureAPI.getByEleve(eleveId),
            PaiementAPI.getByEleve(eleveId)
        ]);
        
        // Calculer les totaux
        const totalDu = factures.reduce((sum, f) => sum + (f.montantTotal || 0), 0);
        const totalPaye = paiements.reduce((sum, p) => sum + (p.montant || 0), 0);
        const solde = totalDu - totalPaye;
        
        document.getElementById('studentTotalDu').textContent = Utils.formatMontant(totalDu);
        document.getElementById('studentTotalPaye').textContent = Utils.formatMontant(totalPaye);
        document.getElementById('studentSolde').textContent = Utils.formatMontant(solde);
        
        // Vérifier dérogation
        const hasDerogation = currentEleve.derogations && currentEleve.derogations.length > 0;
        document.getElementById('studentDerogation').textContent = hasDerogation ? 'Oui' : 'Non';
        
        // Afficher historique
        displayPaiementHistory(paiements.slice(0, 5));
        
    } catch (error) {
        console.error('Erreur chargement finances:', error);
    }
}

// Afficher historique paiements
function displayPaiementHistory(paiements) {
    const container = document.getElementById('paiementsRecents');
    
    if (!paiements || paiements.length === 0) {
        container.innerHTML = '<div style="text-align: center; padding: 20px; color: #7f8c8d;">Aucun paiement</div>';
        return;
    }
    
    container.innerHTML = paiements.map(p => `
        <div class="paiement-history-item">
            <div class="history-info">
                <div class="history-date">${Utils.formatDate(p.datePaiement)}</div>
                <div class="history-mode">${getModePaiementLabel(p.modePaiement)}</div>
            </div>
            <div class="history-amount">${Utils.formatMontant(p.montant)}</div>
        </div>
    `).join('');
}

// Mettre à jour le récapitulatif
function updateSummary() {
    const montant = parseFloat(document.getElementById('montant').value) || 0;
    const mode = document.querySelector('input[name="modePaiement"]:checked')?.value || 'ESPECE';
    
    if (montant > 0 && currentEleve) {
        document.getElementById('summaryContent').style.display = 'block';
        document.getElementById('summaryMontant').textContent = Utils.formatMontant(montant);
        document.getElementById('summaryMode').textContent = getModePaiementLabel(mode);
        document.getElementById('summaryDate').textContent = new Date().toLocaleDateString('fr-FR');
        
        // Calculer nouveau solde
        const soldeActuel = parseFloat(document.getElementById('studentSolde').textContent.replace(/[^\d.-]/g, '')) || 0;
        const nouveauSolde = soldeActuel - montant;
        document.getElementById('summaryNewSolde').textContent = Utils.formatMontant(nouveauSolde);
    }
}

// Soumettre le paiement
async function handleSubmit(e) {
    e.preventDefault();
    
    if (!currentEleve) {
        Utils.showNotification('Veuillez sélectionner un élève', 'error');
        return;
    }
    
    const montant = parseFloat(document.getElementById('montant').value);
    if (!montant || montant <= 0) {
        Utils.showNotification('Veuillez entrer un montant valide', 'error');
        return;
    }
    
    const paiementData = {
        eleve: currentEleve.id,
        montant: montant,
        modePaiement: document.querySelector('input[name="modePaiement"]:checked').value,
        reference: document.getElementById('reference').value || null,
        notes: document.getElementById('notes').value || null,
        datePaiement: new Date().toISOString()
    };
    
    try {
        const submitBtn = document.getElementById('submitBtn');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Enregistrement...';
        
        await PaiementAPI.create(paiementData);
        
        Utils.showNotification('Paiement enregistré avec succès !', 'success');
        
        // Recharger les données
        await loadEleveFinances(currentEleve.id);
        await loadPaiementsRecents();
        
        // Réinitialiser le formulaire
        document.getElementById('montant').value = '';
        document.getElementById('reference').value = '';
        document.getElementById('notes').value = '';
        updateSummary();
        
        submitBtn.disabled = false;
        submitBtn.textContent = 'Enregistrer le paiement';
        
    } catch (error) {
        console.error('Erreur enregistrement paiement:', error);
        Utils.showNotification('Erreur lors de l\'enregistrement du paiement', 'error');
        
        const submitBtn = document.getElementById('submitBtn');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Enregistrer le paiement';
    }
}

// Charger paiements récents globaux
async function loadPaiementsRecents() {
    try {
        const paiements = await PaiementAPI.getRecents(10);
        // Les paiements récents peuvent être affichés ailleurs si nécessaire
    } catch (error) {
        console.error('Erreur chargement paiements récents:', error);
    }
}

// Réinitialiser le formulaire
function resetForm() {
    currentEleve = null;
    document.getElementById('searchEleve').value = '';
    document.getElementById('studentInfo').style.display = 'none';
    document.getElementById('summaryContent').style.display = 'none';
    document.getElementById('paiementForm').reset();
    hideSearchResults();
}

// Utilitaires
function getModePaiementLabel(mode) {
    const labels = {
        'ESPECE': 'Espèces',
        'MOBILE_MONEY': 'Mobile Money',
        'VIREMENT': 'Virement bancaire',
        'CHEQUE': 'Chèque'
    };
    return labels[mode] || mode;
}
