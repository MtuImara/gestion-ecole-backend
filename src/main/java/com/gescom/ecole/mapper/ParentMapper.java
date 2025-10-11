package com.gescom.ecole.mapper;

import com.gescom.ecole.dto.scolaire.EleveSimpleDTO;
import com.gescom.ecole.dto.utilisateur.ParentDTO;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.utilisateur.Parent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParentMapper {
    
    public ParentDTO toDTO(Parent parent) {
        if (parent == null) {
            return null;
        }
        
        ParentDTO dto = ParentDTO.builder()
                .id(parent.getId())
                .nom(parent.getNom())
                .prenom(parent.getPrenom())
                .email(parent.getEmail())
                .telephone(parent.getTelephone())
                .telephoneSecondaire(parent.getTelephoneSecondaire())
                .adresse(parent.getAdresse())
                .profession(parent.getProfession())
                .employeur(parent.getEmployeur())
                .adresseTravail(parent.getAdresseTravail())
                .typeParent(parent.getTypeParent())
                .cin(parent.getCin())
                .numeroPasseport(parent.getNumeroPasseport())
                .numeroParent(parent.getNumeroParent())
                .actif(parent.getActif())
                .dateCreation(parent.getDateCreation())
                .dateModification(parent.getDateModification())
                .build();
        
        // Convertir TypeParent en lienParente pour le frontend
        dto.updateLienParenteFromTypeParent();
        
        // Mapper les enfants
        if (parent.getEnfants() != null && !parent.getEnfants().isEmpty()) {
            List<EleveSimpleDTO> enfantsDTO = parent.getEnfants().stream()
                    .map(this::toEleveSimpleDTO)
                    .collect(Collectors.toList());
            dto.setEnfants(enfantsDTO);
            dto.setNombreEnfants(enfantsDTO.size());
            
            // Extraire les IDs des enfants
            List<Long> enfantIds = parent.getEnfants().stream()
                    .map(Eleve::getId)
                    .collect(Collectors.toList());
            dto.setEnfantIds(enfantIds);
        }
        
        return dto;
    }
    
    public Parent toEntity(ParentDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Parent parent = new Parent();
        updateEntityFromDTO(parent, dto);
        return parent;
    }
    
    public void updateEntityFromDTO(Parent parent, ParentDTO dto) {
        if (parent == null || dto == null) {
            return;
        }
        
        parent.setNom(dto.getNom());
        parent.setPrenom(dto.getPrenom());
        parent.setEmail(dto.getEmail());
        parent.setTelephone(dto.getTelephone());
        parent.setTelephoneSecondaire(dto.getTelephoneSecondaire());
        parent.setAdresse(dto.getAdresse());
        parent.setProfession(dto.getProfession());
        parent.setEmployeur(dto.getEmployeur());
        parent.setAdresseTravail(dto.getAdresseTravail());
        parent.setCin(dto.getCin());
        parent.setNumeroPasseport(dto.getNumeroPasseport());
        
        // Convertir lienParente en TypeParent si nécessaire
        if (dto.getLienParente() != null && dto.getTypeParent() == null) {
            dto.updateTypeParentFromLienParente();
        }
        parent.setTypeParent(dto.getTypeParent());
        
        if (dto.getActif() != null) {
            parent.setActif(dto.getActif());
        }
    }
    
    public List<ParentDTO> toDTOList(List<Parent> parents) {
        if (parents == null) {
            return null;
        }
        return parents.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private EleveSimpleDTO toEleveSimpleDTO(Eleve eleve) {
        if (eleve == null) {
            return null;
        }
        
        String classeNom = null;
        if (eleve.getClasse() != null) {
            classeNom = eleve.getClasse().getDesignation();  // Utiliser getDesignation() au lieu de getNom()
        }
        
        return EleveSimpleDTO.builder()
                .id(eleve.getId())
                .matricule(eleve.getMatricule())
                .nom(eleve.getNom())
                .prenom(eleve.getPrenom())
                .classe(classeNom)
                .statut(eleve.getStatut() != null ? eleve.getStatut().name() : null)
                .build();
    }
}
