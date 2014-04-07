package ru.omdroid.DebtCalc.Forms;


import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.TablePaymentManager;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TablePaymentSavedDebt extends Activity implements TablePaymentManager {
    final String TAG = "ru.omdroid.DebtCalc.Forms.TablePaymentSavedDebt";
    View view = null;
    WorkDB workDB;
    WorkDateDebt workDateDebt = new WorkDateDebt();
    boolean addRecord = true;
    LinearLayout layout;
    LayoutInflater inflater;

    ArrayList<String> listDatePayment = new ArrayList <String>();
    ArrayList <String> listPayment = new ArrayList <String>();
    ArrayList <String> listPaymentPercent = new ArrayList <String>();
    ArrayList <String> listPaymentDebt = new ArrayList <String>();
    ArrayList <String> listBalanceDebt = new ArrayList <String>();
    String fileName;

    DecimalFormat dFormat = new DecimalFormat("###,###,###,###,###.00");
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.payment_info);
        setTitle(getIntent().getExtras().getString("FORM_NAME"));

        workDB = new WorkDB(getBaseContext());
        layout = (LinearLayout)findViewById(R.id.llPaymentInfo);
        getBaseContext();
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        manageData();
    }

    @Override
    public void onStop(){
        super.onStop();
        addRecord = false;
    }

    @Override
    public void manageData() {
        new AsyncTask<Void, View, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = readDataFromDB();
                Double payment = null, feePayment = null, balanceDebt = null, paymentDebt = null, paymentPercent;
                String date, upPayment = "";
                int numPayment = 0, termBalance = 0;
                Drawable color;
                long datePaymentLong = 0;
                while (cursor.moveToNext()){
                    payment = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS));
                    paymentDebt = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS));
                    paymentPercent = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_PAYMENTS));
                    datePaymentLong = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS));
                    date = workDateDebt.getDate(datePaymentLong);
                    balanceDebt = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY));
                    feePayment =  cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS));
                    upPayment = cursor.getString(cursor.getColumnIndex(DebtCalcDB.F_PAYMENT_UP_PAY));
                    termBalance = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_TERM_PAY));
                    numPayment++;

                    if (cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_PAID_PAYMENTS)) == 1)
                        color = getResources().getDrawable(R.drawable.pay_paid);
                    else
                        color =  getResources().getDrawable(R.drawable.pay_next_piad);

                    publishProgress(createDataFromDB(payment, feePayment, date, upPayment, numPayment, balanceDebt, paymentDebt, paymentPercent, color));
                }

                cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_TYPE_DEBT + ", " + DebtCalcDB.FIELD_DATE_LONG_START_DEBT  +", "+ DebtCalcDB.FIELD_PERCENT_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS + " WHERE (" + DebtCalcDB.FIELD_ID_DEBT + " = '" + AppData.ID_DEBT +"')");
                cursor.moveToNext();
                fileName = cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)) + ".csv";
                Long dateStart = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_START_DEBT));
                Double percent = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT));
                cursor.close();
                workDB.disconnectDataBase();

                Arithmetic arithmetic = new Arithmetic(percent);
                datePaymentLong = workDateDebt.createNextDatePayment(datePaymentLong, dateStart);
                balanceDebt -= paymentDebt;
                if (upPayment != null)
                    payment = arithmetic.getPayment(balanceDebt, termBalance - 1);

                while (balanceDebt > 0.01){
                    termBalance--;
                    if (!addRecord)
                        return null;
                    workDateDebt.getCountDayInMonth(datePaymentLong);
                    paymentPercent = arithmetic.getOverpaymentOneMonth(balanceDebt);
                    if (termBalance == 1)
                        payment = balanceDebt + paymentPercent;

                    paymentDebt = payment - paymentPercent;
                    feePayment += payment;
                    date = workDateDebt.getDate(datePaymentLong);
                    numPayment++;
                    publishProgress(createDataFromDB(payment, feePayment, date, null, numPayment, balanceDebt, paymentDebt, paymentPercent, getResources().getDrawable(R.drawable.pay_no_paid)));

                    balanceDebt -= paymentDebt;
                    datePaymentLong = workDateDebt.createNextDatePayment(datePaymentLong, dateStart);
                }

                return null;
            }
            @Override
            protected void onProgressUpdate(View... values) {
                super.onProgressUpdate(values);
                insertField(values[0]);
            }
        }.execute();
    }

    @Override
    public Cursor readDataFromDB() {
        return workDB.readValueFromDataBase("SELECT " +
                DebtCalcDB.FIELD_ID_DEBT_PAYMENTS +", "+
                DebtCalcDB.FIELD_PAYMENT_PAYMENTS +", "+
                DebtCalcDB.FIELD_DEBT_PAYMENTS +", "+
                DebtCalcDB.FIELD_PERCENT_PAYMENTS +", "+
                DebtCalcDB.FIELD_DATE_LONG_PAYMENTS +", "+
                DebtCalcDB.F_BALANCE_DEBT_PAY +", "+
                DebtCalcDB.F_BALANCE_TERM_PAY +", "+
                DebtCalcDB.FIELD_SUM_PAYMENTS +", "+
                DebtCalcDB.F_PAYMENT_UP_PAY +", "+
                DebtCalcDB.FIELD_PAID_PAYMENTS +
                " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +"')");

    }

    @Override
    public View createDataFromDB(Double payment, Double feePayment, String date, String upPayment, int numPayment, final Double balanceDebt, final Double paymentDebt, final Double paymentPercent, Drawable color) {
        addDataForUnload(date, dFormat.format(payment), dFormat.format(paymentDebt), dFormat.format(paymentPercent), dFormat.format(balanceDebt));
        view = inflater.inflate(R.layout.payment_info_record, layout, false);
        view.setBackground(color);

        TextView tvNumPayment = (TextView)view.findViewById(R.id.tvNumPaymentRecord);
        TextView tvPayment = (TextView)view.findViewById(R.id.tvPaymentRecord);
        TextView tvDatePay= (TextView)view.findViewById(R.id.tvInfoDateList);
        TextView tvFeePayment = (TextView)view.findViewById(R.id.tvSumPaymentRecord);

        tvNumPayment.setText(String.valueOf(numPayment));
        tvPayment.setText(dFormat.format(payment));
        tvDatePay.setText(date);
        tvFeePayment.setText(dFormat.format(feePayment));

        if (upPayment != null)
            if (upPayment.equals("1")){
                ImageView ivIpPayment = (ImageView)view.findViewById(R.id.ivUpPayRecord);
                ivIpPayment.setImageResource(R.drawable.arrow);
            }

        view.setOnClickListener(new View.OnClickListener() {
            boolean infoDetail = false;
            LinearLayout.LayoutParams param = null;
            @Override
            public void onClick(View view) {
                LinearLayout layoutChild = (LinearLayout)view.findViewById(R.id.layoutInfoListPod);
                if (!infoDetail){
                    TextView tvBalanceDebt = (TextView)view.findViewById(R.id.debtBalanceListInfo);
                    TextView tvDebt = (TextView)view.findViewById(R.id.tvDebtRecord);
                    TextView tvPercent = (TextView)view.findViewById(R.id.tvPercentRecord);

                    tvBalanceDebt.setText(dFormat.format(balanceDebt));
                    tvDebt.setText(dFormat.format(paymentDebt));
                    tvPercent.setText(dFormat.format(paymentPercent));
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    infoDetail = !infoDetail;
                }
                else{
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    infoDetail = !infoDetail;
                }
                layoutChild.setLayoutParams(param);
            }
        });
        return view;
    }

    @Override
    public View createDataFromArithmetic(Double payment, Double feePayment, String date, int numPayment, Double balanceDebt, Double paymentDebt, Double paymentPercent) {
        return null;
    }

    @Override
    public void insertField(View view) {
        layout.addView(view);
    }

    @Override
    public void addDataForUnload(String date, String payment, String paymentDebt, String paymentPercent, String balanceDebt) {
        listDatePayment.add(date);
        listPayment.add(payment);
        listPaymentDebt.add(paymentDebt);
        listPaymentPercent.add(paymentPercent);
        listBalanceDebt.add(balanceDebt);
    }

    @Override
    public void saveDataInCSV() {
        Resources res = getResources();
        try {
            FileWriter fileWriter = new FileWriter(Environment.getExternalStorageDirectory() + "/Debt_" +  fileName);
            fileWriter.append(res.getString(R.string.number_payment)).append(";").append(res.getString(R.string.date_payment)).append(";").append(res.getString(R.string.sum_payment)).append(";").append(res.getString(R.string.sum_pay_in_debt)).append(";").append(res.getString(R.string.sum_pay_in_percent)).append(";").append(res.getString(R.string.balance_debt)).append("\n");
            for (int i = 0; i < listDatePayment.size(); i++){
                fileWriter.append(String.valueOf(i+1)).append(";").append(listDatePayment.get(i)).append(";").append(listPayment.get(i)).append(";").append(listPaymentDebt.get(i)).append(";").append(listPaymentPercent.get(i)).append(";").append(listBalanceDebt.get(i)).append("\n");
            }
            Toast.makeText(getBaseContext(), res.getString(R.string.notify_for_save_csv) + fileName, Toast.LENGTH_LONG).show();
            fileWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error IO", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater actionMenu = getMenuInflater();
        actionMenu.inflate(R.menu.pm_payment_table, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.saveDataInCSV:
                saveDataInCSV();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        listDatePayment.clear();
        listPayment.clear();
        listPaymentDebt.clear();
        listPaymentPercent.clear();
        listBalanceDebt.clear();
    }
}
