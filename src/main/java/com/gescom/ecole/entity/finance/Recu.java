package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeRecu;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recus",
    uniqueConstraints = @UniqueConstraint(columnNames = "numero_recu")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recu extends BaseEntity {

    @Column(name = "numero_recu", nullable = false, unique = true, length = 50)
    private String numeroRecu;

    @Column(name = "date_emission", nullable = false)
    private LocalDateTime dateEmission;

    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "devise", nullable = false, length = 10)
    private String devise = "XAF";

    @Column(name = "emetteur", nullable = false, length = 150)
    private String emetteur;

    @Column(name = "beneficiaire", nullable = false, length = 150)
    private String beneficiaire;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_recu", nullable = false, length = 20)
    private TypeRecu typeRecu = TypeRecu.PAIEMENT;

    @Column(name = "signature_url")
    private String signatureUrl;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;

    public String genererQRCode() {
        // Générer un QR code contenant les informations du reçu
        StringBuilder qrData = new StringBuilder();
        qrData.append("RECU:").append(numeroRecu).append("|");
        qrData.append("DATE:").append(dateEmission).append("|");
        qrData.append("MONTANT:").append(montant).append(" ").append(devise).append("|");
        qrData.append("BENEFICIAIRE:").append(beneficiaire);
        this.qrCode = qrData.toString();
        return this.qrCode;
    }
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public String getNumeroRecu() {
        return numeroRecu;
    }
    
    public void setNumeroRecu(String numeroRecu) {
        this.numeroRecu = numeroRecu;
    }
    
    public LocalDateTime getDateEmission() {
        return dateEmission;
    }
    
    public void setDateEmission(LocalDateTime dateEmission) {
        this.dateEmission = dateEmission;
    }
    
    public BigDecimal getMontant() {
        return montant;
    }
    
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public String getDevise() {
        return devise;
    }
    
    public void setDevise(String devise) {
        this.devise = devise;
    }
    
    public String getEmetteur() {
        return emetteur;
    }
    
    public void setEmetteur(String emetteur) {
        this.emetteur = emetteur;
    }
    
    public String getBeneficiaire() {
        return beneficiaire;
    }
    
    public void setBeneficiaire(String beneficiaire) {
        this.beneficiaire = beneficiaire;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Paiement getPaiement() {
        return paiement;
    }
    
    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }
}
