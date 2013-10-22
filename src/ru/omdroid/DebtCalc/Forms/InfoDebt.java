package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddPayment;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.util.Calendar;


public class InfoDebt extends Activity {
    View view = null;
    TextView tvPayment = null;
    EditText etPayment = null;

    double newPayment;

    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.debt_info);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvPayment = (TextView)findViewById(R.id.infoPayment);
        TextView tvDate = (TextView)findViewById(R.id.infoDate);
        TextView tvDebt = (TextView)findViewById(R.id.infoDebt);
        TextView tvDateDay = (TextView)findViewById(R.id.tvInfoDate);
        etPayment = (EditText)findViewById(R.id.etPayment);
        Button bPlusPayment = (Button)findViewById(R.id.bPlusPayment);
        final Button bMinusPayment = (Button)findViewById(R.id.bMinusPayment);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(AppData.DATE);
        final InControlFieldAddPayment inControlFieldAddPayment = new InControlFieldAddPayment(etPayment, null, null, Double.valueOf(AppData.PAYMENT_DEFAULT));

        tvPayment.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.PAYMENT)));
        tvDate.setText(getResources().getStringArray(R.array.month)[calendar.get(Calendar.MONTH)]);
        tvDebt.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.DEBT)));
        tvDateDay.setText(String.valueOf(calendar.get(Calendar.DATE)));
        etPayment.setText(new DecimalFormat("###,###,###,###.00").format(Double.valueOf(AppData.PAYMENT)));

        etPayment.selectAll();
        newPayment = Double.parseDouble(AppData.PAYMENT);

        etPayment.addTextChangedListener(inControlFieldAddPayment);

        bPlusPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPayment.removeTextChangedListener(inControlFieldAddPayment);
                newPayment = newPayment + 1000;
                etPayment.setText(new DecimalFormat("###,###,###.00").format(newPayment));
                bMinusPayment.setEnabled(newPayment > Double.valueOf(AppData.PAYMENT_DEFAULT));
                etPayment.addTextChangedListener(inControlFieldAddPayment);
            }
        });

        bMinusPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPayment.removeTextChangedListener(inControlFieldAddPayment);
                newPayment = newPayment - 1000;
                etPayment.setText(new DecimalFormat("###,###,###.00").format(newPayment));
                bMinusPayment.setEnabled(newPayment > Double.valueOf(AppData.PAYMENT_DEFAULT));
                etPayment.addTextChangedListener(inControlFieldAddPayment);
            }
        });

        new AsyncTask<Void, View, Void>() {
            LinearLayout layout = (LinearLayout)findViewById(R.id.llPaymentInfo);
            @Override
            protected Void doInBackground(Void... voids) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
                WorkDB workDB = new WorkDB(getBaseContext());
                Cursor cursorInPayment = workDB.readValueFromDataBase("SELECT " +
                        DebtCalcDB.FIELD_PAYMENT_PAYMENTS +", "+
                        DebtCalcDB.FIELD_DEBT_PAYMENTS +", "+
                        DebtCalcDB.FIELD_PERCENT_PAYMENTS +", "+
                        DebtCalcDB.FIELD_SUM_PAYMENTS +
                        " FROM " + DebtCalcDB.TABLE_NAME_PAYMENTS +
                        " WHERE (" + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '1'" +
                        " AND " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +"')");
                int numPayment = 0;
                while (cursorInPayment.moveToNext()){
                    numPayment++;addRecord(inflater,
                            layout,
                            numPayment,
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PERCENT_PAYMENTS)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)));
                    publishProgress(view);
                }
                return null;  //To change body of implemented methods use File | Settings | File Templates.
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
                          Double paymentDebt,
                          Double paymentPercent,
                          Double feePayment){
        view = inflater.inflate(R.layout.payment_info_record, layout, false);

        TextView tvNumPayment = (TextView)view.findViewById(R.id.tvNumPaymentRecord);
        TextView tvPayment = (TextView)view.findViewById(R.id.tvPaymentRecord);
        TextView tvDebt = (TextView)view.findViewById(R.id.tvDebtRecord);
        TextView tvPercent = (TextView)view.findViewById(R.id.tvPercentRecord);
        TextView tvFeePayment = (TextView)view.findViewById(R.id.tvSumPaymentRecord);

        tvNumPayment.setText(String.valueOf(numPayment));
        tvPayment.setText(new DecimalFormat("###,###,###,###,###.00").format(payment));
        tvDebt.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentDebt));
        tvPercent.setText(new DecimalFormat("###,###,###,###,###.00").format(paymentPercent));
        tvFeePayment.setText(new DecimalFormat("###,###,###,###,###.00").format(feePayment));
        return view;
    }

    private String formatValue(String addPayment){
        String newPay = "";
        for (int i = 0; i < addPayment.length(); i++){
            if ("1234567890".contains(String.valueOf(addPayment.charAt(i))))
                newPay = newPay + String.valueOf(addPayment.charAt(i));
        }
        return newPay;
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
                WorkDB workDB = new WorkDB(getBaseContext());
                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_NAME_PAYMENTS +
                        " SET " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + " = '" + formatValue(etPayment.getText().toString()) +
                        "' WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                        "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
                Cursor cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS +
                        " FROM " + DebtCalcDB.TABLE_NAME_PAYMENTS +
                        " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                        "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
                cursor.moveToNext();
                Log.v("Платеж", cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
                Log.v("Платеж", formatValue(etPayment.getText().toString()));
                tvPayment.setText(etPayment.getText().toString());
                workDB.disconnectDataBase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
