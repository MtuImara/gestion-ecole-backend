let currentPage = 0;
let pageSize = 20;
let filters = { eleveId: null, classeId: null, dateDebut: null, dateFin: null, search: '' };

document.addEventListener('DOMContentLoaded', () => {
    loadRecus();
    loadStatistiques();
    loadFilterOptions();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('logoutBtn')?.addEventListener('click', () => AuthService.logout());
    document.getElementById('searchInput')?.addEventListener('input', (e) => {
        filters.search = e.target.value.trim();
        if (filters.search.length === 0 || filters.search.length >= 2) loadRecus();
    });
    document.getElementById('filterEleve')?.addEventListener('change', (e) => { filters.eleveId = e.target.value || null; loadRecus(); });
    document.getElementById('filterClasse')?.addEventListener('change', (e) => { filters.classeId = e.target.value || null; loadRecus(); });
}

async function loadRecus(page = 0) {
    try {
        currentPage = page;
        const data = await PaiementAPI.getAll(page, pageSize);
        displayRecus(data.content || []);
        displayPagination(data);
    } catch (error) {
        console.error('Erreur:', error);
        document.getElementById('tableContent').innerHTML = '<div style=\"text-align: center; padding: 40px; color: #e74c3c;\"><p>Erreur chargement</p></div>';
    }
}

function displayRecus(recus) {
    const container = document.getElementById('tableContent');
    if (!recus || recus.length === 0) {
        container.innerHTML = '<div style=\"text-align: center; padding: 40px; color: #7f8c8d;\"><p>Aucun reçu</p></div>';
        return;
    }
    container.innerHTML = '<table><thead><tr><th>N° Reçu</th><th>Date</th><th>Élève</th><th>Montant</th><th>Mode</th><th>Actions</th></tr></thead><tbody>' + 
        recus.map(p => '<tr><td><strong>' + (p.numeroRecu || 'R-' + p.id) + '</strong></td><td>' + formatDate(p.datePaiement) + '</td><td>' + (p.eleveNom || 'N/A') + '</td><td><strong>' + formatMontant(p.montant) + '</strong></td><td>' + (p.modePaiement || 'N/A') + '</td><td><div class=\"action-buttons\"><button class=\"btn-icon btn-view\" onclick=\"telechargerRecu(' + p.id + ')\" title=\"Télécharger\">📥</button><button class=\"btn-icon btn-edit\" onclick=\"imprimerRecu(' + p.id + ')\" title=\"Imprimer\">🖨️</button></div></td></tr>').join('') + '</tbody></table>';
}

function displayPagination(data) {
    const pagination = document.getElementById('pagination');
    if (!data || !data.totalPages || data.totalPages <= 1) { pagination.innerHTML = ''; return; }
    let html = '';
    if (currentPage > 0) html += '<button class=\"page-btn\" onclick=\"loadRecus(' + (currentPage - 1) + ')\">‹ Précédent</button>';
    for (let i = 0; i < data.totalPages && i < 5; i++) {
        html += '<button class=\"page-btn ' + (i === currentPage ? 'active' : '') + '\" onclick=\"loadRecus(' + i + ')\">' + (i + 1) + '</button>';
    }
    if (currentPage < data.totalPages - 1) html += '<button class=\"page-btn\" onclick=\"loadRecus(' + (currentPage + 1) + ')\">Suivant ›</button>';
    pagination.innerHTML = html;
}

async function loadStatistiques() {
    try {
        const data = await PaiementAPI.getAll(0, 1000);
        const recus = data.content || [];
        document.getElementById('totalRecus').textContent = recus.length.toLocaleString();
        const today = new Date();
        const mois = recus.filter(r => new Date(r.datePaiement).getMonth() === today.getMonth()).length;
        document.getElementById('recusMois').textContent = mois.toLocaleString();
        const total = recus.reduce((sum, r) => sum + (r.montant || 0), 0);
        document.getElementById('montantTotal').textContent = formatMontant(total);
    } catch (error) { console.error('Erreur stats:', error); }
}

async function loadFilterOptions() {
    try {
        const [eleves, classes] = await Promise.all([EleveAPI.getAll(0, 1000), ClasseAPI.getAll(0, 100)]);
        const selectEleve = document.getElementById('filterEleve');
        if (selectEleve && eleves.content) {
            eleves.content.forEach(e => { selectEleve.innerHTML += '<option value=\"' + e.id + '\">' + e.prenom + ' ' + e.nom + '</option>'; });
        }
        const selectClasse = document.getElementById('filterClasse');
        if (selectClasse && classes.content) {
            classes.content.forEach(c => { selectClasse.innerHTML += '<option value=\"' + c.id + '\">' + c.nom + '</option>'; });
        }
    } catch (error) { console.error('Erreur filtres:', error); }
}

function applyFilters() {
    filters.dateDebut = document.getElementById('dateDebut')?.value || null;
    filters.dateFin = document.getElementById('dateFin')?.value || null;
    loadRecus(0);
}

async function telechargerRecu(paiementId) {
    try {
        const blob = await PaiementAPI.telechargerRecu(paiementId);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'recu-' + paiementId + '.pdf';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        Utils.showNotification('Reçu téléchargé', 'success');
    } catch (error) {
        console.error('Erreur téléchargement:', error);
        Utils.showNotification('Erreur téléchargement', 'error');
    }
}

async function imprimerRecu(paiementId) {
    try {
        const blob = await PaiementAPI.telechargerRecu(paiementId);
        const url = window.URL.createObjectURL(blob);
        const iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        iframe.src = url;
        document.body.appendChild(iframe);
        iframe.onload = () => { iframe.contentWindow.print(); setTimeout(() => { document.body.removeChild(iframe); window.URL.revokeObjectURL(url); }, 100); };
    } catch (error) { console.error('Erreur impression:', error); Utils.showNotification('Erreur impression', 'error'); }
}

function formatDate(dateStr) { if (!dateStr) return 'N/A'; return new Date(dateStr).toLocaleDateString('fr-FR'); }
function formatMontant(montant) { return (montant || 0).toLocaleString('fr-FR') + ' BIF'; }
