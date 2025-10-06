/**
 * Module de graphiques pour le dashboard
 */

class DashboardCharts {
    constructor() {
        this.charts = {};
    }

    init() {
        console.log('[Dashboard Charts] Initialisation des graphiques...');
        
        // Créer tous les graphiques
        this.createElevesChart();
        this.createPaiementsChart();
        this.createGenreChart();
        this.createPresenceChart();
        
        // Charger les données
        this.loadData();
    }

    createElevesChart() {
        const ctx = document.getElementById('chartEleves');
        if (!ctx) return;

        this.charts.eleves = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['6ème A', '6ème B', '5ème A', '5ème B', '4ème', '3ème'],
                datasets: [{
                    label: 'Nombre d\'élèves',
                    data: [25, 23, 28, 26, 30, 24],
                    backgroundColor: [
                        'rgba(102, 126, 234, 0.8)',
                        'rgba(118, 75, 162, 0.8)',
                        'rgba(240, 147, 251, 0.8)',
                        'rgba(245, 87, 108, 0.8)',
                        'rgba(79, 172, 254, 0.8)',
                        'rgba(0, 242, 254, 0.8)'
                    ],
                    borderColor: [
                        'rgba(102, 126, 234, 1)',
                        'rgba(118, 75, 162, 1)',
                        'rgba(240, 147, 251, 1)',
                        'rgba(245, 87, 108, 1)',
                        'rgba(79, 172, 254, 1)',
                        'rgba(0, 242, 254, 1)'
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    title: {
                        display: true,
                        text: 'Répartition des élèves par classe',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 5
                        }
                    }
                }
            }
        });
    }

    createPaiementsChart() {
        const ctx = document.getElementById('chartPaiements');
        if (!ctx) return;

        this.charts.paiements = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin'],
                datasets: [{
                    label: 'Paiements reçus',
                    data: [380000, 420000, 450000, 410000, 460000, 480000],
                    borderColor: 'rgba(102, 126, 234, 1)',
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    tension: 0.4,
                    fill: true
                }, {
                    label: 'Objectif',
                    data: [400000, 400000, 400000, 400000, 400000, 400000],
                    borderColor: 'rgba(255, 99, 132, 0.5)',
                    borderDash: [5, 5],
                    tension: 0,
                    fill: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Évolution des paiements (BIF)',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    legend: {
                        display: true,
                        position: 'bottom'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return value.toLocaleString('fr-FR') + ' BIF';
                            }
                        }
                    }
                }
            }
        });
    }

    createGenreChart() {
        const ctx = document.getElementById('chartGenre');
        if (!ctx) return;

        this.charts.genre = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Garçons', 'Filles'],
                datasets: [{
                    data: [82, 74],
                    backgroundColor: [
                        'rgba(79, 172, 254, 0.8)',
                        'rgba(240, 147, 251, 0.8)'
                    ],
                    borderColor: [
                        'rgba(79, 172, 254, 1)',
                        'rgba(240, 147, 251, 1)'
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Répartition par genre',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    legend: {
                        display: true,
                        position: 'bottom'
                    }
                }
            }
        });
    }

    createPresenceChart() {
        const ctx = document.getElementById('chartPresence');
        if (!ctx) return;

        this.charts.presence = new Chart(ctx, {
            type: 'radar',
            data: {
                labels: ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi'],
                datasets: [{
                    label: 'Cette semaine',
                    data: [95, 92, 94, 93, 90],
                    borderColor: 'rgba(102, 126, 234, 1)',
                    backgroundColor: 'rgba(102, 126, 234, 0.2)',
                    pointBackgroundColor: 'rgba(102, 126, 234, 1)',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: 'rgba(102, 126, 234, 1)'
                }, {
                    label: 'Semaine dernière',
                    data: [93, 90, 91, 92, 88],
                    borderColor: 'rgba(118, 75, 162, 1)',
                    backgroundColor: 'rgba(118, 75, 162, 0.2)',
                    pointBackgroundColor: 'rgba(118, 75, 162, 1)',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: 'rgba(118, 75, 162, 1)'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Taux de présence (%)',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    legend: {
                        display: true,
                        position: 'bottom'
                    }
                },
                scales: {
                    r: {
                        beginAtZero: false,
                        min: 80,
                        max: 100,
                        ticks: {
                            stepSize: 5
                        }
                    }
                }
            }
        });
    }

    async loadData() {
        try {
            // TODO: Charger les vraies données depuis l'API
            console.log('[Dashboard Charts] Données chargées');
            
            // Mettre à jour les statistiques affichées
            this.updateStats();
            
        } catch (error) {
            console.error('[Dashboard Charts] Erreur chargement données:', error);
        }
    }

    updateStats() {
        // Mettre à jour les cartes de statistiques
        const stats = {
            totalEleves: 156,
            elevesActifs: 145,
            totalEnseignants: 12,
            totalClasses: 8,
            paiementsMois: 2600000,
            tauxPresence: 92.5
        };

        // Mettre à jour les éléments DOM
        this.updateStatCard('stat-eleves', stats.totalEleves);
        this.updateStatCard('stat-actifs', stats.elevesActifs);
        this.updateStatCard('stat-enseignants', stats.totalEnseignants);
        this.updateStatCard('stat-classes', stats.totalClasses);
        this.updateStatCard('stat-paiements', this.formatMoney(stats.paiementsMois));
        this.updateStatCard('stat-presence', stats.tauxPresence + '%');
    }

    updateStatCard(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
            // Animation
            element.style.transform = 'scale(1.1)';
            setTimeout(() => {
                element.style.transform = 'scale(1)';
            }, 300);
        }
    }

    formatMoney(amount) {
        return new Intl.NumberFormat('fr-FR').format(amount) + ' BIF';
    }

    // Méthode pour rafraîchir les graphiques
    async refresh() {
        console.log('[Dashboard Charts] Rafraîchissement...');
        await this.loadData();
        
        // Mettre à jour chaque graphique
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.update();
        });
    }
}

// Initialiser au chargement de la page
document.addEventListener('DOMContentLoaded', () => {
    const dashboardCharts = new DashboardCharts();
    dashboardCharts.init();
    
    // Exposer globalement pour debug
    window.dashboardCharts = dashboardCharts;
});
