package ru.omdroid.DebtCalc;


import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;

import java.text.DecimalFormat;
import java.util.Calendar;

public class TablePaymentSavedDebt extends Activity implements TablePaymentManager{
    View view = null;
    WorkDB workDB;
    WorkDateDebt workDateDebt = new WorkDateDebt();
    boolean addRecord = true;
    LinearLayout layout;
    LayoutInflater inflater;
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.payment_info);
        workDB = new WorkDB(getBaseContext());
        layout = (LinearLayout)findViewById(R.id.llPaymentInfo);
        inflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
        manageData();
        /*new AsyncTask<Void, View, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                WorkDateDebt workDateDebt = new WorkDateDebt();
                Calendar datePay = Calendar.getInstance();
                Cursor cursor = workDB.readValueFromDataBase("SELECT " +
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
                int numPayment = 0;
                while (cursor.moveToNext()){
                    if (!addRecord)
                        return null;
                    numPayment++;
                    datePay.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
                    Drawable background;
                    if (cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_PAID_PAYMENTS)) == 1)
                        background = getResources().getDrawable(R.drawable.pay_paid);
                    else
                        background =  getResources().getDrawable(R.drawable.pay_next_piad);
                    addRecord(inflater,
                            layout,
                            numPayment,
                            cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)),
                            cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS)),
                            cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_PAYMENTS)),
                            workDateDebt.getDate(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)),
                            cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY)),
                            cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)),
                            cursor.getString(cursor.getColumnIndex(DebtCalcDB.F_PAYMENT_UP_PAY)),
                            background);
                    publishProgress(view);
                }

                Double paymentPercent, paymentDebt, balanceDebt, payment, feePayment = 0.0;
                int upPayment, termBalance;

                cursor.moveToLast();
                Long datePayment = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS));
                upPayment = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_PAYMENT_UP_PAY));
                termBalance = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_TERM_PAY));
                balanceDebt = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY));
                feePayment = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS));
                paymentDebt = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS));
                payment = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS));
                cursor.close();

                cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_DATE_LONG_START_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS + " WHERE (" + DebtCalcDB.FIELD_ID_DEBT + " = '" + AppData.ID_DEBT +"')");
                cursor.moveToNext();
                Long dateStart = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_START_DEBT));
                cursor.close();
                workDB.disconnectDataBase();

                Arithmetic arithmetic = new Arithmetic(AppData.PERCENT);
                datePay.setTimeInMillis(workDateDebt.createNextDatePayment(datePayment, dateStart));
                balanceDebt = balanceDebt - paymentDebt;
                if (upPayment == 1)
                    payment = arithmetic.getPayment(balanceDebt, termBalance - 1);
                numPayment++;
                termBalance--;
                while (balanceDebt > 0.01){
                    if (!addRecord)
                        return null;
                    workDateDebt.getCountDayInMonth(datePay.getTimeInMillis());

                    paymentPercent = arithmetic.getOverpaymentOneMonth(balanceDebt);

                    if (termBalance == 1)
                        payment = balanceDebt + paymentPercent;

                    paymentDebt = payment - paymentPercent;

                    feePayment = feePayment + payment;
                    //addRecord(inflater, layout, numPayment, payment, paymentDebt, paymentPercent, workDateDebt.getDate(datePay), balanceDebt, feePayment, null, getResources().getDrawable(R.drawable.pay_no_paid));

                    balanceDebt = balanceDebt - paymentDebt;
                    datePay.setTimeInMillis(workDateDebt.createNextDatePayment(datePay.getTimeInMillis(), dateStart));

                    numPayment++;
                    termBalance--;
                    publishProgress(view);
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(View... values) {
                super.onProgressUpdate(values);
                layout.addView(values[0]);
            }
        }.execute();*/
    }

    public View addRecord(LayoutInflater inflater,
                          LinearLayout layout,
                          int numPayment,
                          Double payment,
                          final Double paymentDebt,
                          final Double paymentPercent,
                          String date,
                          final Double balanceDebt,
                          Double feePayment,
                          String upPayment,
                          Drawable color){
        view = inflater.inflate(R.layout.payment_info_record, layout, false);

        view.setBackground(color);

        TextView tvNumPayment = (TextView)view.findViewById(R.id.tvNumPaymentRecord);
        TextView tvPayment = (TextView)view.findViewById(R.id.tvPaymentRecord);
        TextView tvDatePay= (TextView)view.findViewById(R.id.tvInfoDateList);
        TextView tvFeePayment = (TextView)view.findViewById(R.id.tvSumPaymentRecord);

        tvNumPayment.setText(String.valueOf(numPayment));
        tvPayment.setText(new DecimalFormat("###,###,###,###,###.00").format(payment));
        tvDatePay.setText(date);
        tvFeePayment.setText(new DecimalFormat("###,###,###,###,###.00").format(feePayment));

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

                    tvBalanceDebt.setText(new DecimalFormat("###,###,###,###.##").format(balanceDebt));
                    tvDebt.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentDebt));
                    tvPercent.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentPercent));
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
                Double payment = null, feePayment = null, balanceDebt = null, paymentDebt, paymentPercent;
                String date, upPayment = "";
                int numPayment = 0, termBalance = 0;
                Drawable color = null;
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

                cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_DATE_LONG_START_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS + " WHERE (" + DebtCalcDB.FIELD_ID_DEBT + " = '" + AppData.ID_DEBT +"')");
                cursor.moveToNext();
                Long dateStart = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_START_DEBT));
                cursor.close();
                workDB.disconnectDataBase();

                Arithmetic arithmetic = new Arithmetic(AppData.PERCENT);
                //balanceDebt -= payment;
                if (upPayment != null)
                    payment = arithmetic.getPayment(balanceDebt, termBalance - 1);
                //numPayment++;
                while (balanceDebt > 0.01){
                    termBalance--;
                    if (!addRecord)
                        return null;
                    datePaymentLong = workDateDebt.createNextDatePayment(datePaymentLong, dateStart);

                    paymentPercent = arithmetic.getOverpaymentOneMonth(balanceDebt);

                    if (termBalance == 1)
                        payment = balanceDebt + paymentPercent;

                    paymentDebt = payment - paymentPercent;

                    feePayment += payment;

                    balanceDebt -= paymentDebt;
                    date = workDateDebt.getDate(datePaymentLong);
                    //datePay.setTimeInMillis(workDateDebt.createNextDatePayment(datePay.getTimeInMillis(), dateStart));

                    numPayment++;
                    //termBalance--;
                    publishProgress(createDataFromDB(payment, feePayment, date, upPayment, numPayment, balanceDebt, paymentDebt, paymentPercent, getResources().getDrawable(R.drawable.pay_no_paid)));
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
        view = inflater.inflate(R.layout.payment_info_record, layout, false);
        view.setBackground(color);

        TextView tvNumPayment = (TextView)view.findViewById(R.id.tvNumPaymentRecord);
        TextView tvPayment = (TextView)view.findViewById(R.id.tvPaymentRecord);
        TextView tvDatePay= (TextView)view.findViewById(R.id.tvInfoDateList);
        TextView tvFeePayment = (TextView)view.findViewById(R.id.tvSumPaymentRecord);

        tvNumPayment.setText(String.valueOf(numPayment));
        tvPayment.setText(new DecimalFormat("###,###,###,###,###.00").format(payment));
        tvDatePay.setText(date);
        tvFeePayment.setText(new DecimalFormat("###,###,###,###,###.00").format(feePayment));

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

                    tvBalanceDebt.setText(new DecimalFormat("###,###,###,###.##").format(balanceDebt));
                    tvDebt.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentDebt));
                    tvPercent.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentPercent));
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
    public void createDataFromArithmetic() {
    }

    @Override
    public void insertField(View view) {
        layout.addView(view);
    }
}
