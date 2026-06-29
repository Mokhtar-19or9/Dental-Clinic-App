# Run this PowerShell script to set your photo as the app icon
# Usage: .\setup_icon.ps1 -PhotoPath "C:\path\to\photo.jpg"

param(
    [Parameter(Mandatory=$true)]
    [string]$PhotoPath
)

if (-not (Test-Path $PhotoPath)) {
    Write-Host "ERROR: File not found at $PhotoPath" -ForegroundColor Red
    exit 1
}

$resDir = "app\src\main\res"

# Copy as drawable resource (vector icons reference this)
Copy-Item -Path $PhotoPath -Destination "$resDir\drawable\ic_custom_launcher.png" -Force
Write-Host "✅ Copied to drawable/ic_custom_launcher.png" -ForegroundColor Green

# Also copy to mipmap folders for older Android versions
$mipmapDirs = @(
    "mipmap-mdpi",
    "mipmap-hdpi",
    "mipmap-xhdpi",
    "mipmap-xxhdpi",
    "mipmap-xxxhdpi"
)

foreach ($dir in $mipmapDirs) {
    $dest = "$resDir\$dir\ic_launcher.png"
    Copy-Item -Path $PhotoPath -Destination $dest -Force
    Write-Host "✅ Copied to $dir/ic_launcher.png" -ForegroundColor Green
}

Write-Host ""
Write-Host "✅ Icon files ready!" -ForegroundColor Green
Write-Host "Now rebuild the app and the icon will be your photo."
