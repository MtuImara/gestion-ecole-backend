let currentPage = 0;
let pageSize = 10;
let searchTimeout = null;

// Initialisation
document.addEventListener('DOMContentLoaded', () => {
    loadEleves();
    loadStatistiques();
    setupEventListeners();
});

function setupEventListeners() {
    // Recherche avec debounce
    document.getElementById('searchInput').addEventListener('input', (e) => {
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
    
    document.getElementById('logoutBtn').addEventListener('click', () => {
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
                    <tr>
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
    // Rediriger vers page de détail (à créer)
    alert(`Affichage des détails de l'élève #${id}\n\nFonctionnalité à développer`);
}

function editEleve(id) {
    // Rediriger vers formulaire d'édition (à créer)
    alert(`Modification de l'élève #${id}\n\nFonctionnalité à développer`);
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
