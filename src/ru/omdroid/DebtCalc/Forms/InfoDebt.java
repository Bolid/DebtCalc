package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.CustomView.DataForGraph;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddPayment;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.text.DecimalFormat;
import java.util.Calendar;


public class InfoDebt extends Activity {
    TextView tvDeltaAllPay = null,
            tvDigitAllPay = null,
            tvDeltaOnePay = null,
            tvTotalAllPay = null,
            tvTotalOnePay = null;
    EditText etPayment = null;
    InControlFieldAddPayment inControlFieldAddPayment;
    WorkDB workDB;
    double newPayment;
    Boolean setChangeListener = false;

    Menu menu;

    WriteDataInField writeDataInField;
    Arithmetic arithmetic;
    AppData appData = new AppData();

    Calendar calendar = Calendar.getInstance();
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.debt_info);
        ActionBar actionBar = getActionBar();
        workDB = new WorkDB(getBaseContext());

        writeDataInField = new WriteDataInField();
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

        ImageView ivPaymentDefault = (ImageView)findViewById(R.id.ivPaymentDefault);
        ivPaymentDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPayment.removeTextChangedListener(inControlFieldAddPayment);
                setChangeListener = !setChangeListener;
                etPayment.setText(new DecimalFormat("###,###,###,###.##").format(Double.valueOf(AppData.PAYMENT_DEFAULT)));
                newPayment = Double.valueOf(AppData.PAYMENT_DEFAULT);
                writeDataInField.setOverAllPayment(0);
                writeDataInField.setOverOnePayment(Double.valueOf(AppData.PAYMENT_DEFAULT));
            }
        });


        etPayment = (EditText)findViewById(R.id.etPayment);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);


        final DataForGraph dataForGraph = new DataForGraph();
        dataForGraph.createOver(true);
        dataForGraph.createTerm(false);
        dataForGraph.setHeightOver(10);
        calendar.setTimeInMillis(AppData.DATE_PAY);

        etPayment.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.PAYMENT)));

        newPayment = Double.parseDouble(AppData.PAYMENT);

        writeDataInField.setOverAllPayment(0);
        writeDataInField.setOverOnePayment(newPayment);

        etPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setChangeListener){
                    etPayment.addTextChangedListener(inControlFieldAddPayment);
                    setChangeListener = true;
                }
            }
        });

        seekBar.setMax(AppData.TERM_BALANCE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                etPayment.removeTextChangedListener(inControlFieldAddPayment);
                setChangeListener = false;
                if (i == seekBar.getMax())
                    i--;
                newPayment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE - i);
               /* if (Double.valueOf(AppData.DEBT_BALANCE) - newPayment < 0)
                    newPayment = newPayment + (Double.valueOf(AppData.DEBT_BALANCE) - newPayment);*/
                etPayment.setText(new DecimalFormat("###,###,###,###").format(newPayment));
                writeDataInField.setOverAllPayment(i);
                writeDataInField.setOverOnePayment(newPayment);
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

        inControlFieldAddPayment = new InControlFieldAddPayment(etPayment, Double.valueOf(AppData.PAYMENT_DEFAULT), this.menu, writeDataInField);
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
                                            DebtCalcDB.FIELD_SUM_PAYMENTS + " = '" + feePayment + "', " +
                                            DebtCalcDB.FIELD_DEBT_PAYMENTS + " = '" + arithmetic.getPaymentInDebt(formatValue(etPayment.getText().toString()), Double.valueOf(AppData.DEBT_BALANCE)) +
                        "' WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                                  "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
                Log.v("Разность", String.valueOf(Double.valueOf(AppData.PAYMENT_DEFAULT) - formatValue(etPayment.getText().toString())));

               if (Double.valueOf(AppData.PAYMENT_DEFAULT) - formatValue(etPayment.getText().toString()) < 0)
                   workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +
                           " SET " + DebtCalcDB.F_PAYMENT_UP_PAY + " = '1'" +
                           " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT +
                           "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
                else
                   workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +
                           " SET " + DebtCalcDB.F_PAYMENT_UP_PAY + " = '0'" +
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

    private Double getPaymentInPercentFromDataBase(){
        Cursor cursorOver = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_DEBT_PAYMENTS +
                " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT + "' AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
        cursorOver.moveToNext();
        Double overDebt = cursorOver.getDouble(cursorOver.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS));
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

    public class WriteDataInField{

        public void setOverAllPayment(int i){
            tvDigitAllPay.setText(String.valueOf(AppData.TERM_BALANCE - i));
            Double overPay = getOverPayment() + arithmetic.getDeltaNew(AppData.TERM_BALANCE - i, Double.valueOf(AppData.DEBT_BALANCE), newPayment);
            tvDeltaAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay));
            tvTotalAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay + Double.valueOf(AppData.DEBT)));
        }

        public void setOverOnePayment(Double newPayment){
            WorkDateDebt workDateDebt = new WorkDateDebt();
            workDateDebt.getCountDayInMonth(AppData.DATE_PAY);

            Double newDebt = arithmetic.getBalance(newPayment, Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE);
            Double deltaAfter;
            if (AppData.TERM_BALANCE > 1)
                deltaAfter = arithmetic.getDeltaNew(AppData.TERM_BALANCE - 1, newDebt, arithmetic.getPayment(newDebt, AppData.TERM_BALANCE - 1));
            else
                deltaAfter = 0.0;
            Double overPaymentNew = getOverPayment() + arithmetic.getPaymentInPercent(Double.valueOf(AppData.DEBT_BALANCE), AppData.COUNT_DAY_OF_MONTH) + deltaAfter;
            tvDeltaOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew));
            tvTotalOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew + Double.valueOf(AppData.DEBT)));
        }

        public void setOverAllPaymentCustom(Double newPayment){
            arithmetic.getOverpaymentAllMonth(Double.valueOf(AppData.DEBT_BALANCE), newPayment, true);
            tvDigitAllPay.setText(String.valueOf(Arithmetic.allResult.get(6)));
            tvDeltaAllPay.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(5)) + getOverPayment()));
            tvTotalAllPay.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(5)) + getOverPayment() + Double.valueOf(AppData.DEBT)));
        }
    }
}
