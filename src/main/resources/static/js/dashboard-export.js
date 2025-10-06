/**
 * Module d'export des données du dashboard
 * Permet d'exporter en PDF, Excel et d'imprimer
 */

class DashboardExport {
    constructor() {
        this.data = null;
    }

    setData(data) {
        this.data = data;
    }

    /**
     * Export en PDF avec jsPDF
     */
    async exportToPDF() {
        console.log('[Dashboard Export] Génération du PDF...');
        
        // Créer un nouveau document PDF
        const doc = new jsPDF();
        
        // En-tête
        doc.setFontSize(20);
        doc.setTextColor(102, 126, 234);
        doc.text('EcoleGest - Rapport Dashboard', 20, 20);
        
        // Date du rapport
        doc.setFontSize(10);
        doc.setTextColor(100);
        doc.text(`Généré le: ${new Date().toLocaleDateString('fr-FR')} à ${new Date().toLocaleTimeString('fr-FR')}`, 20, 30);
        
        // Ligne de séparation
        doc.setDrawColor(102, 126, 234);
        doc.line(20, 35, 190, 35);
        
        let yPosition = 45;
        
        // Section Statistiques Générales
        doc.setFontSize(14);
        doc.setTextColor(0);
        doc.text('📊 Statistiques Générales', 20, yPosition);
        yPosition += 10;
        
        doc.setFontSize(10);
        const stats = [
            `Total Élèves: ${this.data?.eleves?.total || 0}`,
            `Élèves Actifs: ${this.data?.eleves?.actifs || 0}`,
            `Nouveaux Élèves (ce mois): ${this.data?.eleves?.nouveaux || 0}`,
            `Total Classes: ${this.data?.classes?.total || 0}`,
            `Taux de Remplissage: ${this.data?.classes?.tauxRemplissage || 0}%`,
            `Paiements du Mois: ${this.formatMoney(this.data?.paiements?.mois || 0)} BIF`,
            `Paiements en Retard: ${this.data?.paiements?.retards || 0}`
        ];
        
        stats.forEach(stat => {
            doc.text(`• ${stat}`, 25, yPosition);
            yPosition += 7;
        });
        
        // Section Répartition par Classe
        yPosition += 10;
        doc.setFontSize(14);
        doc.text('👨‍🎓 Répartition par Classe', 20, yPosition);
        yPosition += 10;
        
        doc.setFontSize(10);
        if (this.data?.eleves?.parClasse) {
            Object.entries(this.data.eleves.parClasse).forEach(([classe, count]) => {
                doc.text(`• ${classe}: ${count} élèves`, 25, yPosition);
                yPosition += 7;
            });
        }
        
        // Section Évolution des Paiements
        yPosition += 10;
        doc.setFontSize(14);
        doc.text('💰 Évolution des Paiements (6 derniers mois)', 20, yPosition);
        yPosition += 10;
        
        doc.setFontSize(10);
        if (this.data?.paiements?.evolution) {
            this.data.paiements.evolution.forEach(item => {
                doc.text(`• ${item.mois}: ${this.formatMoney(item.montant)} BIF`, 25, yPosition);
                yPosition += 7;
            });
        }
        
        // Ajouter un graphique (si possible)
        if (document.getElementById('chartEleves')) {
            yPosition = 45;
            doc.addPage();
            doc.setFontSize(14);
            doc.text('📈 Graphiques', 20, yPosition);
            yPosition += 10;
            
            // Convertir le canvas en image
            const canvas = document.getElementById('chartEleves');
            const imgData = canvas.toDataURL('image/png');
            doc.addImage(imgData, 'PNG', 20, yPosition, 170, 80);
        }
        
        // Sauvegarder le PDF
        const fileName = `Dashboard_EcoleGest_${new Date().toISOString().split('T')[0]}.pdf`;
        doc.save(fileName);
        
        console.log('[Dashboard Export] PDF généré:', fileName);
        this.showNotification('PDF généré avec succès!', 'success');
    }

    /**
     * Export en Excel avec SheetJS
     */
    async exportToExcel() {
        console.log('[Dashboard Export] Génération du fichier Excel...');
        
        // Créer un nouveau workbook
        const wb = XLSX.utils.book_new();
        
        // Feuille 1: Statistiques Générales
        const statsData = [
            ['Statistiques Générales', ''],
            ['', ''],
            ['Indicateur', 'Valeur'],
            ['Total Élèves', this.data?.eleves?.total || 0],
            ['Élèves Actifs', this.data?.eleves?.actifs || 0],
            ['Nouveaux Élèves', this.data?.eleves?.nouveaux || 0],
            ['Total Classes', this.data?.classes?.total || 0],
            ['Taux de Remplissage (%)', this.data?.classes?.tauxRemplissage || 0],
            ['Paiements du Mois (BIF)', this.data?.paiements?.mois || 0],
            ['Paiements en Retard', this.data?.paiements?.retards || 0]
        ];
        
        const ws1 = XLSX.utils.aoa_to_sheet(statsData);
        XLSX.utils.book_append_sheet(wb, ws1, 'Statistiques');
        
        // Feuille 2: Répartition par Classe
        if (this.data?.eleves?.parClasse) {
            const classeData = [
                ['Répartition par Classe', ''],
                ['', ''],
                ['Classe', 'Nombre d\'élèves']
            ];
            
            Object.entries(this.data.eleves.parClasse).forEach(([classe, count]) => {
                classeData.push([classe, count]);
            });
            
            const ws2 = XLSX.utils.aoa_to_sheet(classeData);
            XLSX.utils.book_append_sheet(wb, ws2, 'Classes');
        }
        
        // Feuille 3: Évolution des Paiements
        if (this.data?.paiements?.evolution) {
            const paiementsData = [
                ['Évolution des Paiements', '', ''],
                ['', '', ''],
                ['Mois', 'Montant (BIF)', 'Objectif (BIF)']
            ];
            
            this.data.paiements.evolution.forEach(item => {
                paiementsData.push([item.mois, item.montant, item.objectif || '']);
            });
            
            const ws3 = XLSX.utils.aoa_to_sheet(paiementsData);
            XLSX.utils.book_append_sheet(wb, ws3, 'Paiements');
        }
        
        // Feuille 4: Activités Récentes
        const activitiesData = [
            ['Activités Récentes', '', ''],
            ['', '', ''],
            ['Type', 'Description', 'Date/Heure']
        ];
        
        // Ajouter quelques activités d'exemple
        activitiesData.push(
            ['Inscription', 'Nouvel élève: Marie Dupont - 6ème A', 'Il y a 2 heures'],
            ['Paiement', 'Reçu: 150,000 BIF - Jean Martin', 'Il y a 3 heures'],
            ['Note', 'Examen Math - 5ème B', 'Il y a 5 heures']
        );
        
        const ws4 = XLSX.utils.aoa_to_sheet(activitiesData);
        XLSX.utils.book_append_sheet(wb, ws4, 'Activités');
        
        // Générer le fichier Excel
        const fileName = `Dashboard_EcoleGest_${new Date().toISOString().split('T')[0]}.xlsx`;
        XLSX.writeFile(wb, fileName);
        
        console.log('[Dashboard Export] Excel généré:', fileName);
        this.showNotification('Fichier Excel généré avec succès!', 'success');
    }

    /**
     * Export en CSV
     */
    exportToCSV() {
        console.log('[Dashboard Export] Génération du CSV...');
        
        let csv = 'Indicateur,Valeur\n';
        csv += `Total Élèves,${this.data?.eleves?.total || 0}\n`;
        csv += `Élèves Actifs,${this.data?.eleves?.actifs || 0}\n`;
        csv += `Nouveaux Élèves,${this.data?.eleves?.nouveaux || 0}\n`;
        csv += `Total Classes,${this.data?.classes?.total || 0}\n`;
        csv += `Taux de Remplissage (%),${this.data?.classes?.tauxRemplissage || 0}\n`;
        csv += `Paiements du Mois (BIF),${this.data?.paiements?.mois || 0}\n`;
        csv += `Paiements en Retard,${this.data?.paiements?.retards || 0}\n`;
        
        // Ajouter la répartition par classe
        csv += '\n\nClasse,Nombre d\'élèves\n';
        if (this.data?.eleves?.parClasse) {
            Object.entries(this.data.eleves.parClasse).forEach(([classe, count]) => {
                csv += `${classe},${count}\n`;
            });
        }
        
        // Créer un blob et télécharger
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        
        link.setAttribute('href', url);
        link.setAttribute('download', `Dashboard_EcoleGest_${new Date().toISOString().split('T')[0]}.csv`);
        link.style.visibility = 'hidden';
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        this.showNotification('Fichier CSV généré avec succès!', 'success');
    }

    /**
     * Imprimer le dashboard
     */
    print() {
        console.log('[Dashboard Export] Préparation de l\'impression...');
        
        // Créer une fenêtre d'impression
        const printWindow = window.open('', '_blank');
        
        // Contenu HTML pour l'impression
        const printContent = `
            <!DOCTYPE html>
            <html>
            <head>
                <title>Dashboard EcoleGest - Impression</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        padding: 20px;
                    }
                    h1 {
                        color: #667eea;
                        border-bottom: 2px solid #667eea;
                        padding-bottom: 10px;
                    }
                    .section {
                        margin: 20px 0;
                    }
                    .section h2 {
                        color: #333;
                        margin-bottom: 10px;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin: 10px 0;
                    }
                    th, td {
                        border: 1px solid #ddd;
                        padding: 8px;
                        text-align: left;
                    }
                    th {
                        background-color: #f2f2f2;
                    }
                    .stats-grid {
                        display: grid;
                        grid-template-columns: repeat(2, 1fr);
                        gap: 20px;
                        margin: 20px 0;
                    }
                    .stat-box {
                        border: 1px solid #ddd;
                        padding: 15px;
                        border-radius: 5px;
                    }
                    @media print {
                        .no-print {
                            display: none;
                        }
                    }
                </style>
            </head>
            <body>
                <h1>📊 Dashboard EcoleGest</h1>
                <p>Généré le: ${new Date().toLocaleDateString('fr-FR')} à ${new Date().toLocaleTimeString('fr-FR')}</p>
                
                <div class="section">
                    <h2>Statistiques Générales</h2>
                    <div class="stats-grid">
                        <div class="stat-box">
                            <strong>Total Élèves:</strong> ${this.data?.eleves?.total || 0}
                        </div>
                        <div class="stat-box">
                            <strong>Élèves Actifs:</strong> ${this.data?.eleves?.actifs || 0}
                        </div>
                        <div class="stat-box">
                            <strong>Paiements du Mois:</strong> ${this.formatMoney(this.data?.paiements?.mois || 0)} BIF
                        </div>
                        <div class="stat-box">
                            <strong>Classes:</strong> ${this.data?.classes?.total || 0}
                        </div>
                    </div>
                </div>
                
                <div class="section">
                    <h2>Répartition par Classe</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>Classe</th>
                                <th>Nombre d'élèves</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${this.generateClassTableRows()}
                        </tbody>
                    </table>
                </div>
                
                <div class="section">
                    <h2>Évolution des Paiements</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>Mois</th>
                                <th>Montant (BIF)</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${this.generatePaymentTableRows()}
                        </tbody>
                    </table>
                </div>
            </body>
            </html>
        `;
        
        printWindow.document.write(printContent);
        printWindow.document.close();
        
        // Attendre que le contenu soit chargé puis imprimer
        printWindow.onload = function() {
            printWindow.print();
            printWindow.close();
        };
        
        this.showNotification('Préparation de l\'impression...', 'info');
    }

    /**
     * Générer les lignes du tableau des classes
     */
    generateClassTableRows() {
        if (!this.data?.eleves?.parClasse) return '<tr><td colspan="2">Aucune donnée</td></tr>';
        
        return Object.entries(this.data.eleves.parClasse)
            .map(([classe, count]) => `<tr><td>${classe}</td><td>${count}</td></tr>`)
            .join('');
    }

    /**
     * Générer les lignes du tableau des paiements
     */
    generatePaymentTableRows() {
        if (!this.data?.paiements?.evolution) return '<tr><td colspan="2">Aucune donnée</td></tr>';
        
        return this.data.paiements.evolution
            .map(item => `<tr><td>${item.mois}</td><td>${this.formatMoney(item.montant)}</td></tr>`)
            .join('');
    }

    /**
     * Formater les montants
     */
    formatMoney(amount) {
        return new Intl.NumberFormat('fr-FR').format(amount);
    }

    /**
     * Afficher une notification
     */
    showNotification(message, type = 'info') {
        // Créer une notification
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            background: ${type === 'success' ? '#4caf50' : type === 'error' ? '#f44336' : '#2196f3'};
            color: white;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.2);
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
}

// Exposer globalement
window.DashboardExport = DashboardExport;
