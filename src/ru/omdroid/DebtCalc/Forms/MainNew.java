package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.omdroid.DebtCalc.*;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Exceptions.NullInputDataException;

import java.util.Calendar;
import java.util.Random;

public class MainNew extends Activity {
    Calendar calendar;
    Calendar calendarConst;
    EditText etType;
    AppData appData;
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.main_new);

        appData = new AppData();
        appData.allRemove();

        final TextView tvSum = (TextView)findViewById(R.id.tvLabSum);
        final TextView tvTerm = (TextView)findViewById(R.id.tvLabTerm);
        final TextView tvPercent = (TextView)findViewById(R.id.tvLabPercent);
        final TextView tvDate = (TextView)findViewById(R.id.tvDateStartCredit);
        final TextView tvGoal = (TextView)findViewById(R.id.tvLabGoal);

        etType = (EditText)findViewById(R.id.etCreditType);

        final TextView tvSumPre = (TextView)findViewById(R.id.tvLabSum);
        final TextView tvOverPay = (TextView)findViewById(R.id.tvOverPay);
        final TextView tvTotal = (TextView)findViewById(R.id.tvTotal);
        final TextView tvOverPerc = (TextView)findViewById(R.id.tvOverPerc);
        final TextView tvPayment = (TextView)findViewById(R.id.tvPayment);

        LinearLayout llSum = (LinearLayout)findViewById(R.id.llSum);
        LinearLayout llTerm = (LinearLayout)findViewById(R.id.llTerm);
        LinearLayout llPercent = (LinearLayout)findViewById(R.id.llPercent);
        LinearLayout llDate = (LinearLayout)findViewById(R.id.llDateStartCredit);
        LinearLayout llGoal= (LinearLayout)findViewById(R.id.llCreditGoal);

        final PreCalc preCalc = new PreCalc();

        calendar = Calendar.getInstance();
        calendarConst = Calendar.getInstance();
        tvDate.setText(String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH) + 1)+"."+String.valueOf(calendar.get(Calendar.YEAR)));


        final DialogFragment dialogFragment = new DatePickerFragment(tvDate, calendarConst, calendar);
        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment.show(getFragmentManager(), getResources().getString(R.string.app_name));
            }
        });

        llSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = R.layout.dialog_input_sum;
                String value = AppData.DEBT_BALANCE;
                DialogFragment dFragment = new DialogInputData(value, tvSum, preCalc, tvSumPre, tvOverPay, tvTotal, tvOverPerc, tvPayment, res, calendar, calendarConst);
                dFragment.show(getFragmentManager(), "");
            }
        });

        llTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = R.layout.dialog_input_term;
                String value = String.valueOf(AppData.TERM_BALANCE);
                DialogFragment dFragment = new DialogInputData(value, tvTerm, preCalc, tvSumPre, tvOverPay, tvTotal, tvOverPerc, tvPayment, res, calendar, calendarConst);
                dFragment.show(getFragmentManager(), "");
            }
        });

        llPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = R.layout.dialog_input_percent;
                String value = String.valueOf(AppData.PERCENT);
                DialogFragment dFragment = new DialogInputData(value, tvPercent, preCalc, tvSumPre, tvOverPay, tvTotal, tvOverPerc, tvPayment, res, calendar, calendarConst);
                dFragment.show(getFragmentManager(), "");
            }
        });

        llGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = R.layout.dialog_input_goal;
                String value = String.valueOf(AppData.GOAL);
                DialogFragment dFragment = new DialogInputData(value, tvGoal, preCalc, tvSumPre, tvOverPay, tvTotal, tvOverPerc, tvPayment, res, calendar, calendarConst);
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
                try {
                    saveDataInDataBase();
                } catch (NullInputDataException e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.popup:
                View v = findViewById(R.id.popup);
                showPopupMenu(v);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDataInDataBase() throws NullInputDataException {
        if (AppData.DEBT_BALANCE.equals("") || AppData.TERM_BALANCE == 0 || AppData.PERCENT == 0.0 || AppData.GOAL.equals(""))
            throw new NullInputDataException(" ");

        calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DATE));
        Long dateFirstPayment = calendar.getTimeInMillis();
        String date1 = String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH))+"."+String.valueOf(calendar.get(Calendar.YEAR));

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + (Integer.valueOf(AppData.param[2])), calendar.get(Calendar.DATE));
        String date = String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH))+"."+String.valueOf(calendar.get(Calendar.YEAR));

            WorkDB workDB = new WorkDB(getBaseContext());
            if (workDB.countDataInDataBase("SELECT " + DebtCalcDB.FIELD_ID +
                    " FROM " + DebtCalcDB.TABLE_CREDITS +
                    " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '0'") < 9){
                int numCredit = generateNumCredit();
                Arithmetic arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);
                workDB.insertValueToTableDebt("INSERT INTO " + DebtCalcDB.TABLE_CREDITS + " (" +
                        DebtCalcDB.FIELD_ID_DEBT + ", " +
                        DebtCalcDB.FIELD_SUM_DEBT + ", " +
                        DebtCalcDB.FIELD_PERCENT_DEBT + ", " +
                        DebtCalcDB.FIELD_TERM_DEBT + ", " +
                        DebtCalcDB.FIELD_TYPE_DEBT + ", " +
                        DebtCalcDB.FIELD_DATE_LONG_START_DEBT + ", " +
                        DebtCalcDB.FIELD_DATE_STR_START_DEBT + ", " +
                        DebtCalcDB.FIELD_PAID_DEBT + ") " +
                        "VALUES ('" +
                        numCredit + "', '" +
                        AppData.DEBT_BALANCE + "', '" +
                        AppData.PERCENT + "', '" +
                        AppData.TERM_BALANCE + "', '" +
                        AppData.GOAL + "', '" +
                        Calendar.getInstance().getTimeInMillis() + "', '" +
                        date + "', '0')");

                workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_PAYMENTS + " (" +
                        DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + ", " +
                        DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +
                        DebtCalcDB.F_BALANCE_DEBT_PAY + ", " +
                        DebtCalcDB.F_BALANCE_TERM_PAY + ", " +
                        DebtCalcDB.FIELD_SUM_PAYMENTS + ", " +
                        DebtCalcDB.FIELD_DEBT_PAYMENTS + ", " +
                        DebtCalcDB.FIELD_PERCENT_PAYMENTS + ", " +
                        DebtCalcDB.F_OVER_PAY + ", " +
                        DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + ", " +
                        DebtCalcDB.FIELD_PAID_PAYMENTS + ")" +
                        " VALUES ('" +
                        numCredit + "', '" +
                        arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE) + "', '" +
                        AppData.DEBT_BALANCE + "', '" +
                        AppData.TERM_BALANCE + "', '" +
                        arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE) + "', '" +
                        arithmetic.getPaymentInDebt(arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE), Double.valueOf(AppData.DEBT_BALANCE)) + "', '" +
                        arithmetic.getPaymentInPercent(Double.valueOf(AppData.DEBT_BALANCE)) + "', '" +
                        "0.0', '" +
                        dateFirstPayment + "', '" +
                        "0')");
                Toast.makeText(getBaseContext(), "Кредит сохранен", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getBaseContext(), "В БД нельзя сохранить более девяти кредитов", Toast.LENGTH_LONG).show();
            workDB.disconnectDataBase();
    }

    private int generateNumCredit(){
        Random random = new Random();
        return 1000 + random.nextInt(9000 - 1000 + 1);
    }

    private void showPopupMenu(View v){
        final PopupMenu pMenu = new PopupMenu(getBaseContext(), v);
        MenuInflater mInflater = pMenu.getMenuInflater();
        mInflater.inflate(R.menu.pm_d_add, pMenu.getMenu());
        pMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                try {
                    tablePaymentShow();
                } catch (NullInputDataException e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        pMenu.show();
    }

    private void tablePaymentShow() throws NullInputDataException{
        if (AppData.DEBT_BALANCE.equals("") || AppData.TERM_BALANCE == 0 || AppData.PERCENT == 0.0 || AppData.GOAL.equals(""))
            throw new NullInputDataException("Таблица");
        Intent intent = new Intent(getBaseContext(), ListPayment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
