@echo off
FOR /F "usebackq tokens=1,* delims==" %%A IN ("D:\MilestoneMetrics\code\backend\.env") DO (
    IF NOT "%%A"=="" IF NOT "%%A:~0,1%"=="#" SET %%A=%%B
)
cd /d D:\MilestoneMetrics\code\backend
call mvnw.cmd spring-boot:run
pause
