package com.gescom.ecole.service;

import com.gescom.ecole.dto.communication.MessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {
    
    // CRUD de base
    MessageDTO create(MessageDTO messageDTO, List<MultipartFile> piecesJointes);
    MessageDTO findById(Long id);
    MessageDTO update(Long id, MessageDTO messageDTO);
    void delete(Long id);
    
    // Messages envoyés
    Page<MessageDTO> getMessagesEnvoyes(Long userId, Pageable pageable);
    
    // Messages reçus
    Page<MessageDTO> getMessagesRecus(Long userId, Pageable pageable);
    Page<MessageDTO> getMessagesNonLus(Long userId, Pageable pageable);
    Page<MessageDTO> getMessagesImportants(Long userId, Pageable pageable);
    
    // Brouillons
    Page<MessageDTO> getBrouillons(Long userId, Pageable pageable);
    MessageDTO saveBrouillon(MessageDTO messageDTO);
    MessageDTO envoyerBrouillon(Long brouillonId);
    
    // Gestion des messages
    MessageDTO marquerCommeLu(Long messageId, Long userId);
    MessageDTO marquerCommeNonLu(Long messageId, Long userId);
    MessageDTO archiverMessage(Long messageId, Long userId);
    MessageDTO desarchiverMessage(Long messageId, Long userId);
    MessageDTO mettreEnFavori(Long messageId, Long userId);
    MessageDTO retirerDesFavoris(Long messageId, Long userId);
    MessageDTO mettreALaCorbeille(Long messageId, Long userId);
    MessageDTO restaurerDeLaCorbeille(Long messageId, Long userId);
    
    // Archives et corbeille
    Page<MessageDTO> getMessagesArchives(Long userId, Pageable pageable);
    Page<MessageDTO> getMessagesCorbeille(Long userId, Pageable pageable);
    void viderCorbeille(Long userId);
    
    // Recherche
    Page<MessageDTO> searchMessages(Long userId, String recherche, Pageable pageable);
    
    // Statistiques
    Long countMessagesNonLus(Long userId);
    
    // Pièces jointes
    MessageDTO ajouterPieceJointe(Long messageId, MultipartFile file);
    void supprimerPieceJointe(Long messageId, Long pieceJointeId);
    byte[] telechargerPieceJointe(Long pieceJointeId);
    
    // Messages groupés
    MessageDTO envoyerMessageGroupe(MessageDTO messageDTO, List<Long> groupIds);
    MessageDTO envoyerMessageATous(MessageDTO messageDTO, String typeDestinataire);
}
