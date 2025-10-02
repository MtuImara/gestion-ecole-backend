// Gestion des modals de communication

let currentMessageId = null;
let currentAnnonceId = null;

// ========== MODAL COMPOSITION MESSAGE ==========
function openComposeModal() {
    const modal = document.getElementById('composeModal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeComposeModal() {
    const modal = document.getElementById('composeModal');
    if (modal) {
        modal.style.display = 'none';
        document.getElementById('composeForm').reset();
    }
}

async function submitMessage(event) {
    event.preventDefault();
    
    const formData = {
        destinataireId: document.getElementById('destinataire').value,
        sujet: document.getElementById('sujet').value,
        contenu: document.getElementById('contenu').value
    };
    
    const filesInput = document.getElementById('fichiers');
    const files = filesInput.files.length > 0 ? Array.from(filesInput.files) : null;
    
    try {
        await CommunicationAPI.envoyerMessage(formData, files);
        Utils.showNotification('Message envoyé avec succès', 'success');
        closeComposeModal();
        loadMessages();
        loadStatistiques();
    } catch (error) {
        console.error('Erreur envoi message:', error);
        Utils.showNotification('Erreur lors de l envoi', 'error');
    }
}

// ========== MODAL DÉTAILS MESSAGE ==========
async function openMessage(id) {
    try {
        const message = await CommunicationAPI.getMessage(id);
        
        // Marquer comme lu
        if (!message.lu) {
            await CommunicationAPI.marquerMessageCommeLu(id);
            loadMessages();
            loadStatistiques();
        }
        
        currentMessageId = id;
        
        const modal = document.getElementById('messageDetailModal');
        document.getElementById('messageFrom').textContent = message.expediteurNom || 'Système';
        document.getElementById('messageDate').textContent = formatDate(message.dateEnvoi);
        document.getElementById('messageSubject').textContent = message.sujet || 'Sans sujet';
        document.getElementById('messageContent').textContent = message.contenu || '';
        
        modal.style.display = 'flex';
    } catch (error) {
        console.error('Erreur ouverture message:', error);
        Utils.showNotification('Erreur lors du chargement', 'error');
    }
}

function closeMessageDetail() {
    const modal = document.getElementById('messageDetailModal');
    if (modal) {
        modal.style.display = 'none';
        currentMessageId = null;
    }
}

async function archiverCurrentMessage() {
    if (!currentMessageId) return;
    
    try {
        await CommunicationAPI.archiverMessage(currentMessageId);
        Utils.showNotification('Message archivé', 'success');
        closeMessageDetail();
        loadMessages();
    } catch (error) {
        console.error('Erreur archivage:', error);
        Utils.showNotification('Erreur lors de l archivage', 'error');
    }
}

async function supprimerCurrentMessage() {
    if (!currentMessageId) return;
    if (!confirm('Supprimer ce message ?')) return;
    
    try {
        await CommunicationAPI.supprimerMessage(currentMessageId);
        Utils.showNotification('Message supprimé', 'success');
        closeMessageDetail();
        loadMessages();
    } catch (error) {
        console.error('Erreur suppression:', error);
        Utils.showNotification('Erreur lors de la suppression', 'error');
    }
}

// ========== MODAL NOTIFICATION ==========
async function openNotification(id) {
    try {
        const notif = await CommunicationAPI.getNotification(id);
        
        // Marquer comme lue
        if (!notif.lue) {
            await CommunicationAPI.marquerNotificationCommeLue(id);
            loadNotifications();
            loadStatistiques();
        }
        
        const modal = document.getElementById('notificationDetailModal');
        document.getElementById('notifType').textContent = notif.type;
        document.getElementById('notifDate').textContent = formatDate(notif.dateCreation);
        document.getElementById('notifContent').textContent = notif.contenu || '';
        
        modal.style.display = 'flex';
    } catch (error) {
        console.error('Erreur ouverture notification:', error);
        Utils.showNotification('Erreur lors du chargement', 'error');
    }
}

function closeNotificationDetail() {
    const modal = document.getElementById('notificationDetailModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

async function marquerToutesNotificationsLues() {
    try {
        await CommunicationAPI.marquerToutesNotificationsLues();
        Utils.showNotification('Toutes les notifications marquées comme lues', 'success');
        loadNotifications();
        loadStatistiques();
    } catch (error) {
        console.error('Erreur:', error);
        Utils.showNotification('Erreur', 'error');
    }
}

// ========== MODAL ANNONCE ==========
async function openAnnonce(id) {
    try {
        const annonce = await CommunicationAPI.getAnnonce(id);
        
        currentAnnonceId = id;
        
        const modal = document.getElementById('annonceDetailModal');
        document.getElementById('annonceType').textContent = annonce.type;
        document.getElementById('annonceDate').textContent = formatDate(annonce.datePublication);
        document.getElementById('annonceTitle').textContent = annonce.titre;
        document.getElementById('annonceContent').textContent = annonce.contenu || '';
        
        modal.style.display = 'flex';
    } catch (error) {
        console.error('Erreur ouverture annonce:', error);
        Utils.showNotification('Erreur lors du chargement', 'error');
    }
}

function closeAnnonceDetail() {
    const modal = document.getElementById('annonceDetailModal');
    if (modal) {
        modal.style.display = 'none';
        currentAnnonceId = null;
    }
}

// ========== MODAL CRÉATION ANNONCE ==========
function openCreateAnnonceModal() {
    const modal = document.getElementById('createAnnonceModal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeCreateAnnonceModal() {
    const modal = document.getElementById('createAnnonceModal');
    if (modal) {
        modal.style.display = 'none';
        document.getElementById('annonceForm').reset();
    }
}

async function submitAnnonce(event) {
    event.preventDefault();
    
    const formData = {
        titre: document.getElementById('annoncetitre').value,
        contenu: document.getElementById('annonceContenu').value,
        type: document.getElementById('annonceTypeSelect').value,
        epinglee: document.getElementById('annonceEpinglee').checked
    };
    
    const filesInput = document.getElementById('annonceFichiers');
    const files = filesInput.files.length > 0 ? Array.from(filesInput.files) : null;
    
    try {
        await CommunicationAPI.creerAnnonce(formData, files);
        Utils.showNotification('Annonce créée avec succès', 'success');
        closeCreateAnnonceModal();
        loadAnnonces();
        loadStatistiques();
    } catch (error) {
        console.error('Erreur création annonce:', error);
        Utils.showNotification('Erreur lors de la création', 'error');
    }
}

async function epinglerCurrentAnnonce() {
    if (!currentAnnonceId) return;
    
    try {
        await CommunicationAPI.epinglerAnnonce(currentAnnonceId);
        Utils.showNotification('Annonce épinglée', 'success');
        closeAnnonceDetail();
        loadAnnonces();
    } catch (error) {
        console.error('Erreur épinglage:', error);
        Utils.showNotification('Erreur', 'error');
    }
}

async function supprimerCurrentAnnonce() {
    if (!currentAnnonceId) return;
    if (!confirm('Supprimer cette annonce ?')) return;
    
    try {
        await CommunicationAPI.supprimerAnnonce(currentAnnonceId);
        Utils.showNotification('Annonce supprimée', 'success');
        closeAnnonceDetail();
        loadAnnonces();
    } catch (error) {
        console.error('Erreur suppression:', error);
        Utils.showNotification('Erreur', 'error');
    }
}
