@echo off
chcp 65001 > nul
echo ========================================================
echo   DAFL E-COMMERCE - END-TO-END FLOW AUTOMATION TEST
echo   Che do: Truc quan co giao dien (Chrome Desktop GUI)
echo   Trinh duyet duy nhat mo tu Buoc 1 -> Buoc 7
echo   Target SUT: http://localhost:3000
echo ========================================================
rem Tu nhan dien thu muc chua file .bat nay - clone o dau cung chay duoc
cd /d "%~dp0"
rem Uu tien Maven Wrapper (mvnw.cmd) di kem repo - may chi can JDK, khong can cai Maven
if exist "%~dp0mvnw.cmd" goto USE_WRAPPER
where mvn >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay Maven trong PATH va repo khong co Maven Wrapper.
    pause
    exit /b 1
)
call mvn test -Dtest=EndToEndShoppingFlowTest -Dheadless=false "-Dapp.url=http://localhost:3000" %*
goto END
:USE_WRAPPER
call "%~dp0mvnw.cmd" test -Dtest=EndToEndShoppingFlowTest -Dheadless=false "-Dapp.url=http://localhost:3000" %*
:END
pause
