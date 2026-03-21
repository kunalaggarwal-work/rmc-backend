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

# 4. Navigate to k8s folder
echo "📂 Switching to k8s directory..."
cd k8s

# 5. Apply Kubernetes deployment
echo "☸️ Applying Kubernetes deployment..."
kubectl apply -f backend-deployment.yml -n rmc-be

# 6. Restart deployment (important if using latest tag)
echo "🔄 Restarting deployment..."
kubectl rollout restart deployment rmc-backend-deployment -n rmc-be

echo "✅ Redeployment completed successfully!"
