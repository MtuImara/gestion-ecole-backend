package com.gescom.ecole.service;

public interface EmailService {
    
    /**
     * Envoie un email simple
     */
    void sendSimpleMessage(String to, String subject, String text);
    
    /**
     * Envoie un email avec une pièce jointe
     */
    void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String filename);
    
    /**
     * Envoie le reçu de paiement par email
     * @deprecated Utiliser sendRecuEmail à la place pour éviter la dépendance circulaire
     */
    @Deprecated
    void envoyerRecuParEmail(String emailDestinataire, Long paiementId);
    
    /**
     * Envoie le reçu de paiement par email avec le PDF déjà généré
     * @param emailDestinataire Email du destinataire
     * @param recuPdf Le PDF du reçu déjà généré
     * @param paiementId L'ID du paiement pour le nom du fichier
     */
    void sendRecuEmail(String emailDestinataire, byte[] recuPdf, Long paiementId);
}
