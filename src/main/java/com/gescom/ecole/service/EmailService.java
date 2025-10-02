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
     */
    void envoyerRecuParEmail(String emailDestinataire, Long paiementId);
}
