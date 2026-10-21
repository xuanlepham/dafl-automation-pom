@echo off
chcp 65001 > nul
echo ========================================================
echo   DAFL E-COMMERCE - END-TO-END FLOW AUTOMATION TEST
echo   Che do: Headless (Chay ngam toc do cao cho CI/CD)
echo   Target SUT: http://localhost:3000
echo ========================================================
rem Tu nhan dien thu muc chua file .bat nay - clone o dau cung chay duoc
cd /d "%~dp0"

rem ===== Buoc 0: Xac dinh JAVA_HOME (bat buoc la JDK, co javac) =====
call :VALIDATE_JAVA_HOME
if not errorlevel 1 goto JAVA_OK
rem Cach 1: quet cac thu muc cai JDK pho bien cua moi hang
for %%V in ("Eclipse Adoptium" "Java" "Microsoft" "BellSoft" "Amazon Corretto" "Zulu") do (
    for /d %%D in ("C:\Program Files\%%~V\jdk*") do (
        set "JAVA_HOME=%%D"
        call :VALIDATE_JAVA_HOME
        if not errorlevel 1 goto JAVA_OK
    )
    for /d %%D in ("C:\Program Files\%%~V\LibericaJDK-*") do (
        set "JAVA_HOME=%%D"
        call :VALIDATE_JAVA_HOME
        if not errorlevel 1 goto JAVA_OK
    )
)
rem Cach 2: hoi thang Java dang chay qua thuoc tinh java.home (dung voi moi kieu PATH/shim)
java -version >nul 2>nul
if errorlevel 1 goto INSTALL_JAVA
for /f "tokens=1,* delims==" %%A in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /C:"java.home"') do set "JAVA_HOME=%%B"
if "%JAVA_HOME:~0,1%"==" " set "JAVA_HOME=%JAVA_HOME:~1%"
call :VALIDATE_JAVA_HOME
if not errorlevel 1 goto JAVA_OK
echo [LOI] Co lenh java nhung khong phai JDK day du (thieu javac) hoac khong doc duoc java.home.
goto SHOW_DIAG
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
call :VALIDATE_JAVA_HOME
if not errorlevel 1 goto JAVA_OK
echo [LOI] Da chay cai Java nhung chua xac dinh duoc JAVA_HOME hop le.
:SHOW_DIAG
echo ----- Thong tin chan doan (chup lai neu can ho tro) -----
echo JAVA_HOME hien tai: "%JAVA_HOME%"
where java
java -version
echo ----------------------------------------------------------
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
exit /b 0

rem ===== Ham kiem tra: JAVA_HOME hop le = co ca java.exe lan javac.exe =====
:VALIDATE_JAVA_HOME
if not defined JAVA_HOME exit /b 1
if exist "%JAVA_HOME%\bin\java.exe" if exist "%JAVA_HOME%\bin\javac.exe" exit /b 0
exit /b 1
