package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.Fragment.MainFragment;
import ru.omdroid.DebtCalc.Fragment.ResultFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InControlFieldAddOverallPayment implements TextWatcher {
    NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
    EditText etSumCredit;
    String beforeText;
    Double defaultPayment;
    ErrorMessage errorMessage = new ErrorMessage();
    View view;
    int position;
    public InControlFieldAddOverallPayment(EditText etSumCredit, Double defaultPayment, View view){
        this.etSumCredit = etSumCredit;
        this.defaultPayment = defaultPayment;
        this.view = view;
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

        for (int j = oldText.length(); j > 0; j--) {
            if ("1234567890".contains(String.valueOf(oldText.charAt(j-1))))
                s = oldText.charAt(j-1) + s;
            else if (j-1 == oldText.length() - 2 || j-1 == oldText.length() - 1){
                s = "." + s;
            }
        }
        if (s.equals("")){
            ResultFragment.newPayment = defaultPayment;
            errorMessage.readErrorMessagePaymentCredit();
        }
        else if (defaultPayment > Double.valueOf(s)){
            ResultFragment.newPayment = defaultPayment;
            errorMessage.readErrorMessagePaymentCredit();
        }
        else{
            ResultFragment.newPayment = defaultPayment;
            errorMessage.clearErrorMessagePaymentCredit();
            MainFragment.arithmetic.getOverpaymentAllMonth(Double.valueOf(s), true);
            view.invalidate();
        }

        if (!s.equals("")){
            s = String.valueOf(numberFormat.format(Double.valueOf(s)));
            etSumCredit.removeTextChangedListener(this);
            etSumCredit.setText(s);
            etSumCredit.addTextChangedListener(this);
            etSumCredit.setSelection(position + (s.length() - beforeText.length()));
        }
    }

    @Override
    public synchronized void afterTextChanged(Editable s) {
    }
}
