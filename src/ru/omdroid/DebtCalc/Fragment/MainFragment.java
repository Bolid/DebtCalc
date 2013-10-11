package ru.omdroid.DebtCalc.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.Listener.InControlFieldPercentCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldTermCredit;
import ru.omdroid.DebtCalc.R;

import java.util.Calendar;
import java.util.Random;


public class MainFragment extends Fragment {

    public static Arithmetic arithmetic;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.main, null);

        Button butStart = (Button)v.findViewById(R.id.butStart);
        final ImageView ivTypeCredit = (ImageView)v.findViewById(R.id.labelDebtTypeIco);
        final TextView tvTypeCredit = (TextView)v.findViewById(R.id.labelCreditType);
        final EditText etSumCredit = (EditText)v.findViewById(R.id.etCreditSum);
        final EditText etTermCredit = (EditText)v.findViewById(R.id.etTermCredit);
        final EditText etPercent = (EditText)v.findViewById(R.id.etPercentCredit);

        ErrorMessage errorMessage = new ErrorMessage();
        AppData appData = new AppData();
        etSumCredit.addTextChangedListener(new InControlFieldSumCredit((ImageView)v.findViewById(R.id.markerSumCredit), etSumCredit, errorMessage, appData, formatingValue(etSumCredit.getText().toString())));
        etPercent.addTextChangedListener(new InControlFieldPercentCredit((ImageView)v.findViewById(R.id.markerPercentCredit), errorMessage, appData, etPercent.getText().toString()));
        etTermCredit.addTextChangedListener(new InControlFieldTermCredit((ImageView)v.findViewById(R.id.markerTermCredit), errorMessage, appData, etTermCredit.getText().toString()));

        if (AppData.param[3].equals("\nна машину"))
            ivTypeCredit.setImageResource(R.drawable.ico_home);


        tvTypeCredit.append(AppData.param[3]);
        etSumCredit.setText(AppData.param[0]);
        etPercent.setText(AppData.param[1]);
        etTermCredit.setText(AppData.param[2]);

        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkDB workDB = new WorkDB(getActivity().getBaseContext());
                if (workDB.countDataInDataBase("SELECT " + DebtCalcDB.FIELD_ID + " FROM " + DebtCalcDB.TABLE_NAME_CREDITS) < 9){
                    int numCredit = generateNumCredit();
                    arithmetic = new Arithmetic(Double.valueOf(AppData.param[0]), Double.valueOf(AppData.param[1]), Integer.valueOf(AppData.param[2]));

                    workDB.insertValueToTableDebt("INSERT INTO " + DebtCalcDB.TABLE_NAME_CREDITS + " (" +
                            DebtCalcDB.FIELD_ID_DEBT + ", " +
                            DebtCalcDB.FIELD_SUM_DEBT + ", " +
                            DebtCalcDB.FIELD_PERCENT_DEBT + ", " +
                            DebtCalcDB.FIELD_TERM_DEBT + ", " +
                            DebtCalcDB.FIELD_TYPE_DEBT + ", " +
                            DebtCalcDB.FIELD_DATE_LONG_START_DEBT + ", " +
                            DebtCalcDB.FIELD_DATE_STR_START_DEBT + ", " +
                            DebtCalcDB.FIELD_BALANCE_DEBT + ") " +
                            "VALUES ('" +
                            numCredit + "', '" +
                            AppData.param[0] + "', '" +
                            AppData.param[1] + "', '" +
                            AppData.param[2] + "', 'Квартира', '" +
                            Calendar.getInstance().get(Calendar.MILLISECOND) +
                            "', '11.09.2013'" + ", '" +
                            AppData.param[0] + "')");

                    workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_NAME_PAYMENTS + " (" +
                            DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + ", " +
                            DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ")" + " VALUES ('" +
                            numCredit + "', '" +
                            arithmetic.getPayment(Double.valueOf(AppData.param[0]), Integer.valueOf(AppData.param[2])) +"')");
                }
                else
                    Toast.makeText(getActivity().getBaseContext(), "В БД нельзя сохранить более девяти кредитов", Toast.LENGTH_LONG).show();
                workDB.disconnectDataBase();
            }
        });
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

    private int generateNumCredit(){
        Random random = new Random();
        return 1000 + random.nextInt(9000 - 1000 +1);
    }
}