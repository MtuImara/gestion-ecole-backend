// Variables globales
let parentsData = [];
let filteredParents = [];
let currentParentId = null;
let isEditMode = false;

// Initialisation au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    loadParents();
    setupEventListeners();
});

// Configuration des écouteurs d'événements
function setupEventListeners() {
    // Recherche
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            filterParents(e.target.value);
        });
    }
    
    // Déconnexion
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('token');
            window.location.href = '/login.html';
        });
    }
}

// Charger les parents depuis l'API
async function loadParents() {
    try {
        // Charger depuis l'API backend
        const response = await ParentAPI.getAllNoPagination();
        parentsData = response || [];
        console.log('[Parents Page] Données chargées depuis l\'API:', parentsData.length);
        
        filteredParents = [...parentsData];
        displayParents();
        updateStats();
    } catch (error) {
        console.error('[Parents Page] Erreur lors du chargement:', error);
        // Si l'API échoue, afficher un message d'erreur
        parentsData = [];
        filteredParents = [];
        displayParents();
        updateStats();
        showNotification('Erreur lors du chargement des parents', 'error');
    }
}

// Pas de données fictives - utilisation uniquement de l'API

// Afficher les parents
function displayParents() {
    const tableContent = document.getElementById('tableContent');
    
    if (!filteredParents || filteredParents.length === 0) {
        tableContent.innerHTML = `
            <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                <p style="font-size: 18px;">Aucun parent trouvé</p>
                <p style="margin-top: 10px;">Cliquez sur "Nouveau Parent" pour ajouter un parent</p>
            </div>
        `;
        return;
    }
    
    let html = '<div style="padding: 20px;">';
    
    filteredParents.forEach(parent => {
        const initials = getInitials(parent.prenom, parent.nom);
        const relationLabel = getRelationLabel(parent.lienParente);
        
        html += `
            <div class="parent-card">
                <div class="parent-header">
                    <div class="parent-info">
                        <div class="parent-avatar">${initials}</div>
                        <div class="parent-details">
                            <h3>${parent.prenom} ${parent.nom}</h3>
                            <p>${relationLabel} ${parent.profession ? '• ' + parent.profession : ''}</p>
                        </div>
                    </div>
                    <div class="action-buttons">
                        <button class="btn-icon btn-view" onclick="viewParent(${parent.id})" title="Voir détails">
                            👁️
                        </button>
                        <button class="btn-icon btn-edit" onclick="editParent(${parent.id})" title="Modifier">
                            ✏️
                        </button>
                        <button class="btn-icon btn-delete" onclick="deleteParent(${parent.id})" title="Supprimer">
                            🗑️
                        </button>
                    </div>
                </div>
                
                <div class="parent-contacts">
                    ${parent.telephone ? `
                        <div class="contact-item">
                            <span>📞</span>
                            <span>${parent.telephone}</span>
                        </div>
                    ` : ''}
                    ${parent.email ? `
                        <div class="contact-item">
                            <span>✉️</span>
                            <span>${parent.email}</span>
                        </div>
                    ` : ''}
                    ${parent.adresse ? `
                        <div class="contact-item">
                            <span>📍</span>
                            <span>${parent.adresse}</span>
                        </div>
                    ` : ''}
                    ${parent.cin ? `
                        <div class="contact-item">
                            <span>🆔</span>
                            <span>CIN: ${parent.cin}</span>
                        </div>
                    ` : ''}
                </div>
                
                ${parent.enfants && parent.enfants.length > 0 ? `
                    <div class="parent-children">
                        <p style="margin: 0 0 10px 0; color: #7f8c8d; font-size: 13px;">Enfants (${parent.enfants.length})</p>
                        <div class="children-list">
                            ${parent.enfants.map(enfant => `
                                <span class="child-badge">
                                    ${enfant.prenom} ${enfant.nom} - ${enfant.classe}
                                </span>
                            `).join('')}
                        </div>
                    </div>
                ` : ''}
            </div>
        `;
    });
    
    html += '</div>';
    tableContent.innerHTML = html;
}

// Filtrer les parents
function filterParents(searchTerm) {
    if (!searchTerm) {
        filteredParents = [...parentsData];
    } else {
        const term = searchTerm.toLowerCase();
        filteredParents = parentsData.filter(parent => {
            return parent.nom.toLowerCase().includes(term) ||
                   parent.prenom.toLowerCase().includes(term) ||
                   (parent.email && parent.email.toLowerCase().includes(term)) ||
                   (parent.telephone && parent.telephone.includes(term)) ||
                   (parent.profession && parent.profession.toLowerCase().includes(term));
        });
    }
    displayParents();
}

// Mettre à jour les statistiques
function updateStats() {
    const totalParents = document.getElementById('totalParents');
    if (totalParents) {
        totalParents.textContent = parentsData.length;
    }
}

// Ouvrir le modal parent
function openParentModal() {
    isEditMode = false;
    currentParentId = null;
    document.getElementById('modalTitle').textContent = 'Nouveau Parent';
    document.getElementById('parentForm').reset();
    
    const modal = document.getElementById('parentModal');
    modal.style.display = 'flex';
}

// Fermer le modal parent
function closeParentModal() {
    const modal = document.getElementById('parentModal');
    modal.style.display = 'none';
    document.getElementById('parentForm').reset();
}

// Soumettre le formulaire parent
async function submitParentForm(event) {
    event.preventDefault();
    
    const formData = {
        nom: document.getElementById('nom').value,
        prenom: document.getElementById('prenom').value,
        lienParente: document.getElementById('lienParente').value,
        telephone: document.getElementById('telephone').value,
        email: document.getElementById('email').value || null,
        profession: document.getElementById('profession').value || null,
        adresse: document.getElementById('adresse').value || null,
        cin: document.getElementById('cin').value || null,
        telephoneSecondaire: document.getElementById('telephoneSecondaire').value || null
    };
    
    try {
        if (isEditMode && currentParentId) {
            // Mode édition
            await ParentAPI.update(currentParentId, formData);
            showNotification('Parent modifié avec succès', 'success');
        } else {
            // Mode création
            await ParentAPI.create(formData);
            showNotification('Parent créé avec succès', 'success');
        }
        
        closeParentModal();
        loadParents();
    } catch (error) {
        console.error('Erreur lors de la sauvegarde:', error);
        showNotification('Erreur lors de la sauvegarde du parent', 'error');
    }
}

// Voir les détails d'un parent
async function viewParent(id) {
    try {
        const parent = await ParentAPI.getById(id);
        if (parent) {
            showParentDetailsModal(parent);
        }
    } catch (error) {
        console.error('Erreur lors de la récupération des détails:', error);
        showNotification('Erreur lors de la récupération des détails', 'error');
    }
}

// Afficher le modal de détails du parent
async function showParentDetailsModal(parent) {
    // Créer le modal s'il n'existe pas
    let modal = document.getElementById('parentDetailsModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'parentDetailsModal';
        modal.style.cssText = `
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0,0,0,0.5);
            z-index: 2000;
            align-items: center;
            justify-content: center;
        `;
        document.body.appendChild(modal);
    }
    
    // Charger la situation financière depuis l'API
    let totalDu = 0;
    let totalPaye = 0;
    let totalRestant = 0;
    
    try {
        const situation = await ParentAPI.getSituationFinanciere(parent.id);
        totalDu = situation.totalDu || 0;
        totalPaye = situation.totalPaye || 0;
        totalRestant = situation.totalRestant || 0;
    } catch (error) {
        console.error('Erreur lors du chargement de la situation financière:', error);
    }
    
    // Contenu du modal
    modal.innerHTML = `
        <div style="background: white; border-radius: 15px; max-width: 900px; width: 90%; max-height: 90vh; overflow-y: auto; position: relative;">
            <!-- Header -->
            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 15px 15px 0 0;">
                <button onclick="closeParentDetailsModal()" style="position: absolute; top: 20px; right: 20px; background: none; border: none; color: white; font-size: 24px; cursor: pointer;">×</button>
                <div style="display: flex; align-items: center; gap: 20px;">
                    <div style="width: 80px; height: 80px; background: white; color: #667eea; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 32px; font-weight: bold;">
                        ${getInitials(parent.prenom, parent.nom)}
                    </div>
                    <div>
                        <h2 style="margin: 0; font-size: 28px;">${parent.prenom} ${parent.nom}</h2>
                        <p style="margin: 5px 0; opacity: 0.9;">
                            ${getRelationLabel(parent.typeParent || parent.lienParente)} 
                            ${parent.profession ? '• ' + parent.profession : ''}
                        </p>
                        ${parent.numeroParent ? `<p style="margin: 5px 0; opacity: 0.8; font-size: 14px;">N° Parent: ${parent.numeroParent}</p>` : ''}
                    </div>
                </div>
            </div>
            
            <!-- Body -->
            <div style="padding: 30px;">
                <!-- Informations de contact -->
                <div style="margin-bottom: 30px;">
                    <h3 style="color: #2c3e50; margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
                        <span style="font-size: 20px;">📞</span> Informations de contact
                    </h3>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px;">
                        ${parent.telephone ? `
                            <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                <p style="margin: 0; color: #7f8c8d; font-size: 12px;">Téléphone principal</p>
                                <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.telephone}</p>
                            </div>
                        ` : ''}
                        ${parent.telephoneSecondaire ? `
                            <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                <p style="margin: 0; color: #7f8c8d; font-size: 12px;">Téléphone secondaire</p>
                                <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.telephoneSecondaire}</p>
                            </div>
                        ` : ''}
                        ${parent.email ? `
                            <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                <p style="margin: 0; color: #7f8c8d; font-size: 12px;">Email</p>
                                <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.email}</p>
                            </div>
                        ` : ''}
                        ${parent.adresse ? `
                            <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                <p style="margin: 0; color: #7f8c8d; font-size: 12px;">Adresse domicile</p>
                                <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.adresse}</p>
                            </div>
                        ` : ''}
                    </div>
                </div>
                
                <!-- Informations professionnelles -->
                ${(parent.employeur || parent.adresseTravail) ? `
                    <div style="margin-bottom: 30px;">
                        <h3 style="color: #2c3e50; margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
                            <span style="font-size: 20px;">💼</span> Informations professionnelles
                        </h3>
                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px;">
                            ${parent.employeur ? `
                                <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                    <p style="margin: 0; color: #7f8c8d; font-size: 12px;">Employeur</p>
                                    <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.employeur}</p>
                                </div>
                            ` : ''}
                            ${parent.adresseTravail ? `
                                <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                    <p style="margin: 0; color: #7f8c8d; font-size: 12px;">Adresse travail</p>
                                    <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.adresseTravail}</p>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                ` : ''}
                
                <!-- Documents -->
                ${(parent.cin || parent.numeroPasseport) ? `
                    <div style="margin-bottom: 30px;">
                        <h3 style="color: #2c3e50; margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
                            <span style="font-size: 20px;">🆔</span> Documents d'identité
                        </h3>
                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px;">
                            ${parent.cin ? `
                                <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                    <p style="margin: 0; color: #7f8c8d; font-size: 12px;">CIN</p>
                                    <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.cin}</p>
                                </div>
                            ` : ''}
                            ${parent.numeroPasseport ? `
                                <div style="padding: 15px; background: #f8f9fa; border-radius: 8px;">
                                    <p style="margin: 0; color: #7f8c8d; font-size: 12px;">Numéro de passeport</p>
                                    <p style="margin: 5px 0 0 0; color: #2c3e50; font-weight: 600;">${parent.numeroPasseport}</p>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                ` : ''}
                
                <!-- Enfants -->
                <div style="margin-bottom: 30px;">
                    <h3 style="color: #2c3e50; margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
                        <span style="font-size: 20px;">👨‍👩‍👧‍👦</span> Enfants (${parent.enfants ? parent.enfants.length : 0})
                    </h3>
                    ${parent.enfants && parent.enfants.length > 0 ? `
                        <div style="display: grid; gap: 15px;">
                            ${parent.enfants.map(enfant => `
                                <div style="padding: 20px; background: #f8f9fa; border-radius: 8px; display: flex; justify-content: space-between; align-items: center;">
                                    <div>
                                        <h4 style="margin: 0; color: #2c3e50;">${enfant.prenom} ${enfant.nom}</h4>
                                        <p style="margin: 5px 0 0 0; color: #7f8c8d;">
                                            ${enfant.matricule ? `Matricule: ${enfant.matricule} • ` : ''}
                                            ${enfant.classe || 'Classe non définie'}
                                            ${enfant.statut ? ` • ${enfant.statut}` : ''}
                                        </p>
                                    </div>
                                    <button onclick="window.location.href='/eleves.html?id=${enfant.id}'" 
                                            style="padding: 8px 16px; background: #667eea; color: white; border: none; border-radius: 6px; cursor: pointer;">
                                        Voir détails
                                    </button>
                                </div>
                            `).join('')}
                        </div>
                    ` : `
                        <p style="color: #7f8c8d; text-align: center; padding: 20px; background: #f8f9fa; border-radius: 8px;">
                            Aucun enfant enregistré
                        </p>
                    `}
                </div>
                
                <!-- Situation financière -->
                <div style="margin-bottom: 30px;">
                    <h3 style="color: #2c3e50; margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
                        <span style="font-size: 20px;">💰</span> Situation financière globale
                    </h3>
                    <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px;">
                        <div style="padding: 20px; background: #fff3cd; border-radius: 8px; text-align: center;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">Total dû</p>
                            <p style="margin: 10px 0 0 0; color: #856404; font-size: 24px; font-weight: bold;">
                                ${totalDu.toLocaleString('fr-FR')} FBU
                            </p>
                        </div>
                        <div style="padding: 20px; background: #d4edda; border-radius: 8px; text-align: center;">
                            <p style="margin: 0; color: #155724; font-size: 14px;">Total payé</p>
                            <p style="margin: 10px 0 0 0; color: #155724; font-size: 24px; font-weight: bold;">
                                ${totalPaye.toLocaleString('fr-FR')} FBU
                            </p>
                        </div>
                        <div style="padding: 20px; background: #f8d7da; border-radius: 8px; text-align: center;">
                            <p style="margin: 0; color: #721c24; font-size: 14px;">Reste à payer</p>
                            <p style="margin: 10px 0 0 0; color: #721c24; font-size: 24px; font-weight: bold;">
                                ${totalRestant.toLocaleString('fr-FR')} FBU
                            </p>
                        </div>
                    </div>
                </div>
                
                <!-- Actions -->
                <div style="display: flex; gap: 10px; justify-content: flex-end;">
                    <button onclick="editParent(${parent.id})" 
                            style="padding: 10px 20px; background: #667eea; color: white; border: none; border-radius: 8px; cursor: pointer;">
                        ✏️ Modifier
                    </button>
                    <button onclick="closeParentDetailsModal()" 
                            style="padding: 10px 20px; background: #f0f0f0; color: #2c3e50; border: none; border-radius: 8px; cursor: pointer;">
                        Fermer
                    </button>
                </div>
            </div>
        </div>
    `;
    
    modal.style.display = 'flex';
}

// Fermer le modal de détails
function closeParentDetailsModal() {
    const modal = document.getElementById('parentDetailsModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Éditer un parent
function editParent(id) {
    const parent = parentsData.find(p => p.id === id);
    if (!parent) return;
    
    isEditMode = true;
    currentParentId = id;
    
    // Remplir le formulaire avec les données du parent
    document.getElementById('modalTitle').textContent = 'Modifier Parent';
    document.getElementById('nom').value = parent.nom;
    document.getElementById('prenom').value = parent.prenom;
    document.getElementById('lienParente').value = parent.lienParente;
    document.getElementById('telephone').value = parent.telephone;
    document.getElementById('email').value = parent.email || '';
    document.getElementById('profession').value = parent.profession || '';
    document.getElementById('adresse').value = parent.adresse || '';
    document.getElementById('cin').value = parent.cin || '';
    document.getElementById('telephoneSecondaire').value = parent.telephoneSecondaire || '';
    
    // Ouvrir le modal
    const modal = document.getElementById('parentModal');
    modal.style.display = 'flex';
}

// Supprimer un parent
async function deleteParent(id) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce parent ?')) {
        return;
    }
    
    try {
        await ParentAPI.delete(id);
        showNotification('Parent supprimé avec succès', 'success');
        
        // Recharger la liste
        loadParents();
    } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        showNotification('Erreur lors de la suppression du parent', 'error');
    }
}

// Obtenir les initiales
function getInitials(prenom, nom) {
    const p = prenom ? prenom.charAt(0).toUpperCase() : '';
    const n = nom ? nom.charAt(0).toUpperCase() : '';
    return p + n;
}

// Obtenir le label de la relation
function getRelationLabel(relation) {
    const relations = {
        'PERE': 'Père',
        'MERE': 'Mère',
        'TUTEUR': 'Tuteur',
        'AUTRE': 'Autre'
    };
    return relations[relation] || relation;
}

// Afficher une notification
function showNotification(message, type = 'info') {
    // Créer l'élément de notification
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'success' ? '#4caf50' : type === 'error' ? '#f44336' : '#2196f3'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    // Supprimer après 3 secondes
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
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

// L'API ParentAPI est maintenant définie dans api.js
