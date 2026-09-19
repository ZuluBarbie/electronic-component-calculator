import java.awt.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Dependency-free Swing desktop application. */
public final class ComponentCalculator {
    private interface Calculation { String run(String[] values); }
    private static final Color NAVY = new Color(19, 36, 57);
    private static final Color TEAL = new Color(0, 116, 116);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { /* Swing's default remains available. */ }
            JFrame frame = new JFrame("Circuit Desk | Electronic Component Calculator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel root = new JPanel(new BorderLayout(0, 18));
            root.setBorder(new EmptyBorder(24, 24, 20, 24));
            JPanel heading = new JPanel(new GridLayout(0, 1, 0, 6));
            JLabel title = new JLabel("CIRCUIT DESK");
            title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            title.setForeground(NAVY);
            heading.add(title);
            heading.add(new JLabel("Electronic component calculator  /  Java desktop edition"));
            heading.add(new JLabel("SI prefixes: p, n, u, m, k, M, G  |  Examples: 4.7k ohms, 100n farads, 20m amps"));
            root.add(heading, BorderLayout.NORTH);
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Ohm's law", ohms());
            tabs.addTab("Components", components());
            tabs.addTab("LED resistor", form("One LED with a series resistor", "R = (Vs - Vf) / I; resistor dissipation P = (Vs - Vf) x I",
                new String[]{"Supply voltage (V)", "LED forward voltage (V)", "Target current (A)"},
                new String[]{"5", "2", "20m"}, v -> {
                    double supply = n(v[0]), forward = n(v[1]), current = n(v[2]);
                    double r = CircuitMath.ledResistor(supply, forward, current);
                    return "Required resistance: " + fmt(r, "ohm") + "\nResistor dissipation: "
                        + fmt(CircuitMath.power(supply - forward, current), "W")
                        + "\n\nChoose a standard resistor at or above this resistance.\nChoose a power rating with suitable thermal margin.";
                }));
            tabs.addTab("Voltage divider", form("Unloaded voltage divider", "Vout = Vin x R2 / (R1 + R2)",
                new String[]{"Input voltage (V)", "R1: input to output (ohm)", "R2: output to ground (ohm)"},
                new String[]{"12", "10k", "10k"}, v -> "Output voltage: "
                    + fmt(CircuitMath.divider(n(v[0]), n(v[1]), n(v[2])), "V")
                    + "\n\nAssumes no load connected to the output."));
            tabs.addTab("RC timing", form("RC time constant and filter cutoff", "tau = R x C; fc = 1 / (2 x pi x R x C)",
                new String[]{"Resistance (ohm)", "Capacitance (F)"}, new String[]{"10k", "100n"},
                v -> "Time constant: " + fmt(CircuitMath.timeConstant(n(v[0]), n(v[1])), "s")
                    + "\nCutoff frequency: " + fmt(CircuitMath.cutoff(n(v[0]), n(v[1])), "Hz")
                    + "\n\nAfter one time constant, an initially uncharged capacitor\nreaches about 63.2% of the supply voltage."));
            root.add(tabs, BorderLayout.CENTER);
            root.add(new JLabel("Ideal component models. Check tolerances and component ratings in your circuit."), BorderLayout.SOUTH);
            frame.setContentPane(root);
            frame.setMinimumSize(new Dimension(780, 610));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JPanel ohms() {
        JTabbedPane choices = new JTabbedPane();
        choices.addTab("Voltage", form("Find voltage", "V = I x R", new String[]{"Current (A)", "Resistance (ohm)"},
            new String[]{"20m", "470"}, v -> fmt(CircuitMath.voltage(n(v[0]), n(v[1])), "V")));
        choices.addTab("Current", form("Find current", "I = V / R", new String[]{"Voltage (V)", "Resistance (ohm)"},
            new String[]{"9", "1k"}, v -> fmt(CircuitMath.current(n(v[0]), n(v[1])), "A")));
        choices.addTab("Resistance", form("Find resistance", "R = V / I", new String[]{"Voltage (V)", "Current (A)"},
            new String[]{"5", "20m"}, v -> fmt(CircuitMath.resistance(n(v[0]), n(v[1])), "ohm")));
        choices.addTab("Power", form("Find electrical power", "P = V x I", new String[]{"Voltage (V)", "Current (A)"},
            new String[]{"12", "500m"}, v -> fmt(CircuitMath.power(n(v[0]), n(v[1])), "W")));
        JPanel panel = new JPanel(new BorderLayout()); panel.add(choices); return panel;
    }

    private static JPanel components() {
        JTabbedPane choices = new JTabbedPane();
        choices.addTab("Resistors", form("Equivalent resistance", "Series: sum(R); parallel: 1 / sum(1/R)",
            new String[]{"Resistors (ohm), separated by commas"}, new String[]{"1k, 2.2k, 470"}, v -> {
                double[] values = CircuitMath.parseList(v[0]);
                return "Series: " + fmt(CircuitMath.sum(values), "ohm") + "\nParallel: " + fmt(CircuitMath.reciprocalSum(values), "ohm");
            }));
        choices.addTab("Capacitors", form("Equivalent capacitance", "Parallel: sum(C); series: 1 / sum(1/C)",
            new String[]{"Capacitors (F), separated by commas"}, new String[]{"100n, 220n, 1u"}, v -> {
                double[] values = CircuitMath.parseList(v[0]);
                return "Series: " + fmt(CircuitMath.reciprocalSum(values), "F") + "\nParallel: " + fmt(CircuitMath.sum(values), "F");
            }));
        JPanel panel = new JPanel(new BorderLayout()); panel.add(choices); return panel;
    }

    private static JPanel form(String title, String formula, String[] labels, String[] defaults, Calculation calculation) {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel top = new JPanel(new GridLayout(0, 1, 0, 6));
        JLabel heading = new JLabel(title); heading.setFont(heading.getFont().deriveFont(Font.BOLD, 18f));
        top.add(heading); top.add(new JLabel(formula));
        JPanel fields = new JPanel(new GridLayout(0, 2, 12, 10));
        JTextField[] inputs = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            inputs[i] = new JTextField(defaults[i], 20);
            JLabel label = new JLabel(labels[i]); label.setLabelFor(inputs[i]);
            fields.add(label); fields.add(inputs[i]);
        }
        JPanel middle = new JPanel(new BorderLayout(0, 14)); middle.add(fields, BorderLayout.NORTH);
        JTextArea output = new JTextArea(7, 48); output.setEditable(false);
        output.setLineWrap(true); output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        output.setBorder(new EmptyBorder(12, 12, 12, 12));
        output.getAccessibleContext().setAccessibleName("Calculation result");
        Runnable calculate = () -> {
            String[] values = new String[inputs.length];
            for (int i = 0; i < values.length; i++) values[i] = inputs[i].getText();
            try { output.setForeground(NAVY); output.setText(calculation.run(values)); }
            catch (IllegalArgumentException ex) { output.setForeground(new Color(160, 32, 32)); output.setText(ex.getMessage()); }
        };
        JButton button = new JButton("Calculate"); button.setForeground(TEAL);
        button.addActionListener(e -> calculate.run());
        for (JTextField input : inputs) input.addActionListener(e -> calculate.run());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); actions.add(button);
        middle.add(actions, BorderLayout.CENTER); middle.add(new JScrollPane(output), BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH); panel.add(middle, BorderLayout.CENTER);
        calculate.run(); return panel;
    }

    private static double n(String value) { return CircuitMath.parse(value); }
    private static String fmt(double value, String unit) {
        String[] prefixes = {"p", "n", "u", "m", "", "k", "M", "G"};
        int exponent = (int) Math.floor(Math.log10(value) / 3);
        if (exponent < -4 || exponent > 3) return String.format(Locale.ROOT, "%.6g %s", value, unit);
        return String.format(Locale.ROOT, "%.6g %s%s", value / Math.pow(1000, exponent), prefixes[exponent + 4], unit);
    }
}
