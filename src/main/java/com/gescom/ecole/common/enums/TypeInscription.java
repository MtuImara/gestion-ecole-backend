package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeInscription {
    NOUVELLE("NOUVELLE", "Nouvelle inscription"),
    REINSCRIPTION("REINSCRIPTION", "Réinscription"),
    TRANSFERT("TRANSFERT", "Transfert");

    private final String key;
    private final String value;

    TypeInscription(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
