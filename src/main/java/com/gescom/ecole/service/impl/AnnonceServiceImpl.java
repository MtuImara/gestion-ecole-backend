package com.gescom.ecole.service.impl;

import com.gescom.ecole.common.enums.TypeAnnonce;
import com.gescom.ecole.dto.communication.AnnonceDTO;
import com.gescom.ecole.service.AnnonceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class AnnonceServiceImpl implements AnnonceService {

    @Override
    public AnnonceDTO create(AnnonceDTO annonceDTO, List<MultipartFile> piecesJointes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO update(Long id, AnnonceDTO annonceDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<AnnonceDTO> getAnnoncesActives(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<AnnonceDTO> getAnnoncesEpinglees(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<AnnonceDTO> getAnnoncesParType(TypeAnnonce type, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<AnnonceDTO> getAnnoncesParAuteur(Long auteurId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<AnnonceDTO> getAnnoncesPourDestinataire(String destinataire, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public AnnonceDTO epinglerAnnonce(Long annonceId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO desepinglerAnnonce(Long annonceId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO activerAnnonce(Long annonceId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO desactiverAnnonce(Long annonceId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void incrementerVues(Long annonceId) {
        log.debug("Incrémenter vues pour annonce: {}", annonceId);
    }

    @Override
    public AnnonceDTO programmerAnnonce(AnnonceDTO annonceDTO, LocalDateTime datePublication) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<AnnonceDTO> getAnnoncesFutures() {
        return new ArrayList<>();
    }

    @Override
    public AnnonceDTO modifierProgrammation(Long annonceId, LocalDateTime nouvelleDatePublication) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<AnnonceDTO> searchAnnonces(String recherche, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Long countAnnoncesActives() {
        return 0L;
    }

    @Override
    public List<AnnonceDTO> getTop10AnnoncesVues() {
        return new ArrayList<>();
    }

    @Override
    public int desactiverAnnoncesExpirees() {
        return 0;
    }

    @Override
    public void archiverAnnoncesAnciennes(Integer joursConservation) {
        log.info("Archiver annonces anciennes: jours={}", joursConservation);
    }

    @Override
    public AnnonceDTO ajouterPieceJointe(Long annonceId, MultipartFile file) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void supprimerPieceJointe(Long annonceId, Long pieceJointeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] telechargerPieceJointe(Long pieceJointeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO publierPourClasse(AnnonceDTO annonceDTO, Long classeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO publierPourNiveau(AnnonceDTO annonceDTO, Long niveauId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO publierPourParents(AnnonceDTO annonceDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO publierPourEnseignants(AnnonceDTO annonceDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO publierPourTous(AnnonceDTO annonceDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO validerAnnonce(Long annonceId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AnnonceDTO rejeterAnnonce(Long annonceId, String motif) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<AnnonceDTO> getAnnoncesEnAttente(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }
}
