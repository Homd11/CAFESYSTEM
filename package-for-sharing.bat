@echo off
echo Creating deployment package...
echo.

REM Create a clean copy folder
if exist "cafeteria-deploy" rmdir /s /q "cafeteria-deploy"
mkdir "cafeteria-deploy"

REM Copy essential files
xcopy "src" "cafeteria-deploy\src" /e /i /q
xcopy "lib" "cafeteria-deploy\lib" /e /i /q
copy "docker-compose.yml" "cafeteria-deploy\"
copy "Dockerfile" "cafeteria-deploy\"
copy "init-db.sql" "cafeteria-deploy\"
copy "README.md" "cafeteria-deploy\"

echo.
echo ✅ Deployment package created in 'cafeteria-deploy' folder
echo.
echo Send this folder to anyone with Docker installed.
echo They just need to run: docker-compose up --build
echo.
pause
