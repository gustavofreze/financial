FROM gradle:7.1.1-jdk16

ARG FLYWAY_VERSION=9.19.4

WORKDIR /opt/data

EXPOSE 8080

RUN apt-get install curl \
    && rm -rf /var/lib/apt/lists/*

RUN curl -Ls -o /opt/flyway.tar.gz https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}-linux-x64.tar.gz \
    && tar -xzf /opt/flyway.tar.gz -C /opt \
    && ln -s /opt/flyway-${FLYWAY_VERSION}/flyway /usr/local/bin \
    && rm -rf /opt/flyway.tar.gz

ENTRYPOINT ["bash", "/opt/data/entrypoint.sh"]
