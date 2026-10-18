@echo off
chcp 65001 > nul
echo ========================================================
echo   DAFL E-COMMERCE - END-TO-END FLOW AUTOMATION TEST
echo   Che do: Headless (Chay ngam toc do cao cho CI/CD)
echo   Target SUT: http://localhost:3000
echo ========================================================
cd /d "D:\DO_AN_TOT_NGHIEP\dafl_automation_pom"
"D:\DO_AN_TOT_NGHIEP\maven\bin\mvn.cmd" test -Dtest=EndToEndShoppingFlowTest -Dheadless=true "-Dapp.url=http://localhost:3000" %*
pause
