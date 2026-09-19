$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
New-Item -ItemType Directory -Force build/classes | Out-Null
& javac -encoding UTF-8 -d build/classes CircuitMath.java ComponentCalculator.java CircuitMathTest.java
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
& java -cp build/classes CircuitMathTest
if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
& jar --create --file build/circuit-desk.jar --main-class ComponentCalculator -C build/classes CircuitMath.class -C build/classes ComponentCalculator.class -C build/classes 'ComponentCalculator$Calculation.class'
if ($LASTEXITCODE -ne 0) { throw 'Packaging failed' }
Write-Host 'Ready: java -jar build/circuit-desk.jar'
