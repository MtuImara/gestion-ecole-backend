package com.gescom.ecole.service.impl;

import com.gescom.ecole.common.enums.StatutFacture;
import com.gescom.ecole.dto.finance.FactureDTO;
import com.gescom.ecole.entity.finance.Facture;
import com.gescom.ecole.entity.finance.LigneFacture;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.exception.BusinessException;
import com.gescom.ecole.exception.ResourceNotFoundException;
import com.gescom.ecole.mapper.FactureMapper;
import com.gescom.ecole.repository.finance.FactureRepository;
import com.gescom.ecole.repository.scolaire.EleveRepository;
import com.gescom.ecole.repository.scolaire.AnneeScolaireRepository;
import com.gescom.ecole.repository.scolaire.PeriodeRepository;
import com.gescom.ecole.service.FactureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final EleveRepository eleveRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;
    private final PeriodeRepository periodeRepository;
    private final FactureMapper factureMapper;

    @Override
    public FactureDTO create(FactureDTO factureDTO) {
        log.info("Création d'une nouvelle facture pour l'élève ID: {}", factureDTO.getEleve());
        
        // Générer le numéro de facture si non fourni
        if (factureDTO.getNumeroFacture() == null || factureDTO.getNumeroFacture().isEmpty()) {
            factureDTO.setNumeroFacture(generateNumeroFacture());
        }
        
        // Vérifier l'unicité du numéro de facture
        if (factureRepository.existsByNumeroFacture(factureDTO.getNumeroFacture())) {
            throw new BusinessException("Une facture avec ce numéro existe déjà");
        }
        
        Facture facture = factureMapper.toEntity(factureDTO);
        
        // Charger l'élève
        Eleve eleve = eleveRepository.findById(factureDTO.getEleve())
            .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé"));
        facture.setEleve(eleve);
        
        // Charger l'année scolaire
        if (factureDTO.getAnneeScolaire() != null) {
            facture.setAnneeScolaire(anneeScolaireRepository.findById(factureDTO.getAnneeScolaire())
                .orElseThrow(() -> new ResourceNotFoundException("Année scolaire non trouvée")));
        }
        
        // Charger la période si fournie
        if (factureDTO.getPeriode() != null) {
            facture.setPeriode(periodeRepository.findById(factureDTO.getPeriode())
                .orElseThrow(() -> new ResourceNotFoundException("Période non trouvée")));
        }
        
        // Calculer les montants
        BigDecimal montantTotal = calculerMontantTotalLignes(facture.getLignes());
        facture.setMontantTotal(montantTotal);
        facture.setMontantPaye(BigDecimal.ZERO);
        facture.setMontantRestant(montantTotal);
        facture.setStatut(StatutFacture.BROUILLON);
        facture.setDevise("XAF");
        
        facture = factureRepository.save(facture);
        log.info("Facture créée avec succès - Numéro: {}", facture.getNumeroFacture());
        
        return factureMapper.toDTO(facture);
    }

    @Override
    public FactureDTO update(Long id, FactureDTO factureDTO) {
        log.info("Mise à jour de la facture ID: {}", id);
        
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        
        // Vérifier si la facture peut être modifiée
        if (facture.getStatut() == StatutFacture.PAYEE) {
            throw new BusinessException("Une facture payée ne peut pas être modifiée");
        }
        
        if (facture.getStatut() == StatutFacture.ANNULEE) {
            throw new BusinessException("Une facture annulée ne peut pas être modifiée");
        }
        
        // Vérifier l'unicité du numéro si modifié
        if (!facture.getNumeroFacture().equals(factureDTO.getNumeroFacture()) && 
            factureRepository.existsByNumeroFacture(factureDTO.getNumeroFacture())) {
            throw new BusinessException("Une facture avec ce numéro existe déjà");
        }
        
        factureMapper.updateEntityFromDTO(factureDTO, facture);
        
        // Recalculer les montants si nécessaire
        if (facture.getLignes() != null && !facture.getLignes().isEmpty()) {
            BigDecimal montantTotal = calculerMontantTotalLignes(facture.getLignes());
            facture.setMontantTotal(montantTotal);
            facture.setMontantRestant(montantTotal.subtract(facture.getMontantPaye()));
        }
        
        facture = factureRepository.save(facture);
        log.info("Facture mise à jour avec succès - Numéro: {}", facture.getNumeroFacture());
        
        return factureMapper.toDTO(facture);
    }

    @Override
    @Transactional(readOnly = true)
    public FactureDTO findById(Long id) {
        log.debug("Recherche de la facture ID: {}", id);
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        return factureMapper.toDTO(facture);
    }

    @Override
    @Transactional(readOnly = true)
    public FactureDTO findByNumeroFacture(String numeroFacture) {
        log.debug("Recherche de la facture par numéro: {}", numeroFacture);
        Facture facture = factureRepository.findByNumeroFacture(numeroFacture)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        return factureMapper.toDTO(facture);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FactureDTO> findAll(Pageable pageable) {
        log.debug("Récupération de toutes les factures - Page: {}", pageable.getPageNumber());
        Page<Facture> factures = factureRepository.findAll(pageable);
        return factures.map(factureMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureDTO> findByEleveId(Long eleveId) {
        log.debug("Recherche des factures de l'élève ID: {}", eleveId);
        List<Facture> factures = factureRepository.findByEleveId(eleveId);
        return factureMapper.toDTOList(factures);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureDTO> findFacturesImpayees(Long eleveId) {
        log.debug("Recherche des factures impayées de l'élève ID: {}", eleveId);
        List<Facture> factures = factureRepository.findFacturesImpayeesByEleveId(eleveId);
        return factureMapper.toDTOList(factures);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureDTO> findFacturesEchues() {
        log.debug("Recherche des factures échues");
        List<Facture> factures = factureRepository.findFacturesEchues(LocalDate.now());
        return factureMapper.toDTOList(factures);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de la facture ID: {}", id);
        
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        
        // Vérifier si la facture peut être supprimée
        if (facture.getStatut() == StatutFacture.PAYEE || facture.getStatut() == StatutFacture.PARTIELLEMENT_PAYEE) {
            throw new BusinessException("Une facture avec des paiements ne peut pas être supprimée");
        }
        
        factureRepository.deleteById(id);
        log.info("Facture supprimée avec succès");
    }

    @Override
    public String generateNumeroFacture() {
        String prefix = "FACT";
        String annee = String.valueOf(LocalDate.now().getYear());
        String mois = String.format("%02d", LocalDate.now().getMonthValue());
        long count = factureRepository.count() + 1;
        String sequence = String.format("%05d", count);
        return prefix + "-" + annee + mois + "-" + sequence;
    }

    @Override
    public FactureDTO validerFacture(Long id) {
        log.info("Validation de la facture ID: {}", id);
        
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        
        if (facture.getStatut() != StatutFacture.BROUILLON) {
            throw new BusinessException("Seule une facture en brouillon peut être validée");
        }
        
        facture.setStatut(StatutFacture.EMISE);
        facture = factureRepository.save(facture);
        
        log.info("Facture validée avec succès - Numéro: {}", facture.getNumeroFacture());
        return factureMapper.toDTO(facture);
    }

    @Override
    public FactureDTO annulerFacture(Long id) {
        log.info("Annulation de la facture ID: {}", id);
        
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        
        if (facture.getStatut() == StatutFacture.PAYEE) {
            throw new BusinessException("Une facture payée ne peut pas être annulée");
        }
        
        facture.setStatut(StatutFacture.ANNULEE);
        facture = factureRepository.save(facture);
        
        log.info("Facture annulée avec succès - Numéro: {}", facture.getNumeroFacture());
        return factureMapper.toDTO(facture);
    }

    @Override
    public void envoyerRappel(Long id) {
        log.info("Envoi de rappel pour la facture ID: {}", id);
        
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        
        if (facture.getStatut() == StatutFacture.PAYEE) {
            throw new BusinessException("Pas de rappel nécessaire pour une facture payée");
        }
        
        // TODO: Implémenter l'envoi d'email
        facture.setRappelEnvoye(true);
        facture.setNombreRappels(facture.getNombreRappels() != null ? facture.getNombreRappels() + 1 : 1);
        facture.setDernierRappel(LocalDate.now());
        
        factureRepository.save(facture);
        log.info("Rappel envoyé avec succès pour la facture: {}", facture.getNumeroFacture());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerMontantTotal(Long eleveId) {
        List<Facture> factures = factureRepository.findByEleveId(eleveId);
        return factures.stream()
            .map(Facture::getMontantTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerMontantPaye(Long eleveId) {
        List<Facture> factures = factureRepository.findByEleveId(eleveId);
        return factures.stream()
            .map(Facture::getMontantPaye)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerMontantRestant(Long eleveId) {
        List<Facture> factures = factureRepository.findByEleveId(eleveId);
        return factures.stream()
            .map(Facture::getMontantRestant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerMontantTotalLignes(List<LigneFacture> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return lignes.stream()
            .map(ligne -> ligne.getMontantUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
