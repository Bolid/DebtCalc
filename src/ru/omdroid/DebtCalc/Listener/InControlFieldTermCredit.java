package ru.omdroid.DebtCalc.Listener;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.ErrorMessage;

public class InControlFieldTermCredit implements TextWatcher{
    ImageView markerCreditTerm;
    ErrorMessage errorMessage;
    AppData appData;
    String  valueDefault;
    public InControlFieldTermCredit(ImageView markerCreditTerm, ErrorMessage errorMessage, AppData appData, String valueDefault){
        this.markerCreditTerm = markerCreditTerm;
        this.errorMessage = errorMessage;
        this.appData = appData;
        this.valueDefault = valueDefault;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        /*if (charSequence.toString().length() != 0)
            appData.setTerm(charSequence.toString());*/
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
