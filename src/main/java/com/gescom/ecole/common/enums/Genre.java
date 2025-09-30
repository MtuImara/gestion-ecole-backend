package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Genre {
    MASCULIN("MASCULIN", "Masculin"),
    FEMININ("FEMININ", "Féminin"),
    AUTRE("AUTRE", "Autre");

    private final String key;
    private final String value;

    Genre(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
