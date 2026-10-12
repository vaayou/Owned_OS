@echo off
setlocal

if "%~1"=="" (
    set DOCKER_USER=heavycoder
) else (
    set DOCKER_USER=%~1
)

echo ==========================================================
echo  Building and Pushing Owned OS Images to Docker Hub
echo  Username: %DOCKER_USER%
echo  Target Architecture: linux/amd64 (EC2 compatible)
echo ==========================================================

echo [1/6] Building & Pushing Eureka Service...
docker build --platform linux/amd64 -t %DOCKER_USER%/owned-eureka:latest ./eureka-service
if errorlevel 1 goto error
docker push %DOCKER_USER%/owned-eureka:latest

echo [2/6] Building & Pushing Gateway...
docker build --platform linux/amd64 -t %DOCKER_USER%/owned-gateway:latest ./gateway
if errorlevel 1 goto error
docker push %DOCKER_USER%/owned-gateway:latest

echo [3/6] Building & Pushing User Service...
docker build --platform linux/amd64 -t %DOCKER_USER%/owned-user-service:latest ./user-service
if errorlevel 1 goto error
docker push %DOCKER_USER%/owned-user-service:latest

echo [4/6] Building & Pushing Commerce Service...
docker build --platform linux/amd64 -t %DOCKER_USER%/owned-commerce-service:latest ./commerce-service
if errorlevel 1 goto error
docker push %DOCKER_USER%/owned-commerce-service:latest

echo [5/6] Building & Pushing Collection Service...
docker build --platform linux/amd64 -t %DOCKER_USER%/owned-collection-service:latest ./collection-service
if errorlevel 1 goto error
docker push %DOCKER_USER%/owned-collection-service:latest

echo [6/6] Building & Pushing React Frontend...
docker build --platform linux/amd64 -t %DOCKER_USER%/owned-frontend:latest ./owned-frontend
if errorlevel 1 goto error
docker push %DOCKER_USER%/owned-frontend:latest

echo ==========================================================
echo  SUCCESS: All 6 images pushed to Docker Hub!
echo ==========================================================
exit /b 0

:error
echo [ERROR] Build failed! Please check error output above.
exit /b 1
