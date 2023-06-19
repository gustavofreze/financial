DOCKER_COMPOSE = docker-compose
DOCKER_EXEC_APP = docker exec -it app bash
DOCKER_IMAGE_PRUNE = docker image prune --all --force
DOCKER_NETWORK_PRUNE = docker network prune --force

.PHONY: configure clean clean-all

configure:
	@${DOCKER_COMPOSE} up -d --build

register-financial-transaction:
	@${DOCKER_EXEC_APP} ./register-financial-transaction.sh

show-balance:
	@${DOCKER_EXEC_APP} ./show-balance.sh

solve-failures:
	@${DOCKER_EXEC_APP} ./solve-failures.sh

test:
	@${DOCKER_EXEC_APP} ./gradlew allTests

unit-test:
	@${DOCKER_EXEC_APP} ./gradlew unitTest

integration-test:
	@${DOCKER_EXEC_APP} ./gradlew integrationTest

stop:
	@${DOCKER_COMPOSE} stop $(docker ps -a -q)

clean: stop
	@${DOCKER_COMPOSE} rm -vf $(docker ps -a -q)
	@${DOCKER_NETWORK_PRUNE} --filter label="com.docker.compose.project"="financial"

clean-all: clean
	@${DOCKER_IMAGE_PRUNE} --filter label="io.confluent.docker=true"
	@${DOCKER_IMAGE_PRUNE} --filter label="maintainer"="Debezium Community"
	@${DOCKER_IMAGE_PRUNE} --filter label="com.docker.compose.project"="financial"
