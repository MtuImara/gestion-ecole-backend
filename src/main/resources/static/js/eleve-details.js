// Détails de l'élève
let currentEleve = null;

document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const eleveId = urlParams.get('id');
    
    if (eleveId) {
        loadEleveDetails(eleveId);
    } else {
        alert('Aucun élève sélectionné');
        window.location.href = '/eleves.html';
    }
    
    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        AuthService.logout();
    });
});

async function loadEleveDetails(id) {
    try {
        currentEleve = await EleveAPI.getById(id);
        displayEleveDetails(currentEleve);
    } catch (error) {
        console.error('Erreur chargement élève:', error);
        alert('Erreur lors du chargement des détails');
        window.location.href = '/eleves.html';
    }
}

function displayEleveDetails(eleve) {
    // Nom et matricule
    document.getElementById('eleveNom').textContent = eleve.prenom + ' ' + eleve.nom;
    document.getElementById('eleveMatricule').textContent = 'Matricule: ' + (eleve.matricule || 'N/A');
    
    // Statut
    const badge = document.getElementById('eleveStatut');
    badge.textContent = eleve.statut || 'ACTIF';
    badge.className = 'badge ' + (eleve.statut === 'ACTIF' ? 'badge-active' : 'badge-inactive');
    
    // Informations personnelles
    const infosPerso = document.getElementById('infosPersonnelles');
    infosPerso.innerHTML = 
        <div class='detail-row'>
            <div class='detail-label'>Nom complet</div>
            <div class='detail-value'>+eleve.prenom+ +eleve.nom+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Date de naissance</div>
            <div class='detail-value'>+(eleve.dateNaissance ? new Date(eleve.dateNaissance).toLocaleDateString('fr-FR') : 'N/A')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Lieu de naissance</div>
            <div class='detail-value'>+(eleve.lieuNaissance || 'N/A')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Genre</div>
            <div class='detail-value'>+(eleve.genre || 'N/A')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Adresse</div>
            <div class='detail-value'>+(eleve.adresse || 'N/A')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Téléphone</div>
            <div class='detail-value'>+(eleve.telephone || 'N/A')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Email</div>
            <div class='detail-value'>+(eleve.email || 'N/A')+</div>
        </div>
    ;
    
    // Informations scolaires
    const infosScolaires = document.getElementById('infosScolaires');
    infosScolaires.innerHTML = 
        <div class='detail-row'>
            <div class='detail-label'>Classe</div>
            <div class='detail-value'>+(eleve.classeInfo?.nom || 'Non assigné')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Niveau</div>
            <div class='detail-value'>+(eleve.classeInfo?.niveauInfo?.designation || 'N/A')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Année scolaire</div>
            <div class='detail-value'>+(eleve.classeInfo?.anneeScolaireInfo?.libelle || 'N/A')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Boursier</div>
            <div class='detail-value'>+(eleve.boursier ? 'Oui' : 'Non')+</div>
        </div>
        <div class='detail-row'>
            <div class='detail-label'>Statut</div>
            <div class='detail-value'>+(eleve.statut || 'ACTIF')+</div>
        </div>
    ;
}

function effectuerPaiement() {
    if (currentEleve) {
        window.location.href = '/paiements.html?eleveId=' + currentEleve.id + '&matricule=' + encodeURIComponent(currentEleve.matricule);
    }
}

function editEleve() {
    if (currentEleve) {
        window.location.href = '/eleves.html?edit=' + currentEleve.id;
    }
}
