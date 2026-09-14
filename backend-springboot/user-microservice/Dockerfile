# Imagen base ligera (solo runtime)
FROM eclipse-temurin:21-jre-jammy

# Crear usuario no root (seguridad)
RUN useradd -ms /bin/bash appuser

# Directorio de trabajo
WORKDIR /app

# Copiar solo el jar
COPY target/*.jar app.jar

# Cambiar permisos
RUN chown appuser:appuser app.jar

# Cambiar a usuario seguro
USER appuser

# Puerto
EXPOSE 8092

# Variables opcionales (recomendado)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Entry point optimizado
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]