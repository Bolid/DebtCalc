package ru.omdroid.DebtCalc.Forms;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.R;
import android.app.Activity;
import android.os.Bundle;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(AppData.DATE);
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));

                int i = 1;
                //for (int j = AppData.TERM_BALANCE; j > 0; j--)
                while (balance > 0.01){
                    if (!addRecord)
                        return null;
                    feePayment = feePayment + payment;
                    paymentPercent = arithmetic.getPaymentInPercent(balance);
                    paymentDebt = arithmetic.getPaymentInDebt(payment, balance);
                    addRecord(inflater, layout, i, payment, getDate(calendar), balance, paymentDebt, paymentPercent, feePayment);
                    balance = arithmetic.getBalance(payment, balance, AppData.TERM_BALANCE);
                    calendar.setTimeInMillis(createNextDatePayment(calendar, calendar.getTimeInMillis(), AppData.DATE));
                    //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
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

    private String getDate(Calendar calendar) {
        String date;
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd");
        date = format.format(calendar.getTime());
        format.applyPattern("MM");
        date = date + "." + format.format(calendar.getTime()) + "." + String.valueOf(calendar.get(Calendar.YEAR));
        return date;
    }

    private Long createNextDatePayment(Calendar calendar, Long dateLong, Long dateStart){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateStart);
        calendar.setTimeInMillis(dateLong);
        int preMonth = calendar.get(Calendar.MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
        int postMonth = calendar.get(Calendar.MONTH);
        while (postMonth - preMonth > 1){
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)-1);
            postMonth = calendar.get(Calendar.MONTH);
        }
        return calendar.getTimeInMillis();
    }

    @Override
    public void onStop(){
        super.onStop();
        addRecord = false;
    }
}
