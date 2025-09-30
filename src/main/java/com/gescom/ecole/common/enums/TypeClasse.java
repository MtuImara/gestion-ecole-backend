package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeClasse {
    NORMALE("NORMALE", "Normale"),
    SPECIALISEE("SPECIALISEE", "Spécialisée"),
    INTERNATIONALE("INTERNATIONALE", "Internationale"),
    TECHNIQUE("TECHNIQUE", "Technique");

    private final String key;
    private final String value;

    TypeClasse(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
