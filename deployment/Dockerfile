FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
COPY ./applications/app-service/build/libs/solicitudes.jar solicitudes.jar
RUN chmod 444 solicitudes.jar && chown appuser:appuser solicitudes.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar solicitudes.jar"]