document.addEventListener('DOMContentLoaded', () => {
    loadClasses();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('logoutBtn').addEventListener('click', () => AuthService.logout());
}

async function loadClasses() {
    try {
        const classes = await ClasseAPI.getAll();
        displayClasses(classes);
        updateStats(classes);
    } catch (error) {
        console.error('Erreur:', error);
        document.getElementById('tableContent').innerHTML = '<div class="error-state">❌ Erreur de chargement</div>';
    }
}

function displayClasses(classes) {
    if (!classes || classes.length === 0) {
        document.getElementById('tableContent').innerHTML = '<div class="empty-state">Aucune classe trouvée</div>';
        return;
    }

    const html = `
        <table>
            <thead>
                <tr>
                    <th>Nom</th>
                    <th>Niveau</th>
                    <th>Capacité</th>
                    <th>Effectif</th>
                    <th>Disponible</th>
                    <th>Enseignant Principal</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${classes.map(c => `
                    <tr>
                        <td><strong>${c.nom}</strong></td>
                        <td>${c.niveauInfo?.nom || '-'}</td>
                        <td>${c.capaciteMax || '-'}</td>
                        <td>${c.effectifActuel || 0}</td>
                        <td>${(c.capaciteMax || 0) - (c.effectifActuel || 0)}</td>
                        <td>${c.enseignantPrincipal?.nom || 'Non assigné'}</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-icon btn-view" onclick="viewClasse(${c.id})">👁️</button>
                                <button class="btn-icon btn-edit" onclick="editClasse(${c.id})">✏️</button>
                                <button class="btn-icon btn-delete" onclick="deleteClasse(${c.id})">🗑️</button>
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    document.getElementById('tableContent').innerHTML = html;
}

function updateStats(classes) {
    const total = classes.length;
    const effectif = classes.reduce((sum, c) => sum + (c.effectifActuel || 0), 0);
    const capacite = classes.reduce((sum, c) => sum + (c.capaciteMax || 0), 0);
    
    document.getElementById('totalClasses').textContent = total;
    document.getElementById('effectifTotal').textContent = effectif;
    document.getElementById('placesDisponibles').textContent = capacite - effectif;
}

function openAddModal() { alert('Formulaire d\'ajout à développer'); }
function viewClasse(id) { alert(`Voir classe #${id}`); }
function editClasse(id) { alert(`Modifier classe #${id}`); }
async function deleteClasse(id) {
    if (confirm('Supprimer cette classe ?')) {
        try {
            await ClasseAPI.delete(id);
            Utils.showNotification('Classe supprimée', 'success');
            loadClasses();
        } catch (error) {
            Utils.showNotification('Erreur de suppression', 'error');
        }
    }
}
