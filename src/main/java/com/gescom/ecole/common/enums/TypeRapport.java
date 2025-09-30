package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeRapport {
    FINANCIER("FINANCIER", "Rapport financier"),
    ACADEMIQUE("ACADEMIQUE", "Rapport académique"),
    PRESENCE("PRESENCE", "Rapport de présence"),
    BULLETIN("BULLETIN", "Bulletin de notes"),
    STATISTIQUE("STATISTIQUE", "Statistiques générales"),
    LISTE_ELEVES("LISTE_ELEVES", "Liste des élèves"),
    LISTE_ENSEIGNANTS("LISTE_ENSEIGNANTS", "Liste des enseignants"),
    PAIEMENTS("PAIEMENTS", "Rapport des paiements"),
    IMPAYES("IMPAYES", "Rapport des impayés"),
    PERFORMANCE("PERFORMANCE", "Rapport de performance"),
    DISCIPLINE("DISCIPLINE", "Rapport disciplinaire"),
    CUSTOM("CUSTOM", "Rapport personnalisé");

    private final String code;
    private final String libelle;
    
    TypeRapport(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
}
