// Gestion du formulaire modal de classe

let isEditMode = false;
let currentClasseId = null;

// Ouvrir le modal de création
function openCreateModal() {
    isEditMode = false;
    currentClasseId = null;
    document.getElementById('modalTitle').textContent = 'Nouvelle Classe';
    document.getElementById('classeForm').reset();
    document.getElementById('classeModal').style.display = 'flex';
    loadNiveaux();
    loadAnneesScolaires();
}

// Ouvrir le modal d'édition
async function openEditModal(classeId) {
    isEditMode = true;
    currentClasseId = classeId;
    document.getElementById('modalTitle').textContent = 'Modifier Classe';
    document.getElementById('classeModal').style.display = 'flex';
    
    try {
        const classe = await ClasseAPI.getById(classeId);
        populateForm(classe);
        await loadNiveaux();
        await loadAnneesScolaires();
    } catch (error) {
        console.error('Erreur chargement classe:', error);
        Utils.showNotification('Erreur lors du chargement', 'error');
        closeModal();
    }
}

// Fermer le modal
function closeModal() {
    document.getElementById('classeModal').style.display = 'none';
    document.getElementById('classeForm').reset();
    isEditMode = false;
    currentClasseId = null;
}

// Charger les niveaux pour le select
async function loadNiveaux() {
    try {
        const niveaux = await NiveauAPI.getAll();
        const select = document.getElementById('niveauId');
        select.innerHTML = '<option value="">Sélectionner un niveau</option>';
        niveaux.forEach(niveau => {
            select.innerHTML += <option value="+niveau.id+">+niveau.designation+</option>;
        });
    } catch (error) {
        console.error('Erreur chargement niveaux:', error);
    }
}

// Charger les années scolaires
async function loadAnneesScolaires() {
    try {
        const annees = await AnneeScolaireAPI.getAll();
        const select = document.getElementById('anneeScolaireId');
        select.innerHTML = '<option value="">Sélectionner une année</option>';
        annees.forEach(annee => {
            select.innerHTML += <option value="+annee.id+">+(annee.libelle || annee.designation)+</option>;
        });
    } catch (error) {
        console.error('Erreur chargement années:', error);
    }
}

// Remplir le formulaire avec les données
function populateForm(classe) {
    document.getElementById('nom').value = classe.nom || '';
    document.getElementById('niveauId').value = classe.niveauId || '';
    document.getElementById('anneeScolaireId').value = classe.anneeScolaireId || '';
    document.getElementById('capacite').value = classe.capacite || '';
    document.getElementById('fraisInscription').value = classe.fraisInscription || '';
    document.getElementById('fraisScolarite').value = classe.fraisScolarite || '';
}

// Soumettre le formulaire
async function submitForm(event) {
    event.preventDefault();
    
    const formData = {
        nom: document.getElementById('nom').value,
        niveauId: document.getElementById('niveauId').value || null,
        anneeScolaireId: document.getElementById('anneeScolaireId').value || null,
        capacite: parseInt(document.getElementById('capacite').value) || 0,
        fraisInscription: parseFloat(document.getElementById('fraisInscription').value) || 0,
        fraisScolarite: parseFloat(document.getElementById('fraisScolarite').value) || 0
    };
    
    try {
        if (isEditMode) {
            await ClasseAPI.update(currentClasseId, formData);
            Utils.showNotification('Classe modifiée avec succès', 'success');
        } else {
            await ClasseAPI.create(formData);
            Utils.showNotification('Classe créée avec succès', 'success');
        }
        
        closeModal();
        loadClasses();
        loadStatistiques();
    } catch (error) {
        console.error('Erreur sauvegarde:', error);
        Utils.showNotification('Erreur lors de la sauvegarde', 'error');
    }
}

// Mise à jour des fonctions globales
function openAddModal() {
    openCreateModal();
}

function editClasse(id) {
    openEditModal(id);
}
