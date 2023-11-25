.PHONY: help

DOCKERHUB_USERNAME=k8loud
IMAGE_NAME=themis-executor
# w.x.y.z, one digit value each
# when tinkering add -<description> suffix
VER=0.0.2.6

# targets that aren't annotated with ## are not supposed to be run on their own

help: ## show Makefile contents
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

build-jar: ## build a plain jar
	@echo "VER: $(VER)"
	mvn clean install -DthemisExecutorVersion=$(VER)

# GH - GitHub, DH - Dockerhub
# if 409 Conflict is received it means that a package with version VER is already present on GH
# remove it manually: https://github.com/k8loud/themis-executor/packages
# or change VER
build-and-push: ## plain jar -> GH | Docker image from a jar with Spring Boot wrapper -> DH
	make build-jar
	mvn deploy -DthemisExecutorVersion=$(VER) -DskipTests=true
	make build-and-push-docker FULL_IMAGE_NAME=$(DOCKERHUB_USERNAME)/$(IMAGE_NAME):$(VER)

build-and-push-docker:
	@echo "FULL_IMAGE_NAME: $(FULL_IMAGE_NAME)"
	mvn package spring-boot:repackage -DthemisExecutorVersion=$(VER) -DskipTests=true
	docker build -t $(FULL_IMAGE_NAME) . --build-arg VER=$(VER)
	docker login
	docker push $(FULL_IMAGE_NAME)

deploy: ## deploy to the Kubernetes cluster
	kubectl apply -f manifests/deployment.yaml

.DEFAULT_GOAL := help
