package com.gescom.ecole.service;

import com.gescom.ecole.dto.scolaire.ClasseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClasseService {
    ClasseDTO create(ClasseDTO classeDTO);
    ClasseDTO update(Long id, ClasseDTO classeDTO);
    ClasseDTO findById(Long id);
    Page<ClasseDTO> findAll(Pageable pageable);
    List<ClasseDTO> findByNiveauId(Long niveauId);
    List<ClasseDTO> findByAnneeScolaireId(Long anneeScolaireId);
    List<ClasseDTO> findActiveClasses();
    List<ClasseDTO> findClassesWithAvailableSpace();
    void delete(Long id);
    ClasseDTO activerClasse(Long id);
    ClasseDTO desactiverClasse(Long id);
    Integer getEffectifTotal(Long anneeScolaireId);
    boolean estPleine(Long id);
}
