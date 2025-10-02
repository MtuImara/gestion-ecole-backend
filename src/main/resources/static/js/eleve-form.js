// Gestion du formulaire modal d'élève

let isEditMode = false;
let currentEleveId = null;

// Ouvrir le modal de création
function openCreateModal() {
    isEditMode = false;
    currentEleveId = null;
    document.getElementById('modalTitle').textContent = 'Nouvel Élève';
    document.getElementById('eleveForm').reset();
    document.getElementById('eleveModal').style.display = 'flex';
    loadClasses();
}

// Ouvrir le modal d'édition
async function openEditModal(eleveId) {
    isEditMode = true;
    currentEleveId = eleveId;
    document.getElementById('modalTitle').textContent = 'Modifier Élève';
    document.getElementById('eleveModal').style.display = 'flex';
    
    try {
        const eleve = await EleveAPI.getById(eleveId);
        populateForm(eleve);
        await loadClasses();
    } catch (error) {
        console.error('Erreur chargement élève:', error);
        Utils.showNotification('Erreur lors du chargement', 'error');
        closeModal();
    }
}

// Fermer le modal
function closeModal() {
    document.getElementById('eleveModal').style.display = 'none';
    document.getElementById('eleveForm').reset();
    isEditMode = false;
    currentEleveId = null;
}

// Charger les classes pour le select
async function loadClasses() {
    try {
        const classes = await ClasseAPI.getAll();
        const select = document.getElementById('classeId');
        select.innerHTML = '<option value="">Sélectionner une classe</option>';
        classes.forEach(classe => {
            select.innerHTML += <option value="+classe.id+">+classe.nom+</option>;
        });
    } catch (error) {
        console.error('Erreur chargement classes:', error);
    }
}

// Remplir le formulaire avec les données
function populateForm(eleve) {
    document.getElementById('nom').value = eleve.nom || '';
    document.getElementById('prenom').value = eleve.prenom || '';
    document.getElementById('matricule').value = eleve.matricule || '';
    document.getElementById('dateNaissance').value = eleve.dateNaissance || '';
    document.getElementById('lieuNaissance').value = eleve.lieuNaissance || '';
    document.getElementById('genre').value = eleve.genre || '';
    document.getElementById('adresse').value = eleve.adresse || '';
    document.getElementById('telephone').value = eleve.telephone || '';
    document.getElementById('email').value = eleve.email || '';
    document.getElementById('classeId').value = eleve.classeId || '';
    document.getElementById('boursier').checked = eleve.boursier || false;
}

// Soumettre le formulaire
async function submitForm(event) {
    event.preventDefault();
    
    const formData = {
        nom: document.getElementById('nom').value,
        prenom: document.getElementById('prenom').value,
        matricule: document.getElementById('matricule').value,
        dateNaissance: document.getElementById('dateNaissance').value,
        lieuNaissance: document.getElementById('lieuNaissance').value,
        genre: document.getElementById('genre').value,
        adresse: document.getElementById('adresse').value,
        telephone: document.getElementById('telephone').value,
        email: document.getElementById('email').value,
        classeId: document.getElementById('classeId').value || null,
        boursier: document.getElementById('boursier').checked
    };
    
    try {
        if (isEditMode) {
            await EleveAPI.update(currentEleveId, formData);
            Utils.showNotification('Élève modifié avec succès', 'success');
        } else {
            await EleveAPI.create(formData);
            Utils.showNotification('Élève créé avec succès', 'success');
        }
        
        closeModal();
        loadEleves();
        loadStatistiques();
    } catch (error) {
        console.error('Erreur sauvegarde:', error);
        Utils.showNotification('Erreur lors de la sauvegarde', 'error');
    }
}

// Générer un matricule
async function generateMatricule() {
    try {
        const response = await EleveAPI.generateMatricule();
        document.getElementById('matricule').value = response.matricule;
    } catch (error) {
        console.error('Erreur génération matricule:', error);
    }
}
