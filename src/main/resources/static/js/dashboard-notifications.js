/**
 * Module de notifications push et temps réel
 * Gère les notifications système, les alertes en temps réel et les préférences utilisateur
 */

class DashboardNotifications {
    constructor() {
        this.notifications = [];
        this.preferences = this.loadPreferences();
        this.soundEnabled = this.preferences.soundEnabled ?? true;
        this.desktopEnabled = this.preferences.desktopEnabled ?? false;
        this.autoRefresh = this.preferences.autoRefresh ?? true;
        this.refreshInterval = this.preferences.refreshInterval ?? 30000;
        this.websocket = null;
    }

    /**
     * Initialiser le système de notifications
     */
    async init() {
        console.log('[Dashboard Notifications] Initialisation...');
        
        // Demander la permission pour les notifications desktop
        if (this.desktopEnabled) {
            await this.requestDesktopPermission();
        }
        
        // Créer le panneau de préférences
        this.createPreferencesPanel();
        
        // Initialiser la connexion WebSocket pour les notifications temps réel
        this.initWebSocket();
        
        // Charger les notifications existantes
        this.loadNotifications();
        
        // Démarrer la vérification périodique
        this.startNotificationCheck();
    }

    /**
     * Demander la permission pour les notifications desktop
     */
    async requestDesktopPermission() {
        if ('Notification' in window) {
            if (Notification.permission === 'default') {
                const permission = await Notification.requestPermission();
                this.desktopEnabled = permission === 'granted';
                this.savePreferences();
            } else {
                this.desktopEnabled = Notification.permission === 'granted';
            }
        }
    }

    /**
     * Initialiser la connexion WebSocket
     */
    initWebSocket() {
        try {
            // Simuler une connexion WebSocket (remplacer par votre URL WebSocket réelle)
            // this.websocket = new WebSocket('ws://localhost:8080/ws/notifications');
            
            // Pour la démo, on simule des notifications
            this.simulateWebSocketNotifications();
            
        } catch (error) {
            console.error('[Dashboard Notifications] Erreur WebSocket:', error);
        }
    }

    /**
     * Simuler des notifications WebSocket
     */
    simulateWebSocketNotifications() {
        // Simuler des notifications aléatoires
        const notifications = [
            { type: 'info', title: 'Nouvelle inscription', message: 'Un nouvel élève vient de s\'inscrire', icon: '👨‍🎓' },
            { type: 'success', title: 'Paiement reçu', message: 'Paiement de 150,000 BIF reçu', icon: '💰' },
            { type: 'warning', title: 'Absence signalée', message: '3 élèves absents aujourd\'hui', icon: '⚠️' },
            { type: 'info', title: 'Réunion programmée', message: 'Réunion parents-professeurs demain', icon: '📅' },
            { type: 'success', title: 'Note ajoutée', message: 'Notes de l\'examen de math disponibles', icon: '📝' }
        ];

        // Envoyer une notification aléatoire toutes les 30-60 secondes
        setInterval(() => {
            if (Math.random() > 0.7) { // 30% de chance
                const notification = notifications[Math.floor(Math.random() * notifications.length)];
                this.showNotification(notification);
            }
        }, 45000);
    }

    /**
     * Afficher une notification
     */
    showNotification(notification) {
        // Ajouter à la liste
        notification.timestamp = new Date();
        notification.id = Date.now();
        notification.read = false;
        this.notifications.unshift(notification);
        
        // Limiter à 50 notifications
        if (this.notifications.length > 50) {
            this.notifications = this.notifications.slice(0, 50);
        }
        
        // Mettre à jour le badge
        this.updateNotificationBadge();
        
        // Afficher la notification toast
        this.showToastNotification(notification);
        
        // Notification desktop si activée
        if (this.desktopEnabled && Notification.permission === 'granted') {
            this.showDesktopNotification(notification);
        }
        
        // Son si activé
        if (this.soundEnabled) {
            this.playNotificationSound();
        }
        
        // Sauvegarder
        this.saveNotifications();
    }

    /**
     * Afficher une notification toast
     */
    showToastNotification(notification) {
        const toast = document.createElement('div');
        toast.className = `notification-toast notification-${notification.type}`;
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            min-width: 300px;
            max-width: 400px;
            padding: 15px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            z-index: 10000;
            animation: slideInRight 0.3s ease;
            border-left: 4px solid ${this.getNotificationColor(notification.type)};
        `;
        
        toast.innerHTML = `
            <div style="display: flex; align-items: start; gap: 10px;">
                <div style="font-size: 24px;">${notification.icon}</div>
                <div style="flex: 1;">
                    <div style="font-weight: 600; color: #2c3e50; margin-bottom: 5px;">${notification.title}</div>
                    <div style="color: #7f8c8d; font-size: 14px;">${notification.message}</div>
                    <div style="color: #95a5a6; font-size: 12px; margin-top: 5px;">À l'instant</div>
                </div>
                <button onclick="this.parentElement.parentElement.remove()" style="
                    background: none;
                    border: none;
                    font-size: 20px;
                    cursor: pointer;
                    color: #95a5a6;
                ">×</button>
            </div>
        `;
        
        document.body.appendChild(toast);
        
        // Auto-fermer après 5 secondes
        setTimeout(() => {
            toast.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    }

    /**
     * Afficher une notification desktop
     */
    showDesktopNotification(notification) {
        const desktopNotif = new Notification(notification.title, {
            body: notification.message,
            icon: '/favicon.ico',
            badge: '/favicon.ico',
            tag: notification.id,
            requireInteraction: false
        });
        
        // Clic sur la notification
        desktopNotif.onclick = () => {
            window.focus();
            this.markAsRead(notification.id);
        };
    }

    /**
     * Jouer le son de notification
     */
    playNotificationSound() {
        // Créer un son simple avec l'API Web Audio
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();
        
        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);
        
        oscillator.frequency.value = 800;
        oscillator.type = 'sine';
        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.2);
        
        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.2);
    }

    /**
     * Créer le panneau de préférences
     */
    createPreferencesPanel() {
        // Ajouter le bouton de préférences dans l'en-tête
        const header = document.querySelector('.header-actions');
        if (header && !document.getElementById('preferencesBtn')) {
            const prefsBtn = document.createElement('button');
            prefsBtn.id = 'preferencesBtn';
            prefsBtn.innerHTML = '⚙️';
            prefsBtn.title = 'Préférences';
            prefsBtn.style.cssText = 'padding: 6px 10px; background: #6c757d; color: white; border: none; border-radius: 5px; cursor: pointer;';
            prefsBtn.onclick = () => this.showPreferencesModal();
            
            // Insérer avant les notifications
            const notifIcon = header.querySelector('.notification-icon');
            if (notifIcon) {
                header.insertBefore(prefsBtn, notifIcon);
            }
        }
    }

    /**
     * Afficher la modal de préférences
     */
    showPreferencesModal() {
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
            width: 90%;
        `;
        
        content.innerHTML = `
            <h2 style="margin: 0 0 20px 0; color: #2c3e50;">⚙️ Préférences du Dashboard</h2>
            
            <div style="margin-bottom: 20px;">
                <h3 style="color: #34495e; margin-bottom: 15px;">Notifications</h3>
                
                <label style="display: flex; align-items: center; margin-bottom: 10px; cursor: pointer;">
                    <input type="checkbox" id="prefSound" ${this.soundEnabled ? 'checked' : ''} style="margin-right: 10px;">
                    <span>🔊 Son des notifications</span>
                </label>
                
                <label style="display: flex; align-items: center; margin-bottom: 10px; cursor: pointer;">
                    <input type="checkbox" id="prefDesktop" ${this.desktopEnabled ? 'checked' : ''} style="margin-right: 10px;">
                    <span>💻 Notifications bureau</span>
                </label>
                
                <label style="display: flex; align-items: center; margin-bottom: 10px; cursor: pointer;">
                    <input type="checkbox" id="prefAutoRefresh" ${this.autoRefresh ? 'checked' : ''} style="margin-right: 10px;">
                    <span>🔄 Actualisation automatique</span>
                </label>
            </div>
            
            <div style="margin-bottom: 20px;">
                <h3 style="color: #34495e; margin-bottom: 15px;">Affichage</h3>
                
                <label style="display: block; margin-bottom: 10px;">
                    <span>Intervalle d'actualisation (secondes)</span>
                    <input type="number" id="prefInterval" value="${this.refreshInterval / 1000}" min="10" max="300" style="
                        width: 100%;
                        padding: 8px;
                        margin-top: 5px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                    ">
                </label>
                
                <label style="display: block; margin-bottom: 10px;">
                    <span>Thème</span>
                    <select id="prefTheme" style="
                        width: 100%;
                        padding: 8px;
                        margin-top: 5px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                    ">
                        <option value="light">🌞 Clair</option>
                        <option value="dark">🌙 Sombre</option>
                        <option value="auto">🌓 Automatique</option>
                    </select>
                </label>
            </div>
            
            <div style="margin-bottom: 20px;">
                <h3 style="color: #34495e; margin-bottom: 15px;">Données</h3>
                
                <button onclick="dashboardNotifications.clearNotifications()" style="
                    padding: 10px 20px;
                    background: #e74c3c;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-right: 10px;
                ">🗑️ Effacer les notifications</button>
                
                <button onclick="dashboardNotifications.exportPreferences()" style="
                    padding: 10px 20px;
                    background: #3498db;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                ">💾 Exporter les préférences</button>
            </div>
            
            <div style="display: flex; gap: 10px; justify-content: flex-end;">
                <button onclick="this.parentElement.parentElement.parentElement.remove()" style="
                    padding: 10px 20px;
                    background: #95a5a6;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                ">Annuler</button>
                
                <button onclick="dashboardNotifications.savePreferencesFromModal()" style="
                    padding: 10px 20px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                ">Enregistrer</button>
            </div>
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
     * Sauvegarder les préférences depuis la modal
     */
    savePreferencesFromModal() {
        this.soundEnabled = document.getElementById('prefSound').checked;
        this.desktopEnabled = document.getElementById('prefDesktop').checked;
        this.autoRefresh = document.getElementById('prefAutoRefresh').checked;
        this.refreshInterval = parseInt(document.getElementById('prefInterval').value) * 1000;
        
        // Demander la permission desktop si nécessaire
        if (this.desktopEnabled && Notification.permission === 'default') {
            this.requestDesktopPermission();
        }
        
        this.savePreferences();
        
        // Appliquer les changements
        this.applyPreferences();
        
        // Fermer la modal
        document.querySelector('[style*="position: fixed"]').remove();
        
        // Notification de confirmation
        this.showNotification({
            type: 'success',
            title: 'Préférences enregistrées',
            message: 'Vos préférences ont été mises à jour',
            icon: '✅'
        });
    }

    /**
     * Appliquer les préférences
     */
    applyPreferences() {
        // Mettre à jour l'intervalle de rafraîchissement
        if (window.dashboardRealtime) {
            window.dashboardRealtime.refreshInterval = this.refreshInterval;
        }
        
        // Appliquer le thème
        const theme = document.getElementById('prefTheme')?.value || 'light';
        this.applyTheme(theme);
    }

    /**
     * Appliquer un thème
     */
    applyTheme(theme) {
        if (theme === 'dark') {
            document.body.classList.add('dark-theme');
        } else if (theme === 'auto') {
            const isDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            if (isDark) {
                document.body.classList.add('dark-theme');
            } else {
                document.body.classList.remove('dark-theme');
            }
        } else {
            document.body.classList.remove('dark-theme');
        }
    }

    /**
     * Charger les préférences
     */
    loadPreferences() {
        const saved = localStorage.getItem('dashboard_preferences');
        return saved ? JSON.parse(saved) : {};
    }

    /**
     * Sauvegarder les préférences
     */
    savePreferences() {
        const prefs = {
            soundEnabled: this.soundEnabled,
            desktopEnabled: this.desktopEnabled,
            autoRefresh: this.autoRefresh,
            refreshInterval: this.refreshInterval
        };
        localStorage.setItem('dashboard_preferences', JSON.stringify(prefs));
    }

    /**
     * Exporter les préférences
     */
    exportPreferences() {
        const prefs = this.loadPreferences();
        const dataStr = JSON.stringify(prefs, null, 2);
        const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
        
        const link = document.createElement('a');
        link.setAttribute('href', dataUri);
        link.setAttribute('download', 'dashboard_preferences.json');
        link.click();
    }

    /**
     * Charger les notifications
     */
    loadNotifications() {
        const saved = localStorage.getItem('dashboard_notifications');
        if (saved) {
            this.notifications = JSON.parse(saved);
            this.updateNotificationBadge();
        }
    }

    /**
     * Sauvegarder les notifications
     */
    saveNotifications() {
        localStorage.setItem('dashboard_notifications', JSON.stringify(this.notifications));
    }

    /**
     * Effacer les notifications
     */
    clearNotifications() {
        this.notifications = [];
        this.saveNotifications();
        this.updateNotificationBadge();
        
        this.showNotification({
            type: 'info',
            title: 'Notifications effacées',
            message: 'Toutes les notifications ont été supprimées',
            icon: '🗑️'
        });
    }

    /**
     * Marquer comme lu
     */
    markAsRead(notificationId) {
        const notification = this.notifications.find(n => n.id === notificationId);
        if (notification) {
            notification.read = true;
            this.saveNotifications();
            this.updateNotificationBadge();
        }
    }

    /**
     * Mettre à jour le badge de notifications
     */
    updateNotificationBadge() {
        const badge = document.getElementById('notificationCount');
        if (badge) {
            const unreadCount = this.notifications.filter(n => !n.read).length;
            badge.textContent = unreadCount;
            badge.style.display = unreadCount > 0 ? 'block' : 'none';
        }
    }

    /**
     * Démarrer la vérification périodique
     */
    startNotificationCheck() {
        // Vérifier les nouvelles notifications toutes les minutes
        setInterval(() => {
            this.checkForNewNotifications();
        }, 60000);
    }

    /**
     * Vérifier les nouvelles notifications
     */
    async checkForNewNotifications() {
        // Ici vous pouvez appeler votre API pour récupérer les nouvelles notifications
        console.log('[Dashboard Notifications] Vérification des nouvelles notifications...');
    }

    /**
     * Obtenir la couleur selon le type
     */
    getNotificationColor(type) {
        const colors = {
            success: '#27ae60',
            warning: '#f39c12',
            danger: '#e74c3c',
            info: '#3498db'
        };
        return colors[type] || colors.info;
    }
}

// Ajouter les styles d'animation
const style = document.createElement('style');
style.textContent = `
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
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .dark-theme {
        filter: invert(1) hue-rotate(180deg);
    }
    
    .dark-theme img,
    .dark-theme canvas {
        filter: invert(1) hue-rotate(180deg);
    }
`;
document.head.appendChild(style);

// Initialiser au chargement
document.addEventListener('DOMContentLoaded', () => {
    window.dashboardNotifications = new DashboardNotifications();
    window.dashboardNotifications.init();
});
