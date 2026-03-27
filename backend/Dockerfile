#!/usr/bin/env -S docker build . --tag=qualitypaper/pingofamily:latest
FROM amazoncorretto:25-alpine3.21-jdk
LABEL authors="qualitypaper"

# Install dependencies
RUN mkdir /pingofamily

COPY target/pingofamily-0.1.jar /pingofamily/pingofamily.jar
# Copy Docker-specific bootstrap configuration
COPY src/main/resources/bootstrap-docker.yml /pingofamily/bootstrap.yml
COPY weights/ /pingofamily/weights/
COPY src/main/resources/certs /pingofamily/certs

EXPOSE 9090

ENV NAME=pingofamily

RUN printf '#!/bin/sh\nsleep 30\nexec java -jar /pingofamily/pingofamily.jar' > /pingofamily/start.sh && \
    chmod +x /pingofamily/start.sh

CMD ["/pingofamily/start.sh"]
