package com.gescom.ecole.service.impl;

import com.gescom.ecole.common.enums.StatutEleve;
import com.gescom.ecole.dto.scolaire.EleveDTO;
import com.gescom.ecole.entity.scolaire.Classe;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.mapper.EleveMapper;
import com.gescom.ecole.repository.scolaire.ClasseRepository;
import com.gescom.ecole.repository.scolaire.EleveRepository;
import com.gescom.ecole.service.EleveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EleveServiceImpl implements EleveService {
    
    private static final Logger log = LoggerFactory.getLogger(EleveServiceImpl.class);

    private final EleveRepository eleveRepository;
    private final ClasseRepository classeRepository;
    private final EleveMapper eleveMapper;
    @Override
    public EleveDTO create(EleveDTO eleveDTO) {
        log.info("Création d'un nouvel élève: {} {}", eleveDTO.getNom(), eleveDTO.getPrenom());
        
        // Générer le matricule si non fourni
        if (eleveDTO.getMatricule() == null || eleveDTO.getMatricule().isEmpty()) {
            eleveDTO.setMatricule(generateMatricule());
        }
        
        // Vérifier l'unicité du matricule
        if (eleveRepository.existsByMatricule(eleveDTO.getMatricule())) {
            throw new RuntimeException("Un élève avec ce matricule existe déjà: " + eleveDTO.getMatricule());
        }
        
        Eleve eleve = eleveMapper.toEntity(eleveDTO);
        eleve.setStatut(StatutEleve.ACTIF);
        eleve.setDateInscription(LocalDate.now());
        
        // Gérer la classe si fournie
        if (eleveDTO.getClasse() != null) {
            Classe classe = classeRepository.findById(eleveDTO.getClasse())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'ID: " + eleveDTO.getClasse()));
            eleve.setClasse(classe);
            log.info("Classe assignée: {} ({})", classe.getCode(), classe.getId());
        } else {
            log.warn("Aucune classe fournie pour l'élève");
        }
        
        eleve = eleveRepository.save(eleve);
        log.info("Élève créé avec succès - Matricule: {}", eleve.getMatricule());
        
        return eleveMapper.toDTO(eleve);
    }

    @Override
    public EleveDTO update(Long id, EleveDTO eleveDTO) {
        log.info("Mise à jour de l'élève ID: {}", id);
        
        Eleve eleve = eleveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Élève non trouvé"));
        
        // Vérifier l'unicité du matricule si modifié
        if (!eleve.getMatricule().equals(eleveDTO.getMatricule()) && 
            eleveRepository.existsByMatricule(eleveDTO.getMatricule())) {
            throw new RuntimeException("Un élève avec ce matricule existe déjà");
        }
        
        eleveMapper.updateEntityFromDTO(eleveDTO, eleve);
        
        // Gérer la classe si modifiée
        if (eleveDTO.getClasse() != null && 
            (eleve.getClasse() == null || !eleve.getClasse().getId().equals(eleveDTO.getClasse()))) {
            Classe classe = classeRepository.findById(eleveDTO.getClasse())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
            eleve.setClasse(classe);
        }
        
        eleve = eleveRepository.save(eleve);
        log.info("Élève mis à jour avec succès - Matricule: {}", eleve.getMatricule());
        
        return eleveMapper.toDTO(eleve);
    }

    @Override
    @Transactional(readOnly = true)
    public EleveDTO findById(Long id) {
        log.debug("Recherche de l'élève ID: {}", id);
        Eleve eleve = eleveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Élève non trouvé"));
        
        EleveDTO dto = eleveMapper.toDTO(eleve);
        // Calculer l'âge
        if (eleve.getDateNaissance() != null) {
            dto.setAge(Period.between(eleve.getDateNaissance(), LocalDate.now()).getYears());
        }
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public EleveDTO findByMatricule(String matricule) {
        log.debug("Recherche de l'élève par matricule: {}", matricule);
        Eleve eleve = eleveRepository.findByMatricule(matricule)
            .orElseThrow(() -> new RuntimeException("Élève non trouvé"));
        return eleveMapper.toDTO(eleve);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EleveDTO> findAll(Pageable pageable) {
        log.debug("Récupération de tous les élèves - Page: {}", pageable.getPageNumber());
        Page<Eleve> eleves = eleveRepository.findAll(pageable);
        return eleves.map(eleveMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EleveDTO> search(String search, Pageable pageable) {
        log.debug("Recherche d'élèves avec le terme: {}", search);
        Page<Eleve> eleves = eleveRepository.searchEleves(search, pageable);
        return eleves.map(eleveMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EleveDTO> findByClasseId(Long classeId) {
        log.debug("Recherche des élèves de la classe ID: {}", classeId);
        List<Eleve> eleves = eleveRepository.findByClasseId(classeId);
        return eleveMapper.toDTOList(eleves);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EleveDTO> findByParentId(Long parentId) {
        log.debug("Recherche des enfants du parent ID: {}", parentId);
        List<Eleve> eleves = eleveRepository.findByParentId(parentId);
        return eleveMapper.toDTOList(eleves);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de l'élève ID: {}", id);
        
        if (!eleveRepository.existsById(id)) {
            throw new RuntimeException("Élève non trouvé");
        }
        
        eleveRepository.deleteById(id);
        log.info("Élève supprimé avec succès");
    }

    @Override
    public String generateMatricule() {
        String prefix = "ELV";
        String annee = String.valueOf(LocalDate.now().getYear());
        long count = eleveRepository.count() + 1;
        String sequence = String.format("%04d", count);
        return prefix + "-" + annee + "-" + sequence;
    }

    @Override
    public EleveDTO inscrireEleve(Long eleveId, Long classeId) {
        log.info("Inscription de l'élève {} dans la classe {}", eleveId, classeId);
        
        Eleve eleve = eleveRepository.findById(eleveId)
            .orElseThrow(() -> new RuntimeException("Élève non trouvé"));
        
        Classe classe = classeRepository.findById(classeId)
            .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
        
        if (classe.estPleine()) {
            throw new RuntimeException("La classe est pleine");
        }
        
        eleve.inscrire(classe);
        eleve = eleveRepository.save(eleve);
        classeRepository.save(classe);
        
        log.info("Élève inscrit avec succès dans la classe");
        return eleveMapper.toDTO(eleve);
    }

    @Override
    public EleveDTO transfererEleve(Long eleveId, Long nouvelleClasseId) {
        log.info("Transfert de l'élève {} vers la classe {}", eleveId, nouvelleClasseId);
        
        Eleve eleve = eleveRepository.findById(eleveId)
            .orElseThrow(() -> new RuntimeException("Élève non trouvé"));
        
        Classe nouvelleClasse = classeRepository.findById(nouvelleClasseId)
            .orElseThrow(() -> new RuntimeException("Nouvelle classe non trouvée"));
        
        if (nouvelleClasse.estPleine()) {
            throw new RuntimeException("La nouvelle classe est pleine");
        }
        
        Classe ancienneClasse = eleve.getClasse();
        eleve.transferer(nouvelleClasse);
        
        eleve = eleveRepository.save(eleve);
        if (ancienneClasse != null) {
            classeRepository.save(ancienneClasse);
        }
        classeRepository.save(nouvelleClasse);
        
        log.info("Élève transféré avec succès");
        return eleveMapper.toDTO(eleve);
    }
    
    @Override
    @Transactional(readOnly = true)
    public com.gescom.ecole.dto.scolaire.EleveStatistiquesDTO getStatistiques() {
        log.info("Récupération des statistiques des élèves");
        
        long total = eleveRepository.count();
        long actifs = eleveRepository.countByStatut(StatutEleve.ACTIF);
        long boursiers = eleveRepository.countByBoursierTrue();
        // Les inactifs = total - actifs (inclut suspendus, abandonnés, diplômés, transférés)
        long inactifs = total - actifs;
        
        return com.gescom.ecole.dto.scolaire.EleveStatistiquesDTO.builder()
            .total(total)
            .actifs(actifs)
            .boursiers(boursiers)
            .inactifs(inactifs)
            .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<EleveDTO> filter(Long classeId, Long anneeScolaireId, String statut, Pageable pageable) {
        log.info("Filtrage des élèves - classeId: {}, anneeScolaireId: {}, statut: {}", classeId, anneeScolaireId, statut);
        
        StatutEleve statutEnum = null;
        if (statut != null && !statut.isEmpty()) {
            try {
                statutEnum = StatutEleve.valueOf(statut);
            } catch (IllegalArgumentException e) {
                log.warn("Statut invalide: {}", statut);
            }
        }
        
        Page<Eleve> elevesPage = eleveRepository.filterEleves(classeId, anneeScolaireId, statutEnum, pageable);
        return elevesPage.map(eleveMapper::toDTO);
    }
}
