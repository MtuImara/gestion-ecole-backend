package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeAnnonce {
    GENERALE("GENERALE", "Annonce générale"),
    EVENEMENT("EVENEMENT", "Événement"),
    REUNION("REUNION", "Réunion"),
    VACANCES("VACANCES", "Vacances scolaires"),
    EXAMEN("EXAMEN", "Examens"),
    INSCRIPTION("INSCRIPTION", "Inscriptions"),
    URGENT("URGENT", "Urgent"),
    ADMINISTRATIVE("ADMINISTRATIVE", "Administrative"),
    PEDAGOGIQUE("PEDAGOGIQUE", "Pédagogique"),
    CULTURELLE("CULTURELLE", "Culturelle"),
    SPORTIVE("SPORTIVE", "Sportive"),
    MAINTENANCE("MAINTENANCE", "Maintenance"),
    SECURITE("SECURITE", "Sécurité");

    private final String code;
    private final String libelle;
    
    TypeAnnonce(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
}
