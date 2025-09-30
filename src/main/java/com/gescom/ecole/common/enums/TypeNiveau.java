package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeNiveau {
    MATERNELLE("MATERNELLE", "Maternelle"),
    PRIMAIRE("PRIMAIRE", "Primaire"),
    SECONDAIRE("SECONDAIRE", "Secondaire"),
    LYCEE("LYCEE", "Lycée"),
    SUPERIEUR("SUPERIEUR", "Supérieur");

    private final String key;
    private final String value;

    TypeNiveau(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
