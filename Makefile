# Container-first workflow. Every Gradle command runs inside the gradle image, so nothing beyond Docker and GNU Make is
# installed on the host.

SHELL := /bin/bash
.ONESHELL:
.SHELLFLAGS := -eu -o pipefail -c
.DEFAULT_GOAL := help

PROJECT_NAME := financial
GRADLE_IMAGE := $(shell grep -oE '^FROM gradle:[^ ]+' Dockerfile | cut -d ' ' -f 2)
COMPOSE      := docker compose
ENV_FILE     := config/configs.env
GRADLE_HOME  := /app/.gradle-home
GRADLE       := ./gradlew --no-daemon --console=plain

STARTER_BUILD := application/starter/build
STARTER_JAR   := $(STARTER_BUILD)/libs/starter-all.jar

HOST_USER   := $(shell id -u):$(shell id -g)
INTERACTIVE := $(shell [[ -t 0 ]] && echo -it)
EXEC_TTY    := $(if $(INTERACTIVE),,-T)

APP_RUN = docker run $(INTERACTIVE) --rm \
	-u $(HOST_USER) \
	-e HOME=/tmp \
	-e GRADLE_USER_HOME=$(GRADLE_HOME) \
	-v $(CURDIR):/app -w /app \
	$(GRADLE_IMAGE)

APP_TEST_RUN = docker run $(INTERACTIVE) --rm \
	-u $(HOST_USER) \
	-e HOME=/tmp \
	-e GRADLE_USER_HOME=$(GRADLE_HOME) \
	--env-file $(ENV_FILE) \
	-v $(CURDIR):/app -w /app \
	$(GRADLE_IMAGE)

APP_EXEC = $(COMPOSE) exec $(EXEC_TTY) app

# ─── Setup and run ───────────────────────────────────────────
.PHONY: start
start: ## @setup Start the application stack
	@$(COMPOSE) up -d --build

.PHONY: stop
stop: ## @setup Stop the application stack and remove its containers and network
	@$(COMPOSE) down --volumes

.PHONY: configure
configure: ## @setup Install dependencies and build the application jar
	@$(APP_RUN) $(GRADLE) :application:starter:shadowJar

# ─── Use cases ───────────────────────────────────────────────
.PHONY: register-financial-transaction
register-financial-transaction: ## @usecases Register a financial transaction
	@$(APP_EXEC) ./register-financial-transaction.sh

.PHONY: show-balance
show-balance: ## @usecases Show the balance of every account
	@$(APP_EXEC) ./show-balance.sh

.PHONY: solve-failures
solve-failures: ## @usecases Run again the policies that failed
	@$(APP_EXEC) ./solve-failures.sh

# ─── Testing ─────────────────────────────────────────────────
.PHONY: tests
tests: ## @test Run all tests
	@$(APP_TEST_RUN) $(GRADLE) allTests

.PHONY: test-file
test-file: ## @test Run a single test class (usage: make test-file FILE=AccountBalanceTest)
	@$(APP_TEST_RUN) $(GRADLE) allTests --tests '*$(FILE)'

# ─── Maintenance ─────────────────────────────────────────────
.PHONY: clean
clean: ## @maintenance Remove build artifacts and the dependency cache, keeping the application jar
	@rm -rf .gradle .gradle-home build application/{account,bookkeeping,shared}/build
	@find $(STARTER_BUILD) -mindepth 1 -maxdepth 1 ! -name libs -exec rm -rf {} +
	@find $(STARTER_BUILD)/libs -mindepth 1 ! -path $(STARTER_JAR) -exec rm -rf {} +

# ─── Help ────────────────────────────────────────────────────
.PHONY: help
help: ## @help Display this help message
	@echo "Usage: make [target]"
	@echo ""
	@awk 'BEGIN { \
		FS = ":.*?## @"; \
		order[1]="setup"; order[2]="usecases"; order[3]="test"; order[4]="maintenance"; order[5]="help"; \
		title["setup"]="Setup and run"; title["usecases"]="Use cases"; title["test"]="Testing"; \
		title["maintenance"]="Maintenance"; title["help"]="Help"; \
	} \
	/^[a-zA-Z0-9_-]+:.*## @/ { \
		split($$2, parts, " "); \
		section = parts[1]; \
		description = substr($$2, length(section) + 2); \
		entries[section] = entries[section] sprintf("\033[36m%-32s\033[0m %s\n", $$1, description); \
	} \
	END { \
		for (index_ = 1; index_ <= 5; index_++) { \
			section = order[index_]; \
			if (entries[section] != "") { \
				printf "%s\n", title[section]; \
				printf "%s\n", entries[section]; \
			} \
		} \
	}' $(MAKEFILE_LIST)
