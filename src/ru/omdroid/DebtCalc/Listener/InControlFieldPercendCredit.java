package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class InControlFieldPercendCredit implements TextWatcher {
    ImageView markerCreditPercent;

    public InControlFieldPercendCredit(ImageView markerCreditPercent){
        this.markerCreditPercent = markerCreditPercent;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (charSequence.toString().length() == 0)
            markerCreditPercent.setImageResource(R.drawable.marker_red_two);
        else if (Double.valueOf(charSequence.toString().replace(",", ".")) > 100)
                markerCreditPercent.setImageResource(R.drawable.marker_red_two);
            else
            markerCreditPercent.setImageResource(R.drawable.marker_green_two);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
