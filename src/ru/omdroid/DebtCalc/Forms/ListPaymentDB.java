package ru.omdroid.DebtCalc.Forms;


import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.text.DecimalFormat;
import java.util.Calendar;

public class ListPaymentDB extends Activity {
    View view = null;

    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.payment_info);
        final WorkDB workDB = new WorkDB(getBaseContext());

        new AsyncTask<Void, View, Void>() {
            LinearLayout layout = (LinearLayout)findViewById(R.id.llPaymentInfo);
            @Override
            protected Void doInBackground(Void... voids) {
                Calendar datePay = Calendar.getInstance();
                LayoutInflater inflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
                Cursor cursorInPayment = workDB.readValueFromDataBase("SELECT " +
                        DebtCalcDB.FIELD_ID_DEBT_PAYMENTS +", "+
                        DebtCalcDB.FIELD_PAYMENT_PAYMENTS +", "+
                        DebtCalcDB.FIELD_DEBT_PAYMENTS +", "+
                        DebtCalcDB.FIELD_PERCENT_PAYMENTS +", "+
                        DebtCalcDB.FIELD_DATE_LONG_PAYMENTS +", "+
                        DebtCalcDB.F_BALANCE_DEBT_PAY +", "+
                        DebtCalcDB.F_BALANCE_TERM_PAY +", "+
                        DebtCalcDB.FIELD_SUM_PAYMENTS +", "+
                        DebtCalcDB.F_PAYMENT_UP_PAY +
                        " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                        " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +"')");
                int numPayment = 0;
                while (cursorInPayment.moveToNext()){
                    numPayment++;
                    datePay.setTimeInMillis(cursorInPayment.getLong(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
                    String date = String.valueOf(datePay.get(Calendar.DATE)) + "." + String.valueOf(datePay.get(Calendar.MONTH) + 1) + "." + String.valueOf(datePay.get(Calendar.YEAR));
                    addRecord(inflater,
                            layout,
                            numPayment,
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PERCENT_PAYMENTS)),
                            date,
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)),
                            cursorInPayment.getString(cursorInPayment.getColumnIndex(DebtCalcDB.F_PAYMENT_UP_PAY)),
                            getResources().getColor(R.color.infoPaymentBackground));
                    publishProgress(view);
                }

                Double paymentPercent, paymentDebt, balanceDebt, payment, feePayment = 0.0;
                String date;

                cursorInPayment.moveToLast();
                int balanceTerm = cursorInPayment.getInt(cursorInPayment.getColumnIndex(DebtCalcDB.F_BALANCE_TERM_PAY));
                balanceDebt = cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY));
                feePayment = cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS));
                cursorInPayment.close();

                Arithmetic arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT), AppData.PERCENT, AppData.TERM);

                payment = arithmetic.getPayment(balanceDebt, balanceTerm);
                balanceDebt = Double.valueOf(AppData.DEBT);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(AppData.DATE);
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
                for (int j = balanceTerm - 1; j > 0; j--){
                    numPayment++;
                    feePayment = feePayment + payment;
                    paymentPercent = arithmetic.getPaymentInPercent(balanceDebt);
                    paymentDebt = arithmetic.getPaymentInDebt(payment, balanceDebt);
                    balanceDebt = arithmetic.getBalance(payment, balanceDebt, AppData.TERM);
                    date = (calendar.get(Calendar.DATE)) + "." + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
                    addRecord(inflater, layout, numPayment, payment, paymentDebt, paymentPercent, date, balanceDebt, feePayment, null, getResources().getColor(R.color.background));
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
                    publishProgress(view);
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(View... values) {
                super.onProgressUpdate(values);
                layout.addView(values[0]);
            }

            protected void onPostExecute(View view){
                if (view != null)
                    layout.addView(view);
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
                          int color){
        view = inflater.inflate(R.layout.payment_info_record, layout, false);

        view.setBackgroundColor(color);

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

                    tvBalanceDebt.setText(new DecimalFormat("###,###,###,###").format(balanceDebt));
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
}
