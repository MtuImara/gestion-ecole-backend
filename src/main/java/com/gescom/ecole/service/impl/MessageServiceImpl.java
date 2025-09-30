package com.gescom.ecole.service.impl;

import com.gescom.ecole.dto.communication.MessageDTO;
import com.gescom.ecole.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class MessageServiceImpl implements MessageService {

    @Override
    public MessageDTO create(MessageDTO messageDTO, List<MultipartFile> piecesJointes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO update(Long id, MessageDTO messageDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<MessageDTO> getMessagesEnvoyes(Long userId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<MessageDTO> getMessagesRecus(Long userId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<MessageDTO> getMessagesNonLus(Long userId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<MessageDTO> getMessagesImportants(Long userId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<MessageDTO> getBrouillons(Long userId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public MessageDTO saveBrouillon(MessageDTO messageDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO envoyerBrouillon(Long brouillonId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO marquerCommeLu(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO marquerCommeNonLu(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO archiverMessage(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO desarchiverMessage(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO mettreEnFavori(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO retirerDesFavoris(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO mettreALaCorbeille(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO restaurerDeLaCorbeille(Long messageId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<MessageDTO> getMessagesArchives(Long userId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<MessageDTO> getMessagesCorbeille(Long userId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public void viderCorbeille(Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<MessageDTO> searchMessages(Long userId, String recherche, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Long countMessagesNonLus(Long userId) {
        return 0L;
    }

    @Override
    public MessageDTO ajouterPieceJointe(Long messageId, MultipartFile file) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void supprimerPieceJointe(Long messageId, Long pieceJointeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] telechargerPieceJointe(Long pieceJointeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO envoyerMessageGroupe(MessageDTO messageDTO, List<Long> groupIds) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MessageDTO envoyerMessageATous(MessageDTO messageDTO, String typeDestinataire) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
