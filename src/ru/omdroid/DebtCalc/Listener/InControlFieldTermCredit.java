package ru.omdroid.DebtCalc.Listener;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.R;

public class InControlFieldTermCredit implements TextWatcher{
    ImageView markerCreditTerm;
    public InControlFieldTermCredit(ImageView markerCreditTerm){
        this.markerCreditTerm = markerCreditTerm;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (charSequence.toString().length() == 0)
            markerCreditTerm.setImageResource(R.drawable.marker_red_three);
        else
            markerCreditTerm.setImageResource(R.drawable.marker_green_three);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
