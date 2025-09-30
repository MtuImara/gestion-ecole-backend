package com.gescom.ecole.entity.communication;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"expediteur", "destinataires", "piecesJointes"})
public class Message extends BaseEntity {

    @Column(name = "objet", nullable = false, length = 255)
    private String objet;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "important", nullable = false)
    private Boolean important = false;

    @Column(name = "brouillon", nullable = false)
    private Boolean brouillon = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "message_destinataires",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private List<Utilisateur> destinataires = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageLecture> lectures = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PieceJointe> piecesJointes = new ArrayList<>();

    // Méthodes utilitaires
    public void addDestinataire(Utilisateur utilisateur) {
        destinataires.add(utilisateur);
    }

    public void removeDestinataire(Utilisateur utilisateur) {
        destinataires.remove(utilisateur);
    }

    public void addPieceJointe(PieceJointe pieceJointe) {
        piecesJointes.add(pieceJointe);
        pieceJointe.setMessage(this);
    }

    public void removePieceJointe(PieceJointe pieceJointe) {
        piecesJointes.remove(pieceJointe);
        pieceJointe.setMessage(null);
    }
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public String getObjet() {
        return objet;
    }
    
    public void setObjet(String objet) {
        this.objet = objet;
    }
    
    public String getContenu() {
        return contenu;
    }
    
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
    
    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }
    
    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
    
    public Boolean getImportant() {
        return important;
    }
    
    public void setImportant(Boolean important) {
        this.important = important;
    }
    
    public Boolean getBrouillon() {
        return brouillon;
    }
    
    public void setBrouillon(Boolean brouillon) {
        this.brouillon = brouillon;
    }
    
    public Utilisateur getExpediteur() {
        return expediteur;
    }
    
    public void setExpediteur(Utilisateur expediteur) {
        this.expediteur = expediteur;
    }
    
    public List<Utilisateur> getDestinataires() {
        return destinataires;
    }
    
    public void setDestinataires(List<Utilisateur> destinataires) {
        this.destinataires = destinataires;
    }
    
    public List<PieceJointe> getPiecesJointes() {
        return piecesJointes;
    }
    
    public void setPiecesJointes(List<PieceJointe> piecesJointes) {
        this.piecesJointes = piecesJointes;
    }
}
