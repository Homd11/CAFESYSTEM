@echo off
echo Downloading MySQL JDBC Driver...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar' -OutFile 'lib\mysql-connector-java-8.0.33.jar'; if (Test-Path 'lib\mysql-connector-java-8.0.33.jar') { Write-Host 'Download successful!' } else { Write-Host 'Download failed!' }}"
pause

