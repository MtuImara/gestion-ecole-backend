package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeFacture {
    MINERVAL("MINERVAL", "Minerval"),
    INSCRIPTION("INSCRIPTION", "Inscription"),
    FRAIS_ANNEXES("FRAIS_ANNEXES", "Frais annexes"),
    PENALITE("PENALITE", "Pénalité"),
    AUTRE("AUTRE", "Autre");

    private final String key;
    private final String value;

    TypeFacture(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
