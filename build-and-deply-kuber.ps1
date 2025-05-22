# Store the original directory
$originalLocation = Get-Location

try {
    # Deploy to Kubernetes
    Write-Host "`nDeploying to Kubernetes..." -ForegroundColor Cyan

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

    Write-Host "`n🎉 Deployment to Kubernetes complete!" -ForegroundColor Green

} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "An error occurred: $errorMessage" -ForegroundColor Red
} finally {
    # Always return to original directory
    Set-Location $originalLocation
}

Write-Host "Kubernetes deployment script completed!" -ForegroundColor Cyan
