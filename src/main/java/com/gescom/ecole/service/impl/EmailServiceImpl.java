package com.gescom.ecole.service.impl;

import com.gescom.ecole.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@ecolegest.com}")
    private String fromEmail;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email simple envoyé à: {}", to);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi d'email à: {}", to, e);
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String filename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            
            if (attachment != null && attachment.length > 0) {
                helper.addAttachment(filename, new ByteArrayResource(attachment));
            }
            
            mailSender.send(message);
            log.info("Email avec pièce jointe envoyé à: {}", to);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi d'email avec pièce jointe à: {}", to, e);
            throw new RuntimeException("Erreur envoi email avec pièce jointe", e);
        }
    }

    @Override
    public void envoyerRecuParEmail(String emailDestinataire, Long paiementId) {
        // Cette méthode est maintenant dépréciée - utiliser sendRecuEmail à la place
        log.warn("Méthode envoyerRecuParEmail dépréciée - utiliser sendRecuEmail");
        throw new UnsupportedOperationException("Utiliser sendRecuEmail(email, recuPdf, paiementId) à la place");
    }
    
    @Override
    public void sendRecuEmail(String emailDestinataire, byte[] recuPdf, Long paiementId) {
        try {
            String subject = "Reçu de Paiement - EcoleGest";
            String text = "Bonjour,\n\n" +
                         "Veuillez trouver ci-joint votre reçu de paiement.\n\n" +
                         "Cordialement,\n" +
                         "L'équipe EcoleGest";
            
            String filename = String.format("recu_paiement_%d.pdf", paiementId);
            sendEmailWithAttachment(emailDestinataire, subject, text, recuPdf, filename);
            
            log.info("Reçu de paiement {} envoyé par email à: {}", paiementId, emailDestinataire);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du reçu {} par email à: {}", paiementId, emailDestinataire, e);
            throw new RuntimeException("Erreur envoi reçu par email", e);
        }
    }
}
