package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeParent {
    PERE("PERE", "Père"),
    MERE("MERE", "Mère"),
    TUTEUR("TUTEUR", "Tuteur"),
    AUTRE("AUTRE", "Autre");

    private final String key;
    private final String value;

    TypeParent(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
