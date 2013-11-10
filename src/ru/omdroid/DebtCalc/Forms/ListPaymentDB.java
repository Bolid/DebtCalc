package ru.omdroid.DebtCalc.Forms;


import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ListPaymentDB extends Activity {
    View view = null;
    boolean addRecord = true;
    int countDay = 0;

    AppData appData = new AppData();

    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.payment_info);
        final WorkDB workDB = new WorkDB(getBaseContext());

        new AsyncTask<Void, View, Void>() {
            LinearLayout layout = (LinearLayout)findViewById(R.id.llPaymentInfo);
            @Override
            protected Void doInBackground(Void... voids) {

                WorkDateDebt workDateDebt = new WorkDateDebt();
                Calendar datePay = Calendar.getInstance();
                LayoutInflater inflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
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
                            workDateDebt.getDate(datePay),
                            cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY)),
                            cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)),
                            cursor.getString(cursor.getColumnIndex(DebtCalcDB.F_PAYMENT_UP_PAY)),
                            background);
                    publishProgress(view);
                }

                Double paymentPercent, paymentDebt, balanceDebt, payment, feePayment = 0.0;

                cursor.moveToLast();
                Long datePayment = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS));
                int balanceTerm = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_TERM_PAY));
                balanceDebt = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY));
                feePayment = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS));
                cursor.close();

                cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_DATE_LONG_START_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS + " WHERE (" + DebtCalcDB.FIELD_ID_DEBT + " = '" + AppData.ID_DEBT +"')");
                cursor.moveToNext();
                Long dateStart = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_START_DEBT));
                cursor.close();
                workDB.disconnectDataBase();

                Arithmetic arithmetic = new Arithmetic(AppData.PERCENT);

                payment = arithmetic.getPayment(balanceDebt, balanceTerm);
                //balanceDebt = Double.valueOf(AppData.DEBT_BALANCE);



                //datePay.set(datePay.get(Calendar.YEAR), datePay.get(Calendar.MONTH) + 1, datePay.get(Calendar.DATE_PAY));
                //for (int j = balanceTerm - 1; j > 0; j--)
                while (balanceDebt > 0.01){
                    if (!addRecord)
                        return null;
                    numPayment++;
                    if (numPayment == 29)
                        Log.v("","");
                    feePayment = feePayment + payment;
                    balanceDebt = arithmetic.getBalance(payment, balanceDebt, AppData.TERM_BALANCE);

                    datePay.setTimeInMillis(workDateDebt.createNextDatePayment(datePay.getTimeInMillis(), dateStart));
                    paymentPercent = arithmetic.getPaymentInPercent(balanceDebt, countDay);
                    paymentDebt = arithmetic.getPaymentInDebt(payment, balanceDebt);
                    addRecord(inflater, layout, numPayment, payment, paymentDebt, paymentPercent, workDateDebt.getDate(datePay), balanceDebt, feePayment, null, getResources().getDrawable(R.drawable.pay_no_paid));
                    //datePay.setTimeInMillis(createNextDatePayment(datePay, datePay.getTimeInMillis(), dateStart));
                    //datePay.set(datePay.get(Calendar.YEAR), datePay.get(Calendar.MONTH) + 1, datePay.get(Calendar.DATE_PAY));
                    publishProgress(view);
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(View... values) {
                super.onProgressUpdate(values);
                layout.addView(values[0]);
            }
        }.execute();
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
}
