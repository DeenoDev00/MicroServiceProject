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
            continue
        }

        # Run Maven build
        try {
            mvn spring-boot:build-image
            if ($LASTEXITCODE -eq 0) {
                Write-Host "Successfully built $module" -ForegroundColor Green
            } else {
                Write-Host "Failed to build $module" -ForegroundColor Red
            }
        } catch {
            $errorMessage = $_.Exception.Message
            Write-Host "Error building $module`: $errorMessage" -ForegroundColor Red
        }

        # Return to original directory
        Set-Location $originalLocation
    }

    # Start all containers using docker-compose
    Write-Host "`nStarting all containers..." -ForegroundColor Cyan
    docker-compose down
    docker-compose up -d
    Write-Host "All containers have been started!" -ForegroundColor Green

} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "An error occurred`: $errorMessage" -ForegroundColor Red
} finally {
    # Always return to original directory
    Set-Location $originalLocation
}

Write-Host "Build and deployment process completed!" -ForegroundColor Cyan
