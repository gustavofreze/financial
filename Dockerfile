FROM gradle:9.9.2-jdk27 AS build

RUN jlink \
    --add-modules java.base,java.desktop,java.management,java.naming,java.net.http,java.security.jgss,java.security.sasl,java.sql,jdk.jfr,jdk.net,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress zip-9 \
    --output /opt/jre

WORKDIR /build

COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle/libs.versions.toml gradle/libs.versions.toml
COPY application application

RUN --mount=type=cache,target=/root/.gradle \
    gradle --no-daemon --console=plain :application:starter:shadowJar

FROM debian:13.8-slim

LABEL org.opencontainers.image.title="gustavofreze/financial"
LABEL org.opencontainers.image.authors="Gustavo Freze"

ENV JAVA_HOME=/opt/jre
ENV PATH="/opt/jre/bin:${PATH}"

RUN useradd --system --user-group --no-create-home --home-dir /opt/data financial \
    && install -d -o financial -g financial /opt/data

WORKDIR /opt/data

COPY --from=build /opt/jre /opt/jre
COPY --chown=financial:financial application/starter/src/main/resources/kafka/connectors /opt/data/kafka/connectors
COPY --chown=financial:financial application/account/src/main/resources/command/show-balance.sh /opt/data/show-balance.sh
COPY --chown=financial:financial application/account/src/main/resources/command/solve-failures.sh /opt/data/solve-failures.sh
COPY --chown=financial:financial application/bookkeeping/src/main/resources/command/register-financial-transaction.sh /opt/data/register-financial-transaction.sh
COPY --from=build --chown=financial:financial /build/application/starter/build/libs/starter-all.jar /opt/data/starter-all.jar

USER financial

HEALTHCHECK --interval=10s --timeout=5s --start-period=60s --retries=3 CMD ["grep", "-qx", "java", "/proc/1/comm"]

ENTRYPOINT ["java", "-jar", "/opt/data/starter-all.jar", "platform"]
