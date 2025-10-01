# ========================================
# Stage 1: Build
# ========================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Télécharger les dépendances (mis en cache si pom.xml ne change pas)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler et packager l'application (sans tests pour rapidité)
RUN mvn clean package -DskipTests -B

# ========================================
# Stage 2: Runtime
# ========================================
FROM eclipse-temurin:17-jre-alpine

# Créer un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /build/target/gestion-ecole-backend-1.0.0.jar app.jar

# Créer le répertoire pour les uploads
RUN mkdir -p /app/uploads && chown -R spring:spring /app

# Changer vers l'utilisateur non-root
USER spring:spring

# Exposer le port
EXPOSE 8080

# Définir les options JVM pour optimiser les performances
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
