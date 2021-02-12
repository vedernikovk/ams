FROM openjdk:10-jre-slim
RUN groupadd -g 999 appuser \
	&& useradd -r -u 999 -g appuser appuser \
	&& mkdir -p /app/ \
	&& chown -R appuser:appuser /app
WORKDIR /app
USER appuser
COPY --chown=appuser:appuser ./target/contacts-*.jar contacts.jar
ENTRYPOINT ["java","-jar","contacts.jar"]
