package com.gescom.ecole.entity.communication;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_lectures", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "utilisateur_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"message", "utilisateur"})
public class MessageLecture extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_lecture", nullable = false)
    private LocalDateTime dateLecture;

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    @Column(name = "archive", nullable = false)
    private Boolean archive = false;

    @Column(name = "corbeille", nullable = false)
    private Boolean corbeille = false;

    @Column(name = "favori", nullable = false)
    private Boolean favori = false;
}
