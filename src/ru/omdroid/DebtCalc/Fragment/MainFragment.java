package ru.omdroid.DebtCalc.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.Listener.InControlFieldPercentCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldTermCredit;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;


public class MainFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.main, null);

        Button butStart = (Button)v.findViewById(R.id.butStart);
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

        butStart.setVisibility(View.INVISIBLE);
        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String notify = "";
                if (!etPercent.getText().toString().equals(""))
                    if (Double.valueOf(etPercent.getText().toString().replace(",",".")) > 100){
                        notify = "Процентная ставка может быть не более 100%\n";
                        notify = notify + "Пожалуйста, введите следующие параметры для кредита:";
                    }
                    else
                        notify = notify + "Пожалуйста, введите следующие параметры для кредита:";

                if (etSumCredit.getText().toString().equals("") || etSumCredit.getText().toString().equals("0"))
                    notify = notify + "\nСумму кредита.";
                if (etTermCredit.getText().toString().equals("") || etTermCredit.getText().toString().equals("0"))
                    notify = notify + "\nСрок кредита.";
                if (etPercent.getText().toString().equals("") || etPercent.getText().toString().equals("0"))
                    notify = notify + "\nПроцентную ставку.";
                if (notify.length() > 55)
                    Toast.makeText(v.getContext(), notify, Toast.LENGTH_LONG).show();
                else {
                    param[0] = "";
                    for (int j = etSumCredit.getText().length(); j > 0; j--) {
                        if ("1234567890".contains(String.valueOf(etSumCredit.getText().toString().charAt(j-1))))
                            param[0] = etSumCredit.getText().toString().charAt(j-1) + param[0];
                    }
                    param[1] = etTermCredit.getText().toString();
                    param[2] = etPercent.getText().toString();
                    //new AppData(param);
                    /*Intent intent = new Intent(getBaseContext(), TabActivityResult.class);
                    //intent.putExtra(TAG, param);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                }
            }
        });

        return v;

    }
    @Override
    public void onStop(){
        super.onStop();
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