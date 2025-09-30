package com.gescom.ecole.service;

import com.gescom.ecole.dto.finance.FactureDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FactureService {
    FactureDTO create(FactureDTO factureDTO);
    FactureDTO update(Long id, FactureDTO factureDTO);
    FactureDTO findById(Long id);
    FactureDTO findByNumeroFacture(String numeroFacture);
    Page<FactureDTO> findAll(Pageable pageable);
    List<FactureDTO> findByEleveId(Long eleveId);
    List<FactureDTO> findFacturesImpayees(Long eleveId);
    List<FactureDTO> findFacturesEchues();
    void delete(Long id);
    String generateNumeroFacture();
    FactureDTO validerFacture(Long id);
    FactureDTO annulerFacture(Long id);
    void envoyerRappel(Long id);
    BigDecimal calculerMontantTotal(Long eleveId);
    BigDecimal calculerMontantPaye(Long eleveId);
    BigDecimal calculerMontantRestant(Long eleveId);
}
