// ============================================
// FONCTIONNALITÉS AMÉLIORÉES
// ============================================

// Export PDF de la fiche élève
async function exportToPDF() {
    showLoader(true);
    
    try {
        // Créer un élément temporaire pour l'impression PDF
        const printContent = document.createElement('div');
        printContent.style.padding = '20px';
        printContent.style.background = 'white';
        
        // En-tête du PDF
        const header = `
            <div style="text-align: center; margin-bottom: 30px;">
                <h1 style="color: #667eea;">FICHE DE L'ÉLÈVE</h1>
                <p style="color: #666;">Année scolaire 2024-2025</p>
            </div>
        `;
        
        // Informations de l'élève
        const studentInfo = `
            <div style="margin-bottom: 30px;">
                <h2 style="color: #333; border-bottom: 2px solid #667eea; padding-bottom: 10px;">
                    Informations personnelles
                </h2>
                <table style="width: 100%; margin-top: 15px;">
                    <tr>
                        <td style="padding: 8px;"><strong>Matricule:</strong></td>
                        <td style="padding: 8px;">${currentStudent.matricule}</td>
                        <td style="padding: 8px;"><strong>Nom complet:</strong></td>
                        <td style="padding: 8px;">${currentStudent.prenom} ${currentStudent.nom}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px;"><strong>Date de naissance:</strong></td>
                        <td style="padding: 8px;">${formatDate(currentStudent.dateNaissance)}</td>
                        <td style="padding: 8px;"><strong>Âge:</strong></td>
                        <td style="padding: 8px;">${calculateAge(currentStudent.dateNaissance)}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px;"><strong>Genre:</strong></td>
                        <td style="padding: 8px;">${formatGender(currentStudent.genre)}</td>
                        <td style="padding: 8px;"><strong>Nationalité:</strong></td>
                        <td style="padding: 8px;">${currentStudent.nationalite || 'Burundaise'}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px;"><strong>Classe:</strong></td>
                        <td style="padding: 8px;">${currentStudent.classeInfo?.nom || '-'}</td>
                        <td style="padding: 8px;"><strong>Statut:</strong></td>
                        <td style="padding: 8px;">${currentStudent.statut}</td>
                    </tr>
                </table>
            </div>
        `;
        
        // Informations médicales
        const medicalInfo = `
            <div style="margin-bottom: 30px;">
                <h2 style="color: #333; border-bottom: 2px solid #667eea; padding-bottom: 10px;">
                    Informations médicales
                </h2>
                <table style="width: 100%; margin-top: 15px;">
                    <tr>
                        <td style="padding: 8px;"><strong>Groupe sanguin:</strong></td>
                        <td style="padding: 8px;">${currentStudent.groupeSanguin || '-'}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px;"><strong>Allergies:</strong></td>
                        <td style="padding: 8px;">${currentStudent.allergies || 'Aucune'}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px;"><strong>Maladies chroniques:</strong></td>
                        <td style="padding: 8px;">${currentStudent.maladiesChroniques || 'Aucune'}</td>
                    </tr>
                </table>
            </div>
        `;
        
        // Parents
        let parentsInfo = '<div style="margin-bottom: 30px;"><h2 style="color: #333; border-bottom: 2px solid #667eea; padding-bottom: 10px;">Parents/Tuteurs</h2>';
        if (currentStudent.parentsInfo && currentStudent.parentsInfo.length > 0) {
            currentStudent.parentsInfo.forEach(parent => {
                parentsInfo += `
                    <div style="margin: 15px 0; padding: 10px; background: #f8f9fa; border-radius: 5px;">
                        <strong>${parent.prenom} ${parent.nom}</strong> (${parent.lienParente || 'Parent'})<br>
                        Téléphone: ${parent.telephone || '-'}<br>
                        Email: ${parent.email || '-'}
                    </div>
                `;
            });
        } else {
            parentsInfo += '<p>Aucun parent enregistré</p>';
        }
        parentsInfo += '</div>';
        
        // Footer
        const footer = `
            <div style="margin-top: 50px; padding-top: 20px; border-top: 1px solid #ddd; text-align: center; color: #666;">
                <p>Document généré le ${new Date().toLocaleDateString('fr-FR')}</p>
                <p>© 2024 Gestion École - Tous droits réservés</p>
            </div>
        `;
        
        printContent.innerHTML = header + studentInfo + medicalInfo + parentsInfo + footer;
        
        // Créer une nouvelle fenêtre pour l'impression
        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>Fiche Élève - ${currentStudent.prenom} ${currentStudent.nom}</title>
                <style>
                    body { font-family: Arial, sans-serif; }
                    @media print {
                        body { margin: 0; }
                    }
                </style>
            </head>
            <body>
                ${printContent.innerHTML}
            </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.print();
        
        showLoader(false);
        showAlert('Fiche exportée avec succès', 'success');
    } catch (error) {
        console.error('Erreur lors de l\'export PDF:', error);
        showAlert('Erreur lors de l\'export PDF', 'error');
        showLoader(false);
    }
}

// Upload de photo d'élève
function uploadStudentPhoto() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    
    input.onchange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        
        // Vérifier la taille du fichier (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            showAlert('La photo ne doit pas dépasser 5MB', 'error');
            return;
        }
        
        showLoader(true);
        
        try {
            // Créer un FormData pour l'upload
            const formData = new FormData();
            formData.append('photo', file);
            formData.append('eleveId', studentId);
            
            // Simuler l'upload (à remplacer par un vrai appel API)
            const reader = new FileReader();
            reader.onload = function(event) {
                const photoElement = document.getElementById('studentPhoto');
                photoElement.src = event.target.result;
                
                // Sauvegarder en localStorage pour la démo
                localStorage.setItem(`student_photo_${studentId}`, event.target.result);
                
                showLoader(false);
                showAlert('Photo mise à jour avec succès', 'success');
            };
            reader.readAsDataURL(file);
            
        } catch (error) {
            console.error('Erreur lors de l\'upload:', error);
            showAlert('Erreur lors de l\'upload de la photo', 'error');
            showLoader(false);
        }
    };
    
    input.click();
}

// Système de messagerie avec les parents
function openMessaging(parentId) {
    const modal = document.createElement('div');
    modal.className = 'messaging-modal';
    modal.innerHTML = `
        <div class="messaging-content">
            <div class="messaging-header">
                <h3>Envoyer un message</h3>
                <button onclick="closeMessaging()" class="close-btn">&times;</button>
            </div>
            <div class="messaging-body">
                <div class="form-group">
                    <label>Destinataire</label>
                    <input type="text" id="recipient" readonly value="Parent ID: ${parentId}">
                </div>
                <div class="form-group">
                    <label>Objet</label>
                    <input type="text" id="messageSubject" placeholder="Objet du message">
                </div>
                <div class="form-group">
                    <label>Message</label>
                    <textarea id="messageContent" rows="5" placeholder="Votre message..."></textarea>
                </div>
                <div class="form-group">
                    <label>
                        <input type="checkbox" id="urgentMessage">
                        Message urgent
                    </label>
                </div>
            </div>
            <div class="messaging-footer">
                <button onclick="closeMessaging()" class="btn-cancel">Annuler</button>
                <button onclick="sendMessage(${parentId})" class="btn-send">
                    <i class="fas fa-paper-plane"></i> Envoyer
                </button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Ajouter les styles pour la modal
    const style = document.createElement('style');
    style.textContent = `
        .messaging-modal {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
            animation: fadeIn 0.3s ease;
        }
        
        .messaging-content {
            background: white;
            border-radius: 15px;
            width: 500px;
            max-width: 90%;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            animation: slideUp 0.3s ease;
        }
        
        @keyframes slideUp {
            from {
                transform: translateY(50px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }
        
        .messaging-header {
            padding: 20px;
            border-bottom: 1px solid #e0e0e0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .messaging-header h3 {
            margin: 0;
            color: #333;
        }
        
        .close-btn {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #999;
        }
        
        .messaging-body {
            padding: 20px;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #666;
            font-size: 14px;
        }
        
        .form-group input[type="text"],
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        .form-group textarea {
            resize: vertical;
        }
        
        .messaging-footer {
            padding: 20px;
            border-top: 1px solid #e0e0e0;
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }
        
        .btn-cancel {
            padding: 10px 20px;
            border: 1px solid #ddd;
            background: white;
            border-radius: 5px;
            cursor: pointer;
        }
        
        .btn-send {
            padding: 10px 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
    `;
    document.head.appendChild(style);
}

function closeMessaging() {
    const modal = document.querySelector('.messaging-modal');
    if (modal) {
        modal.remove();
    }
}

async function sendMessage(parentId) {
    const subject = document.getElementById('messageSubject').value;
    const content = document.getElementById('messageContent').value;
    const urgent = document.getElementById('urgentMessage').checked;
    
    if (!subject || !content) {
        showAlert('Veuillez remplir tous les champs', 'error');
        return;
    }
    
    showLoader(true);
    
    try {
        // Simuler l'envoi du message
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Sauvegarder le message en localStorage pour la démo
        const messages = JSON.parse(localStorage.getItem('school_messages') || '[]');
        messages.push({
            id: Date.now(),
            parentId,
            studentId,
            subject,
            content,
            urgent,
            date: new Date().toISOString(),
            status: 'sent'
        });
        localStorage.setItem('school_messages', JSON.stringify(messages));
        
        closeMessaging();
        showLoader(false);
        showAlert('Message envoyé avec succès', 'success');
        
        // Ajouter à l'historique
        addToHistory('Message envoyé', `Message envoyé au parent: ${subject}`);
    } catch (error) {
        console.error('Erreur lors de l\'envoi:', error);
        showAlert('Erreur lors de l\'envoi du message', 'error');
        showLoader(false);
    }
}

// Graphiques supplémentaires
function loadAdditionalCharts() {
    // Graphique de présence
    const attendanceCtx = document.createElement('canvas');
    attendanceCtx.id = 'attendanceChart';
    
    const attendanceContainer = document.createElement('div');
    attendanceContainer.className = 'chart-container';
    attendanceContainer.innerHTML = '<h3>Taux de présence mensuel</h3>';
    attendanceContainer.appendChild(attendanceCtx);
    
    // Ajouter le graphique à l'onglet académique
    const academicTab = document.getElementById('academic');
    if (academicTab) {
        academicTab.appendChild(attendanceContainer);
        
        new Chart(attendanceCtx, {
            type: 'bar',
            data: {
                labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév'],
                datasets: [{
                    label: 'Taux de présence (%)',
                    data: [95, 98, 92, 96, 94, 97],
                    backgroundColor: 'rgba(76, 175, 80, 0.6)',
                    borderColor: 'rgba(76, 175, 80, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: function(value) {
                                return value + '%';
                            }
                        }
                    }
                }
            }
        });
    }
    
    // Graphique de progression par matière
    const subjectsCtx = document.createElement('canvas');
    subjectsCtx.id = 'subjectsChart';
    
    const subjectsContainer = document.createElement('div');
    subjectsContainer.className = 'chart-container';
    subjectsContainer.innerHTML = '<h3>Performance par matière</h3>';
    subjectsContainer.appendChild(subjectsCtx);
    
    if (academicTab) {
        academicTab.appendChild(subjectsContainer);
        
        new Chart(subjectsCtx, {
            type: 'radar',
            data: {
                labels: ['Maths', 'Français', 'Sciences', 'Histoire', 'Anglais', 'Sport'],
                datasets: [{
                    label: 'Notes actuelles',
                    data: [16, 14, 15, 13, 17, 18],
                    backgroundColor: 'rgba(102, 126, 234, 0.2)',
                    borderColor: 'rgba(102, 126, 234, 1)',
                    pointBackgroundColor: 'rgba(102, 126, 234, 1)',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: 'rgba(102, 126, 234, 1)'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    r: {
                        beginAtZero: true,
                        max: 20,
                        ticks: {
                            stepSize: 5
                        }
                    }
                }
            }
        });
    }
}

// Système de notifications en temps réel
function initializeNotifications() {
    // Simuler des notifications en temps réel
    setInterval(() => {
        checkForNewNotifications();
    }, 30000); // Vérifier toutes les 30 secondes
}

async function checkForNewNotifications() {
    try {
        // Simuler la récupération de notifications
        const notifications = [
            { type: 'grade', message: 'Nouvelle note ajoutée en Mathématiques' },
            { type: 'payment', message: 'Rappel: Paiement des frais de scolarité' },
            { type: 'event', message: 'Réunion parents-professeurs le 15/10' }
        ];
        
        // Afficher une notification aléatoire (pour la démo)
        if (Math.random() > 0.7) {
            const notification = notifications[Math.floor(Math.random() * notifications.length)];
            showNotification(notification.message, 'info');
        }
    } catch (error) {
        console.error('Erreur lors de la vérification des notifications:', error);
    }
}

// Fonction pour afficher une notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
        <span>${message}</span>
    `;
    
    // Ajouter les styles si nécessaire
    if (!document.getElementById('notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            .notification {
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 15px 20px;
                background: white;
                border-radius: 10px;
                box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
                display: flex;
                align-items: center;
                gap: 10px;
                z-index: 10000;
                animation: slideInRight 0.3s ease;
                max-width: 350px;
            }
            
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            
            .notification-success {
                border-left: 4px solid #4CAF50;
            }
            
            .notification-error {
                border-left: 4px solid #f44336;
            }
            
            .notification-info {
                border-left: 4px solid #2196F3;
            }
            
            .notification i {
                font-size: 20px;
            }
            
            .notification-success i {
                color: #4CAF50;
            }
            
            .notification-error i {
                color: #f44336;
            }
            
            .notification-info i {
                color: #2196F3;
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(notification);
    
    // Supprimer après 5 secondes
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 5000);
}

// Ajouter à l'historique
function addToHistory(title, description) {
    const timeline = document.getElementById('timeline');
    if (!timeline) return;
    
    const item = document.createElement('div');
    item.className = 'timeline-item';
    item.innerHTML = `
        <div class="timeline-date">${new Date().toLocaleDateString('fr-FR')}</div>
        <div class="timeline-content">
            <div class="timeline-title">${title}</div>
            <div class="timeline-description">${description}</div>
        </div>
    `;
    
    // Ajouter en premier
    timeline.insertBefore(item, timeline.firstChild);
    
    // Animation
    item.style.animation = 'slideIn 0.3s ease';
}

// Initialiser les améliorations au chargement
document.addEventListener('DOMContentLoaded', function() {
    // Charger les graphiques supplémentaires après un délai
    setTimeout(() => {
        loadAdditionalCharts();
    }, 1000);
    
    // Initialiser les notifications
    initializeNotifications();
    
    // Charger la photo sauvegardée si elle existe
    const savedPhoto = localStorage.getItem(`student_photo_${studentId}`);
    if (savedPhoto) {
        document.getElementById('studentPhoto').src = savedPhoto;
    }
});
