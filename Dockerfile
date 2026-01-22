ARG FLYWAY_VERSION=9.22.3

FROM flyway/flyway:${FLYWAY_VERSION} AS flyway

FROM gradle:7.1.1-jdk16

LABEL org.opencontainers.image.title="gustavofreze/financial"
LABEL org.opencontainers.image.authors="Gustavo Freze"

WORKDIR /opt/data

EXPOSE 8080

COPY --from=flyway /flyway /opt/flyway

ENV PATH="/opt/flyway:${PATH}"

ENTRYPOINT ["bash", "/opt/data/entrypoint.sh"]
