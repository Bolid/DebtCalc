package ru.omdroid.DebtCalc;


import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;

import java.text.DecimalFormat;

public class TablePaymentNotSavedDebt extends Activity implements TablePaymentManager{
    View view = null;
    boolean addRecord = true;
    LinearLayout layout;
    LayoutInflater inflater;
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.payment_info);
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
                Double paymentPercent, paymentDebt, balanceDebt, payment, feePayment = 0.0;
                Arithmetic arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);
                String date;
                Long datePaymentLong;
                int numPayment = 0, termBalance = AppData.TERM_BALANCE;

                if (!AppData.PAYMENT.equals(""))
                    payment = Double.valueOf(AppData.PAYMENT);
                else
                    payment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE);
                balanceDebt = Double.valueOf(AppData.DEBT_BALANCE);

                WorkDateDebt workDateDebt = new WorkDateDebt();
                datePaymentLong = AppData.DATE_PAY;
                datePaymentLong = workDateDebt.createNextDatePayment(datePaymentLong, AppData.DATE_PAY);

                while (balanceDebt > 0.01){
                    if (!addRecord)
                        return null;
                    workDateDebt.getCountDayInMonth(datePaymentLong);
                    paymentPercent = arithmetic.getOverpaymentOneMonth(balanceDebt);
                    if (termBalance == 1)
                        payment = balanceDebt + paymentPercent;
                    termBalance--;
                    paymentDebt = payment - paymentPercent;
                    feePayment += payment;
                    date = workDateDebt.getDate(datePaymentLong);
                    numPayment++;
                    publishProgress(createDataFromArithmetic(payment, feePayment, date, numPayment, balanceDebt, paymentDebt, paymentPercent));


                    balanceDebt -= paymentDebt;
                    datePaymentLong = workDateDebt.createNextDatePayment(datePaymentLong, AppData.DATE_PAY);
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
        return null;
    }

    @Override
    public View createDataFromDB(Double payment, Double feePayment, String date, String upPayment, int numPayment, final Double balanceDebt, final Double paymentDebt, final Double paymentPercent, Drawable color) {
        return null;
    }

    @Override
    public View createDataFromArithmetic(Double payment, Double feePayment, String date, int numPayment, final Double balanceDebt, final Double paymentDebt, final Double paymentPercent) {
        view = inflater.inflate(R.layout.payment_info_record, layout, false);

        TextView tvNumPayment = (TextView)view.findViewById(R.id.tvNumPaymentRecord);
        TextView tvPayment = (TextView)view.findViewById(R.id.tvPaymentRecord);
        TextView tvDatePay= (TextView)view.findViewById(R.id.tvInfoDateList);
        TextView tvFeePayment = (TextView)view.findViewById(R.id.tvSumPaymentRecord);

        tvNumPayment.setText(String.valueOf(numPayment));
        tvDatePay.setText(date);
        tvPayment.setText(new DecimalFormat("###,###,###,###,###.00").format(payment));
        tvFeePayment.setText(new DecimalFormat("###,###,###,###,###.00").format(feePayment));
        view.setOnClickListener(new View.OnClickListener() {
            boolean infoDetail = false;
            LinearLayout.LayoutParams param = null;

            @Override
            public void onClick(View view) {
                LinearLayout layoutChild = (LinearLayout)view.findViewById(R.id.layoutInfoListPod);
                if (!infoDetail) {
                    TextView tvBalanceDebt = (TextView) view.findViewById(R.id.debtBalanceListInfo);
                    TextView tvDebt = (TextView) view.findViewById(R.id.tvDebtRecord);
                    TextView tvPercent = (TextView) view.findViewById(R.id.tvPercentRecord);

                    tvBalanceDebt.setText(new DecimalFormat("###,###,###,###").format(balanceDebt));
                    tvDebt.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentDebt));
                    tvPercent.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentPercent));
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    infoDetail = !infoDetail;
                } else {
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    infoDetail = !infoDetail;
                }
                layoutChild.setLayoutParams(param);
            }
        });
        return view;
    }

    @Override
    public void insertField(View view) {
        layout.addView(view);
    }
}

