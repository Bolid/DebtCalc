package ru.omdroid.DebtCalc.Fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.Listener.InControlFieldPercentCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldTermCredit;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.util.Calendar;


public class MainFragment extends Fragment {

    public static Arithmetic arithmetic;
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
        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkDB workDB = new WorkDB(getActivity().getBaseContext());
                Cursor cursor = workDB.readValueFromDataBase("SELECT sum_debt, percent_debt, term_debt, date_long_start_debt FROM debts_table");
                while (cursor.moveToNext())
                    Log.v("Сохраненные кредиты: ","сумма: " +
                            cursor.getString(cursor.getColumnIndex("sum_debt")) + " процент: " +
                            cursor.getString(cursor.getColumnIndex("percent_debt")) + " срок: " +
                            cursor.getString(cursor.getColumnIndex("term_debt")) + " дата: " +
                            cursor.getString(cursor.getColumnIndex("date_long_start_debt")));
               /* workDB.insertValueToDataBase("INSERT INTO debts_table (sum_debt, percent_debt, term_debt, date_str_start_debt, date_long_start_debt) VALUES ('" +
                                                                                    etSumCredit.getText().toString() + "', '"+
                                                                                    etTermCredit.getText().toString() + "', '"+
                                                                                    etPercent.getText().toString() + "', '0', '" +
                                                                                    Calendar.getInstance().get(Calendar.MILLISECOND) + "')");*/
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
}