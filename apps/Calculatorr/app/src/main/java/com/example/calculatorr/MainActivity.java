package com.example.calculatorr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText resultEditText;
    private StringBuilder currentExpression;
    private double firstNumber = 0;
    private String operation = "";
    private boolean isNewNumber = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set current date
        TextView dateTextView = findViewById(R.id.dateTextView);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTextView.setText(sdf.format(new Date()));

        resultEditText = findViewById(R.id.resultEditText);
        currentExpression = new StringBuilder();

        // Initialize buttons
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide,
                R.id.btnEquals, R.id.btnClear
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "C":
                currentExpression.setLength(0);
                resultEditText.setText("0");
                firstNumber = 0;
                operation = "";
                isNewNumber = true;
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                if (!operation.isEmpty() && !isNewNumber) {
                    calculateResult();
                }
                operation = buttonText;
                firstNumber = Double.parseDouble(currentExpression.toString());
                currentExpression.append(buttonText);
                resultEditText.setText(currentExpression.toString());
                isNewNumber = true;
                break;
            case "=":
                if (!operation.isEmpty() && !isNewNumber) {
                    calculateResult();
                    operation = "";
                }
                isNewNumber = true;
                break;
            default:
                if (isNewNumber) {
                    if (!operation.isEmpty()) {
                        currentExpression.append(buttonText);
                    } else {
                        currentExpression.setLength(0);
                        currentExpression.append(buttonText);
                    }
                    isNewNumber = false;
                } else {
                    currentExpression.append(buttonText);
                }
                resultEditText.setText(currentExpression.toString());
                break;
        }
    }

    private void calculateResult() {
        if (!operation.isEmpty()) {
            String[] parts = currentExpression.toString().split("\\" + operation);
            if (parts.length > 1) {
                double secondNumber = Double.parseDouble(parts[1]);
                double result = 0;
                switch (operation) {
                    case "+":
                        result = firstNumber + secondNumber;
                        break;
                    case "-":
                        result = firstNumber - secondNumber;
                        break;
                    case "*":
                        result = firstNumber * secondNumber;
                        break;
                    case "/":
                        if (secondNumber != 0) {
                            result = firstNumber / secondNumber;
                        } else {
                            result = Double.POSITIVE_INFINITY;
                        }
                        break;
                }
                currentExpression.setLength(0);
                currentExpression.append(result);
                resultEditText.setText(currentExpression.toString());
                firstNumber = result;
            }
 }
}
}
