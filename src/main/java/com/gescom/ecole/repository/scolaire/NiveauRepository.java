package com.gescom.ecole.repository.scolaire;

import com.gescom.ecole.common.enums.TypeNiveau;
import com.gescom.ecole.entity.scolaire.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau, Long> {
    
    Optional<Niveau> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Niveau> findByTypeNiveau(TypeNiveau typeNiveau);
    
    List<Niveau> findByActif(Boolean actif);
    
    List<Niveau> findByTypeNiveauOrderByOrdre(TypeNiveau typeNiveau);
    
    @Query("SELECT n FROM Niveau n WHERE n.actif = true ORDER BY n.ordre")
    List<Niveau> findAllActiveOrderByOrdre();
    
    @Query("SELECT COUNT(DISTINCT e) FROM Niveau n JOIN n.classes c JOIN c.eleves e WHERE n.id = :niveauId")
    long countElevesByNiveauId(@Param("niveauId") Long niveauId);
}
