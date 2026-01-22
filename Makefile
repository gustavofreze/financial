DOCKER_COMPOSE = docker compose
DOCKER_EXEC_APP = docker exec -it app bash
DOCKER_IMAGE_PRUNE = docker image prune --all --force
DOCKER_NETWORK_PRUNE = docker network prune --force

.PHONY: configure
configure:
	@${DOCKER_COMPOSE} up -d --build

.PHONY: register-financial-transaction
register-financial-transaction:
	@${DOCKER_EXEC_APP} ./register-financial-transaction.sh

.PHONY: show-balance
show-balance:
	@${DOCKER_EXEC_APP} ./show-balance.sh

.PHONY: solve-failures
solve-failures:
	@${DOCKER_EXEC_APP} ./solve-failures.sh

.PHONY: test
test:
	@${DOCKER_EXEC_APP} ./gradlew allTests

.PHONY: unit-test
unit-test:
	@${DOCKER_EXEC_APP} ./gradlew unitTest

.PHONY: integration-test
integration-test:
	@${DOCKER_EXEC_APP} ./gradlew integrationTest

.PHONY: stop
stop:
	@${DOCKER_COMPOSE} stop $(docker ps -a -q)

.PHONY: clean
clean: stop
	@${DOCKER_COMPOSE} rm -vf $(docker ps -a -q)
	@${DOCKER_NETWORK_PRUNE} --filter label="com.docker.compose.project"="financial"

.PHONY: clean-all
clean-all: clean
	@${DOCKER_IMAGE_PRUNE} --filter label="io.confluent.docker=true"
	@${DOCKER_IMAGE_PRUNE} --filter label="maintainer"="Debezium Community"
	@${DOCKER_IMAGE_PRUNE} --filter label="com.docker.compose.project"="financial"
