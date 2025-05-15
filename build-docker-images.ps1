#!/bin/bash

# Array of module directories
MODULES=("api-gateway" "auth-server" "joke-service" "quote-service")

# Build each module
for module in "${MODULES[@]}"
do
    echo "Building Docker image for $module..."
    cd "$module"
    mvnw.cmd spring-boot:build-image
    cd ..
    echo "Finished building $module"
done

echo "All Docker images have been built successfully!"