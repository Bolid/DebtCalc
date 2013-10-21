package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InControlFieldAddPayment implements TextWatcher {
    NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
    ImageView markerDefaultPayment;
    EditText etSumCredit;
    Button button;
    String beforeText;
    Double defaultPayment;
    int position;
    public InControlFieldAddPayment(EditText etSumCredit, ImageView markerDefaultPayment, Button button, Double defaultPayment){
        this.etSumCredit = etSumCredit;
        this.markerDefaultPayment = markerDefaultPayment;
        this.button = button;
        this.defaultPayment = defaultPayment;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        Log.v("beforeTextChanged Позиция i i2 i3: ", charSequence.toString());
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
        if (s.equals("")){
            if (button != null & markerDefaultPayment != null){
                button.setEnabled(false);
                markerDefaultPayment.setImageResource(R.drawable.marker_red_addpayment);
            }
        }
        else if (defaultPayment > Double.valueOf(s))
            if (button != null & markerDefaultPayment != null){
            button.setEnabled(false);
            markerDefaultPayment.setImageResource(R.drawable.marker_red_addpayment);
        }
        else if (button != null & markerDefaultPayment != null){
            button.setEnabled(true);
            markerDefaultPayment.setImageResource(R.drawable.marker_green_addpayment);
        }

        if (!s.equals("")){
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