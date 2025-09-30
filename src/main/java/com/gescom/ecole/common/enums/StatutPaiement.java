package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatutPaiement {
    EN_ATTENTE("EN_ATTENTE", "En attente"),
    VALIDE("VALIDE", "Validé"),
    REJETE("REJETE", "Rejeté"),
    ANNULE("ANNULE", "Annulé"),
    REMBOURSE("REMBOURSE", "Remboursé");

    private final String key;
    private final String value;

    StatutPaiement(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
