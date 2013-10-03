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
        Log.v("Позиция i i2 i3: ", charSequence.toString());
        beforeText = charSequence.toString();

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        Log.v("Позиция i i2 i3: ", charSequence.toString());
        String s = "";
        position = etSumCredit.getSelectionStart();

        for (int j = etSumCredit.getText().length(); j > 0; j--) {
            if ("1234567890".contains(String.valueOf(etSumCredit.getText().toString().charAt(j-1))))
                s = etSumCredit.getText().toString().charAt(j-1) + s;
            else if (j-1 == etSumCredit.getText().toString().length() - 2 || j-1 == etSumCredit.getText().toString().length() - 2)
                s = "." + s;
        }
        if (s.equals("")){
            button.setEnabled(false);
            markerDefaultPayment.setImageResource(R.drawable.marker_red_addpayment);
        }
        else if (defaultPayment > Double.valueOf(s)){
            button.setEnabled(false);
            markerDefaultPayment.setImageResource(R.drawable.marker_red_addpayment);
        }
        else{
            button.setEnabled(true);
            markerDefaultPayment.setImageResource(R.drawable.marker_green_addpayment);
        }

        if (!s.equals("")){
            Log.v("Позиция курсора: ", String.valueOf(position));
            s = String.valueOf(numberFormat.format(Double.valueOf(s)));
            etSumCredit.removeTextChangedListener(this);
            etSumCredit.setText(s);
            etSumCredit.addTextChangedListener(this);
            if (((beforeText.length() - s.length()) > 1 & position != 0) || (position > s.length()))
                etSumCredit.setSelection(position - 1);
            else if ((s.length() - beforeText.length()) > 1 & position != s.length())
                etSumCredit.setSelection(position + 1);
            else
                etSumCredit.setSelection(position);
        }
    }

    @Override
    public synchronized void afterTextChanged(Editable s) {
    }
}