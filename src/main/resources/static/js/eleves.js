let currentPage = 0;
let pageSize = 10;
let searchTimeout = null;

// Filtres
let currentFilters = {
    classeId: null,
    anneeScolaireId: null,
    statut: null,
    search: ''
};

// Initialisation
document.addEventListener('DOMContentLoaded', () => {
    loadEleves();
    loadStatistiques();
    loadFilterOptions();
    setupEventListeners();
});

function setupEventListeners() {
    // Recherche globale avec debounce
    const globalSearchInput = document.getElementById('globalSearchInput');
    if (globalSearchInput) {
        globalSearchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            currentFilters.search = e.target.value.trim();
            
            searchTimeout = setTimeout(() => {
                applyFilters();
            }, 300);
        });
    }
    
    // Filtres
    document.getElementById('filterClasse')?.addEventListener('change', (e) => {
        currentFilters.classeId = e.target.value || null;
        applyFilters();
    });
    
    document.getElementById('filterAnneeScolaire')?.addEventListener('change', (e) => {
        currentFilters.anneeScolaireId = e.target.value || null;
        applyFilters();
    });
    
    document.getElementById('filterStatut')?.addEventListener('change', (e) => {
        currentFilters.statut = e.target.value || null;
        applyFilters();
    });
    
    // Recherche ancienne (on garde pour compatibilité)
    document.getElementById('searchInput')?.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        const query = e.target.value.trim();
        
        searchTimeout = setTimeout(() => {
            if (query.length >= 2) {
                searchEleves(query);
            } else {
                loadEleves();
            }
        }, 300);
    });
    
    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        AuthService.logout();
    });
}

// Charger les élèves
async function loadEleves(page = 0) {
    try {
        currentPage = page;
        const data = await EleveAPI.getAll(page, pageSize);
        
        displayEleves(data.content || []);
        displayPagination(data);
        
    } catch (error) {
        console.error('Erreur chargement élèves:', error);
        document.getElementById('tableContent').innerHTML = `
            <div style="text-align: center; padding: 40px; color: #e74c3c;">
                <p>❌ Erreur lors du chargement des élèves</p>
                <button onclick="loadEleves()" style="margin-top: 15px; padding: 10px 20px; background: #667eea; color: white; border: none; border-radius: 8px; cursor: pointer;">
                    Réessayer
                </button>
            </div>
        `;
    }
}

// Recherche élèves
async function searchEleves(query) {
    try {
        const results = await EleveAPI.search(query);
        displayEleves(results);
        document.getElementById('pagination').innerHTML = '';
        
    } catch (error) {
        console.error('Erreur recherche:', error);
        displayEleves([]);
    }
}

// Afficher les élèves dans le tableau
function displayEleves(eleves) {
    const tableContent = document.getElementById('tableContent');
    
    if (!eleves || eleves.length === 0) {
        tableContent.innerHTML = `
            <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                <p>Aucun élève trouvé</p>
            </div>
        `;
        return;
    }
    
    const html = `
        <table>
            <thead>
                <tr>
                    <th>Matricule</th>
                    <th>Nom & Prénom</th>
                    <th>Classe</th>
                    <th>Genre</th>
                    <th>Date de Naissance</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${eleves.map(eleve => `
                        <td><strong>${eleve.matricule || 'N/A'}</strong></td>
                        <td>${eleve.prenom} ${eleve.nom}</td>
                        <td>${eleve.classeInfo?.nom || 'Non assigné'}</td>
                        <td>${eleve.genre || '-'}</td>
                        <td>${eleve.dateNaissance ? new Date(eleve.dateNaissance).toLocaleDateString('fr-FR') : '-'}</td>
                        <td>
                            <span class="badge ${eleve.statut === 'ACTIF' ? 'badge-active' : 'badge-inactive'}">
                                ${eleve.statut || 'INACTIF'}
                            </span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-icon btn-view" onclick="viewEleve(${eleve.id})" title="Voir">👁️</button>
                                <button class="btn-icon btn-edit" onclick="editEleve(${eleve.id})" title="Modifier">✏️</button>
                                <button class="btn-icon btn-delete" onclick="deleteEleve(${eleve.id})" title="Supprimer">🗑️</button>
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    tableContent.innerHTML = html;
}

// Afficher la pagination
function displayPagination(data) {
    const pagination = document.getElementById('pagination');
    
    if (!data || !data.totalPages || data.totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let html = '';
    
    // Bouton précédent
    if (currentPage > 0) {
        html += `<button class="page-btn" onclick="loadEleves(${currentPage - 1})">‹ Précédent</button>`;
    }
    
    // Numéros de page
    for (let i = 0; i < data.totalPages; i++) {
        if (i === 0 || i === data.totalPages - 1 || (i >= currentPage - 2 && i <= currentPage + 2)) {
            html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" onclick="loadEleves(${i})">${i + 1}</button>`;
        } else if (i === currentPage - 3 || i === currentPage + 3) {
            html += `<span style="padding: 8px;">...</span>`;
        }
    }
    
    // Bouton suivant
    if (currentPage < data.totalPages - 1) {
        html += `<button class="page-btn" onclick="loadEleves(${currentPage + 1})">Suivant ›</button>`;
    }
    
    pagination.innerHTML = html;
}

// Charger les statistiques
async function loadStatistiques() {
    try {
        const stats = await EleveAPI.getStatistiques();
        
        document.getElementById('totalEleves').textContent = stats.total?.toLocaleString() || '0';
        document.getElementById('elevesActifs').textContent = stats.actifs?.toLocaleString() || '0';
        document.getElementById('boursiers').textContent = stats.boursiers?.toLocaleString() || '0';
        
    } catch (error) {
        console.error('Erreur statistiques:', error);
        // Valeurs par défaut
        document.getElementById('totalEleves').textContent = '0';
        document.getElementById('elevesActifs').textContent = '0';
        document.getElementById('boursiers').textContent = '0';
    }
}

// Actions sur les élèves
function viewEleve(id) {
    window.location.href = `/eleve-details.html?id=${id}`;
}

function editEleve(id) {
    openEditModal(id);
}

async function deleteEleve(id) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cet élève ?')) {
        return;
    }
    
    try {
        await EleveAPI.delete(id);
        Utils.showNotification('Élève supprimé avec succès', 'success');
        loadEleves(currentPage);
        loadStatistiques();
    } catch (error) {
        console.error('Erreur suppression:', error);
        Utils.showNotification('Erreur lors de la suppression', 'error');
    }
}

// Charger les options des filtres
async function loadFilterOptions() {
    try {
        // Charger les classes
        const classes = await ClasseAPI.getAll();
        const selectClasse = document.getElementById('filterClasse');
        if (selectClasse && classes) {
            selectClasse.innerHTML = '<option value="">Toutes les classes</option>';
            classes.forEach(classe => {
                selectClasse.innerHTML += `<option value="${classe.id}">${classe.nom}</option>`;
            });
        }
        
        // Charger les années scolaires
        const annees = await AnneeScolaireAPI.getAll();
        const selectAnnee = document.getElementById('filterAnneeScolaire');
        if (selectAnnee && annees) {
            selectAnnee.innerHTML = '<option value="">Toutes les années</option>';
            annees.forEach(annee => {
                selectAnnee.innerHTML += `<option value="${annee.id}">${annee.libelle || annee.designation}</option>`;
            });
        }
    } catch (error) {
        console.error('Erreur chargement options filtres:', error);
    }
}

// Appliquer les filtres
async function applyFilters(page = 0) {
    try {
        currentPage = page;
        
        // Si recherche simple uniquement
        if (currentFilters.search && !currentFilters.classeId && !currentFilters.anneeScolaireId && !currentFilters.statut) {
            const data = await EleveAPI.search(currentFilters.search);
            displayEleves(data.content || data || []);
            if (data.totalPages !== undefined) {
                displayPagination(data);
            }
            return;
        }
        
        // Si filtres avancés
        if (currentFilters.classeId || currentFilters.anneeScolaireId || currentFilters.statut) {
            const data = await EleveAPI.filter(
                currentFilters.classeId,
                currentFilters.anneeScolaireId,
                currentFilters.statut,
                page,
                pageSize
            );
            displayEleves(data.content || []);
            displayPagination(data);
            return;
        }
        
        // Sinon, charger tous les élèves
        loadEleves(page);
        
    } catch (error) {
        console.error('Erreur application filtres:', error);
        Utils.showNotification('Erreur lors du filtrage', 'error');
    }
}

// Réinitialiser les filtres
function resetFilters() {
    currentFilters = {
        classeId: null,
        anneeScolaireId: null,
        statut: null,
        search: ''
    };
    
    // Réinitialiser les champs
    const globalSearch = document.getElementById('globalSearchInput');
    if (globalSearch) globalSearch.value = '';
    
    const filterClasse = document.getElementById('filterClasse');
    if (filterClasse) filterClasse.value = '';
    
    const filterAnnee = document.getElementById('filterAnneeScolaire');
    if (filterAnnee) filterAnnee.value = '';
    
    const filterStatut = document.getElementById('filterStatut');
    if (filterStatut) filterStatut.value = '';
    
    // Recharger tous les élèves
    loadEleves(0);
}
