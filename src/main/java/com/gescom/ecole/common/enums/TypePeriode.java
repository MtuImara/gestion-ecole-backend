package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypePeriode {
    TRIMESTRE("TRIMESTRE", "Trimestre"),
    SEMESTRE("SEMESTRE", "Semestre"),
    ANNEE("ANNEE", "Année"),
    MOIS("MOIS", "Mois");

    private final String key;
    private final String value;

    TypePeriode(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
