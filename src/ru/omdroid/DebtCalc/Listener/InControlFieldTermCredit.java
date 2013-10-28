package ru.omdroid.DebtCalc.Listener;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.R;

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
        /*if (charSequence.toString().length() == 0){
            markerCreditTerm.setImageResource(R.drawable.marker_red_three);
            errorMessage.readErrorMessageTermCredit();
            appData.addTermCredit(valueDefault);
        }
        else{
            markerCreditTerm.setImageResource(R.drawable.marker_green_three);
            errorMessage.clearErrorMessageTermCredit();*/
            appData.addTermCredit(charSequence.toString());
        //}
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
