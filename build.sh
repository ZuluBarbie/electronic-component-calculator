#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes
javac -encoding UTF-8 -d build/classes CircuitMath.java ComponentCalculator.java CircuitMathTest.java
java -cp build/classes CircuitMathTest
jar --create --file build/circuit-desk.jar --main-class ComponentCalculator -C build/classes CircuitMath.class -C build/classes ComponentCalculator.class -C build/classes 'ComponentCalculator$Calculation.class'
echo 'Ready: java -jar build/circuit-desk.jar'
