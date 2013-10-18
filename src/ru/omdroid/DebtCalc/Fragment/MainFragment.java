package ru.omdroid.DebtCalc.Fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import ru.omdroid.DebtCalc.*;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Listener.InControlFieldPercentCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldTermCredit;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class MainFragment extends Fragment {
    Calendar calendar;
    Calendar calendarConst;
    public static Arithmetic arithmetic;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.main, null);
        ResultFragment.newPayment = null;

        Button butStart = (Button)v.findViewById(R.id.butStart);
        final EditText etSumCredit = (EditText)v.findViewById(R.id.etCreditSum);
        final EditText etTermCredit = (EditText)v.findViewById(R.id.etTermCredit);
        final EditText etPercent = (EditText)v.findViewById(R.id.etPercentCredit);
        final EditText etType = (EditText)v.findViewById(R.id.etCreditType);
        final TextView tvDate = (TextView)v.findViewById(R.id.tvDateStartCredit);

        calendar = Calendar.getInstance();
        calendarConst = Calendar.getInstance();

        ErrorMessage errorMessage = new ErrorMessage();
        AppData appData = new AppData();
        etSumCredit.addTextChangedListener(new InControlFieldSumCredit((ImageView)v.findViewById(R.id.markerSumCredit), etSumCredit, errorMessage, appData, formatingValue(etSumCredit.getText().toString())));
        etPercent.addTextChangedListener(new InControlFieldPercentCredit((ImageView)v.findViewById(R.id.markerPercentCredit), errorMessage, appData, etPercent.getText().toString()));
        etTermCredit.addTextChangedListener(new InControlFieldTermCredit((ImageView)v.findViewById(R.id.markerTermCredit), errorMessage, appData, etTermCredit.getText().toString()));



        etType.setText(AppData.param[3]);
        etSumCredit.setText(AppData.param[0]);
        etPercent.setText(AppData.param[1]);
        etTermCredit.setText(AppData.param[2]);
        tvDate.setText(String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH) + 1)+"."+String.valueOf(calendar.get(Calendar.YEAR)));

        final DialogFragment dialogFragment = new DatePickerFragment(tvDate, calendarConst, calendar);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment.show(getFragmentManager(), getResources().getString(R.string.app_name));
            }
        });

        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.setTimeInMillis(calendarConst.getTimeInMillis());
                calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DATE));
                Long dateFirstPayment = calendar.getTimeInMillis();

                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + (Integer.valueOf(AppData.param[2])), calendar.get(Calendar.DATE));
                String date = String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH))+"."+String.valueOf(calendar.get(Calendar.YEAR));

                WorkDB workDB = new WorkDB(getActivity().getBaseContext());
                if (workDB.countDataInDataBase("SELECT " + DebtCalcDB.FIELD_ID +
                                                " FROM " + DebtCalcDB.TABLE_NAME_CREDITS +
                                                " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '0'") < 9){
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
                            DebtCalcDB.FIELD_BALANCE_DEBT + ", " +
                            DebtCalcDB.FIELD_BALANCE_TERM_DEBT + ", " +
                            DebtCalcDB.FIELD_PAID_DEBT + ") " +
                            "VALUES ('" +
                            numCredit + "', '" +
                            AppData.param[0] + "', '" +
                            AppData.param[1] + "', '" +
                            AppData.param[2] + "', '" +
                            etType.getText().toString() + "', '" +
                            Calendar.getInstance().getTimeInMillis() + "', '" +
                            date + "', '" +
                            AppData.param[0] + "', '" +
                            AppData.param[2] + "', '0')");

                    workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_NAME_PAYMENTS + " (" +
                            DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + ", " +
                            DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +
                            DebtCalcDB.FIELD_SUM_PAYMENTS + ", " +
                            DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + ", " +
                            DebtCalcDB.FIELD_PAID_PAYMENTS + ")" +
                            " VALUES ('" +
                            numCredit + "', '" +
                            arithmetic.getPayment(Double.valueOf(AppData.param[0]), Integer.valueOf(AppData.param[2])) +"', '" +
                            "0.0', '" +
                            dateFirstPayment +"', '" +
                            "0')");
                    Toast.makeText(getActivity().getBaseContext(), "Кредит сохранен", Toast.LENGTH_LONG).show();
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