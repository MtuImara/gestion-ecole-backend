package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatutEleve {
    ACTIF("ACTIF", "Actif"),
    SUSPENDU("SUSPENDU", "Suspendu"),
    ABANDONNE("ABANDONNE", "Abandonné"),
    DIPLOME("DIPLOME", "Diplômé"),
    TRANSFERE("TRANSFERE", "Transféré");

    private final String key;
    private final String value;

    StatutEleve(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
