package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ModePaiement {
    ESPECES("ESPECES", "Espèces"),
    VIREMENT("VIREMENT", "Virement bancaire"),
    CHEQUE("CHEQUE", "Chèque"),
    CARTE_BANCAIRE("CARTE_BANCAIRE", "Carte bancaire"),
    MOBILE_MONEY("MOBILE_MONEY", "Mobile Money"),
    AUTRE("AUTRE", "Autre");

    private final String key;
    private final String value;

    ModePaiement(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
