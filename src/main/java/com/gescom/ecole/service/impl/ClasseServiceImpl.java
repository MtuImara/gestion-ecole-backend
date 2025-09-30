package com.gescom.ecole.service.impl;

import com.gescom.ecole.dto.scolaire.ClasseDTO;
import com.gescom.ecole.entity.scolaire.Classe;
import com.gescom.ecole.entity.scolaire.Niveau;
import com.gescom.ecole.entity.scolaire.AnneeScolaire;
import com.gescom.ecole.entity.utilisateur.Enseignant;
import com.gescom.ecole.exception.BusinessException;
import com.gescom.ecole.exception.ResourceNotFoundException;
import com.gescom.ecole.mapper.ClasseMapper;
import com.gescom.ecole.repository.scolaire.ClasseRepository;
import com.gescom.ecole.repository.scolaire.NiveauRepository;
import com.gescom.ecole.repository.scolaire.AnneeScolaireRepository;
import com.gescom.ecole.repository.utilisateur.UtilisateurRepository;
import com.gescom.ecole.service.ClasseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClasseServiceImpl implements ClasseService {

    private final ClasseRepository classeRepository;
    private final NiveauRepository niveauRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClasseMapper classeMapper;

    @Override
    public ClasseDTO create(ClasseDTO classeDTO) {
        log.info("Création d'une nouvelle classe: {}", classeDTO.getDesignation());
        
        // Vérifier l'unicité du code pour l'année scolaire
        if (classeRepository.findByCodeAndAnneeScolaireId(classeDTO.getCode(), classeDTO.getAnneeScolaire()).isPresent()) {
            throw new BusinessException("Une classe avec ce code existe déjà pour cette année scolaire");
        }
        
        Classe classe = classeMapper.toEntity(classeDTO);
        
        // Charger le niveau
        Niveau niveau = niveauRepository.findById(classeDTO.getNiveau())
            .orElseThrow(() -> new ResourceNotFoundException("Niveau non trouvé"));
        classe.setNiveau(niveau);
        
        // Charger l'année scolaire
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findById(classeDTO.getAnneeScolaire())
            .orElseThrow(() -> new ResourceNotFoundException("Année scolaire non trouvée"));
        classe.setAnneeScolaire(anneeScolaire);
        
        // Charger l'enseignant principal si fourni
        if (classeDTO.getEnseignantPrincipal() != null) {
            Enseignant enseignant = (Enseignant) utilisateurRepository.findById(classeDTO.getEnseignantPrincipal())
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));
            classe.setEnseignantPrincipal(enseignant);
        }
        
        classe.setEffectifActuel(0);
        classe.setActive(true);
        
        classe = classeRepository.save(classe);
        log.info("Classe créée avec succès - Code: {}", classe.getCode());
        
        return enrichirDTO(classeMapper.toDTO(classe));
    }

    @Override
    public ClasseDTO update(Long id, ClasseDTO classeDTO) {
        log.info("Mise à jour de la classe ID: {}", id);
        
        Classe classe = classeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        
        // Vérifier l'unicité du code si modifié
        if (!classe.getCode().equals(classeDTO.getCode())) {
            if (classeRepository.findByCodeAndAnneeScolaireId(classeDTO.getCode(), classe.getAnneeScolaire().getId()).isPresent()) {
                throw new BusinessException("Une classe avec ce code existe déjà pour cette année scolaire");
            }
        }
        
        // Vérifier que la nouvelle capacité n'est pas inférieure à l'effectif actuel
        if (classeDTO.getCapaciteMax() != null && classeDTO.getCapaciteMax() < classe.getEffectifActuel()) {
            throw new BusinessException("La capacité maximale ne peut pas être inférieure à l'effectif actuel (" + classe.getEffectifActuel() + ")");
        }
        
        classeMapper.updateEntityFromDTO(classeDTO, classe);
        
        // Mettre à jour le niveau si modifié
        if (classeDTO.getNiveau() != null && !classeDTO.getNiveau().equals(classe.getNiveau().getId())) {
            Niveau niveau = niveauRepository.findById(classeDTO.getNiveau())
                .orElseThrow(() -> new ResourceNotFoundException("Niveau non trouvé"));
            classe.setNiveau(niveau);
        }
        
        // Mettre à jour l'enseignant principal si modifié
        if (classeDTO.getEnseignantPrincipal() != null) {
            Enseignant enseignant = (Enseignant) utilisateurRepository.findById(classeDTO.getEnseignantPrincipal())
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));
            classe.setEnseignantPrincipal(enseignant);
        }
        
        classe = classeRepository.save(classe);
        log.info("Classe mise à jour avec succès - Code: {}", classe.getCode());
        
        return enrichirDTO(classeMapper.toDTO(classe));
    }

    @Override
    @Transactional(readOnly = true)
    public ClasseDTO findById(Long id) {
        log.debug("Recherche de la classe ID: {}", id);
        Classe classe = classeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        return enrichirDTO(classeMapper.toDTO(classe));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClasseDTO> findAll(Pageable pageable) {
        log.debug("Récupération de toutes les classes - Page: {}", pageable.getPageNumber());
        Page<Classe> classes = classeRepository.findAll(pageable);
        return classes.map(classe -> enrichirDTO(classeMapper.toDTO(classe)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClasseDTO> findByNiveauId(Long niveauId) {
        log.debug("Recherche des classes du niveau ID: {}", niveauId);
        List<Classe> classes = classeRepository.findByNiveauId(niveauId);
        return classes.stream()
            .map(classeMapper::toDTO)
            .map(this::enrichirDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClasseDTO> findByAnneeScolaireId(Long anneeScolaireId) {
        log.debug("Recherche des classes de l'année scolaire ID: {}", anneeScolaireId);
        List<Classe> classes = classeRepository.findByAnneeScolaireId(anneeScolaireId);
        return classes.stream()
            .map(classeMapper::toDTO)
            .map(this::enrichirDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClasseDTO> findActiveClasses() {
        log.debug("Recherche des classes actives");
        List<Classe> classes = classeRepository.findActiveClasses();
        return classes.stream()
            .map(classeMapper::toDTO)
            .map(this::enrichirDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClasseDTO> findClassesWithAvailableSpace() {
        log.debug("Recherche des classes avec places disponibles");
        List<Classe> classes = classeRepository.findClassesWithAvailableSpace();
        return classes.stream()
            .map(classeMapper::toDTO)
            .map(this::enrichirDTO)
            .toList();
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de la classe ID: {}", id);
        
        Classe classe = classeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        
        // Vérifier qu'il n'y a pas d'élèves dans la classe
        if (classe.getEffectifActuel() > 0) {
            throw new BusinessException("Impossible de supprimer une classe contenant des élèves");
        }
        
        classeRepository.deleteById(id);
        log.info("Classe supprimée avec succès");
    }

    @Override
    public ClasseDTO activerClasse(Long id) {
        log.info("Activation de la classe ID: {}", id);
        
        Classe classe = classeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        
        classe.setActive(true);
        classe = classeRepository.save(classe);
        
        log.info("Classe activée avec succès - Code: {}", classe.getCode());
        return enrichirDTO(classeMapper.toDTO(classe));
    }

    @Override
    public ClasseDTO desactiverClasse(Long id) {
        log.info("Désactivation de la classe ID: {}", id);
        
        Classe classe = classeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        
        classe.setActive(false);
        classe = classeRepository.save(classe);
        
        log.info("Classe désactivée avec succès - Code: {}", classe.getCode());
        return enrichirDTO(classeMapper.toDTO(classe));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getEffectifTotal(Long anneeScolaireId) {
        log.debug("Calcul de l'effectif total pour l'année scolaire ID: {}", anneeScolaireId);
        Integer effectif = classeRepository.getTotalEffectifByAnneeScolaire(anneeScolaireId);
        return effectif != null ? effectif : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estPleine(Long id) {
        Classe classe = classeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        return classe.estPleine();
    }

    private ClasseDTO enrichirDTO(ClasseDTO dto) {
        if (dto.getCapaciteMax() != null && dto.getEffectifActuel() != null) {
            dto.setPlacesDisponibles(dto.getCapaciteMax() - dto.getEffectifActuel());
            dto.setTauxRemplissage((double) dto.getEffectifActuel() / dto.getCapaciteMax() * 100);
        }
        return dto;
    }
}
