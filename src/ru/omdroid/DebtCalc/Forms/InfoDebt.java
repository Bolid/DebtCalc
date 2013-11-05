package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
    TextView tvDeltaAllPay = null,
            tvDigitAllPay = null,
            tvDeltaOnePay = null,
            tvTotalAllPay = null,
            tvTotalOnePay = null;
    EditText etPayment = null;
    Button bMinusPayment = null;
    InControlFieldAddPayment inControlFieldAddPayment;
    WorkDB workDB;
    double newPayment;

    Menu menu;

    Arithmetic arithmetic;

    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.debt_info);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        workDB = new WorkDB(getBaseContext());

       // final View viewGraph = (View)findViewById(R.id.viewGraph);
       arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);

        TextView tvDebt = (TextView)findViewById(R.id.infoDebt);
        TextView tvTerm = (TextView)findViewById(R.id.infoTerm);
        TextView tvPercent = (TextView)findViewById(R.id.infoPercent);

        tvDebt.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.DEBT)));
        tvTerm.setText(String.valueOf(AppData.TERM));
        tvPercent.setText(String.valueOf(AppData.PERCENT));

        tvDeltaAllPay = (TextView)findViewById(R.id.infoDeltaAllPayment);
        tvDigitAllPay = (TextView)findViewById(R.id.digitAllPay);
        tvTotalAllPay = (TextView)findViewById(R.id.tvTotaAllPay);

        tvDeltaOnePay = (TextView)findViewById(R.id.infoDeltaOnePayment);
        tvTotalOnePay = (TextView)findViewById(R.id.tvTotalOnePay);

        final TextView tvDateDay = (TextView)findViewById(R.id.tvInfoDate);
        final TextView tvDate = (TextView)findViewById(R.id.infoDate);

        etPayment = (EditText)findViewById(R.id.etPayment);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);

        Button bPlusPayment = (Button)findViewById(R.id.bPlusPayment);
        bMinusPayment = (Button)findViewById(R.id.bMinusPayment);

        final DataForGraph dataForGraph = new DataForGraph();
        dataForGraph.createOver(true);
        dataForGraph.createTerm(false);
        dataForGraph.setHeightOver(10);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(AppData.DATE);

        tvDate.setText(getResources().getStringArray(R.array.month)[calendar.get(Calendar.MONTH)]);
        tvDateDay.setText(String.valueOf(calendar.get(Calendar.DATE)));
        etPayment.setText(new DecimalFormat("###,###,###,###.00").format(Double.valueOf(AppData.PAYMENT)));

        newPayment = Double.parseDouble(AppData.PAYMENT);

        setOverAllPayment(0);
        setOverOnePayment();

        seekBar.setMax(AppData.TERM_BALANCE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == seekBar.getMax())
                    i--;
                newPayment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE - i);
                etPayment.setText(new DecimalFormat("###,###,###,###").format(newPayment));
                setOverAllPayment(i);
                setOverOnePayment();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            });
    }

    private Double formatValue(String addPayment){
        String newPay = "";
        for (int i = 0; i < addPayment.length(); i++){
            if ("1234567890".contains(String.valueOf(addPayment.charAt(i))))
                newPay = newPay + String.valueOf(addPayment.charAt(i));
            else if (i == addPayment.length() - 3 || i == addPayment.length() - 2)
                newPay = newPay + ".";
        }
        return Double.valueOf(newPay);
    }

    private void setNewPayment(int inc){
        newPayment = formatValue(etPayment.getText().toString());
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

                Cursor sumPaymentCursor = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.FIELD_SUM_PAYMENTS + ") " +
                    "AS " + DebtCalcDB.FIELD_SUM_PAYMENTS + ", " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS +
                    " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                    " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + AppData.ID_DEBT);
                sumPaymentCursor.moveToNext();
                Double feePayment = formatValue(etPayment.getText().toString()) + (sumPaymentCursor.getDouble(sumPaymentCursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)) - sumPaymentCursor.getDouble(sumPaymentCursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
                sumPaymentCursor.close();

                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +
                                  " SET " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + " = '" + formatValue(etPayment.getText().toString()) + "', " +
                                            DebtCalcDB.FIELD_SUM_PAYMENTS + " = '" + feePayment +
                                  "' WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                                  "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");

               if (Double.valueOf(AppData.PAYMENT_DEFAULT) < formatValue(etPayment.getText().toString()))
                   workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +
                           " SET " + DebtCalcDB.F_PAYMENT_UP_PAY + " = '1'" +
                           " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                           "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
                workDB.disconnectDataBase();
                return true;
            case R.id.popup:
                View v = findViewById(R.id.popup);
                showPopupMenu(v);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Double getOverPayment(){
       Cursor cursorOver = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.F_OVER_PAY + ") " +
           "AS " + DebtCalcDB.F_OVER_PAY +
           " FROM " + DebtCalcDB.TABLE_PAYMENTS +
           " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + AppData.ID_DEBT);
       cursorOver.moveToNext();
       Double overDebt = cursorOver.getDouble(cursorOver.getColumnIndex(DebtCalcDB.F_OVER_PAY));
       cursorOver.close();
       return  overDebt;
   }

    private void showPopupMenu(View v){
        final PopupMenu pMenu = new PopupMenu(getBaseContext(), v);
        MenuInflater mInflater = pMenu.getMenuInflater();
        mInflater.inflate(R.menu.pm_d_add, pMenu.getMenu());
        pMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getBaseContext(), ListPaymentDB.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });
        pMenu.show();
    }

    private void setOverAllPayment(int i){
        tvDigitAllPay.setText(String.valueOf(AppData.TERM_BALANCE - i) + "x");
        Double overPay = getOverPayment() + arithmetic.getDeltaNew(AppData.TERM_BALANCE - i, Double.valueOf(AppData.DEBT_BALANCE), newPayment);
        tvDeltaAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay));
        tvTotalAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay + Double.valueOf(AppData.DEBT)));
    }

    private void setOverOnePayment(){
        Double newDebt = arithmetic.getBalance(newPayment, Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE);
        Double overPaymentNew = getOverPayment() + arithmetic.getPaymentInPercent(Double.valueOf(AppData.DEBT_BALANCE)) + arithmetic.getDeltaNew(AppData.TERM_BALANCE - 1, newDebt, arithmetic.getPayment(newDebt, AppData.TERM_BALANCE - 1));
        tvDeltaOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew));
        tvTotalOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew + Double.valueOf(AppData.DEBT)));
    }
}
