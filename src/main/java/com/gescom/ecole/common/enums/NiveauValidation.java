package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NiveauValidation {
    COMPTABLE("COMPTABLE", "Comptable"),
    DIRECTEUR("DIRECTEUR", "Directeur"),
    CONSEIL_ADMINISTRATION("CONSEIL_ADMINISTRATION", "Conseil d'administration");

    private final String key;
    private final String value;

    NiveauValidation(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
