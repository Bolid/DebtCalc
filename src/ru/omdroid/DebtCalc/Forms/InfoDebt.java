package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.CustomView.DataForGraph;
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
    Button bMinusPayment = null;
    InControlFieldAddPayment inControlFieldAddPayment;
    WorkDB workDB;
    double newPayment;

    Menu menu;

    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.debt_info);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        workDB = new WorkDB(getBaseContext());

        final View viewGraph = (View)findViewById(R.id.viewGraph);

        tvPayment = (TextView)findViewById(R.id.infoPayment);
        TextView tvDate = (TextView)findViewById(R.id.infoDate);
        TextView tvDebt = (TextView)findViewById(R.id.infoDebt);
        TextView tvDateDay = (TextView)findViewById(R.id.tvInfoDate);
        etPayment = (EditText)findViewById(R.id.etPayment);
        Button bPlusPayment = (Button)findViewById(R.id.bPlusPayment);
        bMinusPayment = (Button)findViewById(R.id.bMinusPayment);

        final DataForGraph dataForGraph = new DataForGraph();
        dataForGraph.createOver(true);
        dataForGraph.createTerm(false);
        dataForGraph.setHeightOver(10);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(AppData.DATE);

        tvPayment.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.PAYMENT)));
        tvDate.setText(getResources().getStringArray(R.array.month)[calendar.get(Calendar.MONTH)]);
        tvDebt.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.DEBT)));
        tvDateDay.setText(String.valueOf(calendar.get(Calendar.DATE)));
        etPayment.setText(new DecimalFormat("###,###,###,###.00").format(Double.valueOf(AppData.PAYMENT)));

        etPayment.selectAll();
        newPayment = Double.parseDouble(AppData.PAYMENT);

<<<<<<< HEAD
        dataForGraph.setSum(500000.);
        dataForGraph.setOver(600000.);
        dataForGraph.setTerm(500000.);
        dataForGraph.setNewTerm(400000.);
=======
        Cursor cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.F_OVER_PAY + " FROM " + DebtCalcDB.TABLE_PAYMENTS + " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +"'");
        cursor.moveToNext();
        Double overOld = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_OVER_PAY));
        cursor.close();

        cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_BALANCE_TERM_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS + " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + AppData.ID_DEBT +"'");
        cursor.moveToNext();
        int term = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_BALANCE_TERM_DEBT));
        cursor.close();

        Arithmetic arithmetic = new Arithmetic(AppData.PERCENT);

        Double overAll = overOld + AppData.OVER_PAYMENT + arithmetic.getDeltaNew(term - 1, arithmetic.getBalance(newPayment, Double.valueOf(AppData.DEBT), AppData.TERM), newPayment);

        dataForGraph.setSum(Double.valueOf(AppData.DEBT));
        dataForGraph.setOver(overAll);
        dataForGraph.setParamOlr(120);
        dataForGraph.setParamNew(100);
>>>>>>> 1248bef9f23650e97e6cb4d330ca8756fb4ed92f
        viewGraph.invalidate();

        bPlusPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewPayment(1000);
                bMinusPayment.setEnabled(newPayment > Double.valueOf(AppData.PAYMENT_DEFAULT));
            }
        });

        bMinusPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewPayment(-1000);
                bMinusPayment.setEnabled(newPayment > Double.valueOf(AppData.PAYMENT_DEFAULT));
            }
        });

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
                        DebtCalcDB.FIELD_SUM_PAYMENTS +", "+
                        DebtCalcDB.F_PAYMENT_UP_PAY +
                        " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                        " WHERE (" + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '1'" +
                        " AND " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +"')");
                int numPayment = 0;
                while (cursorInPayment.moveToNext()){
                    numPayment++;
                    datePay.setTimeInMillis(cursorInPayment.getLong(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
                    String date = String.valueOf(datePay.get(Calendar.DATE)) + "." + String.valueOf(datePay.get(Calendar.MONTH) + 1) + "." + String.valueOf(datePay.get(Calendar.YEAR));
                    addRecord(inflater,
                            layout,
                            cursorInPayment.getString(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS)),
                            numPayment,
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PERCENT_PAYMENTS)),
                            date,
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY)),
                            cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)),
                            cursorInPayment.getString(cursorInPayment.getColumnIndex(DebtCalcDB.F_PAYMENT_UP_PAY)));
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
                          final String idDebt,
                          int numPayment,
                          Double payment,
                          final Double paymentDebt,
                          final Double paymentPercent,
                          String date,
                          final Double balanceDebt,
                          Double feePayment,
                          String upPayment){
        view = inflater.inflate(R.layout.payment_info_record, layout, false);

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

    private String formatValue(String addPayment){
        String newPay = "";
        for (int i = 0; i < addPayment.length(); i++){
            if ("1234567890".contains(String.valueOf(addPayment.charAt(i))))
                newPay = newPay + String.valueOf(addPayment.charAt(i));
            else if (i == addPayment.length() - 3 || i == addPayment.length() - 2)
                newPay = newPay + ".";
        }
        return newPay;
    }

    private void setNewPayment(int inc){
        newPayment = Double.parseDouble(formatValue(etPayment.getText().toString()));
        newPayment = newPayment + inc;
        etPayment.setText(new DecimalFormat("###,###,###").format(newPayment));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater actionMenu = getMenuInflater();
        actionMenu.inflate(R.menu.di_action_menu, menu);
        this.menu = menu;
        inControlFieldAddPayment = new InControlFieldAddPayment(etPayment, null, bMinusPayment, Double.valueOf(AppData.PAYMENT_DEFAULT), this.menu);
        etPayment.addTextChangedListener(inControlFieldAddPayment);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.apply_payment:
                WorkDB workDB = new WorkDB(getBaseContext());
                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +
                        " SET " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + " = '" + formatValue(etPayment.getText().toString()) +
                        "' WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                        "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
               if (Double.valueOf(AppData.PAYMENT_DEFAULT) < Double.valueOf(formatValue(etPayment.getText().toString())))
                   workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +
                           " SET " + DebtCalcDB.F_PAYMENT_UP_PAY + " = '1'" +
                           " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                           "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
                tvPayment.setText(etPayment.getText().toString());
                workDB.disconnectDataBase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createGraph(){

    }
}
