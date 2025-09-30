package com.gescom.ecole.entity.communication;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeAnnonce;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "annonces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"auteur", "piecesJointes", "destinataires"})
public class Annonce extends BaseEntity {

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypeAnnonce type;

    @Column(name = "date_publication", nullable = false)
    private LocalDateTime datePublication;

    @Column(name = "date_debut_affichage", nullable = false)
    private LocalDateTime dateDebutAffichage;

    @Column(name = "date_fin_affichage")
    private LocalDateTime dateFinAffichage;

    @Column(name = "priorite", nullable = false)
    private Integer priorite = 0; // 0: normale, 1: importante, 2: urgente

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "epinglee", nullable = false)
    private Boolean epinglee = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @ElementCollection
    @CollectionTable(name = "annonce_destinataires", joinColumns = @JoinColumn(name = "annonce_id"))
    @Column(name = "destinataire")
    private List<String> destinataires = new ArrayList<>(); // TOUS, PARENTS, ENSEIGNANTS, ELEVES, ou classes spécifiques

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PieceJointe> piecesJointes = new ArrayList<>();

    @Column(name = "nombre_vues")
    private Integer nombreVues = 0;

    // Méthodes utilitaires
    public boolean estActive() {
        LocalDateTime maintenant = LocalDateTime.now();
        return active && 
               maintenant.isAfter(dateDebutAffichage) && 
               (dateFinAffichage == null || maintenant.isBefore(dateFinAffichage));
    }

    public void incrementerVues() {
        this.nombreVues++;
    }

    public void addPieceJointe(PieceJointe pieceJointe) {
        piecesJointes.add(pieceJointe);
        pieceJointe.setAnnonce(this);
    }

    public void removePieceJointe(PieceJointe pieceJointe) {
        piecesJointes.remove(pieceJointe);
        pieceJointe.setAnnonce(null);
    }
}
