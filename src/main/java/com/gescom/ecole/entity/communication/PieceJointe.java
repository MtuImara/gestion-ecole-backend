package com.gescom.ecole.entity.communication;

import com.gescom.ecole.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pieces_jointes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"message", "annonce"})
public class PieceJointe extends BaseEntity {

    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    @Column(name = "nom_original", nullable = false, length = 255)
    private String nomOriginal;

    @Column(name = "type_mime", nullable = false, length = 100)
    private String typeMime;

    @Column(name = "taille", nullable = false)
    private Long taille; // en octets

    @Column(name = "chemin_fichier", nullable = false, length = 500)
    private String cheminFichier;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annonce_id")
    private Annonce annonce;

    // Méthodes utilitaires
    public String getTailleFormatee() {
        if (taille < 1024) {
            return taille + " B";
        } else if (taille < 1024 * 1024) {
            return String.format("%.2f KB", taille / 1024.0);
        } else if (taille < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", taille / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", taille / (1024.0 * 1024 * 1024));
        }
    }
}
