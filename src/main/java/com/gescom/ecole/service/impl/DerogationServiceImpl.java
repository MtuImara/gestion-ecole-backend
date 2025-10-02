package com.gescom.ecole.service.impl;

import com.gescom.ecole.common.enums.StatutDerogation;
import com.gescom.ecole.dto.finance.DerogationDTO;
import com.gescom.ecole.entity.finance.Derogation;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.exception.BusinessException;
import com.gescom.ecole.exception.ResourceNotFoundException;
import com.gescom.ecole.mapper.DerogationMapper;
import com.gescom.ecole.repository.finance.DerogationRepository;
import com.gescom.ecole.repository.scolaire.EleveRepository;
import com.gescom.ecole.repository.utilisateur.UtilisateurRepository;
import com.gescom.ecole.service.DerogationService;
import com.gescom.ecole.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DerogationServiceImpl implements DerogationService {
    
    private final DerogationRepository derogationRepository;
    private final EleveRepository eleveRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DerogationMapper derogationMapper;
    private final NotificationService notificationService;

    @Override
    public DerogationDTO create(DerogationDTO derogationDTO) {
        log.info("Création d'une nouvelle dérogation pour élève ID: {}", derogationDTO.getEleveId());
        
        // Générer numéro si non fourni
        if (derogationDTO.getNumeroDerogation() == null || derogationDTO.getNumeroDerogation().isEmpty()) {
            derogationDTO.setNumeroDerogation(generateNumeroDerogation());
        }
        
        // Vérifier unicité
        if (derogationRepository.existsByNumeroDerogation(derogationDTO.getNumeroDerogation())) {
            throw new BusinessException("Une dérogation avec ce numéro existe déjà");
        }
        
        // Charger l'élève
        Eleve eleve = eleveRepository.findById(derogationDTO.getEleveId())
            .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé"));
        
        // Charger le parent
        Parent parent = (Parent) utilisateurRepository.findById(derogationDTO.getParentId())
            .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé"));
        
        Derogation derogation = derogationMapper.toEntity(derogationDTO);
        derogation.setEleve(eleve);
        derogation.setParent(parent);
        derogation.setStatut(StatutDerogation.EN_ATTENTE);
        derogation.setDateDemande(LocalDate.now());
        
        derogation = derogationRepository.save(derogation);
        
        log.info("Dérogation créée avec succès - Numéro: {}", derogation.getNumeroDerogation());
        return derogationMapper.toDTO(derogation);
    }

    @Override
    public DerogationDTO update(Long id, DerogationDTO derogationDTO) {
        log.info("Mise à jour de la dérogation ID: {}", id);
        
        Derogation derogation = derogationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dérogation non trouvée"));
        
        if (derogation.getStatut() != StatutDerogation.EN_ATTENTE) {
            throw new BusinessException("Seule une dérogation en attente peut être modifiée");
        }
        
        derogationMapper.updateEntityFromDTO(derogationDTO, derogation);
        derogation = derogationRepository.save(derogation);
        
        log.info("Dérogation mise à jour avec succès");
        return derogationMapper.toDTO(derogation);
    }

    @Override
    @Transactional(readOnly = true)
    public DerogationDTO findById(Long id) {
        Derogation derogation = derogationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dérogation non trouvée"));
        return derogationMapper.toDTO(derogation);
    }

    @Override
    @Transactional(readOnly = true)
    public DerogationDTO findByNumero(String numeroDerogation) {
        Derogation derogation = derogationRepository.findByNumeroDerogation(numeroDerogation)
            .orElseThrow(() -> new ResourceNotFoundException("Dérogation non trouvée"));
        return derogationMapper.toDTO(derogation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DerogationDTO> findAll(Pageable pageable) {
        Page<Derogation> derogations = derogationRepository.findAll(pageable);
        return derogations.map(derogationMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DerogationDTO> findByEleveId(Long eleveId) {
        List<Derogation> derogations = derogationRepository.findByEleveId(eleveId);
        return derogationMapper.toDTOList(derogations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DerogationDTO> findByParentId(Long parentId) {
        List<Derogation> derogations = derogationRepository.findByParentId(parentId);
        return derogationMapper.toDTOList(derogations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DerogationDTO> findByStatut(StatutDerogation statut, Pageable pageable) {
        Page<Derogation> derogations = derogationRepository.findByStatut(statut, pageable);
        return derogations.map(derogationMapper::toDTO);
    }

    @Override
    public void delete(Long id) {
        Derogation derogation = derogationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dérogation non trouvée"));
        
        if (derogation.getStatut() != StatutDerogation.EN_ATTENTE) {
            throw new BusinessException("Seule une dérogation en attente peut être supprimée");
        }
        
        derogationRepository.deleteById(id);
        log.info("Dérogation supprimée avec succès");
    }

    @Override
    public String generateNumeroDerogation() {
        String prefix = "DER";
        String annee = String.valueOf(LocalDate.now().getYear());
        String mois = String.format("%02d", LocalDate.now().getMonthValue());
        long count = derogationRepository.count() + 1;
        String sequence = String.format("%05d", count);
        return prefix + "-" + annee + mois + "-" + sequence;
    }

    @Override
    public DerogationDTO approuverDerogation(Long id, String decidePar) {
        log.info("Approbation de la dérogation ID: {} par {}", id, decidePar);
        
        Derogation derogation = derogationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dérogation non trouvée"));
        
        if (derogation.getStatut() != StatutDerogation.EN_ATTENTE) {
            throw new BusinessException("Seule une dérogation en attente peut être approuvée");
        }
        
        derogation.setStatut(StatutDerogation.APPROUVEE);
        derogation.setDateDecision(LocalDate.now());
        derogation.setDecidePar(decidePar);
        
        derogation = derogationRepository.save(derogation);
        
        // Envoyer notifications
        try {
            String message = "Votre demande de dérogation n°" + derogation.getNumeroDerogation() + 
                           " a été approuvée.";
            notificationService.notifierPaiementRecu(
                derogation.getEleve().getId(),
                derogation.getId(),
                message
            );
            
            if (derogation.getParent() != null) {
                notificationService.notifierPaiementRecu(
                    derogation.getParent().getId(),
                    derogation.getId(),
                    message
                );
            }
        } catch (Exception e) {
            log.error("Erreur envoi notifications", e);
        }
        
        log.info("Dérogation approuvée avec succès");
        return derogationMapper.toDTO(derogation);
    }

    @Override
    public DerogationDTO rejeterDerogation(Long id, String decidePar, String motifRejet) {
        log.info("Rejet de la dérogation ID: {} par {}", id, decidePar);
        
        Derogation derogation = derogationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dérogation non trouvée"));
        
        if (derogation.getStatut() != StatutDerogation.EN_ATTENTE) {
            throw new BusinessException("Seule une dérogation en attente peut être rejetée");
        }
        
        derogation.setStatut(StatutDerogation.REJETEE);
        derogation.setDateDecision(LocalDate.now());
        derogation.setDecidePar(decidePar);
        derogation.setObservations(motifRejet);
        
        derogation = derogationRepository.save(derogation);
        
        // Envoyer notifications
        try {
            String message = "Votre demande de dérogation n°" + derogation.getNumeroDerogation() + 
                           " a été rejetée. Motif: " + motifRejet;
            notificationService.notifierPaiementRecu(
                derogation.getEleve().getId(),
                derogation.getId(),
                message
            );
            
            if (derogation.getParent() != null) {
                notificationService.notifierPaiementRecu(
                    derogation.getParent().getId(),
                    derogation.getId(),
                    message
                );
            }
        } catch (Exception e) {
            log.error("Erreur envoi notifications", e);
        }
        
        log.info("Dérogation rejetée avec succès");
        return derogationMapper.toDTO(derogation);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByStatut(StatutDerogation statut) {
        return derogationRepository.countByStatut(statut);
    }
}
