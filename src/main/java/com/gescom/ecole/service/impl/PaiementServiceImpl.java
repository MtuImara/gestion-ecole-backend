package com.gescom.ecole.service.impl;

import com.gescom.ecole.common.enums.StatutFacture;
import com.gescom.ecole.common.enums.StatutPaiement;
import com.gescom.ecole.dto.finance.PaiementDTO;
import com.gescom.ecole.entity.finance.Facture;
import com.gescom.ecole.entity.finance.Paiement;
import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.exception.BusinessException;
import com.gescom.ecole.exception.ResourceNotFoundException;
import com.gescom.ecole.mapper.PaiementMapper;
import com.gescom.ecole.repository.finance.FactureRepository;
import com.gescom.ecole.repository.finance.PaiementRepository;
import com.gescom.ecole.repository.utilisateur.UtilisateurRepository;
import com.gescom.ecole.service.PaiementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaiementServiceImpl implements PaiementService {
    
    private static final Logger log = LoggerFactory.getLogger(PaiementServiceImpl.class);

    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PaiementMapper paiementMapper;

    @Override
    public PaiementDTO create(PaiementDTO paiementDTO) {
        log.info("Création d'un nouveau paiement pour la facture ID: {}", paiementDTO.getFacture());
        
        // Générer le numéro de paiement si non fourni
        if (paiementDTO.getNumeroPaiement() == null || paiementDTO.getNumeroPaiement().isEmpty()) {
            paiementDTO.setNumeroPaiement(generateNumeroPaiement());
        }
        
        // Vérifier l'unicité du numéro de paiement
        if (paiementRepository.existsByNumeroPaiement(paiementDTO.getNumeroPaiement())) {
            throw new BusinessException("Un paiement avec ce numéro existe déjà");
        }
        
        // Charger la facture
        Facture facture = factureRepository.findById(paiementDTO.getFacture())
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        
        // Vérifier que la facture n'est pas déjà payée
        if (facture.getStatut() == StatutFacture.PAYEE) {
            throw new BusinessException("Cette facture est déjà entièrement payée");
        }
        
        // Vérifier que le montant ne dépasse pas le montant restant
        if (paiementDTO.getMontant().compareTo(facture.getMontantRestant()) > 0) {
            throw new BusinessException("Le montant du paiement dépasse le montant restant de la facture");
        }
        
        Paiement paiement = paiementMapper.toEntity(paiementDTO);
        paiement.setFacture(facture);
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setStatut(StatutPaiement.EN_ATTENTE);
        
        // Charger le parent si fourni
        if (paiementDTO.getParent() != null) {
            Parent parent = (Parent) utilisateurRepository.findById(paiementDTO.getParent())
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé"));
            paiement.setParent(parent);
        }
        
        paiement = paiementRepository.save(paiement);
        
        // Mettre à jour la facture
        updateFactureAfterPaiement(facture, paiement.getMontant());
        
        log.info("Paiement créé avec succès - Numéro: {}", paiement.getNumeroPaiement());
        return paiementMapper.toDTO(paiement);
    }

    @Override
    public PaiementDTO update(Long id, PaiementDTO paiementDTO) {
        log.info("Mise à jour du paiement ID: {}", id);
        
        Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        
        // Vérifier si le paiement peut être modifié
        if (paiement.getStatut() == StatutPaiement.VALIDE) {
            throw new BusinessException("Un paiement validé ne peut pas être modifié");
        }
        
        if (paiement.getStatut() == StatutPaiement.ANNULE) {
            throw new BusinessException("Un paiement annulé ne peut pas être modifié");
        }
        
        // Sauvegarder l'ancien montant
        BigDecimal ancienMontant = paiement.getMontant();
        
        paiementMapper.updateEntityFromDTO(paiementDTO, paiement);
        
        // Si le montant a changé, mettre à jour la facture
        if (!ancienMontant.equals(paiement.getMontant())) {
            Facture facture = paiement.getFacture();
            // Annuler l'ancien montant
            facture.setMontantPaye(facture.getMontantPaye().subtract(ancienMontant));
            facture.setMontantRestant(facture.getMontantRestant().add(ancienMontant));
            // Appliquer le nouveau montant
            updateFactureAfterPaiement(facture, paiement.getMontant());
        }
        
        paiement = paiementRepository.save(paiement);
        log.info("Paiement mis à jour avec succès - Numéro: {}", paiement.getNumeroPaiement());
        
        return paiementMapper.toDTO(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementDTO findById(Long id) {
        log.debug("Recherche du paiement ID: {}", id);
        Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        return paiementMapper.toDTO(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementDTO findByNumeroPaiement(String numeroPaiement) {
        log.debug("Recherche du paiement par numéro: {}", numeroPaiement);
        Paiement paiement = paiementRepository.findByNumeroPaiement(numeroPaiement)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        return paiementMapper.toDTO(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaiementDTO> findAll(Pageable pageable) {
        log.debug("Récupération de tous les paiements - Page: {}", pageable.getPageNumber());
        Page<Paiement> paiements = paiementRepository.findAll(pageable);
        return paiements.map(paiementMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDTO> findByFactureId(Long factureId) {
        log.debug("Recherche des paiements de la facture ID: {}", factureId);
        List<Paiement> paiements = paiementRepository.findByFactureId(factureId);
        return paiementMapper.toDTOList(paiements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDTO> findByParentId(Long parentId) {
        log.debug("Recherche des paiements du parent ID: {}", parentId);
        List<Paiement> paiements = paiementRepository.findByParentId(parentId);
        return paiementMapper.toDTOList(paiements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDTO> findByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.debug("Recherche des paiements entre {} et {}", dateDebut, dateFin);
        List<Paiement> paiements = paiementRepository.findByPeriode(dateDebut, dateFin);
        return paiementMapper.toDTOList(paiements);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression du paiement ID: {}", id);
        
        Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        
        // Vérifier si le paiement peut être supprimé
        if (paiement.getStatut() == StatutPaiement.VALIDE) {
            throw new BusinessException("Un paiement validé ne peut pas être supprimé");
        }
        
        // Si le paiement était en attente, mettre à jour la facture
        if (paiement.getStatut() == StatutPaiement.EN_ATTENTE) {
            Facture facture = paiement.getFacture();
            facture.setMontantPaye(facture.getMontantPaye().subtract(paiement.getMontant()));
            facture.setMontantRestant(facture.getMontantRestant().add(paiement.getMontant()));
            updateStatutFacture(facture);
            factureRepository.save(facture);
        }
        
        paiementRepository.deleteById(id);
        log.info("Paiement supprimé avec succès");
    }

    @Override
    public String generateNumeroPaiement() {
        String prefix = "PAY";
        String annee = String.valueOf(LocalDateTime.now().getYear());
        String mois = String.format("%02d", LocalDateTime.now().getMonthValue());
        String jour = String.format("%02d", LocalDateTime.now().getDayOfMonth());
        long count = paiementRepository.count() + 1;
        String sequence = String.format("%05d", count);
        return prefix + "-" + annee + mois + jour + "-" + sequence;
    }

    @Override
    public PaiementDTO validerPaiement(Long id) {
        log.info("Validation du paiement ID: {}", id);
        
        Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        
        if (paiement.getStatut() != StatutPaiement.EN_ATTENTE) {
            throw new BusinessException("Seul un paiement en attente peut être validé");
        }
        
        paiement.setStatut(StatutPaiement.VALIDE);
        paiement.setDateValidation(LocalDateTime.now());
        paiement = paiementRepository.save(paiement);
        
        log.info("Paiement validé avec succès - Numéro: {}", paiement.getNumeroPaiement());
        return paiementMapper.toDTO(paiement);
    }

    @Override
    public PaiementDTO annulerPaiement(Long id) {
        log.info("Annulation du paiement ID: {}", id);
        
        Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        
        if (paiement.getStatut() == StatutPaiement.ANNULE) {
            throw new BusinessException("Ce paiement est déjà annulé");
        }
        
        // Mettre à jour la facture
        Facture facture = paiement.getFacture();
        facture.setMontantPaye(facture.getMontantPaye().subtract(paiement.getMontant()));
        facture.setMontantRestant(facture.getMontantRestant().add(paiement.getMontant()));
        updateStatutFacture(facture);
        factureRepository.save(facture);
        
        paiement.setStatut(StatutPaiement.ANNULE);
        paiement = paiementRepository.save(paiement);
        
        log.info("Paiement annulé avec succès - Numéro: {}", paiement.getNumeroPaiement());
        return paiementMapper.toDTO(paiement);
    }

    @Override
    public byte[] genererRecu(Long id) {
        log.info("Génération du reçu pour le paiement ID: {}", id);
        
        Paiement paiement = paiementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        
        if (paiement.getStatut() != StatutPaiement.VALIDE) {
            throw new BusinessException("Un reçu ne peut être généré que pour un paiement validé");
        }
        
        // TODO: Implémenter la génération PDF du reçu
        log.info("Reçu généré avec succès pour le paiement: {}", paiement.getNumeroPaiement());
        return new byte[0]; // Placeholder
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaiementsByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        BigDecimal total = paiementRepository.getTotalPaiementsByPeriode(dateDebut, dateFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    private void updateFactureAfterPaiement(Facture facture, BigDecimal montantPaiement) {
        facture.setMontantPaye(facture.getMontantPaye().add(montantPaiement));
        facture.setMontantRestant(facture.getMontantRestant().subtract(montantPaiement));
        updateStatutFacture(facture);
        factureRepository.save(facture);
    }

    private void updateStatutFacture(Facture facture) {
        if (facture.getMontantRestant().compareTo(BigDecimal.ZERO) == 0) {
            facture.setStatut(StatutFacture.PAYEE);
        } else if (facture.getMontantPaye().compareTo(BigDecimal.ZERO) > 0) {
            facture.setStatut(StatutFacture.PARTIELLEMENT_PAYEE);
        } else {
            facture.setStatut(StatutFacture.EMISE);
        }
    }
}
