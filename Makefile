.PHONY: help

DOCKERHUB_USERNAME=k8loud
IMAGE_NAME=themis-executor
TAG=0.0.2-builder-test

help: ## show Makefile contents
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

build: ## build
	mvn clean install

build-and-push: ## build Docker image and push it to Docker Hub
	make build
	make build-and-push-docker FULL_IMAGE_NAME=$(DOCKERHUB_USERNAME)/$(IMAGE_NAME):$(TAG)

build-and-push-docker:
	@echo "Full image name: $(FULL_IMAGE_NAME)"
	docker login
	docker build -t $(FULL_IMAGE_NAME) .
	docker push $(FULL_IMAGE_NAME)

deploy: ## deploy to the Kubernetes cluster
	kubectl apply -f manifests/deployment.yaml

.DEFAULT_GOAL := help
