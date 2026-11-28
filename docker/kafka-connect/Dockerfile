ARG DEBEZIUM_VERSION=3.7.1.Final
ARG DEBEZIUM_SHA256=0b2ec2bb5b84dacf4501773aca754bba05c299ca2ae278985f1af260460af955

FROM alpine:3.24.4 AS plugin

ARG DEBEZIUM_VERSION
ARG DEBEZIUM_SHA256

RUN wget -qO /tmp/debezium-connector-postgres.tar.gz "https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/${DEBEZIUM_VERSION}/debezium-connector-postgres-${DEBEZIUM_VERSION}-plugin.tar.gz" \
    && echo "${DEBEZIUM_SHA256}  /tmp/debezium-connector-postgres.tar.gz" | sha256sum -c - \
    && tar -xzf /tmp/debezium-connector-postgres.tar.gz -C /tmp \
    && rm /tmp/debezium-connector-postgres.tar.gz

FROM confluentinc/cp-kafka-connect:8.4.1

COPY --from=plugin /tmp/debezium-connector-postgres /usr/share/java/debezium-connector-postgres
