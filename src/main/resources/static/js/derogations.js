let currentPage = 0;
let pageSize = 20;
let currentFilter = 'EN_ATTENTE';

document.addEventListener('DOMContentLoaded', () => {
    loadDerogations();
    loadStatistiques();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('logoutBtn')?.addEventListener('click', () => AuthService.logout());
    
    // Filtres par statut
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');
            currentFilter = e.target.dataset.statut;
            loadDerogations();
        });
    });
}

async function loadDerogations(page = 0) {
    try {
        currentPage = page;
        Utils.showLoader();
        
        const data = await DerogationAPI.getByStatut(currentFilter, page, pageSize);
        displayDerogations(data.content || []);
        displayPagination(data);
        
        Utils.hideLoader();
    } catch (error) {
        Utils.hideLoader();
        console.error('Erreur:', error);
        document.getElementById('tableContent').innerHTML = '<div style="text-align: center; padding: 40px; color: #e74c3c;"><p>Erreur chargement</p></div>';
    }
}

function displayDerogations(derogations) {
    const container = document.getElementById('tableContent');
    
    if (!derogations || derogations.length === 0) {
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: #7f8c8d;"><p>Aucune dérogation</p></div>';
        return;
    }
    
    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>N° Dérogation</th>
                    <th>Date Demande</th>
                    <th>Type</th>
                    <th>Élève</th>
                    <th>Montant Concerné</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${derogations.map(d => `
                    <tr>
                        <td><strong>${d.numeroDerogation || 'N/A'}</strong></td>
                        <td>${formatDate(d.dateDemande)}</td>
                        <td><span class="badge" style="background: #3498db;">${d.typeDerogation}</span></td>
                        <td>${d.elevePrenom || ''} ${d.eleveNom || 'N/A'}</td>
                        <td><strong>${formatMontant(d.montantConcerne)}</strong></td>
                        <td>${getStatutBadge(d.statut)}</td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-icon btn-view" onclick="showDetails(${d.id})" title="Détails">👁️</button>
                                ${d.statut === 'EN_ATTENTE' ? `
                                    <button class="btn-icon btn-edit" onclick="approuverDerogation(${d.id})" title="Approuver" style="background: #27ae60;">✅</button>
                                    <button class="btn-icon btn-delete" onclick="rejeterDerogation(${d.id})" title="Rejeter">❌</button>
                                ` : ''}
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

function getStatutBadge(statut) {
    const badges = {
        'EN_ATTENTE': '<span class="badge badge-warning">⏳ En Attente</span>',
        'APPROUVEE': '<span class="badge badge-active">✅ Approuvée</span>',
        'REJETEE': '<span class="badge badge-inactive">❌ Rejetée</span>'
    };
    return badges[statut] || statut;
}

function displayPagination(data) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    
    if (!data || !data.totalPages || data.totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let html = '';
    if (currentPage > 0) {
        html += `<button class="page-btn" onclick="loadDerogations(${currentPage - 1})">‹ Précédent</button>`;
    }
    
    for (let i = 0; i < data.totalPages && i < 5; i++) {
        html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" onclick="loadDerogations(${i})">${i + 1}</button>`;
    }
    
    if (currentPage < data.totalPages - 1) {
        html += `<button class="page-btn" onclick="loadDerogations(${currentPage + 1})">Suivant ›</button>`;
    }
    
    pagination.innerHTML = html;
}

async function loadStatistiques() {
    try {
        const stats = await DerogationAPI.countParStatut();
        
        document.getElementById('totalDerogations').textContent = (stats.enAttente || 0).toLocaleString();
        document.getElementById('totalBoursiers').textContent = (stats.approuvees || 0).toLocaleString();
        document.getElementById('montantReductions').textContent = '0 BIF'; // TODO: calculer le montant total
        
    } catch (error) {
        console.error('Erreur stats:', error);
    }
}

async function showDetails(derogationId) {
    try {
        const derogation = await DerogationAPI.getById(derogationId);
        
        const modalContent = `
            <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 15px 0;">
                <h3 style="color: #2c3e50; margin-bottom: 15px;">Détails de la Dérogation</h3>
                
                <div style="display: grid; grid-template-columns: 200px 1fr; gap: 10px; margin-bottom: 10px;">
                    <strong>N° Dérogation:</strong>
                    <span>${derogation.numeroDerogation}</span>
                    
                    <strong>Type:</strong>
                    <span>${derogation.typeDerogation}</span>
                    
                    <strong>Date Demande:</strong>
                    <span>${formatDate(derogation.dateDemande)}</span>
                    
                    <strong>Élève:</strong>
                    <span>${derogation.elevePrenom} ${derogation.eleveNom}</span>
                    
                    <strong>Montant Concerné:</strong>
                    <span style="color: #27ae60; font-weight: bold;">${formatMontant(derogation.montantConcerne)}</span>
                    
                    <strong>Statut:</strong>
                    <span>${getStatutBadge(derogation.statut)}</span>
                    
                    ${derogation.dateDecision ? `
                        <strong>Date Décision:</strong>
                        <span>${formatDate(derogation.dateDecision)}</span>
                    ` : ''}
                    
                    ${derogation.decidePar ? `
                        <strong>Décidé Par:</strong>
                        <span>${derogation.decidePar}</span>
                    ` : ''}
                </div>
                
                <div style="margin-top: 15px;">
                    <strong>Motif:</strong>
                    <p style="background: white; padding: 10px; border-radius: 5px; margin-top: 5px;">${derogation.motif}</p>
                </div>
                
                ${derogation.observations ? `
                    <div style="margin-top: 15px;">
                        <strong>Observations:</strong>
                        <p style="background: white; padding: 10px; border-radius: 5px; margin-top: 5px;">${derogation.observations}</p>
                    </div>
                ` : ''}
            </div>
        `;
        
        Utils.showModal('Détails de la Dérogation', modalContent);
        
    } catch (error) {
        console.error('Erreur:', error);
        Utils.showNotification('Erreur chargement détails', 'error');
    }
}

async function approuverDerogation(derogationId) {
    const decidePar = prompt('Votre nom (pour traçabilité):');
    if (!decidePar) return;
    
    if (!confirm('Confirmer l\'approbation de cette dérogation?')) return;
    
    try {
        Utils.showLoader();
        await DerogationAPI.approuver(derogationId, decidePar);
        Utils.hideLoader();
        Utils.showNotification('✅ Dérogation approuvée! Notifications envoyées.', 'success');
        loadDerogations(currentPage);
        loadStatistiques();
    } catch (error) {
        Utils.hideLoader();
        console.error('Erreur:', error);
        Utils.showNotification('Erreur lors de l\'approbation', 'error');
    }
}

async function rejeterDerogation(derogationId) {
    const decidePar = prompt('Votre nom (pour traçabilité):');
    if (!decidePar) return;
    
    const motifRejet = prompt('Motif du rejet:');
    if (!motifRejet) return;
    
    if (!confirm('Confirmer le rejet de cette dérogation?')) return;
    
    try {
        Utils.showLoader();
        await DerogationAPI.rejeter(derogationId, decidePar, motifRejet);
        Utils.hideLoader();
        Utils.showNotification('Dérogation rejetée. Notifications envoyées.', 'success');
        loadDerogations(currentPage);
        loadStatistiques();
    } catch (error) {
        Utils.hideLoader();
        console.error('Erreur:', error);
        Utils.showNotification('Erreur lors du rejet', 'error');
    }
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
        year: 'numeric', month: 'short', day: 'numeric'
    });
}

function formatMontant(montant) {
    return (montant || 0).toLocaleString('fr-FR') + ' BIF';
}
