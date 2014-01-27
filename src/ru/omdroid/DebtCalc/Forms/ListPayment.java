package ru.omdroid.DebtCalc.Forms;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;
import ru.omdroid.DebtCalc.R;
import android.app.Activity;
import android.os.Bundle;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.text.DecimalFormat;
import java.util.Calendar;

public class ListPayment extends Activity {
    View view = null;
    boolean addRecord = true;
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.payment_info);



        new AsyncTask<Void, View, Void>() {
            LinearLayout layout = (LinearLayout)findViewById(R.id.llPaymentInfo);
            @Override
            protected Void doInBackground(Void... voids) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
                Double paymentPercent, paymentDebt, balance, payment, feePayment = 0.0;
                Arithmetic arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);
                String date;

                if (!AppData.PAYMENT.equals(""))
                    payment = Double.valueOf(AppData.PAYMENT);
                else
                    payment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE);
                balance = Double.valueOf(AppData.DEBT_BALANCE);

                WorkDateDebt workDateDebt = new WorkDateDebt();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(AppData.DATE_PAY);
                calendar.setTimeInMillis(workDateDebt.createNextDatePayment(calendar.getTimeInMillis(), AppData.DATE_PAY));

                int i = 1;
                while (balance > 0.01){
                    if (!addRecord)
                        return null;
                    workDateDebt.getCountDayInMonth(calendar.getTimeInMillis());
                    paymentPercent = arithmetic.getOverpaymentOneMonth(balance);
                    paymentDebt = arithmetic.getPaymentInDebt(payment, balance);
                    if (i == AppData.TERM_BALANCE)
                        payment = balance + paymentPercent;
                    feePayment = feePayment + payment;
                    addRecord(inflater, layout, i, payment, workDateDebt.getDate(calendar), balance, paymentDebt, paymentPercent, feePayment);
                    balance = arithmetic.getBalance(payment, balance);
                    calendar.setTimeInMillis(workDateDebt.createNextDatePayment(calendar.getTimeInMillis(), AppData.DATE_PAY));
                    i++;
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
                          String date,
                          final Double balance,
                          final Double paymentDebt,
                          final Double paymentPercent,
                          Double feePayment){
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
                LinearLayout layoutChild = (LinearLayout) view.findViewById(R.id.layoutInfoListPod);
                if (!infoDetail) {
                    TextView tvBalanceDebt = (TextView) view.findViewById(R.id.debtBalanceListInfo);
                    TextView tvDebt = (TextView) view.findViewById(R.id.tvDebtRecord);
                    TextView tvPercent = (TextView) view.findViewById(R.id.tvPercentRecord);

                    tvBalanceDebt.setText(new DecimalFormat("###,###,###,###").format(balance));
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
    public void onStop(){
        super.onStop();
        addRecord = false;
    }
}
