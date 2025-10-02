package com.gescom.ecole.service.impl;

import com.gescom.ecole.service.EmailService;
import com.gescom.ecole.service.PaiementService;
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
    private final PaiementService paiementService;
    
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
        try {
            // Récupérer le PDF du reçu
            byte[] recuPdf = paiementService.genererRecu(paiementId);
            
            String subject = "Reçu de Paiement - EcoleGest";
            String text = "Bonjour,\n\n" +
                         "Veuillez trouver ci-joint votre reçu de paiement.\n\n" +
                         "Vous pouvez également le consulter en vous connectant à votre espace.\n\n" +
                         "Cordialement,\n" +
                         "L'équipe EcoleGest";
            
            String filename = "recu-paiement-" + paiementId + ".pdf";
            
            sendEmailWithAttachment(emailDestinataire, subject, text, recuPdf, filename);
            log.info("Reçu PDF envoyé par email pour paiement ID: {}", paiementId);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du reçu par email pour paiement ID: {}", paiementId, e);
            throw new RuntimeException("Erreur envoi reçu par email", e);
        }
    }
}
