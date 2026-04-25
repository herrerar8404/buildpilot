@echo off
set PGPASSWORD=admin
echo Running BuildPilot DB fix...
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d buildpilot -f "C:\proyectos\buildpilot\fix_db.sql"
echo.
echo Done. Now start the Spring Boot app.
pause
