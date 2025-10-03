// Gestion du formulaire modal d'élève

let isEditMode = false;
let currentEleveId = null;

// Utilitaires pour notifications
const Utils = {
    showNotification: function(message, type = 'info') {
        // Créer une notification temporaire
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            background: ${type === 'success' ? '#27ae60' : type === 'error' ? '#e74c3c' : '#3498db'};
            color: white;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 10000;
            animation: slideIn 0.3s ease-out;
        `;
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        // Supprimer après 3 secondes
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    }
};

// Ouvrir le modal de création
function openCreateModal() {
    console.log('openCreateModal appelée');
    
    try {
        const modal = document.getElementById('eleveModal');
        const modalTitle = document.getElementById('modalTitle');
        const form = document.getElementById('eleveForm');
        
        if (!modal) {
            console.error('Modal eleveModal non trouvé!');
            alert('Erreur: Le modal n\'a pas pu être trouvé');
            return;
        }
        
        if (!modalTitle) {
            console.error('modalTitle non trouvé!');
        }
        
        if (!form) {
            console.error('eleveForm non trouvé!');
        }
        
        isEditMode = false;
        currentEleveId = null;
        
        if (modalTitle) {
            modalTitle.textContent = 'Nouvel Élève';
        }
        
        if (form) {
            form.reset();
        }
        
        modal.style.display = 'flex';
        console.log('Modal affiché avec succès');
        
        loadClasses();
    } catch (error) {
        console.error('Erreur dans openCreateModal:', error);
        alert('Erreur lors de l\'ouverture du modal: ' + error.message);
    }
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
    console.log('loadClasses appelée');
    try {
        const select = document.getElementById('classeId');
        if (!select) {
            console.error('Select classeId non trouvé!');
            return;
        }
        
        // Vérifier si ClasseAPI existe
        if (typeof ClasseAPI === 'undefined') {
            console.warn('ClasseAPI non défini, utilisation de données de test');
            // Données de test
            const classes = [
                {id: 1, nom: '6ème A'},
                {id: 2, nom: '5ème B'},
                {id: 3, nom: '4ème C'}
            ];
            
            select.innerHTML = '<option value="">Sélectionner une classe</option>';
            classes.forEach(classe => {
                select.innerHTML += `<option value="${classe.id}">${classe.nom}</option>`;
            });
            return;
        }
        
        const classes = await ClasseAPI.getAll();
        console.log('Classes chargées:', classes);
        
        select.innerHTML = '<option value="">Sélectionner une classe</option>';
        
        if (classes && classes.length > 0) {
            classes.forEach(classe => {
                select.innerHTML += `<option value="${classe.id}">${classe.nom}</option>`;
            });
        } else {
            console.warn('Aucune classe trouvée');
        }
    } catch (error) {
        console.error('Erreur chargement classes:', error);
        // Utiliser des données de test en cas d'erreur
        const select = document.getElementById('classeId');
        if (select) {
            select.innerHTML = '<option value="">Sélectionner une classe</option>';
            select.innerHTML += '<option value="1">6ème A (Test)</option>';
            select.innerHTML += '<option value="2">5ème B (Test)</option>';
        }
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
