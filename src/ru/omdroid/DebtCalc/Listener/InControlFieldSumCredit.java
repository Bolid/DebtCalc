package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class InControlFieldSumCredit implements TextWatcher{
    NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
    ImageView markerCreditSum;
    EditText etSumCredit;
    String beforeText;
    ErrorMessage errorMessage;
    AppData appData;
    String valueDefault;
    int position;
    public InControlFieldSumCredit(ImageView markerCreditSum, EditText etSumCredit, ErrorMessage errorMessage, AppData appData, String valueDefault){
        this.markerCreditSum = markerCreditSum;
        this.etSumCredit = etSumCredit;
        this.errorMessage = errorMessage;
        this.appData = appData;
        this.valueDefault = valueDefault;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        beforeText = charSequence.toString();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        String s = "";
        position = etSumCredit.getSelectionStart();
        for (int j = etSumCredit.getText().length(); j > 0; j--) {
            if ("1234567890".contains(String.valueOf(etSumCredit.getText().toString().charAt(j-1))))
                s = etSumCredit.getText().toString().charAt(j-1) + s;
        }

        /*if (markerCreditSum != null)
            if (charSequence.toString().length() == 0){
                markerCreditSum.setImageResource(R.drawable.marker_red_one);
                errorMessage.readErrorMessageSumCredit();
                appData.addSumCredit(valueDefault);
            }
            else{
                markerCreditSum.setImageResource(R.drawable.marker_green_one);
                errorMessage.clearErrorMessageSumCredit();*/
                appData.addSumCredit(s);
        appData.setDebt(s);
            //}

        if (!s.equals("")){
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
