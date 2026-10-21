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

rem ===== Buoc 0: Kiem tra Java (JDK) - thieu thi TU CAI bang winget =====
java -version >nul 2>nul
if not errorlevel 1 goto JAVA_OK
echo [JAVA] Chua tim thay Java - dang tu cai Eclipse Temurin JDK 17 qua winget...
echo [JAVA] Buoc nay can Internet; neu Windows hoi quyen Admin hay bam Yes.
where winget >nul 2>nul
if errorlevel 1 (
    echo [LOI] May khong co winget de tu cai Java. Hay cai JDK 17 thu cong tu https://adoptium.net roi chay lai file nay.
    pause
    exit /b 1
)
winget install --id EclipseAdoptium.Temurin.17.JDK -e --silent --accept-package-agreements --accept-source-agreements
rem Nap lai JAVA_HOME va PATH ngay trong phien nay, khong can mo lai cua so
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JAVA_HOME=%%D"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
java -version >nul 2>nul
if errorlevel 1 (
    echo [LOI] Da cai Java nhung phien nay chua nhan dien duoc. Hay DONG cua so nay, mo lai roi chay lai file .bat.
    pause
    exit /b 1
)
:JAVA_OK
echo [JAVA] San sang.

rem ===== Chay test: uu tien Maven Wrapper di kem repo =====
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
