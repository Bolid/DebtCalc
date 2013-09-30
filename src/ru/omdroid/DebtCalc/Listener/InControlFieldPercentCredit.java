package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.R;


public class InControlFieldPercentCredit implements TextWatcher {
    ImageView markerCreditPercent;
    AppData appData;
    String  valueDefault;
    ErrorMessage errorMessage;
    public InControlFieldPercentCredit(ImageView markerCreditPercent, ErrorMessage errorMessage, AppData appData, String valueDefault){
        this.markerCreditPercent = markerCreditPercent;
        this.errorMessage = errorMessage;
        this.appData = appData;
        this.valueDefault = valueDefault;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (charSequence.toString().length() == 0){
            markerCreditPercent.setImageResource(R.drawable.marker_red_two);
            errorMessage.readErrorMessagePercendCredit();
            appData.addPercentCredit(valueDefault);
        }
        else if (Double.valueOf(charSequence.toString().replace(",", ".")) > 100){
            markerCreditPercent.setImageResource(R.drawable.marker_red_two);
            errorMessage.downPercentCredit();
            appData.addPercentCredit(valueDefault);
        }
            else{
            markerCreditPercent.setImageResource(R.drawable.marker_green_two);
            errorMessage.clearErrorMessagePercendCredit();
            appData.addPercentCredit(charSequence.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
