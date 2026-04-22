#!/bin/bash

# Exit immediately if any command fails
set -e

echo "🚀 Starting redeployment..."

# 1. Build project
echo "📦 Building project with Maven..."
mvn clean package -DskipTests

# 2. Build Docker image
echo "🐳 Building Docker image..."
docker build -t kunalaggarwal05/rmc-be:latest .

# 3. Push Docker image
echo "⬆️ Pushing Docker image to Docker Hub..."
docker push kunalaggarwal05/rmc-be:latest

echo  "IMAGE PUSHED TO DOCKER HUB"
