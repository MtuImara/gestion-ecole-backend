package com.gescom.ecole.mapper;

import com.gescom.ecole.dto.scolaire.ClasseDTO;
import com.gescom.ecole.entity.scolaire.Classe;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClasseMapper {
    
    public ClasseDTO toDTO(Classe classe) {
        if (classe == null) {
            return null;
        }
        
        ClasseDTO dto = new ClasseDTO();
        dto.setId(classe.getId());
        dto.setCode(classe.getCode());
        dto.setDesignation(classe.getDesignation());
        dto.setEffectifActuel(classe.getEffectifActuel());
        dto.setCapaciteMax(classe.getCapaciteMax());
        
        // Mapping des relations
        if (classe.getNiveau() != null) {
            dto.setNiveau(classe.getNiveau().getId());
            dto.setNiveauDesignation(classe.getNiveau().getDesignation());
        }
        
        if (classe.getAnneeScolaire() != null) {
            dto.setAnneeScolaire(classe.getAnneeScolaire().getId());
            dto.setAnneeScolaireDesignation(classe.getAnneeScolaire().getDesignation());
        }
        
        if (classe.getEnseignantPrincipal() != null) {
            dto.setEnseignantPrincipal(classe.getEnseignantPrincipal().getId());
            dto.setEnseignantPrincipalNom(classe.getEnseignantPrincipal().getUsername());
        }
        
        // Calculs
        if (classe.getCapaciteMax() != null && classe.getEffectifActuel() != null) {
            dto.setPlacesDisponibles(classe.getCapaciteMax() - classe.getEffectifActuel());
            if (classe.getCapaciteMax() > 0) {
                dto.setTauxRemplissage((double) classe.getEffectifActuel() / classe.getCapaciteMax() * 100);
            }
        }
        
        return dto;
    }
    
    public Classe toEntity(ClasseDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Classe classe = new Classe();
        classe.setId(dto.getId());
        classe.setCode(dto.getCode());
        classe.setDesignation(dto.getDesignation());
        classe.setEffectifActuel(dto.getEffectifActuel());
        classe.setCapaciteMax(dto.getCapaciteMax());
        
        // Les relations (niveau, anneeScolaire, enseignantPrincipal) doivent être définies par le service
        
        return classe;
    }
    
    public List<ClasseDTO> toDTOList(List<Classe> classes) {
        if (classes == null) {
            return new ArrayList<>();
        }
        return classes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<Classe> toEntityList(List<ClasseDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public void updateEntityFromDTO(ClasseDTO dto, Classe classe) {
        if (dto == null || classe == null) {
            return;
        }
        
        if (dto.getCode() != null) {
            classe.setCode(dto.getCode());
        }
        if (dto.getDesignation() != null) {
            classe.setDesignation(dto.getDesignation());
        }
        if (dto.getEffectifActuel() != null) {
            classe.setEffectifActuel(dto.getEffectifActuel());
        }
        if (dto.getCapaciteMax() != null) {
            classe.setCapaciteMax(dto.getCapaciteMax());
        }
        
        // Les relations doivent être mises à jour par le service
    }
}
