package com.gescom.ecole.mapper;

import com.gescom.ecole.dto.scolaire.EleveDTO;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.utilisateur.Parent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EleveMapper {
    
    public EleveDTO toDTO(Eleve eleve) {
        if (eleve == null) {
            return null;
        }
        
        EleveDTO dto = new EleveDTO();
        dto.setId(eleve.getId());
        dto.setMatricule(eleve.getMatricule());
        dto.setNom(eleve.getNom());
        dto.setPrenom(eleve.getPrenom());
        dto.setDeuxiemePrenom(eleve.getDeuxiemePrenom());
        dto.setDateNaissance(eleve.getDateNaissance());
        dto.setLieuNaissance(eleve.getLieuNaissance());
        dto.setGenre(eleve.getGenre());
        dto.setNationalite(eleve.getNationalite());
        dto.setNumeroUrgence(eleve.getNumeroUrgence());
        dto.setPhotoUrl(eleve.getPhotoUrl());
        dto.setGroupeSanguin(eleve.getGroupeSanguin());
        dto.setAllergies(eleve.getAllergies());
        dto.setMaladiesChroniques(eleve.getMaladiesChroniques());
        dto.setStatut(eleve.getStatut());
        dto.setDateInscription(eleve.getDateInscription());
        dto.setEcoleProvenance(eleve.getEcoleProvenance());
        dto.setQuartier(eleve.getQuartier());
        dto.setBoursier(eleve.getBoursier());
        dto.setPourcentageBourse(eleve.getPourcentageBourse());
        
        // Mapping de la classe
        if (eleve.getClasse() != null) {
            dto.setClasse(eleve.getClasse().getId());
            
            EleveDTO.ClasseSimpleDTO classeInfo = new EleveDTO.ClasseSimpleDTO();
            classeInfo.setId(eleve.getClasse().getId());
            classeInfo.setCode(eleve.getClasse().getCode());
            classeInfo.setDesignation(eleve.getClasse().getDesignation());
            classeInfo.setEffectifActuel(eleve.getClasse().getEffectifActuel());
            classeInfo.setCapaciteMax(eleve.getClasse().getCapaciteMax());
            dto.setClasseInfo(classeInfo);
        }
        
        // Mapping des parents
        if (eleve.getParents() != null && !eleve.getParents().isEmpty()) {
            List<Long> parentIds = eleve.getParents().stream()
                    .map(Parent::getId)
                    .collect(Collectors.toList());
            dto.setParents(parentIds);
            
            List<EleveDTO.ParentSimpleDTO> parentsInfo = eleve.getParents().stream()
                    .map(parent -> {
                        EleveDTO.ParentSimpleDTO parentInfo = new EleveDTO.ParentSimpleDTO();
                        parentInfo.setId(parent.getId());
                        parentInfo.setNom(parent.getUsername());  // Utilise username de Utilisateur
                        parentInfo.setEmail(parent.getEmail());
                        parentInfo.setTelephone(parent.getTelephone());
                        if (parent.getTypeParent() != null) {
                            parentInfo.setTypeParent(parent.getTypeParent().name());
                        }
                        return parentInfo;
                    })
                    .collect(Collectors.toList());
            dto.setParentsInfo(parentsInfo);
        }
        
        // Calcul de l'âge
        if (eleve.getDateNaissance() != null) {
            dto.setAge(Period.between(eleve.getDateNaissance(), LocalDate.now()).getYears());
        }
        
        return dto;
    }
    
    public Eleve toEntity(EleveDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Eleve eleve = new Eleve();
        eleve.setId(dto.getId());
        eleve.setMatricule(dto.getMatricule());
        eleve.setNom(dto.getNom());
        eleve.setPrenom(dto.getPrenom());
        eleve.setDeuxiemePrenom(dto.getDeuxiemePrenom());
        eleve.setDateNaissance(dto.getDateNaissance());
        eleve.setLieuNaissance(dto.getLieuNaissance());
        eleve.setGenre(dto.getGenre());
        eleve.setNationalite(dto.getNationalite());
        eleve.setNumeroUrgence(dto.getNumeroUrgence());
        eleve.setPhotoUrl(dto.getPhotoUrl());
        eleve.setGroupeSanguin(dto.getGroupeSanguin());
        eleve.setAllergies(dto.getAllergies());
        eleve.setMaladiesChroniques(dto.getMaladiesChroniques());
        eleve.setStatut(dto.getStatut());
        eleve.setDateInscription(dto.getDateInscription());
        eleve.setEcoleProvenance(dto.getEcoleProvenance());
        eleve.setQuartier(dto.getQuartier());
        eleve.setBoursier(dto.getBoursier());
        eleve.setPourcentageBourse(dto.getPourcentageBourse());
        
        // Les relations (classe, parents) doivent être définies par le service
        
        return eleve;
    }
    
    public List<EleveDTO> toDTOList(List<Eleve> eleves) {
        if (eleves == null) {
            return new ArrayList<>();
        }
        return eleves.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<Eleve> toEntityList(List<EleveDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public void updateEntityFromDTO(EleveDTO dto, Eleve eleve) {
        if (dto == null || eleve == null) {
            return;
        }
        
        // Le matricule n'est jamais modifié
        if (dto.getNom() != null) {
            eleve.setNom(dto.getNom());
        }
        if (dto.getPrenom() != null) {
            eleve.setPrenom(dto.getPrenom());
        }
        if (dto.getDeuxiemePrenom() != null) {
            eleve.setDeuxiemePrenom(dto.getDeuxiemePrenom());
        }
        if (dto.getDateNaissance() != null) {
            eleve.setDateNaissance(dto.getDateNaissance());
        }
        if (dto.getLieuNaissance() != null) {
            eleve.setLieuNaissance(dto.getLieuNaissance());
        }
        if (dto.getGenre() != null) {
            eleve.setGenre(dto.getGenre());
        }
        if (dto.getNationalite() != null) {
            eleve.setNationalite(dto.getNationalite());
        }
        if (dto.getNumeroUrgence() != null) {
            eleve.setNumeroUrgence(dto.getNumeroUrgence());
        }
        if (dto.getPhotoUrl() != null) {
            eleve.setPhotoUrl(dto.getPhotoUrl());
        }
        if (dto.getGroupeSanguin() != null) {
            eleve.setGroupeSanguin(dto.getGroupeSanguin());
        }
        if (dto.getAllergies() != null) {
            eleve.setAllergies(dto.getAllergies());
        }
        if (dto.getMaladiesChroniques() != null) {
            eleve.setMaladiesChroniques(dto.getMaladiesChroniques());
        }
        if (dto.getStatut() != null) {
            eleve.setStatut(dto.getStatut());
        }
        if (dto.getDateInscription() != null) {
            eleve.setDateInscription(dto.getDateInscription());
        }
        if (dto.getEcoleProvenance() != null) {
            eleve.setEcoleProvenance(dto.getEcoleProvenance());
        }
        if (dto.getQuartier() != null) {
            eleve.setQuartier(dto.getQuartier());
        }
        if (dto.getBoursier() != null) {
            eleve.setBoursier(dto.getBoursier());
        }
        if (dto.getPourcentageBourse() != null) {
            eleve.setPourcentageBourse(dto.getPourcentageBourse());
        }
        
        // Les relations (classe, parents) doivent être mises à jour par le service
    }
}
