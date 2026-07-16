.PHONY: help \
	build install clean test run health \
	docker-up docker-down docker-logs docker-ps

.DEFAULT_GOAL := help

##@ Build

build: ## Compile the project
	@mvn compile

install: ## Build and install artifacts to the local Maven repository
	@mvn clean install

clean: ## Clean Maven build artifacts
	@mvn clean

test: ## Run tests
	@mvn test

##@ Run

run: docker-up ## Start dependencies and run the Spring Boot application
	@exec mvn -pl example-rest-api spring-boot:run

health: ## Check the application health endpoint
	@./scripts/health.sh

##@ Docker

docker-up: ## Start Docker services
	@docker compose up -d

docker-down: ## Stop Docker services
	@docker compose down

docker-logs: ## Follow Docker logs
	@docker compose logs -f

docker-ps: ## Show Docker service status
	@docker compose ps

##@ Help

help: ## Show this help
	@awk '\
	BEGIN {FS=":.*##"; section=""} \
	/^##@/ { \
		section=substr($$0,5); \
		printf "\n%s\n", section; \
	} \
	/^[a-zA-Z0-9_.-]+:.*##/ { \
		printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2; \
	}' $(MAKEFILE_LIST)
