# Container-first workflow. Every Gradle command runs inside the gradle image, so nothing beyond Docker and GNU Make is
# installed on the host.

SHELL := /bin/bash
.ONESHELL:
.SHELLFLAGS := -eu -o pipefail -c
.DEFAULT_GOAL := help

PROJECT_NAME     := financial
GRADLE_IMAGE     := $(shell grep -oE '^FROM gradle:[^ ]+' Dockerfile | cut -d ' ' -f 2)
SHELLCHECK_IMAGE := koalaman/shellcheck:v0.11.0
COMPOSE          := docker compose
GRADLE_HOME      := /app/.gradle-home
GRADLE           := ./gradlew --no-daemon --console=plain
SHELL_SCRIPTS    := $(wildcard $(shell git ls-files '*.sh'))

GATED_MODULES   := shared account bookkeeping
COVERAGE_HTML   := $(foreach module,$(GATED_MODULES),application/$(module)/build/reports/jacoco/test/html/index.html)
MUTATION_REPORT := $(foreach module,$(GATED_MODULES),application/$(module)/build/reports/pitest/index.html)

HOST_USER   := $(shell id -u):$(shell id -g)
INTERACTIVE := $(shell [[ -t 0 ]] && echo -it)
EXEC_TTY    := $(if $(INTERACTIVE),,-T)
OPEN_CMD    := $(shell command -v sensible-browser 2>/dev/null || command -v xdg-open 2>/dev/null || echo open)

APP_RUN = docker run $(INTERACTIVE) --rm \
	-u $(HOST_USER) \
	-e HOME=/tmp \
	-e GRADLE_USER_HOME=$(GRADLE_HOME) \
	-v $(CURDIR):/app -w /app

APP_TEST_RUN = docker run $(INTERACTIVE) --rm \
	-u $(HOST_USER) \
	-e HOME=/tmp \
	-e GRADLE_USER_HOME=$(GRADLE_HOME) \
	-v $(CURDIR):/app -w /app \
	$(GRADLE_IMAGE)

APP_EXEC = $(COMPOSE) exec $(EXEC_TTY) app

# ─── Setup and run ───────────────────────────────────────────
.PHONY: start
start: ## @setup Start the service stack
	@$(COMPOSE) up -d --build --wait
	@$(COMPOSE) rm -f $(PROJECT_NAME)-migrate

.PHONY: stop
stop: ## @setup Stop the service stack and drop its data volume
	@$(COMPOSE) down --volumes

.PHONY: configure
configure: ## @setup Install dependencies
	@$(APP_RUN) $(GRADLE_IMAGE) $(GRADLE) testClasses

.PHONY: configure-and-update
configure-and-update: ## @setup Update dependencies
	@$(APP_RUN) $(GRADLE_IMAGE) $(GRADLE) --refresh-dependencies testClasses

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
tests: ## @test Run all tests with coverage and mutation testing
	@$(APP_TEST_RUN) $(GRADLE) test jacocoTestReport jacocoTestCoverageVerification pitest

.PHONY: test-file
test-file: ## @test Run a single test file without coverage (usage: make test-file FILE=AccountBalanceTest)
	$(if $(FILE),,$(error FILE is required, usage: make test-file FILE=AccountBalanceTest))
	@$(APP_TEST_RUN) $(GRADLE) test --tests '*$(FILE)'

# ─── Code review ─────────────────────────────────────────────
.PHONY: review
review: ## @review Run static code analysis
	@$(APP_RUN) $(GRADLE_IMAGE) $(GRADLE) ktlintCheck
	@$(APP_RUN) $(SHELLCHECK_IMAGE) $(SHELL_SCRIPTS)

.PHONY: fix-review
fix-review: ## @review Fix static code analysis issues
	@$(APP_RUN) $(GRADLE_IMAGE) $(GRADLE) ktlintFormat

# ─── Reports ─────────────────────────────────────────────────
.PHONY: show-reports
show-reports: ## @reports Open coverage and mutation reports in the browser
	@for report in $(COVERAGE_HTML) $(MUTATION_REPORT); do $(OPEN_CMD) $$report; done

.PHONY: show-outdated
show-outdated: ## @reports Show outdated dependencies
	@$(APP_RUN) $(GRADLE_IMAGE) $(GRADLE) showOutdated

# ─── Maintenance ─────────────────────────────────────────────
.PHONY: clean
clean: ## @maintenance Remove build artifacts, reports, and the dependency cache
	@rm -rf .gradle .gradle-home .kotlin build application/*/build

# ─── Help ────────────────────────────────────────────────────
.PHONY: help
help: ## @help Display this help message
	@echo "Usage: make [target]"
	@echo ""
	@awk 'BEGIN { \
		FS = ":.*?## @"; \
		order[1]="setup"; order[2]="usecases"; order[3]="test"; order[4]="review"; \
		order[5]="reports"; order[6]="maintenance"; order[7]="help"; \
		title["setup"]="Setup and run"; title["usecases"]="Use cases"; title["test"]="Testing"; \
		title["review"]="Code review"; title["reports"]="Reports"; title["maintenance"]="Maintenance"; \
		title["help"]="Help"; \
	} \
	/^[a-zA-Z0-9_-]+:.*## @/ { \
		split($$2, parts, " "); \
		section = parts[1]; \
		description = substr($$2, length(section) + 2); \
		entries[section] = entries[section] sprintf("\033[36m%-32s\033[0m %s\n", $$1, description); \
	} \
	END { \
		for (index_ = 1; index_ <= 7; index_++) { \
			section = order[index_]; \
			if (entries[section] != "") { \
				printf "%s\n", title[section]; \
				printf "%s\n", entries[section]; \
			} \
		} \
	}' $(MAKEFILE_LIST)
