// Gestion des Classes
let currentPage = 0;
let pageSize = 10;
let searchTimeout = null;

// Initialisation
document.addEventListener('DOMContentLoaded', () => {
    loadClasses();
    loadStatistiques();
    setupEventListeners();
});

function setupEventListeners() {
    // Recherche
    document.getElementById('searchInput')?.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        const query = e.target.value.trim();
        
        searchTimeout = setTimeout(() => {
            if (query.length >= 2) {
                searchClasses(query);
            } else {
                loadClasses();
            }
        }, 300);
    });
    
    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        AuthService.logout();
    });
}

// Charger les classes
async function loadClasses(page = 0) {
    try {
        currentPage = page;
        const data = await ClasseAPI.getAll(page, pageSize);
        
        displayClasses(data.content || []);
        
    } catch (error) {
        console.error('Erreur chargement classes:', error);
        document.getElementById('tableContent').innerHTML = 
            <div style="text-align: center; padding: 40px; color: #e74c3c;">
                <p>Erreur lors du chargement des classes</p>
                <button onclick="loadClasses()" style="margin-top: 15px; padding: 10px 20px; background: #667eea; color: white; border: none; border-radius: 8px; cursor: pointer;">
                    Réessayer
                </button>
            </div>
        ;
    }
}

// Recherche
async function searchClasses(query) {
    try {
        const results = await ClasseAPI.search(query);
        displayClasses(results);
        
    } catch (error) {
        console.error('Erreur recherche:', error);
        displayClasses([]);
    }
}

// Afficher les classes
function displayClasses(classes) {
    const tableContent = document.getElementById('tableContent');
    
    if (!classes || classes.length === 0) {
        tableContent.innerHTML = 
            <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                <p>Aucune classe trouvée</p>
            </div>
        ;
        return;
    }
    
    const html = 
        <table>
            <thead>
                <tr>
                    <th>Nom</th>
                    <th>Niveau</th>
                    <th>Année Scolaire</th>
                    <th>Effectif</th>
                    <th>Capacité</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                +classes.map(classe => 
                    <tr>
                        <td><strong>+classe.nom+</strong></td>
                        <td>+(classe.niveauInfo?.designation || '-')+</td>
                        <td>+(classe.anneeScolaireInfo?.libelle || '-')+</td>
                        <td>+(classe.effectif || 0)+</td>
                        <td>+(classe.capacite || 0)+</td>
                        <td>
                            <span class="badge +(classe.active ? 'badge-active' : 'badge-inactive')+">
                                +(classe.active ? 'Active' : 'Inactive')+
                            </span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-icon btn-view" onclick="viewClasse(+classe.id+)" title="Voir">👁️</button>
                                <button class="btn-icon btn-edit" onclick="editClasse(+classe.id+)" title="Modifier">✏️</button>
                                <button class="btn-icon btn-delete" onclick="deleteClasse(+classe.id+)" title="Supprimer">🗑️</button>
                            </div>
                        </td>
                    </tr>
                ).join('')+
            </tbody>
        </table>
    ;
    
    tableContent.innerHTML = html;
}

// Charger les statistiques
async function loadStatistiques() {
    try {
        const classes = await ClasseAPI.getAll(0, 1000);
        const data = classes.content || classes || [];
        
        const totalClasses = data.length;
        const effectifTotal = data.reduce((sum, c) => sum + (c.effectif || 0), 0);
        const capaciteTotale = data.reduce((sum, c) => sum + (c.capacite || 0), 0);
        const placesDisponibles = capaciteTotale - effectifTotal;
        
        document.getElementById('totalClasses').textContent = totalClasses.toLocaleString();
        document.getElementById('effectifTotal').textContent = effectifTotal.toLocaleString();
        document.getElementById('placesDisponibles').textContent = placesDisponibles.toLocaleString();
        
    } catch (error) {
        console.error('Erreur statistiques:', error);
        document.getElementById('totalClasses').textContent = '0';
        document.getElementById('effectifTotal').textContent = '0';
        document.getElementById('placesDisponibles').textContent = '0';
    }
}

// Actions
function viewClasse(id) {
    window.location.href = `/classe-details.html?id=${id}`;
}

async function deleteClasse(id) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette classe ?')) {
        return;
    }
    
    try {
        await ClasseAPI.delete(id);
        Utils.showNotification('Classe supprimée avec succès', 'success');
        loadClasses(currentPage);
        loadStatistiques();
    } catch (error) {
        console.error('Erreur suppression:', error);
        Utils.showNotification('Erreur lors de la suppression', 'error');
    }
}
