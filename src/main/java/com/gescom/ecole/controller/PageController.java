package com.gescom.ecole.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour servir les pages HTML statiques
 * Note: /login et /dashboard sont déjà gérés par HomeController
 */
@Controller
public class PageController {
    
    @GetMapping("/eleves")
    public String elevesPage() {
        return "forward:/eleves.html";
    }
    
    @GetMapping("/classes")
    public String classesPage() {
        return "forward:/classes.html";
    }
    
    @GetMapping("/paiements")
    public String paiementsPage() {
        return "forward:/paiements.html";
    }
    
    @GetMapping("/communication")
    public String communicationPage() {
        return "forward:/communication.html";
    }
    
    @GetMapping("/enseignants")
    public String enseignantsPage() {
        return "forward:/enseignants.html";
    }
    
    @GetMapping("/parents")
    public String parentsPage() {
        return "forward:/parents.html";
    }
    
    @GetMapping("/rapports")
    public String rapportsPage() {
        return "forward:/rapports.html";
    }
    
    @GetMapping("/parametres")
    public String parametresPage() {
        return "forward:/parametres.html";
    }
    
    @GetMapping("/factures")
    public String facturesPage() {
        return "forward:/factures.html";
    }
    
    @GetMapping("/recus")
    public String recusPage() {
        return "forward:/recus.html";
    }
    
    @GetMapping("/eleve-detail")
    public String eleveDetailPage() {
        return "forward:/eleve-detail.html";
    }
    
    @GetMapping("/validation-paiements")
    public String validationPaiementsPage() {
        return "forward:/validation-paiements.html";
    }
    
    @GetMapping("/derogations")
    public String derogationsPage() {
        return "forward:/derogations.html";
    }
}
