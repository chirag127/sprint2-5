# Development Setup Script for Online Grocery Ordering System
# Author: Chirag Singhal
# Version: 1.0.0

Write-Host "🛒 Online Grocery Ordering System - Development Setup" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green

# Check prerequisites
Write-Host "`n📋 Checking prerequisites..." -ForegroundColor Yellow

# Check Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString() }
    if ($javaVersion -match "17|18|19|20|21") {
        Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
    } else {
        Write-Host "❌ Java 17+ required. Please install Java 17 or higher." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Java not found. Please install Java 17 or higher." -ForegroundColor Red
    exit 1
}

# Check Node.js
try {
    $nodeVersion = node --version
    if ($nodeVersion -match "v1[8-9]|v[2-9][0-9]") {
        Write-Host "✅ Node.js found: $nodeVersion" -ForegroundColor Green
    } else {
        Write-Host "❌ Node.js 18+ required. Please install Node.js 18 or higher." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Node.js not found. Please install Node.js 18 or higher." -ForegroundColor Red
    exit 1
}

# Check PostgreSQL
try {
    $pgVersion = psql --version
    Write-Host "✅ PostgreSQL found: $pgVersion" -ForegroundColor Green
} catch {
    Write-Host "⚠️  PostgreSQL not found. Please install PostgreSQL 13+ or use Docker." -ForegroundColor Yellow
}

# Check Maven
try {
    $mvnVersion = mvn --version | Select-String "Apache Maven" | ForEach-Object { $_.ToString() }
    Write-Host "✅ Maven found: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven not found. Please install Apache Maven." -ForegroundColor Red
    exit 1
}

Write-Host "`n🔧 Setting up the project..." -ForegroundColor Yellow

# Create environment files
Write-Host "📝 Creating environment files..." -ForegroundColor Cyan

# Backend .env
if (-not (Test-Path "backend\.env")) {
    Copy-Item "backend\.env.example" "backend\.env"
    Write-Host "✅ Created backend/.env from template" -ForegroundColor Green
} else {
    Write-Host "ℹ️  backend/.env already exists" -ForegroundColor Blue
}

# Frontend .env
if (-not (Test-Path "frontend\.env")) {
    Copy-Item "frontend\.env.example" "frontend\.env"
    Write-Host "✅ Created frontend/.env from template" -ForegroundColor Green
} else {
    Write-Host "ℹ️  frontend/.env already exists" -ForegroundColor Blue
}

# Install backend dependencies
Write-Host "`n📦 Installing backend dependencies..." -ForegroundColor Cyan
Set-Location backend
try {
    mvn clean install -DskipTests
    Write-Host "✅ Backend dependencies installed successfully" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to install backend dependencies" -ForegroundColor Red
    Set-Location ..
    exit 1
}
Set-Location ..

# Install frontend dependencies
Write-Host "`n📦 Installing frontend dependencies..." -ForegroundColor Cyan
Set-Location frontend
try {
    npm install
    Write-Host "✅ Frontend dependencies installed successfully" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to install frontend dependencies" -ForegroundColor Red
    Set-Location ..
    exit 1
}
Set-Location ..

# Database setup
Write-Host "`n🗄️  Database setup..." -ForegroundColor Cyan
Write-Host "Please ensure PostgreSQL is running and create the database:" -ForegroundColor Yellow
Write-Host "  1. Connect to PostgreSQL: psql -U postgres" -ForegroundColor White
Write-Host "  2. Create database: CREATE DATABASE grocery_db;" -ForegroundColor White
Write-Host "  3. Run schema: \i database-schema.sql" -ForegroundColor White
Write-Host "  Or use Docker: docker-compose up postgres" -ForegroundColor White

Write-Host "`n🎉 Setup completed successfully!" -ForegroundColor Green
Write-Host "=============================" -ForegroundColor Green

Write-Host "`n🚀 To start the development servers:" -ForegroundColor Yellow
Write-Host "Backend:  cd backend && mvn spring-boot:run" -ForegroundColor White
Write-Host "Frontend: cd frontend && npm run dev" -ForegroundColor White
Write-Host "Docker:   docker-compose up" -ForegroundColor White

Write-Host "`n🌐 Application URLs:" -ForegroundColor Yellow
Write-Host "Frontend: http://localhost:5173" -ForegroundColor White
Write-Host "Backend:  http://localhost:8080" -ForegroundColor White
Write-Host "API Docs: http://localhost:8080/swagger-ui.html" -ForegroundColor White

Write-Host "`n📚 Useful commands:" -ForegroundColor Yellow
Write-Host "Run tests:     npm test (frontend) | mvn test (backend)" -ForegroundColor White
Write-Host "Build:         npm run build (frontend) | mvn package (backend)" -ForegroundColor White
Write-Host "Lint:          npm run lint (frontend)" -ForegroundColor White
Write-Host "Format:        npm run format (frontend)" -ForegroundColor White

Write-Host "`n⚠️  Don't forget to:" -ForegroundColor Yellow
Write-Host "1. Update database credentials in backend/.env" -ForegroundColor White
Write-Host "2. Configure email service credentials" -ForegroundColor White
Write-Host "3. Update JWT secret for production" -ForegroundColor White

Write-Host "`nHappy coding! 🎯" -ForegroundColor Green
