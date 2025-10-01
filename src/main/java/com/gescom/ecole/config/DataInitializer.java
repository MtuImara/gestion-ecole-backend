package com.gescom.ecole.config;

import com.gescom.ecole.common.enums.Genre;
import com.gescom.ecole.common.enums.StatutEleve;
import com.gescom.ecole.common.enums.TypeClasse;
import com.gescom.ecole.common.enums.TypeContrat;
import com.gescom.ecole.common.enums.TypeNiveau;
import com.gescom.ecole.common.enums.TypeParent;
import com.gescom.ecole.common.enums.TypePeriode;
import com.gescom.ecole.entity.scolaire.*;
import com.gescom.ecole.entity.utilisateur.*;
import com.gescom.ecole.repository.scolaire.*;
import com.gescom.ecole.repository.utilisateur.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(
            UtilisateurRepository utilisateurRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            AnneeScolaireRepository anneeScolaireRepository,
            NiveauRepository niveauRepository,
            ClasseRepository classeRepository,
            EleveRepository eleveRepository) {
        
        return args -> {
            log.info("Initialisation des données de test...");

            // Créer les permissions
            Permission p1 = createPermission(permissionRepository, "users.create", "Créer des utilisateurs", "USERS", "CREATE");
            Permission p2 = createPermission(permissionRepository, "users.read", "Lire les utilisateurs", "USERS", "READ");
            Permission p3 = createPermission(permissionRepository, "users.update", "Modifier les utilisateurs", "USERS", "UPDATE");
            Permission p4 = createPermission(permissionRepository, "users.delete", "Supprimer les utilisateurs", "USERS", "DELETE");
            Permission p5 = createPermission(permissionRepository, "finance.manage", "Gérer les finances", "FINANCE", "MANAGE");
            Permission p6 = createPermission(permissionRepository, "eleves.manage", "Gérer les élèves", "ELEVES", "MANAGE");

            // Créer les rôles
            Role adminRole = createRole(roleRepository, "ADMIN", "Administrateur", 
                Set.of(p1, p2, p3, p4, p5, p6));
            Role comptableRole = createRole(roleRepository, "COMPTABLE", "Comptable", 
                Set.of(p2, p5));
            Role secretaireRole = createRole(roleRepository, "SECRETAIRE", "Secrétaire", 
                Set.of(p2, p6));
            Role parentRole = createRole(roleRepository, "PARENT", "Parent", 
                Set.of(p2));
            Role enseignantRole = createRole(roleRepository, "ENSEIGNANT", "Enseignant", 
                Set.of(p2));

            // Créer les utilisateurs
            if (utilisateurRepository.count() == 0) {
                // Admin
                Utilisateur admin = new Utilisateur();
                admin.setUsername("admin");
                admin.setEmail("admin@ecole.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setTelephone("+237 6 00 00 00 00");
                admin.setActif(true);
                admin.setRoles(Set.of(adminRole));
                utilisateurRepository.save(admin);
                log.info("Admin créé : username=admin, password=admin123");

                // Comptable
                Utilisateur comptable = new Utilisateur();
                comptable.setUsername("comptable");
                comptable.setEmail("comptable@ecole.com");
                comptable.setPassword(passwordEncoder.encode("comptable123"));
                comptable.setTelephone("+237 6 11 11 11 11");
                comptable.setActif(true);
                comptable.setRoles(Set.of(comptableRole));
                utilisateurRepository.save(comptable);
                log.info("Comptable créé : username=comptable, password=comptable123");

                // Parent
                Parent parent = new Parent();
                parent.setUsername("parent1");
                parent.setEmail("parent@ecole.com");
                parent.setPassword(passwordEncoder.encode("parent123"));
                parent.setTelephone("+237 6 22 22 22 22");
                parent.setActif(true);
                parent.setRoles(Set.of(parentRole));
                parent.setNumeroParent("PAR-2024-001");
                parent.setProfession("Ingénieur");
                parent.setTypeParent(TypeParent.PERE);
                parent.setCin("123456789");
                utilisateurRepository.save(parent);
                log.info("Parent créé : username=parent1, password=parent123");

                // Enseignant
                Enseignant enseignant = new Enseignant();
                enseignant.setUsername("enseignant1");
                enseignant.setEmail("enseignant@ecole.com");
                enseignant.setPassword(passwordEncoder.encode("enseignant123"));
                enseignant.setTelephone("+237 6 33 33 33 33");
                enseignant.setActif(true);
                enseignant.setRoles(Set.of(enseignantRole));
                enseignant.setMatricule("ENS-2024-001");
                enseignant.setSpecialite("Mathématiques");
                enseignant.setDiplome("Master");
                enseignant.setDateEmbauche(LocalDate.of(2020, 9, 1));
                enseignant.setTypeContrat(TypeContrat.CDI);
                utilisateurRepository.save(enseignant);
                log.info("Enseignant créé : username=enseignant1, password=enseignant123");
            }

            // Créer l'année scolaire
            if (anneeScolaireRepository.count() == 0) {
                AnneeScolaire anneeScolaire = AnneeScolaire.builder()
                    .code("2024-2025")
                    .designation("Année Scolaire 2024-2025")
                    .dateDebut(LocalDate.of(2024, 9, 2))
                    .dateFin(LocalDate.of(2025, 7, 15))
                    .active(true)
                    .cloturee(false)
                    .build();
                anneeScolaire = anneeScolaireRepository.save(anneeScolaire);
                log.info("Année scolaire créée : {}", anneeScolaire.getCode());

                // Créer les périodes
                Periode t1 = Periode.builder()
                    .code("T1")
                    .designation("Premier Trimestre")
                    .typePeriode(TypePeriode.TRIMESTRE)
                    .dateDebut(LocalDate.of(2024, 9, 2))
                    .dateFin(LocalDate.of(2024, 12, 20))
                    .numero(1)
                    .active(false)
                    .anneeScolaire(anneeScolaire)
                    .build();

                Periode t2 = Periode.builder()
                    .code("T2")
                    .designation("Deuxième Trimestre")
                    .typePeriode(TypePeriode.TRIMESTRE)
                    .dateDebut(LocalDate.of(2025, 1, 6))
                    .dateFin(LocalDate.of(2025, 3, 28))
                    .numero(2)
                    .active(true)
                    .anneeScolaire(anneeScolaire)
                    .build();

                Periode t3 = Periode.builder()
                    .code("T3")
                    .designation("Troisième Trimestre")
                    .typePeriode(TypePeriode.TRIMESTRE)
                    .dateDebut(LocalDate.of(2025, 4, 7))
                    .dateFin(LocalDate.of(2025, 7, 15))
                    .numero(3)
                    .active(false)
                    .anneeScolaire(anneeScolaire)
                    .build();

                anneeScolaire.setPeriodes(Arrays.asList(t1, t2, t3));
                anneeScolaireRepository.save(anneeScolaire);
                log.info("Périodes créées pour l'année scolaire");
            }

            // Créer les niveaux
            if (niveauRepository.count() == 0) {
                for (int i = 1; i <= 6; i++) {
                    Niveau niveau = Niveau.builder()
                        .code(i + "EME")
                        .designation(getDesignationNiveau(i))
                        .typeNiveau(TypeNiveau.PRIMAIRE)
                        .ordre(i)
                        .description("Niveau " + i + " du primaire")
                        .build();
                    niveauRepository.save(niveau);
                    log.info("Niveau créé : {}", niveau.getDesignation());
                }
            }

            // Créer quelques classes
            if (classeRepository.count() == 0) {
                AnneeScolaire anneeScolaire = anneeScolaireRepository.findByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("Aucune année scolaire active"));
                
                Niveau niveau6 = niveauRepository.findById(6L)
                    .orElseThrow(() -> new RuntimeException("Niveau 6 non trouvé"));
                
                Enseignant enseignantPrincipal = (Enseignant) utilisateurRepository.findByUsername("enseignant1")
                    .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

                Classe classe6A = Classe.builder()
                    .code("6A")
                    .designation("6ème A")
                    .capaciteMax(35)
                    .effectifActuel(0)
                    .salle("A-101")
                    .typeClasse(TypeClasse.NORMALE)
                    .active(true)
                    .niveau(niveau6)
                    .anneeScolaire(anneeScolaire)
                    .enseignantPrincipal(enseignantPrincipal)
                    .build();
                classeRepository.save(classe6A);
                log.info("Classe créée : {}", classe6A.getDesignation());

                Classe classe6B = Classe.builder()
                    .code("6B")
                    .designation("6ème B")
                    .capaciteMax(35)
                    .effectifActuel(0)
                    .salle("A-102")
                    .typeClasse(TypeClasse.NORMALE)
                    .active(true)
                    .niveau(niveau6)
                    .anneeScolaire(anneeScolaire)
                    .build();
                classeRepository.save(classe6B);
                log.info("Classe créée : {}", classe6B.getDesignation());
            }

            // Créer quelques élèves
            if (eleveRepository.count() == 0) {
                Classe classe = classeRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
                
                Parent parent = (Parent) utilisateurRepository.findByUsername("parent1")
                    .orElseThrow(() -> new RuntimeException("Parent non trouvé"));

                Eleve eleve1 = Eleve.builder()
                    .matricule("ELV-2024-0001")
                    .nom("Dupont")
                    .prenom("Jean")
                    .dateNaissance(LocalDate.of(2012, 5, 15))
                    .lieuNaissance("Douala")
                    .genre(Genre.MASCULIN)
                    .nationalite("Camerounaise")
                    .numeroUrgence("+237 6 99 99 99 99")
                    .statut(StatutEleve.ACTIF)
                    .dateInscription(LocalDate.of(2024, 9, 1))
                    .adresseDomicile("Quartier Bonapriso")
                    .quartier("Bonapriso")
                    .boursier(false)
                    .classe(classe)
                    .parents(Set.of(parent))
                    .build();
                eleveRepository.save(eleve1);
                classe.setEffectifActuel(classe.getEffectifActuel() + 1);
                classeRepository.save(classe);
                log.info("Élève créé : {} {}", eleve1.getNom(), eleve1.getPrenom());

                Eleve eleve2 = Eleve.builder()
                    .matricule("ELV-2024-0002")
                    .nom("Martin")
                    .prenom("Marie")
                    .dateNaissance(LocalDate.of(2012, 8, 20))
                    .lieuNaissance("Yaoundé")
                    .genre(Genre.FEMININ)
                    .nationalite("Camerounaise")
                    .numeroUrgence("+237 6 88 88 88 88")
                    .statut(StatutEleve.ACTIF)
                    .dateInscription(LocalDate.of(2024, 9, 1))
                    .adresseDomicile("Avenue Kennedy")
                    .quartier("Bastos")
                    .boursier(true)
                    .classe(classe)
                    .build();
                eleveRepository.save(eleve2);
                classe.setEffectifActuel(classe.getEffectifActuel() + 1);
                classeRepository.save(classe);
                log.info("Élève créé : {} {}", eleve2.getNom(), eleve2.getPrenom());
            }

            log.info("Initialisation des données terminée avec succès!");
        };
    }

    private Permission createPermission(PermissionRepository repository, String code, String designation, 
                                       String module, String action) {
        return repository.findByCode(code).orElseGet(() -> {
            Permission permission = Permission.builder()
                .code(code)
                .designation(designation)
                .module(module)
                .action(action)
                .build();
            return repository.save(permission);
        });
    }

    private Role createRole(RoleRepository repository, String code, String designation, Set<Permission> permissions) {
        return repository.findByCode(code).orElseGet(() -> {
            Role role = Role.builder()
                .code(code)
                .designation(designation)
                .permissions(permissions)
                .build();
            return repository.save(role);
        });
    }

    private String getDesignationNiveau(int niveau) {
        return switch (niveau) {
            case 1 -> "Première";
            case 2 -> "Deuxième";
            case 3 -> "Troisième";
            case 4 -> "Quatrième";
            case 5 -> "Cinquième";
            case 6 -> "Sixième";
            default -> niveau + "ème";
        };
    }
}
