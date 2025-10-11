// Variables globales
let currentStudent = null;
let studentId = null;
let performanceChart = null;

// Initialisation au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    // Récupérer l'ID de l'élève depuis l'URL
    const urlParams = new URLSearchParams(window.location.search);
    studentId = urlParams.get('id');
    
    if (!studentId) {
        showAlert('Aucun élève spécifié', 'error');
        setTimeout(() => {
            window.location.href = '/eleves';
        }, 2000);
        return;
    }
    
    // Charger les données de l'élève
    loadStudentData();
});

// Fonction pour charger les données de l'élève
async function loadStudentData() {
    showLoader(true);
    
    try {
        // Récupérer les données de l'élève
        currentStudent = await EleveAPI.getById(studentId);
        
        // Debug: Afficher la structure des données reçues
        console.log('Données de l\'élève reçues:', currentStudent);
        console.log('Type du statut:', typeof currentStudent.statut, 'Valeur:', currentStudent.statut);
        console.log('Type du genre:', typeof currentStudent.genre, 'Valeur:', currentStudent.genre);
        
        // Mettre à jour l'interface
        updateProfileCard();
        updateInformationsTab();
        updateFinancialTab();
        updateMedicalTab();
        updateParentsTab();
        loadAcademicData();
        loadHistory();
        
        showLoader(false);
    } catch (error) {
        console.error('Erreur lors du chargement des données:', error);
        showAlert('Erreur lors du chargement des données de l\'élève', 'error');
        showLoader(false);
    }
}

// Mettre à jour la carte de profil
function updateProfileCard() {
    if (!currentStudent) return;
    
    // Photo
    const photoElement = document.getElementById('studentPhoto');
    if (currentStudent.photoUrl) {
        photoElement.src = currentStudent.photoUrl;
    } else {
        // Générer une image avec les initiales
        const initials = getInitials(currentStudent.prenom, currentStudent.nom);
        photoElement.src = generateAvatarUrl(initials);
    }
    
    // Nom et matricule
    document.getElementById('studentName').textContent = 
        `${currentStudent.prenom} ${currentStudent.deuxiemePrenom || ''} ${currentStudent.nom}`.trim();
    document.getElementById('studentMatricule').textContent = `Matricule: ${currentStudent.matricule}`;
    
    // Statut
    const statusBadge = document.getElementById('statusBadge');
    // Gérer le statut qu'il soit un objet ou une chaîne
    let statutText = 'ACTIF';
    if (currentStudent.statut) {
        if (typeof currentStudent.statut === 'object') {
            statutText = currentStudent.statut.key || currentStudent.statut.value || 'ACTIF';
        } else {
            statutText = currentStudent.statut;
        }
    }
    statusBadge.textContent = statutText;
    if (statutText === 'INACTIF') {
        statusBadge.classList.add('inactive');
    }
    
    // Informations rapides
    document.getElementById('birthDate').textContent = formatDate(currentStudent.dateNaissance);
    document.getElementById('studentClass').textContent = currentStudent.classeInfo?.nom || '-';
    document.getElementById('emergencyContact').textContent = currentStudent.numeroUrgence || '-';
    document.getElementById('enrollmentDate').textContent = formatDate(currentStudent.dateInscription);
}

// Mettre à jour l'onglet Informations
function updateInformationsTab() {
    if (!currentStudent) return;
    
    document.getElementById('fullName').textContent = 
        `${currentStudent.prenom} ${currentStudent.deuxiemePrenom || ''} ${currentStudent.nom}`.trim();
    document.getElementById('gender').textContent = formatGender(currentStudent.genre);
    document.getElementById('age').textContent = currentStudent.age ? `${currentStudent.age} ans` : calculateAge(currentStudent.dateNaissance);
    document.getElementById('nationality').textContent = currentStudent.nationalite || 'Burundaise';
    document.getElementById('birthPlace').textContent = currentStudent.lieuNaissance || '-';
    document.getElementById('address').textContent = currentStudent.adresseDomicile || '-';
    document.getElementById('quartier').textContent = currentStudent.quartier || '-';
    document.getElementById('previousSchool').textContent = currentStudent.ecoleProvenance || '-';
}

// Mettre à jour l'onglet Financier
function updateFinancialTab() {
    if (!currentStudent) return;
    
    // Montants financiers
    const totalToPay = currentStudent.montantTotal || 0;
    const totalPaid = currentStudent.montantPaye || 0;
    const remaining = currentStudent.montantDu || (totalToPay - totalPaid);
    
    document.getElementById('totalToPay').textContent = formatCurrency(totalToPay);
    document.getElementById('totalPaid').textContent = formatCurrency(totalPaid);
    document.getElementById('remainingBalance').textContent = formatCurrency(remaining);
    
    // Informations de bourse
    const isScholar = currentStudent.boursier || false;
    const scholarPercentage = currentStudent.pourcentageBourse || 0;
    
    document.getElementById('scholarshipStatus').textContent = isScholar ? 'Boursier' : 'Non boursier';
    document.getElementById('scholarshipPercentage').textContent = `${scholarPercentage}%`;
    
    // Charger l'historique des paiements
    loadPaymentHistory();
}

// Mettre à jour l'onglet Médical
function updateMedicalTab() {
    if (!currentStudent) return;
    
    document.getElementById('bloodType').textContent = currentStudent.groupeSanguin || '-';
    document.getElementById('allergies').textContent = currentStudent.allergies || 'Aucune allergie connue';
    document.getElementById('chronicDiseases').textContent = currentStudent.maladiesChroniques || 'Aucune';
}

// Mettre à jour l'onglet Parents
function updateParentsTab() {
    if (!currentStudent || !currentStudent.parentsInfo || currentStudent.parentsInfo.length === 0) {
        return;
    }
    
    const parentCardsContainer = document.getElementById('parentCards');
    parentCardsContainer.innerHTML = '';
    
    currentStudent.parentsInfo.forEach(parent => {
        const parentCard = createParentCard(parent);
        parentCardsContainer.appendChild(parentCard);
    });
}

// Créer une carte parent
function createParentCard(parent) {
    const card = document.createElement('div');
    card.className = 'parent-card';
    
    const initials = getInitials(parent.prenom, parent.nom);
    const parentId = parent.id || Math.floor(Math.random() * 10000);
    
    card.innerHTML = `
        <div class="parent-header">
            <div class="parent-avatar">${initials}</div>
            <div class="parent-info">
                <h4>${parent.prenom} ${parent.nom}</h4>
                <div class="parent-relation">${parent.lienParente || 'Parent'}</div>
            </div>
        </div>
        <div class="parent-contacts">
            ${parent.telephone ? `
                <div class="contact-item">
                    <i class="fas fa-phone"></i>
                    <span>${parent.telephone}</span>
                </div>
            ` : ''}
            ${parent.email ? `
                <div class="contact-item">
                    <i class="fas fa-envelope"></i>
                    <span>${parent.email}</span>
                </div>
            ` : ''}
            ${parent.adresse ? `
                <div class="contact-item">
                    <i class="fas fa-map-marker-alt"></i>
                    <span>${parent.adresse}</span>
                </div>
            ` : ''}
        </div>
        <div style="margin-top: 15px; padding-top: 15px; border-top: 1px solid #e0e0e0;">
            <button onclick="openMessaging(${parentId})" style="
                width: 100%;
                padding: 10px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                font-weight: 500;
                transition: all 0.3s ease;
            " onmouseover="this.style.transform='translateY(-2px)'" onmouseout="this.style.transform='translateY(0)'">
                <i class="fas fa-comment"></i> Envoyer un message
            </button>
        </div>
    `;
    
    return card;
}

// Charger les données académiques
async function loadAcademicData() {
    // Créer un graphique de performance fictif
    const ctx = document.getElementById('performanceChart').getContext('2d');
    
    if (performanceChart) {
        performanceChart.destroy();
    }
    
    performanceChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Sept', 'Oct', 'Nov', 'Déc', 'Jan', 'Fév', 'Mars', 'Avr', 'Mai', 'Juin'],
            datasets: [{
                label: 'Moyenne générale',
                data: [12, 13, 12.5, 14, 13.5, 14.5, 15, 14.8, 15.2, 15.5],
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                title: {
                    display: true,
                    text: 'Évolution de la moyenne générale'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 20,
                    ticks: {
                        callback: function(value) {
                            return value + '/20';
                        }
                    }
                }
            }
        }
    });
    
    // Charger les dernières notes (données fictives pour la démo)
    loadRecentGrades();
}

// Charger les notes récentes
function loadRecentGrades() {
    const gradesData = [
        { subject: 'Mathématiques', grade: 16, coefficient: 4, date: '2024-10-10', appreciation: 'Très bien' },
        { subject: 'Français', grade: 14, coefficient: 3, date: '2024-10-09', appreciation: 'Bien' },
        { subject: 'Sciences', grade: 15, coefficient: 3, date: '2024-10-08', appreciation: 'Très bien' },
        { subject: 'Histoire-Géo', grade: 13, coefficient: 2, date: '2024-10-07', appreciation: 'Assez bien' },
        { subject: 'Anglais', grade: 17, coefficient: 2, date: '2024-10-06', appreciation: 'Excellent' }
    ];
    
    const tbody = document.getElementById('gradesTableBody');
    tbody.innerHTML = '';
    
    gradesData.forEach(grade => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${grade.subject}</td>
            <td><span class="grade-badge ${getGradeClass(grade.grade)}">${grade.grade}/20</span></td>
            <td>${grade.coefficient}</td>
            <td>${formatDate(grade.date)}</td>
            <td>${grade.appreciation}</td>
        `;
        tbody.appendChild(row);
    });
}

// Charger l'historique des paiements
async function loadPaymentHistory() {
    // Données fictives pour la démo
    const paymentsData = [
        { date: '2024-10-01', amount: 50000, type: 'Frais de scolarité', mode: 'Virement', status: 'Payé' },
        { date: '2024-09-15', amount: 25000, type: 'Inscription', mode: 'Espèces', status: 'Payé' },
        { date: '2024-09-01', amount: 10000, type: 'Uniforme', mode: 'Mobile Money', status: 'Payé' }
    ];
    
    const tbody = document.getElementById('paymentsTableBody');
    tbody.innerHTML = '';
    
    paymentsData.forEach(payment => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${formatDate(payment.date)}</td>
            <td>${formatCurrency(payment.amount)}</td>
            <td>${payment.type}</td>
            <td>${payment.mode}</td>
            <td><span class="grade-badge grade-excellent">${payment.status}</span></td>
        `;
        tbody.appendChild(row);
    });
}

// Charger l'historique
function loadHistory() {
    const timelineData = [
        { date: currentStudent.dateInscription, title: 'Inscription', description: 'L\'élève a été inscrit dans l\'établissement' },
        { date: '2024-10-01', title: 'Paiement effectué', description: 'Paiement des frais de scolarité' },
        { date: '2024-09-15', title: 'Changement de classe', description: 'Transfert vers la classe ' + (currentStudent.classeInfo?.nom || 'actuelle') }
    ];
    
    const timeline = document.getElementById('timeline');
    timeline.innerHTML = '';
    
    timelineData.forEach(event => {
        const item = document.createElement('div');
        item.className = 'timeline-item';
        item.innerHTML = `
            <div class="timeline-date">${formatDate(event.date)}</div>
            <div class="timeline-content">
                <div class="timeline-title">${event.title}</div>
                <div class="timeline-description">${event.description}</div>
            </div>
        `;
        timeline.appendChild(item);
    });
}

// Changer d'onglet
function switchTab(tabName) {
    // Retirer la classe active de tous les boutons et contenus
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    // Ajouter la classe active au bouton et contenu sélectionnés
    event.target.classList.add('active');
    document.getElementById(tabName).classList.add('active');
}

// Modifier l'élève
function editStudent() {
    if (!studentId) return;
    window.location.href = `/eleve-form.html?id=${studentId}`;
}

// Imprimer la fiche
function printStudentCard() {
    window.print();
}

// Supprimer l'élève
async function deleteStudent() {
    if (!studentId) return;
    
    if (!confirm(`Êtes-vous sûr de vouloir supprimer l'élève ${currentStudent.prenom} ${currentStudent.nom} ?`)) {
        return;
    }
    
    showLoader(true);
    
    try {
        await EleveAPI.delete(studentId);
        showAlert('Élève supprimé avec succès', 'success');
        setTimeout(() => {
            window.location.href = '/eleves';
        }, 1500);
    } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        showAlert('Erreur lors de la suppression de l\'élève', 'error');
        showLoader(false);
    }
}

// Fonctions utilitaires
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', { 
        day: '2-digit', 
        month: 'long', 
        year: 'numeric' 
    });
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'XOF',
        minimumFractionDigits: 0
    }).format(amount).replace('XOF', 'FCFA');
}

function formatGender(gender) {
    // Gérer le genre qu'il soit un objet ou une chaîne
    let genderKey = gender;
    if (typeof gender === 'object' && gender !== null) {
        genderKey = gender.key || gender.value || gender;
    }
    
    const genders = {
        'MASCULIN': 'Masculin',
        'FEMININ': 'Féminin',
        'AUTRE': 'Autre'
    };
    return genders[genderKey] || genderKey || '-';
}

function calculateAge(birthDate) {
    if (!birthDate) return '-';
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
        age--;
    }
    return `${age} ans`;
}

function getInitials(firstName, lastName) {
    const first = firstName ? firstName.charAt(0).toUpperCase() : '';
    const last = lastName ? lastName.charAt(0).toUpperCase() : '';
    return first + last;
}

function generateAvatarUrl(initials) {
    // Générer une URL d'avatar avec les initiales
    const canvas = document.createElement('canvas');
    canvas.width = 150;
    canvas.height = 150;
    const ctx = canvas.getContext('2d');
    
    // Fond dégradé
    const gradient = ctx.createLinearGradient(0, 0, 150, 150);
    gradient.addColorStop(0, '#667eea');
    gradient.addColorStop(1, '#764ba2');
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, 150, 150);
    
    // Texte
    ctx.fillStyle = 'white';
    ctx.font = 'bold 60px Poppins';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(initials, 75, 75);
    
    return canvas.toDataURL();
}

function getGradeClass(grade) {
    if (grade >= 16) return 'grade-excellent';
    if (grade >= 14) return 'grade-good';
    if (grade >= 10) return 'grade-average';
    return 'grade-poor';
}

function showLoader(show) {
    const loader = document.getElementById('loader');
    if (show) {
        loader.classList.add('active');
    } else {
        loader.classList.remove('active');
    }
}

function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alertContainer');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} show`;
    alert.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
        <span>${message}</span>
    `;
    
    alertContainer.appendChild(alert);
    
    setTimeout(() => {
        alert.remove();
    }, 5000);
}
