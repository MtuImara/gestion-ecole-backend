package com.gescom.ecole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = "com.gescom.ecole",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gescom.ecole.service.impl.EleveServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gescom.ecole.config.DataInitializer"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gescom.ecole.service.impl.FactureServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gescom.ecole.service.impl.PaiementServiceImpl"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gescom.ecole.service.impl.AuthServiceImpl")
    }
)
public class GestionEcoleBackendApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev");
        System.setProperty("server.error.whitelabel.enabled", "false");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        
        try {
            SpringApplication.run(GestionEcoleBackendApplication.class, args);
            System.out.println("\n========================================");
            System.out.println("Application démarrée avec succès!");
            System.out.println("URL: http://localhost:8080/api");
            System.out.println("Swagger: http://localhost:8080/api/swagger-ui.html");
            System.out.println("========================================\n");
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
