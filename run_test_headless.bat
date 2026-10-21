@echo off
chcp 65001 > nul
echo ========================================================
echo   DAFL E-COMMERCE - END-TO-END FLOW AUTOMATION TEST
echo   Che do: Headless (Chay ngam toc do cao cho CI/CD)
echo   Target SUT: http://localhost:3000
echo ========================================================
rem Tu nhan dien thu muc chua file .bat nay - clone o dau cung chay duoc
cd /d "%~dp0"

rem ===== Buoc 0: Chuan hoa JAVA_HOME (Maven bat buoc can bien nay tro dung JDK) =====
if exist "%JAVA_HOME%\bin\java.exe" goto JAVA_OK
rem Uu tien thu muc Temurin JDK 17 mac dinh (bo cai DAFL / winget cai vao day)
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JAVA_HOME=%%D"
if exist "%JAVA_HOME%\bin\java.exe" goto JAVA_OK
rem Java co trong PATH nhung JAVA_HOME thieu/sai -> suy ra tu vi tri java.exe
java -version >nul 2>nul
if errorlevel 1 goto INSTALL_JAVA
for /f "delims=" %%J in ('where java') do (
    set "JAVA_HOME=%%~dpJ"
    goto DERIVED_HOME
)
:DERIVED_HOME
set "JAVA_HOME=%JAVA_HOME:~0,-5%"
if exist "%JAVA_HOME%\bin\java.exe" goto JAVA_OK
echo [LOI] Khong xac dinh duoc JAVA_HOME hop le. Gia tri hien tai: "%JAVA_HOME%"
echo Hay cai lai Temurin JDK 17 hoac dat JAVA_HOME tro vao thu muc JDK (khong phai JRE, khong kem \bin).
pause
exit /b 1
:INSTALL_JAVA
echo [JAVA] Chua tim thay Java - dang tu cai Eclipse Temurin JDK 17 qua winget...
echo [JAVA] Buoc nay can Internet; neu Windows hoi quyen Admin hay bam Yes.
where winget >nul 2>nul
if errorlevel 1 (
    echo [LOI] May khong co winget de tu cai Java. Hay cai JDK 17 thu cong tu https://adoptium.net roi chay lai file nay.
    pause
    exit /b 1
)
winget install --id EclipseAdoptium.Temurin.17.JDK -e --silent --accept-package-agreements --accept-source-agreements
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JAVA_HOME=%%D"
if exist "%JAVA_HOME%\bin\java.exe" goto JAVA_OK
echo [LOI] Da cai Java nhung chua dat duoc JAVA_HOME. Hay DONG cua so nay, mo lai roi chay lai file .bat.
pause
exit /b 1
:JAVA_OK
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo [JAVA] JAVA_HOME = %JAVA_HOME%
java -version

rem ===== Chay test: uu tien Maven Wrapper di kem repo =====
if exist "%~dp0mvnw.cmd" goto USE_WRAPPER
where mvn >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay Maven trong PATH va repo khong co Maven Wrapper.
    pause
    exit /b 1
)
call mvn test -Dtest=EndToEndShoppingFlowTest -Dheadless=true "-Dapp.url=http://localhost:3000" %*
goto END
:USE_WRAPPER
call "%~dp0mvnw.cmd" test -Dtest=EndToEndShoppingFlowTest -Dheadless=true "-Dapp.url=http://localhost:3000" %*
:END
pause
