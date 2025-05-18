package C1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Calculator extends JPanel implements Serializable {
    private JTextField display;
    private double firstNumber = 2;   // Default
    private double secondNumber = 3;  // Default
    private double result = 0;
    private String operator = "";
    private boolean startNewNumber = true;
    private List<CalcListener> listeners = new ArrayList<>();

    private boolean btnClearEnabled = true;
    private boolean displayEnabled = true;
    private boolean preschoolMode = false;
    private boolean keyboardEnabled = true;

    private JButton clearButton;
    private JPanel mainPanel;
    private JPanel classicPanel;
    private JPanel preschoolPanel;
    private CardLayout layout;
    
    private JButton plusButton;
    private JButton minusButton;

    public Calculator() {
        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        createClassicPanel();
        createPreschoolPanel();

        display = new JTextField(String.valueOf(result));
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(display, BorderLayout.CENTER);

        if (displayEnabled) {
            add(topPanel, BorderLayout.NORTH);
        }


        add(mainPanel, BorderLayout.CENTER);
        updateMode();
        setupKeyboardListener();
    }

    private void createClassicPanel() {
        classicPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", "=", "+", "C"
        };

        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(new ButtonClickListener());

            if (label.equals("C")) {
                clearButton = button;
                clearButton.setVisible(btnClearEnabled);
            }

            classicPanel.add(button);
        }

        mainPanel.add(classicPanel, "CLASSIC");
    }

    private String preschoolOperation = "+"; // Domyślna operacja

    private void createPreschoolPanel() {
        preschoolPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        JPanel rightPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Create shared toggle logic for button clicks
        ItemListener toggleListener = e -> {
            JToggleButton btn = (JToggleButton) e.getSource();
            boolean selected = btn.isSelected();

            if (selected) {
                if (preschoolOperation.equals("+")) {
                    result += 1;
                } else {
                    result -= 1;
                }
            } else {
                if (preschoolOperation.equals("+")) {
                    result -= 1;
                } else {
                    result += 1;
                }
            }
            display.setText(String.valueOf(result));
            notifyCalculationListeners(result);
        };

        // Lewa kolumna (+1)
        for (int i = 0; i < 5; i++) {
            JToggleButton oneLeft = new JToggleButton("1");
            oneLeft.setFont(new Font("Arial", Font.BOLD, 32));
            oneLeft.addItemListener(toggleListener);
            leftPanel.add(oneLeft);
        }

        // Prawa kolumna (-1)
        for (int i = 0; i < 5; i++) {
            JToggleButton oneRight = new JToggleButton("1");
            oneRight.setFont(new Font("Arial", Font.BOLD, 32));
            oneRight.addItemListener(toggleListener);
            rightPanel.add(oneRight);
        }

        // Przycisk "+"
        plusButton = new JButton("+");
        plusButton.setFont(new Font("Arial", Font.BOLD, 36));
        plusButton.addActionListener(e -> {
            preschoolOperation = "+";
            plusButton.setBackground(Color.GREEN);
            minusButton.setBackground(null);
        });

        // Przycisk "-"
        minusButton = new JButton("-");
        minusButton.setFont(new Font("Arial", Font.BOLD, 36));
        minusButton.addActionListener(e -> {
            preschoolOperation = "-";
            minusButton.setBackground(Color.RED);
            plusButton.setBackground(null);
        });

        centerPanel.add(plusButton);
        centerPanel.add(minusButton);

        preschoolPanel.add(leftPanel, BorderLayout.WEST);
        preschoolPanel.add(centerPanel, BorderLayout.CENTER);
        preschoolPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(preschoolPanel, "PRESCHOOL");
    }



    private void updateMode() {
        if (preschoolMode) {
            layout.show(mainPanel, "PRESCHOOL");
        } else {
            layout.show(mainPanel, "CLASSIC");
        }
    }

    private void setupKeyboardListener() {
        if (!keyboardEnabled) return;

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();
                if (Character.isDigit(key)) {
                    processDigit(String.valueOf(key));
                } else if ("+-*/".indexOf(key) >= 0) {
                    processOperator(String.valueOf(key));
                } else if (key == '=') {
                    calculateResult();
                } else if (key == 'c' || key == 'C') {
                    clear();
                }
            }
        });
    }

    private void processDigit(String digit) {
        if (startNewNumber) {
            display.setText(digit);
            startNewNumber = false;
        } else {
            display.setText(display.getText() + digit);
        }
    }

    private void processOperator(String op) {
        firstNumber = Double.parseDouble(display.getText());
        operator = op;
        startNewNumber = true;
    }

    private void calculateResult() {
        secondNumber = Double.parseDouble(display.getText());
        switch (operator) {
            case "+": result = firstNumber + secondNumber; break;
            case "-": result = firstNumber - secondNumber; break;
            case "*": result = firstNumber * secondNumber; break;
            case "/": result = secondNumber != 0 ? firstNumber / secondNumber : 0; break;
            default: result = 0;
        }
        display.setText(String.valueOf(result));
        notifyCalculationListeners(result);
        startNewNumber = true;
    }

    private void clear() {
        display.setText("0");
        firstNumber = 0;
        secondNumber = 0;
        result = 0;
        operator = "";
        startNewNumber = true;
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if ("0123456789".contains(command)) {
                processDigit(command);
            } else if ("+-*/".contains(command)) {
                processOperator(command);
            } else if (command.equals("=")) {
                calculateResult();
            } else if (command.equals("C")) {
                clear();
            }
        }
    }

    public interface CalcListener {
        void onCalculationPerformed(double result);
    }

    public void addCalculationListener(CalcListener listener) {
        listeners.add(listener);
    }

    private void notifyCalculationListeners(double result) {
        for (CalcListener listener : listeners) {
            listener.onCalculationPerformed(result);
        }
    }

    // ======== Gettery i Settery ========

    public double getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(double firstNumber) {
        this.firstNumber = firstNumber;
        display.setText(String.valueOf(firstNumber));
    }

    public double getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(double secondNumber) {
        this.secondNumber = secondNumber;
        display.setText(String.valueOf(secondNumber));
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
        display.setText(String.valueOf(result));
    }

    public boolean isBtnClearEnabled() {
        return btnClearEnabled;
    }

    public void setBtnClearEnabled(boolean btnClearEnabled) {
        this.btnClearEnabled = btnClearEnabled;
        if (clearButton != null) {
            clearButton.setVisible(btnClearEnabled);
        }
    }

    public boolean isDisplayEnabled() {
        return displayEnabled;
    }

    public void setDisplayEnabled(boolean displayEnabled) {
        this.displayEnabled = displayEnabled;
        if (display != null) {
            display.setVisible(displayEnabled);
        }
    }

    public boolean isPreschoolMode() {
        return preschoolMode;
    }

    public void setPreschoolMode(boolean preschoolMode) {
        this.preschoolMode = preschoolMode;
        updateMode();
    }

    public boolean isKeyboardEnabled() {
        return keyboardEnabled;
    }

    public void setKeyboardEnabled(boolean keyboardEnabled) {
        this.keyboardEnabled = keyboardEnabled;
        // pełna implementacja mogłaby przełączyć nasłuchiwanie
    }
}
