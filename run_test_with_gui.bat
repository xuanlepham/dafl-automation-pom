@echo off
chcp 65001 > nul
echo ========================================================
echo   DAFL E-COMMERCE - END-TO-END FLOW AUTOMATION TEST
echo   Che do: Truc quan co giao dien (Chrome Desktop GUI)
echo   Trinh duyet duy nhat mo tu Buoc 1 -> Buoc 7
echo   Target SUT: http://localhost:3000
echo ========================================================
cd /d "D:\DO_AN_TOT_NGHIEP\dafl_automation_pom"
"D:\DO_AN_TOT_NGHIEP\maven\bin\mvn.cmd" test -Dtest=EndToEndShoppingFlowTest -Dheadless=false "-Dapp.url=http://localhost:3000" %*
pause
