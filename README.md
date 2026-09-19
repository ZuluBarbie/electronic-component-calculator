# Circuit Desk

A lightweight electronic component calculator written in Java, with a tabbed Swing desktop interface. No external Java libraries, accounts, or network connection are required to run it.

## Calculators

| Calculator | Results |
| --- | --- |
| Ohm's law | Voltage, current, resistance, and power |
| Resistor combinations | Series and parallel equivalent resistance |
| Capacitor combinations | Series and parallel equivalent capacitance |
| LED resistor | Series resistance and resistor power dissipation |
| Voltage divider | Unloaded output voltage |
| RC timing | Time constant and first-order filter cutoff frequency |

Each calculator includes its formula, editable examples, input validation, and engineering-unit results. Press **Calculate** or Enter in an input field.

## Build and run

Install a **JDK 17 or newer**, with `java`, `javac`, and `jar` on your PATH. A graphical desktop is required for the app; tests can run headlessly.

Windows PowerShell:

```powershell
./build.ps1
java -jar build/circuit-desk.jar
```

macOS / Linux:

```sh
sh build.sh
java -jar build/circuit-desk.jar
```

The build compiles the app, runs the automated calculation tests, and produces an executable JAR. If your PowerShell policy blocks scripts, run the individual `javac`, `java`, and `jar` commands shown in `build.ps1`.

## Entering values

Use base SI units as indicated by each field: volts, amps, ohms, farads, and seconds. Optional prefixes are case-sensitive:

| Prefix | Multiplier | Example |
| --- | --- | --- |
| p | 10^-12 | `22p` |
| n | 10^-9 | `100n` |
| u or micro sign | 10^-6 | `4.7u` |
| m | 10^-3 | `20m` |
| k | 10^3 | `4.7k` |
| M | 10^6 | `1M` |
| G | 10^9 | `1G` |

Scientific notation such as `1e-6` also works. Omit unit letters: enter `4.7k`, not `4.7kohm`. Use a decimal point. For component combinations, separate at least two values with commas, semicolons, or spaces, e.g. `1k, 2.2k, 470`.

All inputs must be finite and greater than zero. Zero-valued components, signed circuit quantities, and open circuits are outside this calculator's scope. Results outside the numeric range are rejected.

## Examples and assumptions

- LED: 5 V supply, 2 V forward drop, 20 mA target gives **150 ohms**, dissipating **60 mW** in the resistor. Choose a standard resistance at or above the result and a suitable power rating.
- Divider: 12 V with two 10 kohm resistors gives **6 V**, assuming no output load.
- RC: 10 kohms and 100 nF gives **1 ms**, with cutoff approximately **159.155 Hz**.
- Two 1 kohm resistors give **2 kohms in series** or **500 ohms in parallel**.

These are ideal-component calculations. Actual results depend on component tolerances, temperature, loading, and ratings. The LED model assumes a single constant forward voltage; RC calculations assume an ideal first-order circuit.

## Project layout

- `CircuitMath.java`: pure calculations and SI input parsing.
- `ComponentCalculator.java`: desktop user interface.
- `CircuitMathTest.java`: reference results, invalid input, and numeric edge cases.
- `build.ps1` / `build.sh`: dependency-free build, test, and packaging scripts.
