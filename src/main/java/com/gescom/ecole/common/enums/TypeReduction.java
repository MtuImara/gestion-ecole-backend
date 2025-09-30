package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeReduction {
    FRATRIE("FRATRIE", "Fratrie"),
    EXCELLENCE("EXCELLENCE", "Excellence"),
    SOCIALE("SOCIALE", "Sociale"),
    PERSONNEL("PERSONNEL", "Personnel"),
    AUTRE("AUTRE", "Autre");

    private final String key;
    private final String value;

    TypeReduction(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
