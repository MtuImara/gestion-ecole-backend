package com.gescom.ecole.mapper;

import com.gescom.ecole.dto.finance.DerogationDTO;
import com.gescom.ecole.entity.finance.Derogation;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DerogationMapper {
    
    public DerogationDTO toDTO(Derogation entity) {
        if (entity == null) return null;
        
        DerogationDTO dto = DerogationDTO.builder()
            .id(entity.getId())
            .numeroDerogation(entity.getNumeroDerogation())
            .typeDerogation(entity.getTypeDerogation())
            .dateDemande(entity.getDateDemande())
            .dateDecision(entity.getDateDecision())
            .statut(entity.getStatut())
            .motif(entity.getMotif())
            .justificatifs(entity.getJustificatifs())
            .montantConcerne(entity.getMontantConcerne())
            .montantAccorde(entity.getMontantAccorde())
            .nouvelleEcheance(entity.getNouvelleEcheance())
            .observations(entity.getObservations())
            .decidePar(entity.getDecidePar())
            .niveauValidation(entity.getNiveauValidation())
            .build();
        
        if (entity.getEleve() != null) {
            dto.setEleveId(entity.getEleve().getId());
            dto.setEleveNom(entity.getEleve().getNom());
            dto.setElevePrenom(entity.getEleve().getPrenom());
        }
        
        if (entity.getParent() != null) {
            dto.setParentId(entity.getParent().getId());
            dto.setParentNom(entity.getParent().getUsername()); // ou getEmail() selon votre modèle
        }
        
        if (entity.getTraitePar() != null) {
            dto.setTraiteParId(entity.getTraitePar().getId());
            dto.setTraiteParNom(entity.getTraitePar().getUsername()); // ou getEmail()
        }
        
        return dto;
    }
    
    public Derogation toEntity(DerogationDTO dto) {
        if (dto == null) return null;
        
        return Derogation.builder()
            .numeroDerogation(dto.getNumeroDerogation())
            .typeDerogation(dto.getTypeDerogation())
            .dateDemande(dto.getDateDemande())
            .dateDecision(dto.getDateDecision())
            .statut(dto.getStatut())
            .motif(dto.getMotif())
            .justificatifs(dto.getJustificatifs())
            .montantConcerne(dto.getMontantConcerne())
            .montantAccorde(dto.getMontantAccorde())
            .nouvelleEcheance(dto.getNouvelleEcheance())
            .observations(dto.getObservations())
            .decidePar(dto.getDecidePar())
            .niveauValidation(dto.getNiveauValidation())
            .build();
    }
    
    public void updateEntityFromDTO(DerogationDTO dto, Derogation entity) {
        if (dto == null || entity == null) return;
        
        entity.setTypeDerogation(dto.getTypeDerogation());
        entity.setMotif(dto.getMotif());
        entity.setJustificatifs(dto.getJustificatifs());
        entity.setMontantConcerne(dto.getMontantConcerne());
        entity.setObservations(dto.getObservations());
    }
    
    public List<DerogationDTO> toDTOList(List<Derogation> entities) {
        if (entities == null) return null;
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
