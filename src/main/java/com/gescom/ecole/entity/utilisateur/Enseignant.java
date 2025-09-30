package com.gescom.ecole.entity.utilisateur;

import com.gescom.ecole.common.enums.TypeContrat;
import com.gescom.ecole.entity.scolaire.Classe;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "enseignants")
@DiscriminatorValue("ENSEIGNANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enseignant extends Utilisateur {

    @Column(name = "matricule", unique = true, length = 50)
    private String matricule;

    @Column(name = "specialite", length = 100)
    private String specialite;

    @Column(name = "diplome", length = 150)
    private String diplome;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contrat", length = 20)
    private TypeContrat typeContrat;

    @OneToMany(mappedBy = "enseignantPrincipal")
    private Set<Classe> classesPrincipales = new HashSet<>();

    @ManyToMany(mappedBy = "enseignants")
    private Set<Classe> classesEnseignees = new HashSet<>();
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
}
