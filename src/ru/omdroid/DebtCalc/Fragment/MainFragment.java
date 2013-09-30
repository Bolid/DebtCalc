package ru.omdroid.DebtCalc.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.Listener.InControlFieldPercentCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldTermCredit;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;


public class MainFragment extends Fragment {

    public static Arithmetic arithmetic;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.main, null);

        final EditText etSumCredit = (EditText)v.findViewById(R.id.etCreditSum);
        final EditText etTermCredit = (EditText)v.findViewById(R.id.etTermCredit);
        final EditText etPercent = (EditText)v.findViewById(R.id.etPercentCredit);
        final String[] param = new String[3];
        ErrorMessage errorMessage = new ErrorMessage();
        AppData appData = new AppData();

        etSumCredit.addTextChangedListener(new InControlFieldSumCredit((ImageView)v.findViewById(R.id.markerSumCredit), etSumCredit, errorMessage, appData, formatingValue(etSumCredit.getText().toString())));
        etPercent.addTextChangedListener(new InControlFieldPercentCredit((ImageView) v.findViewById(R.id.markerPercentCredit), errorMessage, appData, etPercent.getText().toString()));
        etTermCredit.addTextChangedListener(new InControlFieldTermCredit((ImageView)v.findViewById(R.id.markerTermCredit), errorMessage, appData, etTermCredit.getText().toString()));

        etSumCredit.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(etSumCredit.getText().toString())));
        etPercent.setText(etPercent.getText().toString());
        etTermCredit.setText(etTermCredit.getText().toString());
        return v;

    }

    private String formatingValue(String s){
        String value = "";
        for (int j = s.length(); j > 0; j--) {
            if ("1234567890".contains(String.valueOf(s.charAt(j - 1))))
                value = s.charAt(j - 1) + value;
        }
        return value;
    }
}