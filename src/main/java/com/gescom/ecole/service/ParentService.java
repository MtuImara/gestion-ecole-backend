package com.gescom.ecole.service;

import com.gescom.ecole.dto.utilisateur.ParentDTO;
import com.gescom.ecole.common.enums.TypeParent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ParentService {
    
    ParentDTO create(ParentDTO parentDTO);
    
    ParentDTO update(Long id, ParentDTO parentDTO);
    
    ParentDTO findById(Long id);
    
    ParentDTO findByEmail(String email);
    
    ParentDTO findByCin(String cin);
    
    void delete(Long id);
    
    Page<ParentDTO> findAll(Pageable pageable);
    
    List<ParentDTO> findAll();
    
    Page<ParentDTO> search(String searchTerm, Pageable pageable);
    
    List<ParentDTO> searchSimple(String searchTerm);
    
    List<ParentDTO> findByTypeParent(TypeParent typeParent);
    
    List<ParentDTO> findByEleveId(Long eleveId);
    
    boolean existsByEmail(String email);
    
    boolean existsByCin(String cin);
    
    long count();
    
    void addEnfantToParent(Long parentId, Long eleveId);
    
    void removeEnfantFromParent(Long parentId, Long eleveId);
    
    String generateNumeroParent();
    
    Map<String, Object> getSituationFinanciere(Long parentId);
}
