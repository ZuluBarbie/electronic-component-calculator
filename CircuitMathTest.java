public final class CircuitMathTest {
    private static int checks;
    private static void equal(double expected, double actual) {
        checks++;
        if (!Double.isFinite(actual) || Math.abs(expected - actual) > Math.abs(expected) * 1e-10)
            throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    private static void rejects(Runnable action) {
        checks++;
        try { action.run(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Expected invalid input to be rejected");
    }
    public static void main(String[] args) {
        equal(9.4, CircuitMath.voltage(.02, 470));
        equal(.009, CircuitMath.current(9, 1000));
        equal(250, CircuitMath.resistance(5, .02));
        equal(6, CircuitMath.power(12, .5));
        equal(3670, CircuitMath.sum(1000, 2200, 470));
        equal(500, CircuitMath.reciprocalSum(1000, 1000));
        equal(50e-9, CircuitMath.reciprocalSum(100e-9, 100e-9));
        equal(320e-9, CircuitMath.sum(100e-9, 220e-9));
        equal(6, CircuitMath.divider(12, 10000, 10000));
        equal(150, CircuitMath.ledResistor(5, 2, .02));
        equal(.001, CircuitMath.timeConstant(10000, 100e-9));
        equal(159.15494309189535, CircuitMath.cutoff(10000, 100e-9));
        equal(4700, CircuitMath.parse("4.7k"));
        equal(.001, CircuitMath.parse("1m"));
        equal(1e6, CircuitMath.parse("1M"));
        equal(1e-6, CircuitMath.parse("1u"));
        equal(1e-6, CircuitMath.parse("1\u00b5"));
        equal(1e-6, CircuitMath.parse("1e-6"));
        equal(1e-12, CircuitMath.parse("1p"));
        equal(1e9, CircuitMath.parse("1G"));
        equal(3300, CircuitMath.sum(CircuitMath.parseList("1k, 2k; 300")));
        equal(5e307, CircuitMath.reciprocalSum(1e308, 1e308));
        equal(6, CircuitMath.divider(12, 1e308, 1e308));
        for (String invalid : new String[]{"", "-1", "0", "NaN", "Infinity", "1e999", "10ohm", "1e-999"})
            rejects(() -> CircuitMath.parse(invalid));
        rejects(() -> CircuitMath.current(1, 0));
        rejects(() -> CircuitMath.sum(100));
        rejects(() -> CircuitMath.reciprocalSum(100));
        rejects(() -> CircuitMath.ledResistor(2, 2, .02));
        rejects(() -> CircuitMath.ledResistor(2, 3, .02));
        rejects(() -> CircuitMath.sum(1e308, 1e308));
        rejects(() -> CircuitMath.voltage(1e308, 1e308));
        rejects(() -> CircuitMath.sum(CircuitMath.parseList("1k,")));
        System.out.println("PASS: " + checks + " calculation and validation checks");
    }
}
