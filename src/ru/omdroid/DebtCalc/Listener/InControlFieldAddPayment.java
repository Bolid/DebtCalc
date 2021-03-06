package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.Forms.InfoDebt;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InControlFieldAddPayment implements TextWatcher {
    NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
    EditText etSumCredit;
    Button button;
    Menu menu;
    String beforeText;
    Double defaultPayment;

    InfoDebt.WriteDataInField writeDataInField;
    View view;

    int position;
    public InControlFieldAddPayment(EditText etSumCredit, Double defaultPayment, Menu menu, InfoDebt.WriteDataInField writeDataInField){
        this.etSumCredit = etSumCredit;
        this.writeDataInField = writeDataInField;
        this.defaultPayment = defaultPayment;
        this.menu = menu;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        beforeText = charSequence.toString();
        position = etSumCredit.getSelectionStart();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        String s = "", oldText = etSumCredit.getText().toString();
        if (button != null)
            button.setEnabled(true);
        for (int j = oldText.length(); j > 0; j--) {
            if ("1234567890".contains(String.valueOf(oldText.charAt(j-1))))
                s = oldText.charAt(j-1) + s;
            else if (j-1 == oldText.length() - 2 || j-1 == oldText.length() - 1){
                s = "." + s;
            }
        }
        if (s.equals("") || s.equals(".")){
            if (menu != null)
                menu.getItem(0).setEnabled(false);
        }
        else if (defaultPayment > Double.valueOf(s)){
            if (menu != null)
                menu.getItem(0).setEnabled(false);
        }
        else {
            if (menu!= null)
                menu.getItem(0).setEnabled(true);
            if (writeDataInField != null){
                writeDataInField.setOverAllPaymentCustom(Double.valueOf(s));
                writeDataInField.setOverOnePayment(Double.valueOf(s));
            }

            if (view != null)
                view.invalidate();
        }

        if (!s.equals("") & !s.equals(".")){
            s = String.valueOf(numberFormat.format(Double.valueOf(s)));
            etSumCredit.removeTextChangedListener(this);
            etSumCredit.setText(s);
            etSumCredit.addTextChangedListener(this);
            if (position + (s.length() - beforeText.length()) < 0)
                etSumCredit.setSelection(0);
            else
                etSumCredit.setSelection(position + (s.length() - beforeText.length()));
        }
    }

    @Override
    public synchronized void afterTextChanged(Editable s) {
    }
}