# 🔧 Résolution Erreur 404 - Fichiers Statiques

## 🎯 Problème Identifié

**Erreur:** HTTP 404 Not Found lors de l'accès à `http://localhost:8080/` ou `http://localhost:8080/login.html`

**Cause:** Les fichiers statiques HTML/CSS/JS ne sont pas accessibles via Spring Boot.

---

## ✅ Solutions Appliquées

### 1. HomeController Créé
```java
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "forward:/login.html";
    }
}
```

### 2. SecurityConfig Vérifié
Les routes statiques sont bien autorisées :
```java
.requestMatchers("/", "/login.html", "/css/**", "/js/**", "/images/**").permitAll()
```

### 3. WebConfig Configuré
```java
registry.addResourceHandler("/*.html")
    .addResourceLocations("classpath:/static/");
```

### 4. Fichiers Compilés
```bash
mvn clean package -DskipTests
```

---

## 🚀 Étapes de Test

### Méthode 1: Démarrage Simple
```bash
# 1. Arrêter tous les processus Java
taskkill /F /IM java.exe

# 2. Nettoyer et compiler
mvn clean package -DskipTests

# 3. Lancer l'application
java -jar target/gestion-ecole-backend-1.0.0.jar

# 4. Attendre le message
# "Started GestionEcoleApplication in X seconds"

# 5. Tester
http://localhost:8080/
```

### Méthode 2: Avec Maven
```bash
# 1. Arrêter les processus
taskkill /F /IM java.exe

# 2. Lancer avec Maven
mvn spring-boot:run

# 3. Attendre le démarrage

# 4. Tester
http://localhost:8080/login.html
```

---

## 🔍 Vérifications

### 1. Vérifier que les Fichiers Sont dans le JAR
```bash
jar -tf target/gestion-ecole-backend-1.0.0.jar | findstr static
```

**Résultat attendu:**
```
BOOT-INF/classes/static/login.html
BOOT-INF/classes/static/dashboard.html
BOOT-INF/classes/static/eleves.html
BOOT-INF/classes/static/css/styles.css
BOOT-INF/classes/static/js/api.js
...
```

### 2. Tester les Endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Login page
curl http://localhost:8080/login.html

# API Auth (devrait retourner 400 sans body)
curl -X POST http://localhost:8080/api/auth/login
```

### 3. Vérifier les Logs
Regarder dans le terminal pour :
```
Started GestionEcoleApplication in X seconds (JVM running for Y)
Tomcat started on port(s): 8080 (http)
```

---

## 🐛 Si le Problème Persiste

### Solution A: Forcer la Ressource Location
Modifier `WebConfig.java` :
```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(0);
}
```

### Solution B: Créer un Contrôleur Rest pour Test
```java
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public String test() {
        return "Backend is running!";
    }
}
```
Tester: `http://localhost:8080/test`

### Solution C: Utiliser Spring DevTools
Dans `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Solution D: Désactiver Temporairement la Sécurité
Dans `SecurityConfig.java`:
```java
.authorizeHttpRequests(auth -> auth
    .anyRequest().permitAll()  // TEMPORAIRE POUR TEST
)
```

---

## 📋 Checklist de Diagnostic

- [ ] Application démarre sans erreur
- [ ] Port 8080 disponible
- [ ] Logs montrent "Started GestionEcoleApplication"
- [ ] `actuator/health` retourne UP
- [ ] Fichiers dans `src/main/resources/static/` existent
- [ ] Fichiers dans `target/classes/static/` après compilation
- [ ] Fichiers dans le JAR après `mvn package`
- [ ] SecurityConfig autorise les routes statiques
- [ ] WebConfig configure les resource handlers
- [ ] HomeController existe et gère `/`

---

## 🎯 Test Rapide Final

```bash
# 1. Clean build
mvn clean package -DskipTests

# 2. Vérifier le JAR
jar -tf target/gestion-ecole-backend-1.0.0.jar | findstr "login.html"

# Si login.html apparaît, lancer:
java -jar target/gestion-ecole-backend-1.0.0.jar

# Attendre 20-30 secondes

# Ouvrir navigateur:
http://localhost:8080/
```

---

## 📞 Dernière Solution: Mode Debug

```bash
# Lancer avec logs détaillés
java -jar target/gestion-ecole-backend-1.0.0.jar --logging.level.org.springframework.web=DEBUG

# Chercher dans les logs:
# - "Mapped [...]" pour voir les mappings
# - "ResourceHandler" pour voir les handlers
# - Erreurs de démarrage
```

---

## ✅ Confirmation de Succès

Lorsque tout fonctionne, vous devriez voir :

1. **Dans le terminal:**
   ```
   Started GestionEcoleApplication in 15.234 seconds
   ```

2. **Dans le navigateur (`http://localhost:8080/`):**
   - Page de login s'affiche
   - CSS chargé (formulaire stylisé)
   - Aucune erreur 404

3. **Console navigateur (F12):**
   - Aucune erreur rouge
   - Fichiers CSS/JS chargés (onglet Network)

---

## 🎉 Une Fois Résolu

Tester ces fonctionnalités :
- [ ] Login avec admin/admin123
- [ ] Redirection vers dashboard
- [ ] Navigation entre pages
- [ ] Chargement des styles CSS
- [ ] Exécution des scripts JS

---

**Si aucune solution ne fonctionne, partagez :**
1. Les logs complets du démarrage
2. Le résultat de `jar -tf target/*.jar | findstr static`
3. Le contenu de `src/main/resources/static/`

**Date:** 30 Septembre 2024  
**Version:** 1.0.0
