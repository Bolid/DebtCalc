package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ru.omdroid.DebtCalc.*;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;

import java.util.Calendar;
import java.util.Random;

public class MainNew extends Activity {
    Calendar calendar;
    Calendar calendarConst;
    EditText etType;
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.main_new);

        final TextView tvSum = (TextView)findViewById(R.id.tvLabSum);
        final TextView tvTerm = (TextView)findViewById(R.id.tvLabTerm);
        final TextView tvPercent = (TextView)findViewById(R.id.tvLabPercent);
        final TextView tvDate = (TextView)findViewById(R.id.tvDateStartCredit);
        etType = (EditText)findViewById(R.id.etCreditType);

        final TextView tvSumPre = (TextView)findViewById(R.id.tvSum);
        final TextView tvOverPay = (TextView)findViewById(R.id.tvOverPay);
        final TextView tvTotal = (TextView)findViewById(R.id.tvTotal);
        final TextView tvOverPerc = (TextView)findViewById(R.id.tvOverPerc);

        LinearLayout llSum = (LinearLayout)findViewById(R.id.llSum);
        LinearLayout llTerm = (LinearLayout)findViewById(R.id.llTerm);
        LinearLayout llPercent = (LinearLayout)findViewById(R.id.llPercent);

        final PreCalc preCalc = new PreCalc();

        calendar = Calendar.getInstance();
        calendarConst = Calendar.getInstance();
        tvDate.setText(String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH) + 1)+"."+String.valueOf(calendar.get(Calendar.YEAR)));

        final DialogFragment dialogFragment = new DatePickerFragment(tvDate, calendarConst, calendar);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment.show(getFragmentManager(), getResources().getString(R.string.app_name));
            }
        });

        llSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dFragment = new DialogInputData(tvSum, getResources().getString(R.string.main_layout_label_credit_sum), preCalc, tvSumPre, tvOverPay, tvTotal, tvOverPerc);
                dFragment.show(getFragmentManager(), "");
            }
        });

        llTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dFragment = new DialogInputData(tvTerm, getResources().getString(R.string.main_layout_label_credit_term), preCalc, tvSumPre, tvOverPay, tvTotal, tvOverPerc);
                dFragment.show(getFragmentManager(), "");
            }
        });

        llPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dFragment = new DialogInputData(tvPercent, getResources().getString(R.string.main_layout_label_credit_percent), preCalc, tvSumPre, tvOverPay, tvTotal, tvOverPerc);
                dFragment.show(getFragmentManager(), "");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater actionMenu = getMenuInflater();
        actionMenu.inflate(R.menu.di_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.apply_payment:
                calendar.setTimeInMillis(calendarConst.getTimeInMillis());
                calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DATE));
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + (Integer.valueOf(AppData.param[2])), calendar.get(Calendar.DATE));

                Long dateFirstPayment = calendar.getTimeInMillis();
                String date = String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH))+"."+String.valueOf(calendar.get(Calendar.YEAR));

                WorkDB workDB = new WorkDB(getBaseContext());
                if (workDB.countDataInDataBase("SELECT " + DebtCalcDB.FIELD_ID +
                        " FROM " + DebtCalcDB.TABLE_CREDITS +
                        " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '0'") < 9){
                    int numCredit = generateNumCredit();
                    Arithmetic arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT), Double.valueOf(AppData.PERCENT), Integer.valueOf(AppData.TERM));
                    workDB.insertValueToTableDebt("INSERT INTO " + DebtCalcDB.TABLE_CREDITS + " (" +
                            DebtCalcDB.FIELD_ID_DEBT + ", " +
                            DebtCalcDB.FIELD_SUM_DEBT + ", " +
                            DebtCalcDB.FIELD_PERCENT_DEBT + ", " +
                            DebtCalcDB.FIELD_TERM_DEBT + ", " +
                            DebtCalcDB.FIELD_TYPE_DEBT + ", " +
                            DebtCalcDB.FIELD_DATE_LONG_START_DEBT + ", " +
                            DebtCalcDB.FIELD_DATE_STR_START_DEBT + ", " +
                            DebtCalcDB.FIELD_BALANCE_TERM_DEBT + ", " +
                            DebtCalcDB.FIELD_PAID_DEBT + ") " +
                            "VALUES ('" +
                            numCredit + "', '" +
                            AppData.DEBT + "', '" +
                            AppData.PERCENT + "', '" +
                            AppData.TERM + "', '" +
                            etType.getText().toString() + "', '" +
                            Calendar.getInstance().getTimeInMillis() + "', '" +
                            date + "', '" +
                            AppData.TERM + "', '0')");

                    workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_PAYMENTS + " (" +
                            DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + ", " +
                            DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +
                            DebtCalcDB.F_BALANCE_DEBT_PAY + ", " +
                            DebtCalcDB.FIELD_SUM_PAYMENTS + ", " +
                            DebtCalcDB.F_OVER_PAY + ", " +
                            DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + ", " +
                            DebtCalcDB.FIELD_PAID_PAYMENTS + ")" +
                            " VALUES ('" +
                            numCredit + "', '" +
                            arithmetic.getPayment(Double.valueOf(AppData.DEBT), Integer.valueOf(AppData.TERM)) + "', '" +
                            AppData.DEBT + "', '" +
                            "0.0', '" +
                            "0.0', '" +
                            dateFirstPayment + "', '" +
                            "0')");
                    Toast.makeText(getBaseContext(), "Кредит сохранен", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getBaseContext(), "В БД нельзя сохранить более девяти кредитов", Toast.LENGTH_LONG).show();
                workDB.disconnectDataBase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int generateNumCredit(){
        Random random = new Random();
        return 1000 + random.nextInt(9000 - 1000 +1);
    }
}
