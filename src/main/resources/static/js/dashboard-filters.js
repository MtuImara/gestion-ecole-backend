/**
 * Module de filtres et alertes pour le dashboard
 * Gère les filtres temporels, les comparaisons et les alertes
 */

class DashboardFilters {
    constructor() {
        this.currentPeriod = '30'; // 30 jours par défaut
        this.alerts = [];
        this.thresholds = {
            paiementsRetard: 20,
            tauxPresenceMin: 85,
            nouveauxElevesMin: 5,
            tauxRemplissageMin: 70
        };
    }

    /**
     * Initialiser les filtres
     */
    init() {
        console.log('[Dashboard Filters] Initialisation...');
        this.setupFilterControls();
        this.checkAlerts();
        
        // Vérifier les alertes toutes les minutes
        setInterval(() => this.checkAlerts(), 60000);
    }

    /**
     * Configurer les contrôles de filtres
     */
    setupFilterControls() {
        // Créer le panneau de filtres s'il n'existe pas
        if (!document.getElementById('filterPanel')) {
            this.createFilterPanel();
        }
        
        // Ajouter les event listeners
        const periodFilter = document.getElementById('periodFilter');
        if (periodFilter) {
            periodFilter.addEventListener('change', (e) => {
                this.currentPeriod = e.target.value;
                this.applyPeriodFilter(this.currentPeriod);
            });
        }
        
        // Boutons de période rapide
        this.addQuickFilters();
    }

    /**
     * Créer le panneau de filtres
     */
    createFilterPanel() {
        const filterHTML = `
            <div id="filterPanel" style="
                background: white;
                padding: 20px;
                border-radius: 15px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                margin-bottom: 20px;
            ">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px;">
                    <div>
                        <h3 style="margin: 0 0 10px 0; color: #2c3e50;">🔍 Filtres et Période</h3>
                        <div id="quickFilters" style="display: flex; gap: 10px; flex-wrap: wrap;">
                            <button class="filter-btn" data-period="1">Aujourd'hui</button>
                            <button class="filter-btn" data-period="7">7 jours</button>
                            <button class="filter-btn active" data-period="30">30 jours</button>
                            <button class="filter-btn" data-period="90">3 mois</button>
                            <button class="filter-btn" data-period="365">Cette année</button>
                            <button class="filter-btn" data-period="custom">Personnalisé</button>
                        </div>
                    </div>
                    
                    <div style="display: flex; gap: 10px;">
                        <button onclick="dashboardFilters.compareWithLastPeriod()" style="
                            padding: 10px 20px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            border: none;
                            border-radius: 8px;
                            cursor: pointer;
                        ">📊 Comparer</button>
                        
                        <button onclick="dashboardFilters.showAlerts()" style="
                            padding: 10px 20px;
                            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
                            color: white;
                            border: none;
                            border-radius: 8px;
                            cursor: pointer;
                            position: relative;
                        ">
                            🔔 Alertes
                            <span id="alertBadge" style="
                                position: absolute;
                                top: -5px;
                                right: -5px;
                                background: red;
                                color: white;
                                border-radius: 50%;
                                width: 20px;
                                height: 20px;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                font-size: 12px;
                            ">0</span>
                        </button>
                    </div>
                </div>
                
                <div id="customDateRange" style="display: none; margin-top: 15px;">
                    <label>Date début: <input type="date" id="startDate"></label>
                    <label style="margin-left: 15px;">Date fin: <input type="date" id="endDate"></label>
                    <button onclick="dashboardFilters.applyCustomDateRange()" style="margin-left: 15px;">Appliquer</button>
                </div>
                
                <div id="comparisonResults" style="margin-top: 20px; display: none;"></div>
            </div>
        `;
        
        // Insérer le panneau avant les stats
        const mainContent = document.querySelector('.main-content');
        const statsGrid = document.querySelector('.stats-grid');
        if (mainContent && statsGrid) {
            const filterDiv = document.createElement('div');
            filterDiv.innerHTML = filterHTML;
            mainContent.insertBefore(filterDiv.firstChild, statsGrid);
        }
        
        // Ajouter les styles CSS
        this.addFilterStyles();
    }

    /**
     * Ajouter les styles pour les filtres
     */
    addFilterStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .filter-btn {
                padding: 8px 16px;
                background: #f0f0f0;
                border: 1px solid #ddd;
                border-radius: 5px;
                cursor: pointer;
                transition: all 0.3s;
            }
            
            .filter-btn:hover {
                background: #e0e0e0;
            }
            
            .filter-btn.active {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border-color: #667eea;
            }
            
            .alert-item {
                padding: 10px;
                margin: 5px 0;
                border-radius: 5px;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            
            .alert-warning {
                background: #fff3cd;
                border-left: 4px solid #ffc107;
            }
            
            .alert-danger {
                background: #f8d7da;
                border-left: 4px solid #dc3545;
            }
            
            .alert-success {
                background: #d4edda;
                border-left: 4px solid #28a745;
            }
            
            .comparison-card {
                background: #f8f9fa;
                padding: 15px;
                border-radius: 8px;
                margin: 10px 0;
            }
            
            .comparison-value {
                font-size: 24px;
                font-weight: bold;
                color: #2c3e50;
            }
            
            .comparison-change {
                display: inline-block;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 600;
                margin-left: 10px;
            }
            
            .change-positive {
                background: #d4edda;
                color: #155724;
            }
            
            .change-negative {
                background: #f8d7da;
                color: #721c24;
            }
        `;
        document.head.appendChild(style);
    }

    /**
     * Ajouter les boutons de filtres rapides
     */
    addQuickFilters() {
        const quickFilters = document.getElementById('quickFilters');
        if (!quickFilters) return;
        
        const buttons = quickFilters.querySelectorAll('.filter-btn');
        buttons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                // Retirer la classe active de tous les boutons
                buttons.forEach(b => b.classList.remove('active'));
                // Ajouter la classe active au bouton cliqué
                e.target.classList.add('active');
                
                const period = e.target.dataset.period;
                if (period === 'custom') {
                    document.getElementById('customDateRange').style.display = 'block';
                } else {
                    document.getElementById('customDateRange').style.display = 'none';
                    this.applyPeriodFilter(period);
                }
            });
        });
    }

    /**
     * Appliquer le filtre de période
     */
    async applyPeriodFilter(period) {
        console.log(`[Dashboard Filters] Application du filtre: ${period} jours`);
        
        // Calculer les dates
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - parseInt(period));
        
        // Recharger les données avec la nouvelle période
        if (window.dashboardRealtime) {
            await window.dashboardRealtime.loadAllData();
        }
        
        // Afficher la période sélectionnée
        this.showPeriodInfo(startDate, endDate);
    }

    /**
     * Appliquer une période personnalisée
     */
    applyCustomDateRange() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        
        if (!startDate || !endDate) {
            alert('Veuillez sélectionner les deux dates');
            return;
        }
        
        console.log(`[Dashboard Filters] Période personnalisée: ${startDate} à ${endDate}`);
        
        // Recharger les données
        if (window.dashboardRealtime) {
            window.dashboardRealtime.loadAllData();
        }
        
        this.showPeriodInfo(new Date(startDate), new Date(endDate));
    }

    /**
     * Afficher les informations de période
     */
    showPeriodInfo(startDate, endDate) {
        const info = `Période: ${startDate.toLocaleDateString('fr-FR')} - ${endDate.toLocaleDateString('fr-FR')}`;
        console.log(`[Dashboard Filters] ${info}`);
    }

    /**
     * Comparer avec la période précédente
     */
    async compareWithLastPeriod() {
        console.log('[Dashboard Filters] Comparaison avec la période précédente...');
        
        // Simuler les données de comparaison
        const comparison = {
            eleves: {
                current: 156,
                previous: 145,
                change: ((156 - 145) / 145 * 100).toFixed(1)
            },
            paiements: {
                current: 2600000,
                previous: 2400000,
                change: ((2600000 - 2400000) / 2400000 * 100).toFixed(1)
            },
            presence: {
                current: 92.5,
                previous: 90.2,
                change: (92.5 - 90.2).toFixed(1)
            },
            retards: {
                current: 15,
                previous: 22,
                change: ((15 - 22) / 22 * 100).toFixed(1)
            }
        };
        
        // Afficher les résultats
        this.displayComparison(comparison);
    }

    /**
     * Afficher les résultats de comparaison
     */
    displayComparison(comparison) {
        const resultsDiv = document.getElementById('comparisonResults');
        if (!resultsDiv) return;
        
        resultsDiv.innerHTML = `
            <h4 style="color: #2c3e50; margin-bottom: 15px;">📊 Comparaison avec la période précédente</h4>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px;">
                <div class="comparison-card">
                    <div style="color: #7f8c8d; font-size: 14px;">Élèves</div>
                    <div>
                        <span class="comparison-value">${comparison.eleves.current}</span>
                        <span class="comparison-change ${comparison.eleves.change > 0 ? 'change-positive' : 'change-negative'}">
                            ${comparison.eleves.change > 0 ? '↑' : '↓'} ${Math.abs(comparison.eleves.change)}%
                        </span>
                    </div>
                </div>
                
                <div class="comparison-card">
                    <div style="color: #7f8c8d; font-size: 14px;">Paiements (BIF)</div>
                    <div>
                        <span class="comparison-value">${this.formatMoney(comparison.paiements.current)}</span>
                        <span class="comparison-change ${comparison.paiements.change > 0 ? 'change-positive' : 'change-negative'}">
                            ${comparison.paiements.change > 0 ? '↑' : '↓'} ${Math.abs(comparison.paiements.change)}%
                        </span>
                    </div>
                </div>
                
                <div class="comparison-card">
                    <div style="color: #7f8c8d; font-size: 14px;">Taux de présence</div>
                    <div>
                        <span class="comparison-value">${comparison.presence.current}%</span>
                        <span class="comparison-change ${comparison.presence.change > 0 ? 'change-positive' : 'change-negative'}">
                            ${comparison.presence.change > 0 ? '↑' : '↓'} ${Math.abs(comparison.presence.change)}%
                        </span>
                    </div>
                </div>
                
                <div class="comparison-card">
                    <div style="color: #7f8c8d; font-size: 14px;">Paiements en retard</div>
                    <div>
                        <span class="comparison-value">${comparison.retards.current}</span>
                        <span class="comparison-change ${comparison.retards.change < 0 ? 'change-positive' : 'change-negative'}">
                            ${comparison.retards.change < 0 ? '↓' : '↑'} ${Math.abs(comparison.retards.change)}%
                        </span>
                    </div>
                </div>
            </div>
        `;
        
        resultsDiv.style.display = 'block';
    }

    /**
     * Vérifier les alertes
     */
    checkAlerts() {
        console.log('[Dashboard Filters] Vérification des alertes...');
        this.alerts = [];
        
        // Récupérer les données actuelles
        const data = window.dashboardRealtime?.data || {};
        
        // Vérifier les paiements en retard
        if (data.paiements?.retards > this.thresholds.paiementsRetard) {
            this.alerts.push({
                type: 'danger',
                icon: '⚠️',
                title: 'Paiements en retard élevés',
                message: `${data.paiements.retards} paiements en retard (seuil: ${this.thresholds.paiementsRetard})`
            });
        }
        
        // Vérifier le taux de présence
        if (data.presence?.moyenne < this.thresholds.tauxPresenceMin) {
            this.alerts.push({
                type: 'warning',
                icon: '📉',
                title: 'Taux de présence faible',
                message: `Taux de présence: ${data.presence?.moyenne}% (minimum: ${this.thresholds.tauxPresenceMin}%)`
            });
        }
        
        // Vérifier les nouvelles inscriptions
        if (data.eleves?.nouveaux < this.thresholds.nouveauxElevesMin) {
            this.alerts.push({
                type: 'warning',
                icon: '👥',
                title: 'Peu de nouvelles inscriptions',
                message: `Seulement ${data.eleves?.nouveaux} nouveaux élèves ce mois`
            });
        }
        
        // Vérifier le taux de remplissage
        if (data.classes?.tauxRemplissage < this.thresholds.tauxRemplissageMin) {
            this.alerts.push({
                type: 'warning',
                icon: '🏫',
                title: 'Taux de remplissage faible',
                message: `Taux de remplissage: ${data.classes?.tauxRemplissage}% (minimum: ${this.thresholds.tauxRemplissageMin}%)`
            });
        }
        
        // Si tout va bien
        if (this.alerts.length === 0) {
            this.alerts.push({
                type: 'success',
                icon: '✅',
                title: 'Tout va bien!',
                message: 'Tous les indicateurs sont dans les normes'
            });
        }
        
        // Mettre à jour le badge
        this.updateAlertBadge();
    }

    /**
     * Mettre à jour le badge d'alertes
     */
    updateAlertBadge() {
        const badge = document.getElementById('alertBadge');
        if (badge) {
            const count = this.alerts.filter(a => a.type !== 'success').length;
            badge.textContent = count;
            badge.style.display = count > 0 ? 'flex' : 'none';
        }
    }

    /**
     * Afficher les alertes
     */
    showAlerts() {
        // Créer une modal pour les alertes
        const modal = document.createElement('div');
        modal.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
        `;
        
        const content = document.createElement('div');
        content.style.cssText = `
            background: white;
            padding: 30px;
            border-radius: 15px;
            max-width: 500px;
            max-height: 70vh;
            overflow-y: auto;
        `;
        
        content.innerHTML = `
            <h2 style="margin: 0 0 20px 0; color: #2c3e50;">🔔 Alertes et Notifications</h2>
            <div>
                ${this.alerts.map(alert => `
                    <div class="alert-item alert-${alert.type}">
                        <span style="font-size: 24px;">${alert.icon}</span>
                        <div>
                            <strong>${alert.title}</strong><br>
                            <span style="font-size: 14px; color: #666;">${alert.message}</span>
                        </div>
                    </div>
                `).join('')}
            </div>
            <button onclick="this.parentElement.parentElement.remove()" style="
                margin-top: 20px;
                padding: 10px 20px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                width: 100%;
            ">Fermer</button>
        `;
        
        modal.appendChild(content);
        document.body.appendChild(modal);
        
        // Fermer en cliquant à l'extérieur
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.remove();
            }
        });
    }

    /**
     * Formater les montants
     */
    formatMoney(amount) {
        return new Intl.NumberFormat('fr-FR').format(amount);
    }
}

// Initialiser au chargement
document.addEventListener('DOMContentLoaded', () => {
    window.dashboardFilters = new DashboardFilters();
    window.dashboardFilters.init();
});
