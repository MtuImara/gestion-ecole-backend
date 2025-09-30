package com.gescom.ecole.mapper;

import com.gescom.ecole.dto.finance.PaiementDTO;
import com.gescom.ecole.entity.finance.Paiement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaiementMapper {
    
    public PaiementDTO toDTO(Paiement paiement) {
        if (paiement == null) {
            return null;
        }
        
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        dto.setNumeroPaiement(paiement.getNumeroPaiement());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setMontant(paiement.getMontant());
        dto.setModePaiement(paiement.getModePaiement());
        
        // Mapping de la facture
        if (paiement.getFacture() != null) {
            dto.setFacture(paiement.getFacture().getId());
            
            PaiementDTO.FactureSimpleDTO factureInfo = new PaiementDTO.FactureSimpleDTO();
            factureInfo.setNumeroFacture(paiement.getFacture().getNumeroFacture());
            factureInfo.setMontantTotal(paiement.getFacture().getMontantTotal());
            factureInfo.setMontantRestant(paiement.getFacture().getMontantRestant());
            if (paiement.getFacture().getEleve() != null) {
                factureInfo.setEleveNom(paiement.getFacture().getEleve().getNom());
                factureInfo.setElevePrenom(paiement.getFacture().getEleve().getPrenom());
            }
            dto.setFactureInfo(factureInfo);
        }
        
        // Mapping du parent
        if (paiement.getParent() != null) {
            dto.setParent(paiement.getParent().getId());
            
            PaiementDTO.ParentSimpleDTO parentInfo = new PaiementDTO.ParentSimpleDTO();
            parentInfo.setEmail(paiement.getParent().getEmail());
            parentInfo.setTelephone(paiement.getParent().getTelephone());
            dto.setParentInfo(parentInfo);
        }
        
        // Mapping du reçu
        if (paiement.getRecu() != null) {
            dto.setRecu(paiement.getRecu().getId());
        }
        
        return dto;
    }
    
    public Paiement toEntity(PaiementDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Paiement paiement = new Paiement();
        paiement.setId(dto.getId());
        paiement.setNumeroPaiement(dto.getNumeroPaiement());
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setMontant(dto.getMontant());
        paiement.setModePaiement(dto.getModePaiement());
        
        // Les relations (facture, parent, recu) doivent être définies par le service
        
        return paiement;
    }
    
    public List<PaiementDTO> toDTOList(List<Paiement> paiements) {
        if (paiements == null) {
            return new ArrayList<>();
        }
        return paiements.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<Paiement> toEntityList(List<PaiementDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public void updateEntityFromDTO(PaiementDTO dto, Paiement paiement) {
        if (dto == null || paiement == null) {
            return;
        }
        
        // Le numéro de paiement n'est jamais modifié
        if (dto.getDatePaiement() != null) {
            paiement.setDatePaiement(dto.getDatePaiement());
        }
        if (dto.getMontant() != null) {
            paiement.setMontant(dto.getMontant());
        }
        if (dto.getModePaiement() != null) {
            paiement.setModePaiement(dto.getModePaiement());
        }
        
        // Les relations ne sont jamais mises à jour via cette méthode
    }
}
