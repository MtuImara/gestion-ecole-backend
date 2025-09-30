package com.gescom.ecole.service;

import com.gescom.ecole.dto.finance.PaiementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaiementService {
    PaiementDTO create(PaiementDTO paiementDTO);
    PaiementDTO update(Long id, PaiementDTO paiementDTO);
    PaiementDTO findById(Long id);
    PaiementDTO findByNumeroPaiement(String numeroPaiement);
    Page<PaiementDTO> findAll(Pageable pageable);
    List<PaiementDTO> findByFactureId(Long factureId);
    List<PaiementDTO> findByParentId(Long parentId);
    List<PaiementDTO> findByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin);
    void delete(Long id);
    String generateNumeroPaiement();
    PaiementDTO validerPaiement(Long id);
    PaiementDTO annulerPaiement(Long id);
    byte[] genererRecu(Long id);
    BigDecimal getTotalPaiementsByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin);
}
