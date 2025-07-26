#!/bin/bash

# Docker build and run script for Aditya Resume Backend

set -e

# Configuration
IMAGE_NAME="aditya-resume-backend"
CONTAINER_NAME="aditya-resume-backend"
PORT="8080"
AWS_REGION="${AWS_REGION:-us-east-1}"
SECRET_NAME="${AWS_SECRETS_SECRET_NAME:-aditya-resume-backend-secrets}"

echo "Building Docker image..."
docker build -t $IMAGE_NAME:latest .

echo "Stopping existing container if running..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

echo "Starting container..."
docker run -d \
  --name $CONTAINER_NAME \
  -p $PORT:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e AWS_REGION=$AWS_REGION \
  -e AWS_SECRET_NAME=$SECRET_NAME \
  $IMAGE_NAME:latest

echo "Container started successfully!"
echo "Application will be available at: http://localhost:$PORT"
echo "Health check: http://localhost:$PORT/actuator/health"
echo ""
echo "To view logs: docker logs -f $CONTAINER_NAME"
echo "To stop: docker stop $CONTAINER_NAME" 