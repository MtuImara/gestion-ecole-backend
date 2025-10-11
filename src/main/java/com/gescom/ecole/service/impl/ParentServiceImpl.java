package com.gescom.ecole.service.impl;

import com.gescom.ecole.common.enums.TypeParent;
import com.gescom.ecole.dto.utilisateur.ParentDTO;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.exception.ResourceNotFoundException;
import com.gescom.ecole.exception.BusinessException;
import com.gescom.ecole.mapper.ParentMapper;
import com.gescom.ecole.entity.utilisateur.Role;
import com.gescom.ecole.repository.scolaire.EleveRepository;
import com.gescom.ecole.repository.utilisateur.ParentRepository;
import com.gescom.ecole.repository.utilisateur.RoleRepository;
import com.gescom.ecole.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParentServiceImpl implements ParentService {
    
    private final ParentRepository parentRepository;
    private final EleveRepository eleveRepository;
    private final RoleRepository roleRepository;
    private final ParentMapper parentMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public ParentDTO create(ParentDTO parentDTO) {
        log.info("Création d'un nouveau parent: {} {}", parentDTO.getPrenom(), parentDTO.getNom());
        
        // Vérifier l'unicité de l'email
        if (parentDTO.getEmail() != null && parentRepository.existsByEmail(parentDTO.getEmail())) {
            throw new BusinessException("Un parent avec cet email existe déjà");
        }
        
        // Vérifier l'unicité du CIN
        if (parentDTO.getCin() != null && parentRepository.existsByCin(parentDTO.getCin())) {
            throw new BusinessException("Un parent avec ce CIN existe déjà");
        }
        
        Parent parent = parentMapper.toEntity(parentDTO);
        
        // Générer le numéro parent si non fourni
        if (parent.getNumeroParent() == null) {
            parent.setNumeroParent(generateNumeroParent());
        }
        
        // Générer un username basé sur l'email ou le nom
        if (parent.getUsername() == null) {
            if (parentDTO.getEmail() != null && !parentDTO.getEmail().isEmpty()) {
                parent.setUsername(parentDTO.getEmail());
            } else {
                // Générer un username unique basé sur le nom et prénom
                String baseUsername = (parentDTO.getPrenom() + "." + parentDTO.getNom()).toLowerCase()
                        .replaceAll("[^a-z0-9.]", "");
                parent.setUsername(baseUsername + "." + System.currentTimeMillis());
            }
        }
        
        // Définir le mot de passe par défaut (à changer lors de la première connexion)
        if (parent.getPassword() == null) {
            String defaultPassword = "Parent@" + LocalDateTime.now().getYear();
            parent.setPassword(passwordEncoder.encode(defaultPassword));
        }
        
        // Définir les dates
        parent.setDateCreation(LocalDateTime.now());
        parent.setDateModification(LocalDateTime.now());
        parent.setActif(true);
        
        // Ajouter le rôle PARENT
        Role roleParent = roleRepository.findByCode("ROLE_PARENT")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setCode("ROLE_PARENT");
                    newRole.setDesignation("Parent");
                    newRole.setDescription("Rôle pour les parents d'élèves");
                    newRole.setActif(true);
                    return roleRepository.save(newRole);
                });
        parent.getRoles().add(roleParent);
        
        // Gérer les enfants si fournis
        if (parentDTO.getEnfantIds() != null && !parentDTO.getEnfantIds().isEmpty()) {
            Set<Eleve> enfants = new HashSet<>();
            for (Long eleveId : parentDTO.getEnfantIds()) {
                Eleve eleve = eleveRepository.findById(eleveId)
                        .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé avec l'ID: " + eleveId));
                enfants.add(eleve);
                eleve.getParents().add(parent);
            }
            parent.setEnfants(enfants);
        }
        
        Parent savedParent = parentRepository.save(parent);
        log.info("Parent créé avec succès avec l'ID: {}", savedParent.getId());
        
        return parentMapper.toDTO(savedParent);
    }
    
    @Override
    public ParentDTO update(Long id, ParentDTO parentDTO) {
        log.info("Mise à jour du parent avec l'ID: {}", id);
        
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec l'ID: " + id));
        
        // Vérifier l'unicité de l'email si modifié
        if (parentDTO.getEmail() != null && !parentDTO.getEmail().equals(parent.getEmail())) {
            if (parentRepository.existsByEmail(parentDTO.getEmail())) {
                throw new BusinessException("Un parent avec cet email existe déjà");
            }
        }
        
        // Vérifier l'unicité du CIN si modifié
        if (parentDTO.getCin() != null && !parentDTO.getCin().equals(parent.getCin())) {
            if (parentRepository.existsByCin(parentDTO.getCin())) {
                throw new BusinessException("Un parent avec ce CIN existe déjà");
            }
        }
        
        parentMapper.updateEntityFromDTO(parent, parentDTO);
        parent.setDateModification(LocalDateTime.now());
        
        // Gérer les enfants si fournis
        if (parentDTO.getEnfantIds() != null) {
            Set<Eleve> newEnfants = new HashSet<>();
            for (Long eleveId : parentDTO.getEnfantIds()) {
                Eleve eleve = eleveRepository.findById(eleveId)
                        .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé avec l'ID: " + eleveId));
                newEnfants.add(eleve);
            }
            
            // Retirer le parent des anciens enfants
            for (Eleve oldEnfant : parent.getEnfants()) {
                if (!newEnfants.contains(oldEnfant)) {
                    oldEnfant.getParents().remove(parent);
                }
            }
            
            // Ajouter le parent aux nouveaux enfants
            for (Eleve newEnfant : newEnfants) {
                if (!parent.getEnfants().contains(newEnfant)) {
                    newEnfant.getParents().add(parent);
                }
            }
            
            parent.setEnfants(newEnfants);
        }
        
        Parent updatedParent = parentRepository.save(parent);
        log.info("Parent mis à jour avec succès");
        
        return parentMapper.toDTO(updatedParent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ParentDTO findById(Long id) {
        log.debug("Recherche du parent avec l'ID: {}", id);
        Parent parent = parentRepository.findByIdWithEnfants(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec l'ID: " + id));
        return parentMapper.toDTO(parent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ParentDTO findByEmail(String email) {
        log.debug("Recherche du parent avec l'email: {}", email);
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec l'email: " + email));
        return parentMapper.toDTO(parent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ParentDTO findByCin(String cin) {
        log.debug("Recherche du parent avec le CIN: {}", cin);
        Parent parent = parentRepository.findByCin(cin)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec le CIN: " + cin));
        return parentMapper.toDTO(parent);
    }
    
    @Override
    public void delete(Long id) {
        log.info("Suppression du parent avec l'ID: {}", id);
        
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec l'ID: " + id));
        
        // Retirer le parent de tous ses enfants
        for (Eleve enfant : parent.getEnfants()) {
            enfant.getParents().remove(parent);
        }
        parent.getEnfants().clear();
        
        parentRepository.delete(parent);
        log.info("Parent supprimé avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParentDTO> findAll(Pageable pageable) {
        log.debug("Récupération de tous les parents avec pagination");
        Page<Parent> parents = parentRepository.findAll(pageable);
        return parents.map(parentMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ParentDTO> findAll() {
        log.debug("Récupération de tous les parents");
        List<Parent> parents = parentRepository.findAllWithEnfants();
        return parentMapper.toDTOList(parents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ParentDTO> search(String searchTerm, Pageable pageable) {
        log.debug("Recherche de parents avec le terme: {}", searchTerm);
        Page<Parent> parents = parentRepository.searchParents(searchTerm, pageable);
        return parents.map(parentMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ParentDTO> searchSimple(String searchTerm) {
        log.debug("Recherche simple de parents avec le terme: {}", searchTerm);
        List<Parent> parents = parentRepository.searchParentsSimple(searchTerm);
        return parentMapper.toDTOList(parents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ParentDTO> findByTypeParent(TypeParent typeParent) {
        log.debug("Recherche de parents par type: {}", typeParent);
        List<Parent> parents = parentRepository.findByTypeParent(typeParent);
        return parentMapper.toDTOList(parents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ParentDTO> findByEleveId(Long eleveId) {
        log.debug("Recherche des parents de l'élève avec l'ID: {}", eleveId);
        List<Parent> parents = parentRepository.findByEnfantId(eleveId);
        return parentMapper.toDTOList(parents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return parentRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCin(String cin) {
        return parentRepository.existsByCin(cin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return parentRepository.countAllParents();
    }
    
    @Override
    public void addEnfantToParent(Long parentId, Long eleveId) {
        log.info("Ajout de l'élève {} au parent {}", eleveId, parentId);
        
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec l'ID: " + parentId));
        
        Eleve eleve = eleveRepository.findById(eleveId)
                .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé avec l'ID: " + eleveId));
        
        parent.getEnfants().add(eleve);
        eleve.getParents().add(parent);
        
        parentRepository.save(parent);
        eleveRepository.save(eleve);
        
        log.info("Élève ajouté au parent avec succès");
    }
    
    @Override
    public void removeEnfantFromParent(Long parentId, Long eleveId) {
        log.info("Retrait de l'élève {} du parent {}", eleveId, parentId);
        
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec l'ID: " + parentId));
        
        Eleve eleve = eleveRepository.findById(eleveId)
                .orElseThrow(() -> new ResourceNotFoundException("Élève non trouvé avec l'ID: " + eleveId));
        
        parent.getEnfants().remove(eleve);
        eleve.getParents().remove(parent);
        
        parentRepository.save(parent);
        eleveRepository.save(eleve);
        
        log.info("Élève retiré du parent avec succès");
    }
    
    @Override
    public String generateNumeroParent() {
        String prefix = "PAR";
        String year = String.valueOf(LocalDateTime.now().getYear());
        
        // Récupérer le dernier numéro parent
        long count = parentRepository.count() + 1;
        String sequence = String.format("%04d", count);
        
        return prefix + year + sequence;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSituationFinanciere(Long parentId) {
        log.debug("Calcul de la situation financière pour le parent: {}", parentId);
        
        Parent parent = parentRepository.findByIdWithEnfants(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent non trouvé avec l'ID: " + parentId));
        
        Map<String, Object> situation = new HashMap<>();
        
        BigDecimal totalDu = BigDecimal.ZERO;
        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal totalRestant = BigDecimal.ZERO;
        
        // Calculer pour chaque enfant
        for (Eleve enfant : parent.getEnfants()) {
            if (enfant.getFactures() != null) {
                for (var facture : enfant.getFactures()) {
                    totalDu = totalDu.add(facture.getMontantTotal());
                    totalPaye = totalPaye.add(facture.getMontantPaye());
                    totalRestant = totalRestant.add(facture.getMontantRestant());
                }
            }
        }
        
        situation.put("parentId", parentId);
        situation.put("parentNom", parent.getNom() + " " + parent.getPrenom());
        situation.put("nombreEnfants", parent.getEnfants().size());
        situation.put("totalDu", totalDu);
        situation.put("totalPaye", totalPaye);
        situation.put("totalRestant", totalRestant);
        situation.put("tauxPaiement", totalDu.compareTo(BigDecimal.ZERO) > 0 
            ? totalPaye.multiply(BigDecimal.valueOf(100)).divide(totalDu, 2, BigDecimal.ROUND_HALF_UP) 
            : BigDecimal.ZERO);
        
        return situation;
    }
}
