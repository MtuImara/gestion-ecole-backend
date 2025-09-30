package com.gescom.ecole.mapper;

import com.gescom.ecole.dto.utilisateur.UtilisateurDTO;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UtilisateurMapper {
    
    public UtilisateurDTO toDTO(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }
        
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setUsername(utilisateur.getUsername());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setTelephoneSecondaire(utilisateur.getTelephoneSecondaire());
        dto.setPhotoUrl(utilisateur.getPhotoUrl());
        dto.setActif(utilisateur.getActif());
        dto.setDerniereConnexion(utilisateur.getDerniereConnexion());
        
        // Type de l'utilisateur (PARENT, ENSEIGNANT, etc.)
        dto.setType(getUtilisateurType(utilisateur));
        
        // Password n'est jamais exposé dans le DTO
        
        return dto;
    }
    
    public Utilisateur toEntity(UtilisateurDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setUsername(dto.getUsername());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setTelephoneSecondaire(dto.getTelephoneSecondaire());
        utilisateur.setPhotoUrl(dto.getPhotoUrl());
        utilisateur.setActif(dto.getActif());
        
        // Les relations et champs de sécurité sont gérés par le service
        
        return utilisateur;
    }
    
    public List<UtilisateurDTO> toDTOList(List<Utilisateur> utilisateurs) {
        if (utilisateurs == null) {
            return new ArrayList<>();
        }
        return utilisateurs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<Utilisateur> toEntityList(List<UtilisateurDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public void updateEntityFromDTO(UtilisateurDTO dto, Utilisateur utilisateur) {
        if (dto == null || utilisateur == null) {
            return;
        }
        
        if (dto.getUsername() != null) {
            utilisateur.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            utilisateur.setEmail(dto.getEmail());
        }
        if (dto.getTelephone() != null) {
            utilisateur.setTelephone(dto.getTelephone());
        }
        if (dto.getTelephoneSecondaire() != null) {
            utilisateur.setTelephoneSecondaire(dto.getTelephoneSecondaire());
        }
        if (dto.getPhotoUrl() != null) {
            utilisateur.setPhotoUrl(dto.getPhotoUrl());
        }
        if (dto.getActif() != null) {
            utilisateur.setActif(dto.getActif());
        }
        
        // Password et roles ne sont jamais mis à jour via cette méthode
    }
    
    private String getUtilisateurType(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }
        return utilisateur.getClass().getSimpleName().toUpperCase();
    }
}
