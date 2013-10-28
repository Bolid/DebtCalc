package ru.omdroid.DebtCalc.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.ErrorMessage;


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
      /*  if (charSequence.toString().length() != 0)
            appData.setPercent(charSequence.toString());*/
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
