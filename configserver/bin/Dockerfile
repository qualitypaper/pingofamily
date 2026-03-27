FROM alpine:latest
LABEL authors="qualitypaper"
ARG jarPath="/pingofamily/configserver.jar"
ARG sshPath="src/main/resources/git_keys/key"
ARG keyfilename="id_rsa"

# Install dependencies
RUN apk add --no-cache openjdk21-jre openssh-client git \
    && mkdir /pingofamily \
    && mkdir -p ~/.ssh \
    && chmod 700 ~/.ssh

COPY target/configserver-0.1.jar /pingofamily/configserver.jar
COPY "${sshPath}" "/root/.ssh/${keyfilename}"
COPY "${sshPath}.pub" "/root/.ssh/${keyfilename}.pub"

RUN chmod 600 "/root/.ssh/${keyfilename}" && chmod 644 "/root/.ssh/${keyfilename}.pub" \
    && echo "Host github.com\n\tHostName github.com\n\tUser git\n\tIdentityFile /root/.ssh/${keyfilename}\n\tStrictHostKeyChecking no" > /root/.ssh/config \
    && chmod 600 /root/.ssh/config \
    && ssh-keyscan -t rsa github.com >> /root/.ssh/known_hosts \
    && chmod 644 /root/.ssh/known_hosts

ENV NAME=pingofamily-configserver

# set up other needed env variables


EXPOSE 8888:8888
CMD ["java", "-Djavax.net.debug=all", "-jar", "/pingofamily/configserver.jar"]
