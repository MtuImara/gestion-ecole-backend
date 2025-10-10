/**
 * Module unifié pour la gestion complète de la page élèves
 */

// Variables globales
let elevesData = [];
let currentPage = 1;
let itemsPerPage = 10;
let currentEleve = null;
let filteredEleves = [];
let classesData = []; // Pour stocker la liste des classes

// Initialisation au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    console.log('[Eleves Page] Initialisation...');
    
    // Charger les classes d'abord
    loadClasses();
    
    // Charger les élèves
    loadEleves();
    
    // Configurer les événements
    setupEventListeners();
    
    // Ajouter le conteneur pour le tableau si nécessaire
    const tableContent = document.getElementById('tableContent');
    if (tableContent && !document.getElementById('elevesTable')) {
        const table = document.createElement('div');
        table.id = 'elevesTable';
        tableContent.appendChild(table);
    }
});

// Configuration des événements
function setupEventListeners() {
    // Recherche globale
    const globalSearch = document.getElementById('globalSearchInput');
    if (globalSearch) {
        globalSearch.addEventListener('input', function(e) {
            filterEleves();
        });
    }
    
    // Recherche dans le tableau
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            filterEleves();
        });
    }
    
    // Filtre par classe
    const filterClasse = document.getElementById('filterClasse');
    if (filterClasse) {
        filterClasse.addEventListener('change', function(e) {
            filterEleves();
        });
    }
    
    // Filtre par statut
    const filterStatut = document.getElementById('filterStatut');
    if (filterStatut) {
        filterStatut.addEventListener('change', function(e) {
            filterEleves();
        });
    }
}

// Charger les classes depuis l'API
async function loadClasses() {
    try {
        if (window.ClasseAPI) {
            const response = await window.ClasseAPI.getAll();
            classesData = response.content || response;
            console.log('[Eleves Page] Classes chargées via ClasseAPI:', classesData.length);
        } else {
            const response = await fetch('/api/classes', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('ecolegest_token')}`
                }
            });
            
            if (response.ok) {
                const data = await response.json();
                classesData = data.content || data;
                console.log('[Eleves Page] Classes chargées via fetch:', classesData.length);
            }
        }
        
        // Mettre à jour les selects de classe
        updateClasseSelects();
    } catch (error) {
        console.error('[Eleves Page] Erreur chargement classes:', error);
        // Classes par défaut si l'API échoue - celles créées par DataInitializer
        classesData = [
            { id: 1, code: '6A', designation: '6ème A' },
            { id: 2, code: '6B', designation: '6ème B' }
        ];
        updateClasseSelects();
    }
}

// Mettre à jour les selects de classe
function updateClasseSelects() {
    // Select dans le formulaire
    const formSelect = document.getElementById('classe');
    if (formSelect) {
        const currentValue = formSelect.value;
        formSelect.innerHTML = '<option value="">Sélectionner une classe</option>';
        classesData.forEach(classe => {
            formSelect.innerHTML += `<option value="${classe.id}">${classe.designation || classe.code}</option>`;
        });
        formSelect.value = currentValue;
    }
    
    // Select dans les filtres
    const filterSelect = document.getElementById('filterClasse');
    if (filterSelect) {
        const currentValue = filterSelect.value;
        filterSelect.innerHTML = '<option value="">Toutes les classes</option>';
        classesData.forEach(classe => {
            filterSelect.innerHTML += `<option value="${classe.id}">${classe.designation || classe.code}</option>`;
        });
        filterSelect.value = currentValue;
    }
}

// Charger les élèves depuis l'API
async function loadEleves() {
    try {
        // Essayer de charger depuis l'API
        if (window.EleveAPI) {
            const response = await window.EleveAPI.getAll();
            elevesData = response.content || response;
            console.log('[Eleves Page] Données chargées depuis l\'API:', elevesData.length);
        } else if (window.API) {
            elevesData = await window.API.getEleves();
            console.log('[Eleves Page] Données chargées depuis l\'API (APIIntegration):', elevesData.length);
        } else {
            throw new Error('API non disponible');
        }
    } catch (error) {
        console.error('[Eleves Page] Erreur chargement API, utilisation des données de test:', error);
        // Données de test
        elevesData = generateMockEleves();
    }
    
    // Initialiser les données filtrées
    filteredEleves = [...elevesData];
    
    // Afficher les élèves
    displayEleves();
    
    // Mettre à jour les statistiques
    updateStats();
}

// Générer des données de test
function generateMockEleves() {
    const noms = ['Dupont', 'Martin', 'Bernard', 'Durand', 'Moreau', 'Laurent', 'Simon', 'Michel', 'Lefebvre', 'Leroy'];
    const prenoms = ['Jean', 'Marie', 'Pierre', 'Sophie', 'Lucas', 'Emma', 'Louis', 'Léa', 'Julie', 'Thomas'];
    const classes = ['6A', '6B', '5A', '5B', '4', '3'];
    
    const eleves = [];
    for (let i = 1; i <= 30; i++) {
        eleves.push({
            id: i,
            matricule: `ELV${String(i).padStart(4, '0')}`,
            nom: noms[Math.floor(Math.random() * noms.length)],
            prenom: prenoms[Math.floor(Math.random() * prenoms.length)],
            dateNaissance: `${2008 + Math.floor(Math.random() * 6)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`,
            genre: Math.random() > 0.5 ? 'M' : 'F',
            classe: classes[Math.floor(Math.random() * classes.length)],
            email: `eleve${i}@ecole.com`,
            telephone: `+257 79 ${Math.floor(Math.random() * 900) + 100} ${Math.floor(Math.random() * 900) + 100}`,
            adresse: `Rue ${Math.floor(Math.random() * 100) + 1}, Quartier ${Math.floor(Math.random() * 10) + 1}, Bujumbura`,
            statut: Math.random() > 0.1 ? 'ACTIF' : 'INACTIF',
            parentNom: noms[Math.floor(Math.random() * noms.length)] + ' ' + prenoms[Math.floor(Math.random() * prenoms.length)],
            parentTelephone: `+257 79 ${Math.floor(Math.random() * 900) + 100} ${Math.floor(Math.random() * 900) + 100}`,
            dateInscription: '2024-09-01',
            boursier: Math.random() > 0.8
        });
    }
    return eleves;
}

// Afficher les élèves dans le tableau
function displayEleves() {
    const tableContent = document.getElementById('tableContent');
    if (!tableContent) return;
    
    // Pagination
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedEleves = filteredEleves.slice(startIndex, endIndex);
    
    if (paginatedEleves.length === 0) {
        tableContent.innerHTML = `
            <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                <p style="font-size: 18px;">Aucun élève trouvé</p>
                <p style="margin-top: 10px;">Modifiez vos critères de recherche ou ajoutez un nouvel élève</p>
            </div>
        `;
        return;
    }
    
    tableContent.innerHTML = `
        <table style="width: 100%; border-collapse: collapse;">
            <thead>
                <tr style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                    <th style="padding: 12px; text-align: left;">Matricule</th>
                    <th style="padding: 12px; text-align: left;">Nom</th>
                    <th style="padding: 12px; text-align: left;">Prénom</th>
                    <th style="padding: 12px; text-align: left;">Classe</th>
                    <th style="padding: 12px; text-align: left;">Genre</th>
                    <th style="padding: 12px; text-align: left;">Contact</th>
                    <th style="padding: 12px; text-align: left;">Statut</th>
                    <th style="padding: 12px; text-align: center;">Actions</th>
                </tr>
            </thead>
            <tbody>
                ${paginatedEleves.map((eleve, index) => `
                    <tr style="border-bottom: 1px solid #f0f0f0; ${index % 2 === 0 ? 'background: #f8f9fa;' : 'background: white;'}">
                        <td style="padding: 12px; font-weight: 600;">${eleve.matricule}</td>
                        <td style="padding: 12px;">${eleve.nom}</td>
                        <td style="padding: 12px;">${eleve.prenom}</td>
                        <td style="padding: 12px;">
                            <span style="padding: 4px 8px; background: #e8eaf6; color: #667eea; border-radius: 12px; font-size: 12px;">
                                ${getClasseDisplay(eleve)}
                            </span>
                        </td>
                        <td style="padding: 12px; text-align: center;">
                            ${getGenreIcon(eleve.genre)}
                        </td>
                        <td style="padding: 12px;">
                            <div style="font-size: 12px;">
                                <div>${eleve.email || '-'}</div>
                                <div style="color: #7f8c8d;">${eleve.telephone || '-'}</div>
                            </div>
                        </td>
                        <td style="padding: 12px;">
                            <span style="
                                padding: 4px 8px;
                                border-radius: 12px;
                                font-size: 12px;
                                background: ${eleve.statut === 'ACTIF' ? '#d4edda' : '#f8d7da'};
                                color: ${eleve.statut === 'ACTIF' ? '#155724' : '#721c24'};
                            ">${eleve.statut}</span>
                        </td>
                        <td style="padding: 12px; text-align: center;">
                            <button onclick="viewEleve(${eleve.id})" title="Voir" style="
                                padding: 6px 10px;
                                margin: 0 2px;
                                background: #17a2b8;
                                color: white;
                                border: none;
                                border-radius: 3px;
                                cursor: pointer;
                            ">👁️</button>
                            <button onclick="editEleve(${eleve.id})" title="Modifier" style="
                                padding: 6px 10px;
                                margin: 0 2px;
                                background: #667eea;
                                color: white;
                                border: none;
                                border-radius: 3px;
                                cursor: pointer;
                            ">✏️</button>
                            <button onclick="deleteEleve(${eleve.id})" title="Supprimer" style="
                                padding: 6px 10px;
                                margin: 0 2px;
                                background: #e74c3c;
                                color: white;
                                border: none;
                                border-radius: 3px;
                                cursor: pointer;
                            ">🗑️</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    // Afficher la pagination
    displayPagination();
}

// Afficher la pagination
function displayPagination() {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    
    const totalPages = Math.ceil(filteredEleves.length / itemsPerPage);
    
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let paginationHTML = '';
    
    // Bouton précédent
    paginationHTML += `
        <button onclick="changePage(${currentPage - 1})" ${currentPage === 1 ? 'disabled' : ''} style="
            padding: 8px 12px;
            margin: 0 5px;
            background: ${currentPage === 1 ? '#e0e0e0' : '#667eea'};
            color: ${currentPage === 1 ? '#999' : 'white'};
            border: none;
            border-radius: 5px;
            cursor: ${currentPage === 1 ? 'not-allowed' : 'pointer'};
        ">‹ Précédent</button>
    `;
    
    // Numéros de page
    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || (i >= currentPage - 2 && i <= currentPage + 2)) {
            paginationHTML += `
                <button onclick="changePage(${i})" style="
                    padding: 8px 12px;
                    margin: 0 2px;
                    background: ${i === currentPage ? '#667eea' : 'white'};
                    color: ${i === currentPage ? 'white' : '#667eea'};
                    border: 1px solid #667eea;
                    border-radius: 5px;
                    cursor: pointer;
                ">${i}</button>
            `;
        } else if (i === currentPage - 3 || i === currentPage + 3) {
            paginationHTML += '<span style="margin: 0 5px;">...</span>';
        }
    }
    
    // Bouton suivant
    paginationHTML += `
        <button onclick="changePage(${currentPage + 1})" ${currentPage === totalPages ? 'disabled' : ''} style="
            padding: 8px 12px;
            margin: 0 5px;
            background: ${currentPage === totalPages ? '#e0e0e0' : '#667eea'};
            color: ${currentPage === totalPages ? '#999' : 'white'};
            border: none;
            border-radius: 5px;
            cursor: ${currentPage === totalPages ? 'not-allowed' : 'pointer'};
        ">Suivant ›</button>
    `;
    
    pagination.innerHTML = paginationHTML;
}

// Changer de page
function changePage(page) {
    const totalPages = Math.ceil(filteredEleves.length / itemsPerPage);
    if (page >= 1 && page <= totalPages) {
        currentPage = page;
        displayEleves();
    }
}

// Filtrer les élèves
function filterEleves() {
    const searchTerm = (document.getElementById('globalSearchInput')?.value || 
                       document.getElementById('searchInput')?.value || '').toLowerCase();
    const classeFilter = document.getElementById('filterClasse')?.value || '';
    const statutFilter = document.getElementById('filterStatut')?.value || '';
    
    filteredEleves = elevesData.filter(eleve => {
        // Filtre de recherche
        const matchSearch = !searchTerm || 
            eleve.nom.toLowerCase().includes(searchTerm) ||
            eleve.prenom.toLowerCase().includes(searchTerm) ||
            eleve.matricule.toLowerCase().includes(searchTerm) ||
            eleve.email?.toLowerCase().includes(searchTerm);
        
        // Filtre de classe
        const matchClasse = !classeFilter || eleve.classe === classeFilter;
        
        // Filtre de statut
        const matchStatut = !statutFilter || eleve.statut === statutFilter;
        
        return matchSearch && matchClasse && matchStatut;
    });
    
    // Réinitialiser à la première page
    currentPage = 1;
    
    // Réafficher
    displayEleves();
    updateStats();
}

// Réinitialiser les filtres
function resetFilters() {
    document.getElementById('globalSearchInput').value = '';
    document.getElementById('searchInput').value = '';
    document.getElementById('filterClasse').value = '';
    document.getElementById('filterStatut').value = '';
    
    filteredEleves = [...elevesData];
    currentPage = 1;
    displayEleves();
    updateStats();
}

// Mettre à jour les statistiques
function updateStats() {
    const totalEleves = document.getElementById('totalEleves');
    const elevesActifs = document.getElementById('elevesActifs');
    const boursiers = document.getElementById('boursiers');
    
    if (totalEleves) totalEleves.textContent = filteredEleves.length;
    if (elevesActifs) elevesActifs.textContent = filteredEleves.filter(e => e.statut === 'ACTIF').length;
    if (boursiers) boursiers.textContent = filteredEleves.filter(e => e.boursier).length;
}

// Ouvrir le modal de création
function openCreateModal() {
    currentEleve = null;
    const modal = document.getElementById('eleveModal');
    const modalTitle = document.getElementById('modalTitle');
    
    if (modal) {
        modal.style.display = 'flex';
        if (modalTitle) modalTitle.textContent = 'Nouvel Élève';
        
        // Réinitialiser le formulaire
        document.getElementById('eleveForm').reset();
        
        // Générer un nouveau matricule
        generateMatricule();
    }
}

// Fermer le modal
function closeModal() {
    const modal = document.getElementById('eleveModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Générer un matricule automatique
function generateMatricule() {
    const matriculeInput = document.getElementById('matricule');
    if (matriculeInput) {
        const nextNumber = elevesData.length + 1;
        matriculeInput.value = `ELV${String(nextNumber).padStart(4, '0')}`;
    }
}

// Voir les détails d'un élève
function viewEleve(id) {
    const eleve = elevesData.find(e => e.id === id);
    if (!eleve) return;
    
    alert(`Détails de l'élève:\n\n` +
          `Matricule: ${eleve.matricule}\n` +
          `Nom: ${eleve.nom} ${eleve.prenom}\n` +
          `Classe: ${eleve.classe}\n` +
          `Email: ${eleve.email}\n` +
          `Téléphone: ${eleve.telephone}\n` +
          `Parent: ${eleve.parentNom}\n` +
          `Statut: ${eleve.statut}`);
}

// Modifier un élève
function editEleve(id) {
    const eleve = elevesData.find(e => e.id === id);
    if (!eleve) return;
    
    currentEleve = eleve;
    const modal = document.getElementById('eleveModal');
    const modalTitle = document.getElementById('modalTitle');
    
    if (modal) {
        modal.style.display = 'flex';
        if (modalTitle) modalTitle.textContent = 'Modifier l\'élève';
        
        // Remplir le formulaire
        document.getElementById('matricule').value = eleve.matricule || '';
        document.getElementById('nom').value = eleve.nom || '';
        document.getElementById('prenom').value = eleve.prenom || '';
        document.getElementById('dateNaissance').value = eleve.dateNaissance || '';
        
        // Gérer le genre (peut être un objet ou une string)
        const genreValue = typeof eleve.genre === 'object' ? 
            (eleve.genre.key === 'MASCULIN' ? 'M' : 'F') : 
            (eleve.genre === 'MASCULIN' ? 'M' : eleve.genre);
        document.getElementById('genre').value = genreValue || '';
        
        // Gérer la classe (peut être un ID ou un objet)
        const classeValue = typeof eleve.classe === 'object' ? eleve.classe.id : eleve.classe;
        document.getElementById('classe').value = classeValue || '';
        
        document.getElementById('email').value = eleve.email || '';
        document.getElementById('telephone').value = eleve.telephone || '';
        document.getElementById('adresse').value = eleve.adresse || eleve.quartier || '';
        
        // Gérer les parents si disponibles
        if (eleve.parentsInfo && eleve.parentsInfo.length > 0) {
            const parent = eleve.parentsInfo[0];
            document.getElementById('parentNom').value = `${parent.nom} ${parent.prenom}` || '';
            document.getElementById('parentTelephone').value = parent.telephone || '';
        } else {
            document.getElementById('parentNom').value = eleve.parentNom || '';
            document.getElementById('parentTelephone').value = eleve.parentTelephone || '';
        }
        
        // Gérer le statut (peut être un objet ou une string)
        const statutValue = typeof eleve.statut === 'object' ? eleve.statut.key : eleve.statut;
        document.getElementById('statut').value = statutValue || 'ACTIF';
    }
}

// Supprimer un élève
async function deleteEleve(id) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cet élève ?')) return;
    
    try {
        if (window.API) {
            await window.API.deleteEleve(id);
        }
        
        // Supprimer de la liste locale
        elevesData = elevesData.filter(e => e.id !== id);
        filteredEleves = filteredEleves.filter(e => e.id !== id);
        
        // Réafficher
        displayEleves();
        updateStats();
        
        showNotification('Élève supprimé avec succès', 'success');
    } catch (error) {
        console.error('Erreur suppression:', error);
        showNotification('Erreur lors de la suppression', 'error');
    }
}

// Soumettre le formulaire
async function submitForm(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData);
    
    // Convertir les données pour correspondre au backend
    const eleveData = {
        matricule: data.matricule,
        nom: data.nom,
        prenom: data.prenom,
        dateNaissance: data.dateNaissance,
        genre: convertGenre(data.genre), // Convertir M/F en MASCULIN/FEMININ
        email: data.email || null,
        telephone: data.telephone || null,
        adresse: data.adresse || null,
        quartier: data.adresse || null, // Utiliser l'adresse comme quartier
        statut: data.statut || 'ACTIF',
        // Pour l'instant, on ne gère pas les parents directement
        parents: []
    };
    
    // Gérer la classe séparément pour éviter NaN
    const classeId = parseInt(data.classe);
    if (!isNaN(classeId) && classeId > 0) {
        eleveData.classe = classeId;
    }
    
    // Supprimer les champs vides ou null
    Object.keys(eleveData).forEach(key => {
        if (eleveData[key] === '' || eleveData[key] === undefined || eleveData[key] === null) {
            delete eleveData[key];
        }
    });
    
    console.log('[Eleves Page] Données préparées pour envoi:', eleveData);
    
    try {
        if (currentEleve) {
            // Modification
            if (window.API) {
                const updated = await window.API.updateEleve(currentEleve.id, eleveData);
                const index = elevesData.findIndex(e => e.id === currentEleve.id);
                if (index !== -1) {
                    elevesData[index] = updated;
                }
            } else {
                // Mode local
                const index = elevesData.findIndex(e => e.id === currentEleve.id);
                if (index !== -1) {
                    elevesData[index] = { ...elevesData[index], ...eleveData };
                }
            }
            showNotification('Élève modifié avec succès', 'success');
        } else {
            // Création
            let newEleve = null;
            
            if (window.EleveAPI) {
                newEleve = await window.EleveAPI.create(eleveData);
                console.log('[Eleves Page] Réponse de EleveAPI.create:', newEleve);
            } else if (window.API) {
                newEleve = await window.API.createEleve(eleveData);
                console.log('[Eleves Page] Réponse de APIIntegration.createEleve:', newEleve);
            }
            
            // Vérifier que newEleve est valide
            if (!newEleve) {
                console.error('[Eleves Page] Aucune donnée retournée par l\'API');
                // Créer un élève local avec les données envoyées
                newEleve = {
                    id: Date.now(),
                    ...eleveData,
                    dateInscription: new Date().toISOString().split('T')[0]
                };
                console.log('[Eleves Page] Élève créé localement:', newEleve);
            }
            
            // Ajouter à la liste seulement si valide
            if (newEleve && newEleve.matricule) {
                elevesData.push(newEleve);
                showNotification('Élève ajouté avec succès', 'success');
            } else {
                console.error('[Eleves Page] Élève invalide, pas de matricule:', newEleve);
                showNotification('Élève créé mais données incomplètes', 'warning');
            }
        }
        
        // Fermer le modal et rafraîchir
        closeModal();
        filteredEleves = [...elevesData];
        displayEleves();
        updateStats();
        
    } catch (error) {
        console.error('[Eleves Page] Erreur complète:', error);
        console.error('[Eleves Page] Stack:', error.stack);
        
        // Extraire un message d'erreur plus détaillé
        let errorMessage = 'Erreur inconnue';
        if (error.message) {
            errorMessage = error.message;
        }
        
        showNotification(`Erreur: ${errorMessage}`, 'error');
    }
}

// Fonction helper pour convertir le genre M/F en MASCULIN/FEMININ
function convertGenre(genre) {
    const genreMapping = {
        'M': 'MASCULIN',
        'F': 'FEMININ',
        'MASCULIN': 'MASCULIN',
        'FEMININ': 'FEMININ'
    };
    return genreMapping[genre] || 'MASCULIN';
}

// Fonction helper pour convertir le genre du backend vers M/F pour l'affichage
function convertGenreToDisplay(genre) {
    if (typeof genre === 'object' && genre.key) {
        // Si c'est un objet avec key/value
        return genre.key === 'MASCULIN' ? 'M' : 'F';
    }
    // Si c'est une string
    return genre === 'MASCULIN' ? 'M' : 'F';
}

// Fonction pour obtenir l'icône du genre
function getGenreIcon(genre) {
    if (typeof genre === 'object' && genre.key) {
        return genre.key === 'MASCULIN' ? '👦' : '👧';
    }
    if (genre === 'MASCULIN' || genre === 'M') {
        return '👦';
    }
    return '👧';
}

// Fonction pour obtenir le nom de la classe
function getClasseDisplay(eleve) {
    // Si classeInfo est fourni
    if (eleve.classeInfo) {
        return eleve.classeInfo.designation || eleve.classeInfo.code;
    }
    // Si c'est juste un ID, chercher dans classesData
    if (typeof eleve.classe === 'number') {
        const classe = classesData.find(c => c.id === eleve.classe);
        return classe ? (classe.designation || classe.code) : eleve.classe;
    }
    // Sinon retourner tel quel
    return eleve.classe;
}

// Afficher une notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'success' ? '#28a745' : type === 'error' ? '#dc3545' : '#17a2b8'};
        color: white;
        border-radius: 5px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
        z-index: 10001;
        animation: slideIn 0.3s ease;
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Ajouter les animations CSS
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Exposer les fonctions globalement
window.openCreateModal = openCreateModal;
window.closeModal = closeModal;
window.submitForm = submitForm;
window.viewEleve = viewEleve;
window.editEleve = editEleve;
window.deleteEleve = deleteEleve;
window.resetFilters = resetFilters;
window.changePage = changePage;
window.generateMatricule = generateMatricule;
