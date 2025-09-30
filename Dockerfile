FROM openjdk:17-jdk-slim

# Créer un utilisateur non-root
RUN groupadd -r spring && useradd -r -g spring spring

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR
COPY target/gestion-ecole-backend-1.0.0.jar app.jar

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
