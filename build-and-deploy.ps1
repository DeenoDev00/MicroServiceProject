# Define the modules to build
$modules = @("api-gateway", "auth-server", "joke-service", "quote-service")

# Store the original directory
$originalLocation = Get-Location

try {
    # Build each module
    foreach ($module in $modules) {
        Write-Host "Building $module..."

        # Check if directory exists
        if (-not (Test-Path $module)) {
            Write-Host "Error: Directory '$module' not found!" -ForegroundColor Red
            continue
        }

        # Change to module directory
        Set-Location $module

        # Check if pom.xml exists
        if (-not (Test-Path "pom.xml")) {
            Write-Host "Error: pom.xml not found in $module!" -ForegroundColor Red
            Set-Location $originalLocation
            continue
        }

        # Run Maven build-image
        try {
            mvn spring-boot:build-image
            if ($LASTEXITCODE -eq 0) {
                Write-Host "Successfully built $module" -ForegroundColor Green
            } else {
                Write-Host "Failed to build $module" -ForegroundColor Red
                Set-Location $originalLocation
                continue
            }
        } catch {
            $errorMessage = $_.Exception.Message
            Write-Host "Error building $module`: $errorMessage" -ForegroundColor Red
            Set-Location $originalLocation
            continue
        }

        # Return to original directory before next module
        Set-Location $originalLocation
    }

    # Start all containers using docker-compose (optional step)
    Write-Host "`nStarting all containers with docker-compose..." -ForegroundColor Cyan
    docker-compose down
    docker-compose up -d
    Write-Host "All containers have been started!" -ForegroundColor Green

    # Deploy to Kubernetes
    Write-Host "`nDeploying to Kubernetes..."

    $k8sPath = Join-Path -Path $originalLocation -ChildPath "k8s"
    if (-not (Test-Path $k8sPath)) {
        Write-Host "Error: Kubernetes manifests folder 'k8s/' not found!" -ForegroundColor Red
        exit 1
    }

    kubectl apply -f $k8sPath
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: kubectl apply failed!" -ForegroundColor Red
        exit 1
    }

    # Wait for pods to be ready
    Write-Host "`nWaiting for pods to be ready..."

    $apps = @("api-gateway", "auth-server", "joke-service", "quote-service")
    foreach ($app in $apps) {
        kubectl wait --for=condition=ready pod -l app=$app --timeout=300s
        if ($LASTEXITCODE -ne 0) {
            Write-Warning "⚠️ Timeout or error waiting for pod: $app"
        } else {
            Write-Host "Pod for $app is ready." -ForegroundColor Green
        }
    }

    Write-Host "`n🎉 Deployment to Kubernetes complete!"

} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "An error occurred`: $errorMessage" -ForegroundColor Red
} finally {
    # Always return to original directory
    Set-Location $originalLocation
}

Write-Host "Build and deployment process completed!" -ForegroundColor Cyan
