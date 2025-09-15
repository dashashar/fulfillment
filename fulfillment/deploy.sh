#!/bin/sh
set -e

SERVICE=$1
BRANCH=$2
TAG=$3
SSH=$4
IMAGE=$5

echo "Deploying $SERVICE:$TAG to $SSH..."

which ssh-agent || (apk add --no-cache openssh-client)
eval "$(ssh-agent -s)"
echo "$SSH_PRIVATE_KEY" | tr -d '\r' | ssh-add - > /dev/null
mkdir -p ~/.ssh && chmod 700 ~/.ssh
echo -e "Host *\n\tStrictHostKeyChecking no\n" > ~/.ssh/config

ssh "$SSH" << EOF
  set -e
  echo "Connected to $SSH"

  echo "Docker login"
  docker login -u gitlab-ci-token -p $CI_JOB_TOKEN $CI_REGISTRY

  echo "Pulling image: $IMAGE:$BRANCH"
  docker pull $IMAGE:$BRANCH || true

  echo "Running docker-compose"
  export SERVICE_IMAGE=$IMAGE:$BRANCH
  docker-compose down || true
  docker-compose up -d --no-build

  echo "Docker logout"
  docker logout $CI_REGISTRY
EOF

echo "Deployment completed: $SERVICE:$TAG"
