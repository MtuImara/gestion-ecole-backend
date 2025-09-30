package com.gescom.ecole.service;

import com.gescom.ecole.common.enums.TypeAnnonce;
import com.gescom.ecole.dto.communication.AnnonceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnonceService {
    
    // CRUD de base
    AnnonceDTO create(AnnonceDTO annonceDTO, List<MultipartFile> piecesJointes);
    AnnonceDTO findById(Long id);
    AnnonceDTO update(Long id, AnnonceDTO annonceDTO);
    void delete(Long id);
    
    // Récupération des annonces
    Page<AnnonceDTO> getAnnoncesActives(Pageable pageable);
    Page<AnnonceDTO> getAnnoncesEpinglees(Pageable pageable);
    Page<AnnonceDTO> getAnnoncesParType(TypeAnnonce type, Pageable pageable);
    Page<AnnonceDTO> getAnnoncesParAuteur(Long auteurId, Pageable pageable);
    Page<AnnonceDTO> getAnnoncesPourDestinataire(String destinataire, Pageable pageable);
    
    // Gestion des annonces
    AnnonceDTO epinglerAnnonce(Long annonceId);
    AnnonceDTO desepinglerAnnonce(Long annonceId);
    AnnonceDTO activerAnnonce(Long annonceId);
    AnnonceDTO desactiverAnnonce(Long annonceId);
    void incrementerVues(Long annonceId);
    
    // Programmation
    AnnonceDTO programmerAnnonce(AnnonceDTO annonceDTO, LocalDateTime datePublication);
    List<AnnonceDTO> getAnnoncesFutures();
    AnnonceDTO modifierProgrammation(Long annonceId, LocalDateTime nouvelleDatePublication);
    
    // Recherche
    Page<AnnonceDTO> searchAnnonces(String recherche, Pageable pageable);
    
    // Statistiques
    Long countAnnoncesActives();
    List<AnnonceDTO> getTop10AnnoncesVues();
    
    // Maintenance
    int desactiverAnnoncesExpirees();
    void archiverAnnoncesAnciennes(Integer joursConservation);
    
    // Pièces jointes
    AnnonceDTO ajouterPieceJointe(Long annonceId, MultipartFile file);
    void supprimerPieceJointe(Long annonceId, Long pieceJointeId);
    byte[] telechargerPieceJointe(Long pieceJointeId);
    
    // Diffusion ciblée
    AnnonceDTO publierPourClasse(AnnonceDTO annonceDTO, Long classeId);
    AnnonceDTO publierPourNiveau(AnnonceDTO annonceDTO, Long niveauId);
    AnnonceDTO publierPourParents(AnnonceDTO annonceDTO);
    AnnonceDTO publierPourEnseignants(AnnonceDTO annonceDTO);
    AnnonceDTO publierPourTous(AnnonceDTO annonceDTO);
    
    // Validation et modération
    AnnonceDTO validerAnnonce(Long annonceId);
    AnnonceDTO rejeterAnnonce(Long annonceId, String motif);
    Page<AnnonceDTO> getAnnoncesEnAttente(Pageable pageable);
}
