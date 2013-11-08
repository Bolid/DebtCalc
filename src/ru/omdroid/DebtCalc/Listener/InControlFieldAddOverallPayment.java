package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InControlFieldAddOverallPayment implements TextWatcher {
    NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
    EditText etPayment;
    String beforeText;
    Double defaultPayment;
    View view;
    int position;

    AppData appData = new AppData();

    Arithmetic arithmetic;
    public InControlFieldAddOverallPayment(EditText etPayment, Double defaultPayment, View view, Arithmetic arithmetic){
        this.etPayment = etPayment;
        this.defaultPayment = defaultPayment;
        this.view = view;
        this.arithmetic = arithmetic;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        beforeText = charSequence.toString();
        position = etPayment.getSelectionStart();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        String s = "", oldText = etPayment.getText().toString();

        for (int j = oldText.length(); j > 0; j--) {
            if ("1234567890".contains(String.valueOf(oldText.charAt(j-1))))
                s = oldText.charAt(j-1) + s;
            else if (j-1 == oldText.length() - 2 || j-1 == oldText.length() - 1){
                s = "." + s;
            }
        }
        if (!s.equals(""))
            if (defaultPayment < Double.valueOf(s)){
                appData.setPayment(s, String.valueOf(defaultPayment));
                arithmetic.getOverpaymentAllMonth(Double.valueOf(AppData.DEBT_BALANCE), Double.valueOf(s), true);
                view.invalidate();
            }
            else
                appData.setPayment("", String.valueOf(defaultPayment));

        if (!s.equals("")){
            s = String.valueOf(numberFormat.format(Double.valueOf(s)));
            etPayment.removeTextChangedListener(this);
            etPayment.setText(s);
            etPayment.addTextChangedListener(this);
            if (position + (s.length() - beforeText.length()) < 0)
                etPayment.setSelection(0);
            else
                etPayment.setSelection(position + (s.length() - beforeText.length()));
        }
    }

    @Override
    public synchronized void afterTextChanged(Editable s) {
    }
}
