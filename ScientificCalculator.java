import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class ScientificCalculator extends JFrame implements ActionListener {
    private final JTextField display;
    private final StringBuilder currentInput = new StringBuilder();
    private double firstNumber = 0.0;
    private String operator = null;
    private final DecimalFormat df = new DecimalFormat("0.######");

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setSize(620, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));

        display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 26));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(7, 4, 6, 6));
        String[] labels = {
            "C", "←", "π", "e",
            "7","8","9","/",
            "4","5","6","*",
            "1","2","3","-",
            "0",".","=","+",
            "sin","cos","tan","sqrt",
            "log","ln","^","±"
        };

        for (String s : labels) {
            JButton b = new JButton(s);
            b.setFont(new Font("Arial", Font.BOLD, 18));
            b.addActionListener(this);
            panel.add(b);
        }

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        try {
            switch (cmd) {
                case "C": // Clear everything
                    currentInput.setLength(0);
                    operator = null;
                    firstNumber = 0.0;
                    display.setText("");
                    break;

                case "←": // Backspace
                    if (currentInput.length() > 0) {
                        currentInput.deleteCharAt(currentInput.length() - 1);
                        display.setText(currentInput.toString());
                    }
                    break;

                case "±": 
                    toggleSign();
                    break;

                case "π":
                    appendToInput(df.format(Math.PI));
                    break;

                case "e":
                    appendToInput(df.format(Math.E));
                    break;

                case "+":
                case "-":
                case "*":
                case "/":
                case "^":
                    prepareBinaryOperator(cmd);
                    break;

                case "=":
                    evaluateBinaryOperation();
                    break;

                // Trigonometric and scientific functions
                case "sin":
                case "cos":
                case "tan":
                case "sqrt":
                case "log":
                case "ln":
                    applyUnaryOperation(cmd);
                    break;

                default: // numbers and decimal point
                    if (cmd.matches("[0-9]")) {
                        appendToInput(cmd);
                    } else if (".".equals(cmd)) {
                        if (!currentInput.toString().contains(".")) {
                            if (currentInput.length() == 0) appendToInput("0.");
                            else appendToInput(".");
                        }
                    }
                    break;
            }
        } catch (NumberFormatException ex) {
            showError("Error");
        } catch (ArithmeticException ex) {
            showError("Math Error");
        } catch (Exception ex) {
            showError("Error");
        }
    }

    private void appendToInput(String s) {
        currentInput.append(s);
        display.setText(currentInput.toString());
    }

    private void toggleSign() {
        if (currentInput.length() > 0) {
            if (currentInput.charAt(0) == '-') currentInput.deleteCharAt(0);
            else currentInput.insert(0, '-');
            display.setText(currentInput.toString());
        }
    }

    private void prepareBinaryOperator(String op) {
        double value = getCurrentValueOrThrow();
        firstNumber = value;
        operator = op;
        currentInput.setLength(0);
        display.setText("");
    }

    private void evaluateBinaryOperation() {
        if (operator == null) return;
        double secondNumber = getCurrentValueOrThrow();
        double result = 0;

        switch (operator) {
            case "+": result = firstNumber + secondNumber; break;
            case "-": result = firstNumber - secondNumber; break;
            case "*": result = firstNumber * secondNumber; break;
            case "/": 
                if (secondNumber == 0) throw new ArithmeticException();
                result = firstNumber / secondNumber; break;
            case "^": result = Math.pow(firstNumber, secondNumber); break;
        }
        displayResult(result);
        operator = null;
    }

    private void applyUnaryOperation(String op) {
        double value = getCurrentValueOrThrow();
        double result = 0;

        switch (op) {
            case "sin": result = Math.sin(Math.toRadians(value)); break;
            case "cos": result = Math.cos(Math.toRadians(value)); break;
            case "tan": result = Math.tan(Math.toRadians(value)); break;
            case "sqrt": 
                if (value < 0) throw new ArithmeticException();
                result = Math.sqrt(value); break;
            case "log": 
                if (value <= 0) throw new ArithmeticException();
                result = Math.log10(value); break;
            case "ln": 
                if (value <= 0) throw new ArithmeticException();
                result = Math.log(value); break;
        }
        displayResult(result);
    }

    private double getCurrentValueOrThrow() {
        if (currentInput.length() == 0 && display.getText().isEmpty()) {
            throw new NumberFormatException();
        }
        String text = currentInput.length() > 0 ? currentInput.toString() : display.getText();
        return Double.parseDouble(text);
    }

    private void displayResult(double result) {
        String formatted = df.format(result);
        display.setText(formatted);
        currentInput.setLength(0);
        currentInput.append(formatted);
    }

    private void showError(String msg) {
        display.setText(msg);
        currentInput.setLength(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScientificCalculator calc = new ScientificCalculator();
            calc.setVisible(true);
        });
    }
}
