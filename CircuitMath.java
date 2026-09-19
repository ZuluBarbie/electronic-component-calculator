/** Pure calculations using SI units (V, A, ohms, F, seconds). */
public final class CircuitMath {
    private CircuitMath() {}

    public static double positive(double value) {
        if (!Double.isFinite(value) || value <= 0)
            throw new IllegalArgumentException("Use finite values greater than zero.");
        return value;
    }

    private static double result(double value) {
        if (!Double.isFinite(value) || value <= 0)
            throw new IllegalArgumentException("Result is outside the supported numeric range.");
        return value;
    }

    public static double voltage(double current, double resistance) {
        return result(positive(current) * positive(resistance));
    }
    public static double current(double voltage, double resistance) {
        return result(positive(voltage) / positive(resistance));
    }
    public static double resistance(double voltage, double current) {
        return result(positive(voltage) / positive(current));
    }
    public static double power(double voltage, double current) {
        return result(positive(voltage) * positive(current));
    }
    public static double sum(double... values) {
        if (values.length < 2) throw new IllegalArgumentException("Enter at least two components.");
        double total = 0;
        for (double value : values) total += positive(value);
        return result(total);
    }
    public static double reciprocalSum(double... values) {
        if (values.length < 2) throw new IllegalArgumentException("Enter at least two components.");
        double minimum = Double.POSITIVE_INFINITY;
        for (double value : values) minimum = Math.min(minimum, positive(value));
        double scaled = 0;
        for (double value : values) scaled += minimum / value;
        return result(minimum / scaled);
    }
    public static double divider(double supply, double top, double bottom) {
        positive(supply); positive(top); positive(bottom);
        double scale = Math.max(top, bottom);
        return result(supply * ((bottom / scale) / (top / scale + bottom / scale)));
    }
    public static double ledResistor(double supply, double forward, double current) {
        positive(supply); positive(forward); positive(current);
        if (supply <= forward) throw new IllegalArgumentException("Supply voltage must exceed LED forward voltage.");
        return result((supply - forward) / current);
    }
    public static double timeConstant(double resistance, double capacitance) {
        return result(positive(resistance) * positive(capacitance));
    }
    public static double cutoff(double resistance, double capacitance) {
        return result((1 / (2 * Math.PI)) / timeConstant(resistance, capacitance));
    }

    /** Optional, case-sensitive SI suffix: p, n, u, micro sign, m, k, M, G. */
    public static double parse(String input) {
        String text = input.trim();
        if (text.isEmpty()) throw new IllegalArgumentException("Enter a value in every field.");
        double multiplier = 1;
        char suffix = text.charAt(text.length() - 1);
        switch (suffix) {
            case 'p': multiplier = 1e-12; break;
            case 'n': multiplier = 1e-9; break;
            case 'u': case '\u00b5': case '\u03bc': multiplier = 1e-6; break;
            case 'm': multiplier = 1e-3; break;
            case 'k': multiplier = 1e3; break;
            case 'M': multiplier = 1e6; break;
            case 'G': multiplier = 1e9; break;
            default: break;
        }
        if (multiplier != 1) text = text.substring(0, text.length() - 1).trim();
        try { return positive(Double.parseDouble(text) * multiplier); }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Use numbers such as 470, 4.7k, 100n, or 1e-6. Omit unit letters.");
        }
    }
    public static double[] parseList(String input) {
        String[] parts = input.trim().split("[,;\\s]+", -1);
        double[] values = new double[parts.length];
        for (int i = 0; i < parts.length; i++) values[i] = parse(parts[i]);
        return values;
    }
}
