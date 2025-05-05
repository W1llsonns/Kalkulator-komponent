/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package C1;

/**
 *
 * @author Willsonowie
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Calculator extends JPanel implements Serializable {
    private JTextField display;
    private double firstNumber = 0;
    private double secondNumber = 0;
    private double result = 0;
    private String operator = "";
    private boolean startNewNumber = true;
    private List<CalcListener> listeners = new ArrayList<>();

    public Calculator() {
        setLayout(new BorderLayout());

        // Wyświetlacz
        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        add(display, BorderLayout.NORTH);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));

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
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if ("0123456789".contains(command)) {
                if (startNewNumber) {
                    display.setText(command);
                    startNewNumber = false;
                } else {
                    display.setText(display.getText() + command);
                }
            } else if ("+-*/".contains(command)) {
                firstNumber = Double.parseDouble(display.getText());
                operator = command;
                startNewNumber = true;
            } else if (command.equals("=")) {
                secondNumber = Double.parseDouble(display.getText());

                switch (operator) {
                    case "+": result = firstNumber + secondNumber; break;
                    case "-": result = firstNumber - secondNumber; break;
                    case "*": result = firstNumber * secondNumber; break;
                    case "/": result = secondNumber != 0 ? firstNumber / secondNumber : 0; break;
                    default: result = 0;
                }

                display.setText("" + result);
                notifyCalculationListeners(result);
                startNewNumber = true;
            } else if (command.equals("C")) {
                display.setText("0");
                firstNumber = 0;
                secondNumber = 0;
                result = 0;
                operator = "";
                startNewNumber = true;
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

    // ======== GETTERY i SETTERY ========

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
}
