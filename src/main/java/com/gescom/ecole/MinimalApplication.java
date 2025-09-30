package com.gescom.ecole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {
    "com.gescom.ecole.entity.communication",
    "com.gescom.ecole.entity.reporting"
})
@EnableJpaRepositories(basePackages = {
    "com.gescom.ecole.repository.communication",
    "com.gescom.ecole.repository.reporting"
})
@ComponentScan(
    basePackages = {
        "com.gescom.ecole.controller",
        "com.gescom.ecole.service.impl.communication",
        "com.gescom.ecole.service.impl.reporting"
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*EleveServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*FactureServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*PaiementServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*AuthServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ClasseServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*DataInitializer"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*SecurityConfig"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*JwtTokenProvider"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*JwtAuthenticationFilter")
    }
)
@RestController
@RequestMapping("/api")
public class MinimalApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev");
        System.setProperty("server.port", "8080");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        System.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb");
        System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
        System.setProperty("spring.h2.console.enabled", "true");
        System.setProperty("spring.jpa.show-sql", "false");
        System.setProperty("logging.level.root", "WARN");
        System.setProperty("logging.level.com.gescom.ecole", "INFO");
        
        SpringApplication app = new SpringApplication(MinimalApplication.class);
        app.run(args);
        
        System.out.println("\n========================================");
        System.out.println("✅ Backend démarré avec succès!");
        System.out.println("========================================");
        System.out.println("📍 URL de base: http://localhost:8080/api");
        System.out.println("📊 Endpoints disponibles:");
        System.out.println("   - GET  /api/status");
        System.out.println("   - GET  /api/info");
        System.out.println("   - GET  /api/modules");
        System.out.println("🗄️ H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());
        status.put("application", "Gestion École Backend");
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Gestion École - Système de Gestion Scolaire");
        info.put("description", "API REST pour la gestion complète d'un établissement scolaire");
        info.put("modules", Arrays.asList("Communication", "Reporting", "Dashboard", "Statistiques"));
        info.put("features", Map.of(
            "communication", "Messages, Notifications, Annonces",
            "reporting", "Rapports, Statistiques, Tableaux de bord",
            "finance", "Factures, Paiements, Bourses (en développement)",
            "scolaire", "Élèves, Classes, Inscriptions (en développement)"
        ));
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/modules")
    public ResponseEntity<List<ModuleInfo>> getModules() {
        List<ModuleInfo> modules = Arrays.asList(
            new ModuleInfo("Communication", "Actif", "Gestion des messages, notifications et annonces"),
            new ModuleInfo("Reporting", "Actif", "Génération de rapports et statistiques"),
            new ModuleInfo("Dashboard", "Actif", "Tableaux de bord et visualisations"),
            new ModuleInfo("Finance", "En développement", "Gestion financière et comptable"),
            new ModuleInfo("Scolaire", "En développement", "Gestion des élèves et classes")
        );
        return ResponseEntity.ok(modules);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
    
    static class ModuleInfo {
        public String name;
        public String status;
        public String description;
        
        public ModuleInfo(String name, String status, String description) {
            this.name = name;
            this.status = status;
            this.description = description;
        }
    }
}
