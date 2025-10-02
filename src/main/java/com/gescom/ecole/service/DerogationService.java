package com.gescom.ecole.service;

import com.gescom.ecole.common.enums.StatutDerogation;
import com.gescom.ecole.dto.finance.DerogationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DerogationService {
    
    DerogationDTO create(DerogationDTO derogationDTO);
    
    DerogationDTO update(Long id, DerogationDTO derogationDTO);
    
    DerogationDTO findById(Long id);
    
    DerogationDTO findByNumero(String numeroDerogation);
    
    Page<DerogationDTO> findAll(Pageable pageable);
    
    List<DerogationDTO> findByEleveId(Long eleveId);
    
    List<DerogationDTO> findByParentId(Long parentId);
    
    Page<DerogationDTO> findByStatut(StatutDerogation statut, Pageable pageable);
    
    void delete(Long id);
    
    String generateNumeroDerogation();
    
    DerogationDTO approuverDerogation(Long id, String decidePar);
    
    DerogationDTO rejeterDerogation(Long id, String decidePar, String motifRejet);
    
    Long countByStatut(StatutDerogation statut);
}
