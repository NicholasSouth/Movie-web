@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0test.ps1" %*
if errorlevel 1 exit /b 1
exit /b 0
