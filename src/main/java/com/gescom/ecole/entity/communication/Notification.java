package com.gescom.ecole.entity.communication;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeNotification;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"utilisateur"})
public class Notification extends BaseEntity {

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypeNotification type;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    @Column(name = "priorite", nullable = false)
    private Integer priorite = 0; // 0: normale, 1: importante, 2: urgente

    @Column(name = "lien_action", length = 500)
    private String lienAction; // URL ou route vers l'action associée

    @Column(name = "donnees_supplementaires", columnDefinition = "TEXT")
    private String donneesSupplementaires; // JSON pour données additionnelles

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "expire_le")
    private LocalDateTime expireLe;

    // Méthodes utilitaires
    public boolean estExpiree() {
        return expireLe != null && LocalDateTime.now().isAfter(expireLe);
    }

    public void marquerCommeLue() {
        this.lu = true;
        this.dateLecture = LocalDateTime.now();
    }
}
