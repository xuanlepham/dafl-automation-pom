@echo off
chcp 65001 > nul
echo ========================================================
echo   DAFL E-COMMERCE - END-TO-END FLOW AUTOMATION TEST
echo   Che do: Headless (Chay ngam toc do cao cho CI/CD)
echo   Target SUT: http://localhost:3000
echo ========================================================
rem Tu nhan dien thu muc chua file .bat nay - clone o dau cung chay duoc
cd /d "%~dp0"
where mvn >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay Maven trong PATH. Hay cai Maven 3.9+ hoac them thu muc bin cua Maven vao PATH roi chay lai.
    pause
    exit /b 1
)
mvn test -Dtest=EndToEndShoppingFlowTest -Dheadless=true "-Dapp.url=http://localhost:3000" %*
pause
