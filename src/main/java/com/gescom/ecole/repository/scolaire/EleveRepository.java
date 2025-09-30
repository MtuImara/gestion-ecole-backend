package com.gescom.ecole.repository.scolaire;

import com.gescom.ecole.common.enums.StatutEleve;
import com.gescom.ecole.entity.scolaire.Eleve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long>, JpaSpecificationExecutor<Eleve> {
    
    Optional<Eleve> findByMatricule(String matricule);
    boolean existsByMatricule(String matricule);
    
    List<Eleve> findByStatut(StatutEleve statut);
    
    List<Eleve> findByClasseId(Long classeId);
    
    // Recherche par parent
    @Query("SELECT e FROM Eleve e JOIN e.parents p WHERE p.id = :parentId")
    List<Eleve> findByParentId(@Param("parentId") Long parentId);
    
    // Méthodes pour les statistiques
    Long countByStatut(String statut);
    
    @Query("SELECT COUNT(e) FROM Eleve e WHERE e.classe.anneeScolaire.id = :anneeScolaireId")
    Long countByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT COUNT(e) FROM Eleve e WHERE e.classe.anneeScolaire.id = :anneeScolaireId AND e.statut = :statut")
    Long countByAnneeScolaireIdAndStatut(@Param("anneeScolaireId") Long anneeScolaireId, @Param("statut") String statut);
    
    Long countByClasseId(Long classeId);
    
    @Query("SELECT COUNT(e) FROM Eleve e WHERE e.classe.anneeScolaire.id = :anneeScolaireId AND e.genre = :genre")
    Long countByAnneeScolaireIdAndGenre(@Param("anneeScolaireId") Long anneeScolaireId, @Param("genre") String genre);
    
    @Query("SELECT COUNT(e) FROM Eleve e WHERE e.classe.anneeScolaire.id = :anneeScolaireId AND e.boursier = true")
    Long countBoursiers(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT e FROM Eleve e WHERE e.boursier = true")
    List<Eleve> findBoursiers();
    
    @Query("SELECT e FROM Eleve e WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Eleve> searchEleves(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM Eleve e WHERE e.statut = :statut")
    long countByStatut(@Param("statut") StatutEleve statut);
    
    @Query("SELECT e FROM Eleve e WHERE e.classe.anneeScolaire.id = :anneeScolaireId")
    List<Eleve> findByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT e FROM Eleve e WHERE e.classe.niveau.id = :niveauId")
    List<Eleve> findByNiveauId(@Param("niveauId") Long niveauId);
}
