let currentPage = 0;
let pageSize = 20;
let isEditMode = false;

document.addEventListener('DOMContentLoaded', () => {
    if (!RoleManager.isAdmin()) {
        alert('Accès non autorisé');
        window.location.href = '/dashboard.html';
        return;
    }
    
    loadUsers();
    loadStatistics();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('searchInput')?.addEventListener('input', (e) => {
        const query = e.target.value.trim();
        if (query.length >= 2) {
            searchUsers(query);
        } else if (query.length === 0) {
            loadUsers();
        }
    });
    
    document.getElementById('userForm')?.addEventListener('submit', handleSubmit);
}

async function loadUsers(page = 0) {
    try {
        currentPage = page;
        const data = await UserManagementAPI.getAll(page, pageSize);
        displayUsers(data.content || []);
        displayPagination(data);
    } catch (error) {
        console.error('Erreur:', error);
        document.getElementById('tableContent').innerHTML = '<div style="text-align: center; padding: 40px; color: #e74c3c;"><p>Erreur chargement</p></div>';
    }
}

function displayUsers(users) {
    const container = document.getElementById('tableContent');
    if (!users || users.length === 0) {
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: #7f8c8d;"><p>Aucun utilisateur</p></div>';
        return;
    }
    
    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Téléphone</th>
                    <th>Type</th>
                    <th>Statut</th>
                    <th>Dernière Connexion</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${users.map(u => `
                    <tr>
                        <td><strong>${u.username}</strong></td>
                        <td>${u.email}</td>
                        <td>${u.telephone || 'N/A'}</td>
                        <td><span class="badge" style="background: #3498db;">${u.type || 'USER'}</span></td>
                        <td>${u.actif ? '<span class="badge badge-active">✅ Actif</span>' : '<span class="badge badge-inactive">❌ Inactif</span>'}</td>
                        <td>${formatDate(u.derniereConnexion)}</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-icon btn-edit" onclick="editUser(${u.id})" title="Modifier">✏️</button>
                                ${u.actif ? 
                                    `<button class="btn-icon btn-delete" onclick="deactivateUser(${u.id})" title="Désactiver">❌</button>` :
                                    `<button class="btn-icon btn-view" onclick="activateUser(${u.id})" title="Activer" style="background: #27ae60;">✅</button>`
                                }
                                <button class="btn-icon btn-view" onclick="changePassword(${u.id})" title="Mot de passe">🔑</button>
                                <button class="btn-icon btn-delete" onclick="deleteUser(${u.id})" title="Supprimer">🗑️</button>
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

function displayPagination(data) {
    const pagination = document.getElementById('pagination');
    if (!data || !data.totalPages || data.totalPages <= 1) { pagination.innerHTML = ''; return; }
    let html = '';
    if (currentPage > 0) html += `<button class="page-btn" onclick="loadUsers(${currentPage - 1})">‹ Précédent</button>`;
    for (let i = 0; i < Math.min(data.totalPages, 5); i++) {
        html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" onclick="loadUsers(${i})">${i + 1}</button>`;
    }
    if (currentPage < data.totalPages - 1) html += `<button class="page-btn" onclick="loadUsers(${currentPage + 1})">Suivant ›</button>`;
    pagination.innerHTML = html;
}

async function loadStatistics() {
    try {
        const stats = await UserManagementAPI.getStatistics();
        document.getElementById('totalUsers').textContent = (stats.totalUsers || 0).toLocaleString();
        document.getElementById('activeUsers').textContent = (stats.activeUsers || 0).toLocaleString();
        document.getElementById('inactiveUsers').textContent = (stats.inactiveUsers || 0).toLocaleString();
    } catch (error) {
        console.error('Erreur stats:', error);
    }
}

async function searchUsers(query) {
    try {
        const data = await UserManagementAPI.search(query, 0, pageSize);
        displayUsers(data.content || []);
    } catch (error) {
        console.error('Erreur recherche:', error);
    }
}

function openCreateModal() {
    isEditMode = false;
    document.getElementById('modalTitle').textContent = 'Nouvel Utilisateur';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('passwordField').style.display = 'block';
    document.getElementById('password').required = true;
    document.getElementById('userModal').style.display = 'block';
}

async function editUser(id) {
    try {
        isEditMode = true;
        const user = await UserManagementAPI.getById(id);
        
        document.getElementById('modalTitle').textContent = 'Modifier Utilisateur';
        document.getElementById('userId').value = user.id;
        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('telephone').value = user.telephone || '';
        document.getElementById('passwordField').style.display = 'none';
        document.getElementById('password').required = false;
        document.getElementById('userModal').style.display = 'block';
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur chargement utilisateur');
    }
}

function closeModal() {
    document.getElementById('userModal').style.display = 'none';
    document.getElementById('userForm').reset();
}

async function handleSubmit(e) {
    e.preventDefault();
    
    const userData = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        telephone: document.getElementById('telephone').value,
        password: document.getElementById('password').value
    };
    
    try {
        if (isEditMode) {
            const id = document.getElementById('userId').value;
            await UserManagementAPI.update(id, userData);
            alert('✅ Utilisateur modifié avec succès');
        } else {
            await UserManagementAPI.create(userData);
            alert('✅ Utilisateur créé avec succès');
        }
        
        closeModal();
        loadUsers(currentPage);
        loadStatistics();
    } catch (error) {
        console.error('Erreur:', error);
        alert('❌ Erreur: ' + (error.message || 'Opération échouée'));
    }
}

async function activateUser(id) {
    if (!confirm('Activer cet utilisateur?')) return;
    
    try {
        await UserManagementAPI.activate(id);
        alert('✅ Utilisateur activé');
        loadUsers(currentPage);
        loadStatistics();
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur activation');
    }
}

async function deactivateUser(id) {
    if (!confirm('Désactiver cet utilisateur?')) return;
    
    try {
        await UserManagementAPI.deactivate(id);
        alert('Utilisateur désactivé');
        loadUsers(currentPage);
        loadStatistics();
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur désactivation');
    }
}

async function deleteUser(id) {
    if (!confirm('⚠️ SUPPRIMER définitivement cet utilisateur?\nCette action est irréversible!')) return;
    
    try {
        await UserManagementAPI.delete(id);
        alert('Utilisateur supprimé');
        loadUsers(currentPage);
        loadStatistics();
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur suppression');
    }
}

async function changePassword(id) {
    const newPassword = prompt('Nouveau mot de passe (min 6 caractères):');
    if (!newPassword || newPassword.length < 6) {
        alert('Mot de passe invalide');
        return;
    }
    
    try {
        await UserManagementAPI.changePassword(id, newPassword);
        alert('✅ Mot de passe modifié');
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur changement mot de passe');
    }
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('fr-FR', { 
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}
