// Gestion de la Communication

let currentTab = 'messages';
let refreshInterval = null;

// Initialisation
document.addEventListener('DOMContentLoaded', () => {
    loadStatistiques();
    loadMessages();
    loadNotifications();
    loadAnnonces();
    setupEventListeners();
    startAutoRefresh();
});

function setupEventListeners() {
    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        AuthService.logout();
    });
}

// Switcher entre les onglets
function switchTab(tab) {
    currentTab = tab;
    
    // Mise à jour des onglets
    document.querySelectorAll('.comm-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    
    event.target.classList.add('active');
    document.getElementById(tab + '-tab').classList.add('active');
}

// Charger les statistiques
async function loadStatistiques() {
    try {
        const [messagesCount, notifCount, annoncesCount] = await Promise.all([
            CommunicationAPI.countMessagesNonLus(),
            CommunicationAPI.countNotificationsNonLues(),
            CommunicationAPI.countAnnoncesActives()
        ]);
        
        document.getElementById('messagesNonLus').textContent = messagesCount.toLocaleString();
        document.getElementById('notificationsNonLues').textContent = notifCount.toLocaleString();
        document.getElementById('annoncesActives').textContent = annoncesCount.toLocaleString();
        
        // Mise à jour du badge de notifications
        const badge = document.getElementById('notifBadge');
        if (badge) {
            if (notifCount > 0) {
                badge.textContent = notifCount > 99 ? '99+' : notifCount;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        }
        
    } catch (error) {
        console.error('Erreur chargement statistiques:', error);
        document.getElementById('messagesNonLus').textContent = '0';
        document.getElementById('notificationsNonLues').textContent = '0';
        document.getElementById('annoncesActives').textContent = '0';
    }
}

// Charger les messages
async function loadMessages() {
    try {
        const data = await CommunicationAPI.getMessagesRecus(0, 20);
        displayMessages(data.content || data || []);
    } catch (error) {
        console.error('Erreur chargement messages:', error);
        document.getElementById('messagesList').innerHTML = 
            <div style='text-align: center; padding: 40px; color: #e74c3c;'>
                <p>Erreur lors du chargement des messages</p>
            </div>
        ;
    }
}

// Afficher les messages
function displayMessages(messages) {
    const container = document.getElementById('messagesList');
    
    if (!messages || messages.length === 0) {
        container.innerHTML = `
            <div style='text-align: center; padding: 40px; color: #7f8c8d;'>
                <p>Aucun message</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = messages.map(msg => `
        <div class='message-item ${msg.lu ? '' : 'unread'}' onclick='openMessage(${msg.id})'>
            <div class='message-header'>
                <span class='message-from'>${msg.expediteurNom || 'Système'}</span>
                <span class='message-date'>${formatDate(msg.dateEnvoi)}</span>
            </div>
            <div class='message-subject'>${msg.sujet || 'Sans sujet'}</div>
            <div class='message-preview'>${msg.contenu ? msg.contenu.substring(0, 100) + '...' : ''}</div>
        </div>
    `).join('');
}

// Charger les notifications
let allNotifications = [];

async function loadNotifications() {
    try {
        const data = await CommunicationAPI.getNotifications(0, 100);
        allNotifications = data.content || data || [];
        filterNotificationsByType();
    } catch (error) {
        console.error('Erreur chargement notifications:', error);
        document.getElementById('notificationsList').innerHTML = `
            <div style='text-align: center; padding: 40px; color: #e74c3c;'>
                <p>Erreur lors du chargement des notifications</p>
            </div>
        `;
    }
}

// Filtrer les notifications par type
function filterNotificationsByType() {
    const typeFilter = document.getElementById('notificationTypeFilter')?.value;
    
    let filtered = allNotifications;
    if (typeFilter) {
        filtered = allNotifications.filter(n => n.type === typeFilter);
    }
    
    displayNotifications(filtered);
}

// Afficher les notifications
function displayNotifications(notifications) {
    const container = document.getElementById('notificationsList');
    if (!notifications || notifications.length === 0) {
        container.innerHTML = `
            <div style='text-align: center; padding: 40px; color: #7f8c8d;'>
                <p>Aucune notification</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = notifications.map(notif => `
        <div class='message-item ${notif.lue ? '' : 'unread'}' onclick='openNotification(${notif.id})'>
            <div class='message-header'>
                <span class='message-from'>${notif.type}</span>
                <span class='message-date'>${formatDate(notif.dateCreation)}</span>
            </div>
            <div class='message-preview'>${notif.contenu || ''}</div>
        </div>
    `).join('');
}

// Charger les annonces
async function loadAnnonces() {
    try {
        const data = await CommunicationAPI.getAnnoncesActives(0, 20);
        displayAnnonces(data.content || data || []);
    } catch (error) {
        console.error('Erreur chargement annonces:', error);
        document.getElementById('annoncesList').innerHTML = 
            <div style='text-align: center; padding: 40px; color: #e74c3c;'>
                <p>Erreur lors du chargement des annonces</p>
            </div>
        ;
    }
}

// Afficher les annonces
function displayAnnonces(annonces) {
    const container = document.getElementById('annoncesList');
    
    if (!annonces || annonces.length === 0) {
        container.innerHTML = `
            <div style='text-align: center; padding: 40px; color: #7f8c8d;'>
                <p>Aucune annonce</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = annonces.map(annonce => `
        <div class='annonce-item' onclick='openAnnonce(${annonce.id})'>
            <div class='message-header'>
                <span class='annonce-badge ${annonce.type === 'URGENTE' ? 'urgent' : 'info'}'>
                    ${annonce.type}
                </span>
                <span class='message-date'>${formatDate(annonce.datePublication)}</span>
            </div>
            <div class='annonce-title'>${annonce.titre}</div>
            <div class='message-preview'>${annonce.contenu ? annonce.contenu.substring(0, 150) + '...' : ''}</div>
        </div>
    `).join('');
}

// Les fonctions openMessage, openNotification, openAnnonce et openComposeModal
// sont maintenant dans communication-modals.js

// ========== AUTO-REFRESH ==========
function startAutoRefresh() {
    // Rafraîchir toutes les 30 secondes
    refreshInterval = setInterval(() => {
        loadStatistiques();
        
        // Rafraîchir seulement l'onglet actif
        if (currentTab === 'messages') {
            loadMessages();
        } else if (currentTab === 'notifications') {
            loadNotifications();
        } else if (currentTab === 'annonces') {
            loadAnnonces();
        }
    }, 30000); // 30 secondes
}

function stopAutoRefresh() {
    if (refreshInterval) {
        clearInterval(refreshInterval);
        refreshInterval = null;
    }
}

// Arrêter le rafraîchissement quand l'utilisateur quitte la page
window.addEventListener('beforeunload', () => {
    stopAutoRefresh();
});

// Rafraîchir manuellement
function refreshCurrent() {
    loadStatistiques();
    
    if (currentTab === 'messages') {
        loadMessages();
    } else if (currentTab === 'notifications') {
        loadNotifications();
    } else if (currentTab === 'annonces') {
        loadAnnonces();
    }
    
    Utils.showNotification('Contenu actualisé', 'success');
}

// Utilitaires
function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diff = now - date;
    const minutes = Math.floor(diff / 60000);
    
    if (minutes < 1) return 'À l instant';
    if (minutes < 60) return minutes + ' min';
    if (minutes < 1440) return Math.floor(minutes / 60) + 'h';
    return date.toLocaleDateString('fr-FR');
}
