package com.gescom.ecole.service;

import com.gescom.ecole.dto.scolaire.EleveDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EleveService {
    EleveDTO create(EleveDTO eleveDTO);
    EleveDTO update(Long id, EleveDTO eleveDTO);
    EleveDTO findById(Long id);
    EleveDTO findByMatricule(String matricule);
    Page<EleveDTO> findAll(Pageable pageable);
    Page<EleveDTO> search(String search, Pageable pageable);
    List<EleveDTO> findByClasseId(Long classeId);
    List<EleveDTO> findByParentId(Long parentId);
    void delete(Long id);
    String generateMatricule();
    EleveDTO inscrireEleve(Long eleveId, Long classeId);
    EleveDTO transfererEleve(Long eleveId, Long nouvelleClasseId);
}
