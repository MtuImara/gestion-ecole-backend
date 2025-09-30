package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeNotification {
    PAIEMENT("PAIEMENT", "Notification de paiement"),
    FACTURE("FACTURE", "Nouvelle facture"),
    ABSENCE("ABSENCE", "Absence signalée"),
    NOTE("NOTE", "Nouvelle note"),
    MESSAGE("MESSAGE", "Nouveau message"),
    ANNONCE("ANNONCE", "Nouvelle annonce"),
    RAPPEL("RAPPEL", "Rappel"),
    ALERTE("ALERTE", "Alerte"),
    SYSTEME("SYSTEME", "Notification système"),
    INSCRIPTION("INSCRIPTION", "Inscription"),
    EVALUATION("EVALUATION", "Évaluation à venir"),
    REUNION("REUNION", "Réunion programmée"),
    DOCUMENT("DOCUMENT", "Document disponible"),
    DISCIPLINE("DISCIPLINE", "Action disciplinaire");

    private final String code;
    private final String libelle;
    
    TypeNotification(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
}
