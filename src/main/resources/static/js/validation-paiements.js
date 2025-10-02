let currentPage = 0;
let pageSize = 20;
let currentPaiementId = null;

document.addEventListener('DOMContentLoaded', () => {
    loadPaiementsEnAttente();
    loadStatistiques();
    document.getElementById('logoutBtn')?.addEventListener('click', () => AuthService.logout());
});

async function loadPaiementsEnAttente(page = 0) {
    try {
        currentPage = page;
        const data = await PaiementAPI.getPaiementsEnAttente(page, pageSize);
        displayPaiements(data.content || []);
        displayPagination(data);
    } catch (error) {
        console.error('Erreur:', error);
        document.getElementById('tableContent').innerHTML = '<div style="text-align: center; padding: 40px; color: #e74c3c;"><p>Erreur chargement</p></div>';
    }
}

function displayPaiements(paiements) {
    const container = document.getElementById('tableContent');
    if (!paiements || paiements.length === 0) {
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: #7f8c8d;"><p>✅ Aucun paiement en attente</p></div>';
        return;
    }
    
    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>N° Paiement</th>
                    <th>Date</th>
                    <th>Élève</th>
                    <th>Montant</th>
                    <th>Mode</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${paiements.map(p => `
                    <tr>
                        <td><strong>${p.numeroPaiement || 'N/A'}</strong></td>
                        <td>${formatDate(p.datePaiement)}</td>
                        <td>${p.eleveNom || 'N/A'}</td>
                        <td><strong style="color: #27ae60;">${formatMontant(p.montant)}</strong></td>
                        <td>${p.modePaiement || 'N/A'}</td>
                        <td><span class="badge badge-warning">⏳ ${p.statut}</span></td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-icon btn-view" onclick="showDetails(${p.id})" title="Détails">👁️</button>
                                <button class="btn-icon btn-edit" onclick="validerPaiementDirect(${p.id})" title="Valider" style="background: #27ae60;">✅</button>
                                <button class="btn-icon btn-delete" onclick="refuserPaiementDirect(${p.id})" title="Refuser">❌</button>
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
    if (currentPage > 0) html += `<button class="page-btn" onclick="loadPaiementsEnAttente(${currentPage - 1})">‹ Précédent</button>`;
    for (let i = 0; i < data.totalPages && i < 5; i++) {
        html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" onclick="loadPaiementsEnAttente(${i})">${i + 1}</button>`;
    }
    if (currentPage < data.totalPages - 1) html += `<button class="page-btn" onclick="loadPaiementsEnAttente(${currentPage + 1})">Suivant ›</button>`;
    pagination.innerHTML = html;
}

async function loadStatistiques() {
    try {
        const data = await PaiementAPI.getPaiementsEnAttente(0, 1000);
        const paiements = data.content || [];
        document.getElementById('totalEnAttente').textContent = paiements.length.toLocaleString();
        
        const today = new Date().toDateString();
        const validesAujourdhui = 0; // TODO: implémenter endpoint
        document.getElementById('validesAujourdhui').textContent = validesAujourdhui.toLocaleString();
        
        const total = paiements.reduce((sum, p) => sum + (p.montant || 0), 0);
        document.getElementById('montantTotal').textContent = formatMontant(total);
    } catch (error) { console.error('Erreur stats:', error); }
}

async function showDetails(paiementId) {
    try {
        const paiement = await PaiementAPI.getById(paiementId);
        currentPaiementId = paiementId;
        
        document.getElementById('detailsContent').innerHTML = `
            <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 15px 0;">
                <p><strong>N° Paiement:</strong> ${paiement.numeroPaiement}</p>
                <p><strong>Montant:</strong> <span style="color: #27ae60; font-size: 1.2em;">${formatMontant(paiement.montant)}</span></p>
                <p><strong>Date:</strong> ${formatDate(paiement.datePaiement)}</p>
                <p><strong>Mode:</strong> ${paiement.modePaiement}</p>
                <p><strong>Statut:</strong> <span class="badge badge-warning">${paiement.statut}</span></p>
                ${paiement.reference ? `<p><strong>Référence:</strong> ${paiement.reference}</p>` : ''}
            </div>
        `;
        
        document.getElementById('detailsModal').style.display = 'block';
    } catch (error) {
        console.error('Erreur:', error);
        Utils.showNotification('Erreur chargement détails', 'error');
    }
}

function closeDetailsModal() {
    document.getElementById('detailsModal').style.display = 'none';
    currentPaiementId = null;
}

async function validerPaiement() {
    if (!currentPaiementId) return;
    await validerPaiementDirect(currentPaiementId);
    closeDetailsModal();
}

async function annulerPaiement() {
    if (!currentPaiementId) return;
    await refuserPaiementDirect(currentPaiementId);
    closeDetailsModal();
}

async function validerPaiementDirect(paiementId) {
    if (!confirm('Confirmer la validation de ce paiement?\n\nCeci enverra une notification et un email au parent.')) return;
    
    try {
        Utils.showLoader();
        await PaiementAPI.validerPaiement(paiementId);
        Utils.hideLoader();
        Utils.showNotification('✅ Paiement validé avec succès! Notifications envoyées.', 'success');
        loadPaiementsEnAttente(currentPage);
        loadStatistiques();
    } catch (error) {
        Utils.hideLoader();
        console.error('Erreur:', error);
        Utils.showNotification('Erreur lors de la validation', 'error');
    }
}

async function refuserPaiementDirect(paiementId) {
    if (!confirm('Confirmer le refus de ce paiement?')) return;
    
    try {
        Utils.showLoader();
        await PaiementAPI.annulerPaiement(paiementId);
        Utils.hideLoader();
        Utils.showNotification('Paiement refusé', 'success');
        loadPaiementsEnAttente(currentPage);
        loadStatistiques();
    } catch (error) {
        Utils.hideLoader();
        console.error('Erreur:', error);
        Utils.showNotification('Erreur lors du refus', 'error');
    }
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('fr-FR', { 
        year: 'numeric', month: 'short', day: 'numeric', 
        hour: '2-digit', minute: '2-digit' 
    });
}

function formatMontant(montant) {
    return (montant || 0).toLocaleString('fr-FR') + ' BIF';
}
