package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeFrais {
    MINERVAL("MINERVAL", "Minerval"),
    INSCRIPTION("INSCRIPTION", "Inscription"),
    ASSURANCE("ASSURANCE", "Assurance"),
    ACTIVITES("ACTIVITES", "Activités"),
    EXAMEN("EXAMEN", "Examen"),
    UNIFORME("UNIFORME", "Uniforme"),
    CANTINE("CANTINE", "Cantine"),
    TRANSPORT("TRANSPORT", "Transport"),
    AUTRE("AUTRE", "Autre");

    private final String key;
    private final String value;

    TypeFrais(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
