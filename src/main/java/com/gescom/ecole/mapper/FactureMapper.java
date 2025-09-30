package com.gescom.ecole.mapper;

import com.gescom.ecole.dto.finance.FactureDTO;
import com.gescom.ecole.entity.finance.Facture;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FactureMapper {
    
    public FactureDTO toDTO(Facture facture) {
        if (facture == null) {
            return null;
        }
        
        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setNumeroFacture(facture.getNumeroFacture());
        dto.setDateEmission(facture.getDateEmission());
        dto.setDateEcheance(facture.getDateEcheance());
        dto.setMontantTotal(facture.getMontantTotal());
        dto.setMontantPaye(facture.getMontantPaye());
        dto.setMontantRestant(facture.getMontantRestant());
        dto.setStatut(facture.getStatut());
        dto.setDescription(facture.getDescription());
        
        // Mapping de l'élève
        if (facture.getEleve() != null) {
            dto.setEleve(facture.getEleve().getId());
            
            FactureDTO.EleveSimpleDTO eleveInfo = new FactureDTO.EleveSimpleDTO();
            eleveInfo.setMatricule(facture.getEleve().getMatricule());
            eleveInfo.setNom(facture.getEleve().getNom());
            eleveInfo.setPrenom(facture.getEleve().getPrenom());
            if (facture.getEleve().getClasse() != null) {
                eleveInfo.setClasse(facture.getEleve().getClasse().getDesignation());
            }
            dto.setEleveInfo(eleveInfo);
        }
        
        // Mapping de l'année scolaire
        if (facture.getAnneeScolaire() != null) {
            dto.setAnneeScolaire(facture.getAnneeScolaire().getId());
            dto.setAnneeScolaireDesignation(facture.getAnneeScolaire().getDesignation());
        }
        
        // Mapping de la période
        if (facture.getPeriode() != null) {
            dto.setPeriode(facture.getPeriode().getId());
            dto.setPeriodeDesignation(facture.getPeriode().getDesignation());
        }
        
        return dto;
    }
    
    public Facture toEntity(FactureDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Facture facture = new Facture();
        facture.setId(dto.getId());
        facture.setNumeroFacture(dto.getNumeroFacture());
        facture.setDateEmission(dto.getDateEmission());
        facture.setDateEcheance(dto.getDateEcheance());
        facture.setMontantTotal(dto.getMontantTotal());
        facture.setMontantPaye(dto.getMontantPaye());
        facture.setMontantRestant(dto.getMontantRestant());
        facture.setStatut(dto.getStatut());
        facture.setDescription(dto.getDescription());
        
        // Les relations (élève, annéeScolaire, période) doivent être définies par le service
        
        return facture;
    }
    
    public List<FactureDTO> toDTOList(List<Facture> factures) {
        if (factures == null) {
            return new ArrayList<>();
        }
        return factures.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<Facture> toEntityList(List<FactureDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public void updateEntityFromDTO(FactureDTO dto, Facture facture) {
        if (dto == null || facture == null) {
            return;
        }
        
        // Le numéro de facture n'est jamais modifié
        if (dto.getDateEmission() != null) {
            facture.setDateEmission(dto.getDateEmission());
        }
        if (dto.getDateEcheance() != null) {
            facture.setDateEcheance(dto.getDateEcheance());
        }
        if (dto.getMontantTotal() != null) {
            facture.setMontantTotal(dto.getMontantTotal());
        }
        if (dto.getMontantPaye() != null) {
            facture.setMontantPaye(dto.getMontantPaye());
        }
        if (dto.getMontantRestant() != null) {
            facture.setMontantRestant(dto.getMontantRestant());
        }
        if (dto.getStatut() != null) {
            facture.setStatut(dto.getStatut());
        }
        if (dto.getDescription() != null) {
            facture.setDescription(dto.getDescription());
        }
        
        // Les relations ne sont jamais mises à jour via cette méthode
    }
}
