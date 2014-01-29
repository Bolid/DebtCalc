package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.omdroid.DebtCalc.*;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;
import ru.omdroid.DebtCalc.Arithmetic.ExactArithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Dialog.DialogAddNotify;
import ru.omdroid.DebtCalc.Dialog.DialogDelNotify;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddPayment;

import java.text.DecimalFormat;
import java.util.Calendar;


public class InfoDebt extends Activity {
    TextView tvDeltaAllPay = null,
            tvDigitAllPay = null,
            tvDeltaOnePay = null,
            tvTotalAllPay = null,
            tvTotalOnePay = null,
            tvOverInPercentOne = null,
            tvOverInPercentAll = null;
    EditText etPayment = null;
    InControlFieldAddPayment inControlFieldAddPayment;
    WorkDB workDB;
    double newPayment;
    double paymentDefault = 0.0;
    Boolean setChangeListener = false;

    Menu menu;

    WriteDataInField writeDataInField;
    Arithmetic arithmetic;

    Calendar calendar = Calendar.getInstance();
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.debt_info);
        workDB = new WorkDB(getBaseContext());

        writeDataInField = new WriteDataInField();
        arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);

        final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        paymentDefault = Double.parseDouble(AppData.PAYMENT_DEFAULT);//arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE);

        TextView tvDebt = (TextView)findViewById(R.id.infoDebt);
        TextView tvTerm = (TextView)findViewById(R.id.infoTerm);
        TextView tvPercent = (TextView)findViewById(R.id.infoPercent);

        tvDebt.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.SUM_DEBT)));
        tvTerm.setText(String.valueOf(AppData.TERM_DEBT));
        tvPercent.setText(String.valueOf(AppData.PERCENT));

        LinearLayout llOver = (LinearLayout)findViewById(R.id.llOnePay);
        LinearLayout llTotal = (LinearLayout)findViewById(R.id.llAllPay);
        LinearLayout llControlPay = (LinearLayout)findViewById(R.id.llPayControl);

        llOver.setOnClickListener(new View.OnClickListener() {
            boolean showOverAll = false;
            LinearLayout llOverChild = (LinearLayout)findViewById(R.id.llOverAllPay);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            @Override
            public void onClick(View view) {
                if (!showOverAll) {
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    showOverAll = !showOverAll;
                }
                else{
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    showOverAll = !showOverAll;
                }
                llOverChild.setLayoutParams(param);
            }
        });
        llTotal.setOnClickListener(new View.OnClickListener() {
            boolean showTotalAll = false;
            LinearLayout llTotalChild = (LinearLayout) findViewById(R.id.llTotalAllPay);
            LinearLayout.LayoutParams param;

            @Override
            public void onClick(View view) {
                if (!showTotalAll) {
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    showTotalAll = !showTotalAll;
                } else {
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    showTotalAll = !showTotalAll;
                }
                llTotalChild.setLayoutParams(param);
            }
        });
        llControlPay.setOnLongClickListener(new View.OnLongClickListener() {
            boolean showSeekBar = true;
            LinearLayout.LayoutParams param;
            @Override
            public boolean onLongClick(View view) {
                if (!showSeekBar){
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    showSeekBar = !showSeekBar;
                }
                else{
                    param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    showSeekBar = !showSeekBar;
                }
                findViewById(R.id.llLabelControlPay).setLayoutParams(param);
                seekBar.setLayoutParams(param);
                return false;
            }
        });

        tvDeltaAllPay = (TextView)findViewById(R.id.infoDeltaAllPayment);
        tvDigitAllPay = (TextView)findViewById(R.id.digitAllPay);
        tvTotalAllPay = (TextView)findViewById(R.id.tvTotaAllPay);

        tvDeltaOnePay = (TextView)findViewById(R.id.infoDeltaOnePayment);
        tvTotalOnePay = (TextView)findViewById(R.id.tvTotalOnePay);
        tvOverInPercentOne = (TextView)findViewById(R.id.tvRiceOver);
        tvOverInPercentAll = (TextView)findViewById(R.id.tvRiceOverAll);

        ImageView ivPaymentDefault = (ImageView)findViewById(R.id.ivPaymentDefault);
        ivPaymentDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPayment.removeTextChangedListener(inControlFieldAddPayment);
                setChangeListener = !setChangeListener;
                newPayment = paymentDefault;
                etPayment.setText(new DecimalFormat("###,###,###,###.00").format(newPayment));
                writeDataInField.setOverAllPayment(0);
                writeDataInField.setOverOnePayment(newPayment);
            }
        });


        etPayment = (EditText)findViewById(R.id.etPayment);

        calendar.setTimeInMillis(AppData.DATE_PAY);

        etPayment.setText(new DecimalFormat("###,###,###,###.00").format(Double.valueOf(AppData.PAYMENT)));

        newPayment = Double.parseDouble(AppData.PAYMENT);

        //writeDataInField.setOverAllPayment(0);
        writeDataInField.setOverAllPaymentCustom(newPayment);
        writeDataInField.setOverOnePayment(newPayment);
        arithmetic.getTerm(newPayment, Double.valueOf(AppData.DEBT_BALANCE));

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
            int seekPos;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                etPayment.removeTextChangedListener(inControlFieldAddPayment);
                setChangeListener = false;
                if (i == seekBar.getMax())
                    i--;
                seekPos = i;
                newPayment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE - i);
                etPayment.setText(new DecimalFormat("###,###,###,###.00").format(newPayment));
                writeDataInField.setOverOneForSeekBar(newPayment);
                writeDataInField.setOverAllForSeekBar(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                writeDataInField.setOverAllPayment(seekPos);
                writeDataInField.setOverOnePayment(newPayment);
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
                WorkDateDebt workDateDebt = new WorkDateDebt();
                workDateDebt.getCountDayInMonth(AppData.DATE_PAY);

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

               if (paymentDefault - formatValue(etPayment.getText().toString()) < 0)
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
                Toast.makeText(getBaseContext(), "Сумма ближайшего платежа установлена.", Toast.LENGTH_LONG).show();
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
        int i = workDB.countDataInDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_ID_DEBT_NOTIFY + " = '" + AppData.ID_DEBT + "'");
        pMenu.getMenu().getItem(1).setVisible(i == 0);
        pMenu.getMenu().getItem(2).setVisible(i > 0);
        pMenu.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getBaseContext(), ListPaymentDB.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });

        pMenu.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DialogAddNotify dialogNotify = new DialogAddNotify(getBaseContext());
                dialogNotify.show(getFragmentManager(), "");
                return false;
            }
        });

        pMenu.getMenu().getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DialogDelNotify dialogNotify = new DialogDelNotify(getBaseContext());
                dialogNotify.show(getFragmentManager(), "");
                /*WorkNotification workNotification = new WorkNotification(getBaseContext());
                workNotification.delNotify(AppData.ID_DEBT);*/
                return false;
            }
        });
        pMenu.show();
    }

    public class WriteDataInField{
        ExactArithmetic exactArithmetic = new ExactArithmetic(AppData.PERCENT);

        public void setOverAllPayment(int i){

            Double overPay = getOverPayment() + exactArithmetic.getOverpaymentAllMonth(Double.valueOf(AppData.DEBT_BALANCE), newPayment, AppData.DATE_PAY, 0);//arithmetic.getDeltaNew(AppData.TERM_BALANCE - i, Double.valueOf(AppData.DEBT_BALANCE), newPayment);


            tvDigitAllPay.setText(exactArithmetic.getTotalTerm());
            tvDeltaAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay));
            tvTotalAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay + Double.valueOf(AppData.SUM_DEBT)));
            int overInPercent = arithmetic.getOverInPercent(overPay, Double.valueOf(AppData.SUM_DEBT));
            tvOverInPercentAll.setText(String.valueOf(overInPercent) + "%");
        }

        public void setOverOnePayment(Double currentPayment){
            WorkDateDebt workDateDebt = new WorkDateDebt();
            workDateDebt.getCountDayInMonth(AppData.DATE_PAY);
            Double over = arithmetic.getOverpaymentOneMonth(Double.valueOf(AppData.DEBT_BALANCE));
            if (currentPayment > Double.valueOf(AppData.DEBT_BALANCE) + over)
                currentPayment = Double.valueOf(AppData.DEBT_BALANCE) + over;
            Double newDebt = Double.valueOf(AppData.DEBT_BALANCE) - (currentPayment - over);/*arithmetic.getBalance(currentPayment, Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE)*/;
            Double deltaAfter;
            Double nextPayment = currentPayment;
            if (/*AppData.UP_PAYMENT == 1 || currentPayment > Double.valueOf(AppData.PAYMENT)*/currentPayment > paymentDefault)
                if (AppData.TERM_BALANCE == 1)
                    nextPayment = paymentDefault;
                else
                    nextPayment =  arithmetic.getPayment(newDebt, AppData.TERM_BALANCE - 1);
            if (AppData.TERM_BALANCE > 1){
                deltaAfter = exactArithmetic.getOverpaymentAllMonth(newDebt, nextPayment, workDateDebt.createNextDatePayment(AppData.DATE_PAY, AppData.DATE_DEBT_START), 1);
            }//arithmetic.getDeltaNew(AppData.TERM_BALANCE - 1, newDebt, nextPayment);
            else
                deltaAfter = 0.0;
            Double overPaymentNew = getOverPayment() + /*arithmetic.getOverpaymentOneMonth(Double.valueOf(AppData.DEBT_BALANCE), AppData.COUNT_DAY_OF_MONTH)*/ AppData.OVER_PAYMENT + deltaAfter;

            int overInPercent = arithmetic.getOverInPercent(overPaymentNew, Double.valueOf(AppData.SUM_DEBT));
            tvDeltaOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew));
            tvTotalOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew + Double.valueOf(AppData.SUM_DEBT)));
            tvOverInPercentOne.setText(String.valueOf(overInPercent) + "%");
        }

        public void setOverAllPaymentCustom(Double newPayment){
            Double overAllMouth =  exactArithmetic.getOverpaymentAllMonth(Double.valueOf(AppData.DEBT_BALANCE), newPayment, AppData.DATE_PAY, 0);

            tvDigitAllPay.setText(String.valueOf(Arithmetic.allResult.get(6)));
            tvDeltaAllPay.setText(new DecimalFormat("###,###,###,###").format(overAllMouth + getOverPayment()));
            tvTotalAllPay.setText(new DecimalFormat("###,###,###,###").format(overAllMouth + getOverPayment() + Double.valueOf(AppData.SUM_DEBT)));
            int overInPercent = arithmetic.getOverInPercent(overAllMouth + getOverPayment(), Double.valueOf(AppData.SUM_DEBT));
            tvOverInPercentAll.setText(String.valueOf(overInPercent) + "%");
        }

        public void setOverAllForSeekBar(int i){
            tvDigitAllPay.setText(String.valueOf(AppData.TERM_BALANCE - i));
            Double overPay = getOverPayment() + arithmetic.getDeltaNew(AppData.TERM_BALANCE - i, Double.valueOf(AppData.DEBT_BALANCE), newPayment);
            tvDeltaAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay));
            tvTotalAllPay.setText(new DecimalFormat("###,###,###,###").format(overPay + Double.valueOf(AppData.SUM_DEBT)));
            int overInPercent = arithmetic.getOverInPercent(overPay, Double.valueOf(AppData.SUM_DEBT));
            tvOverInPercentAll.setText(String.valueOf(overInPercent) + "%");
        }

        public void setOverOneForSeekBar(Double currentPayment){
            WorkDateDebt workDateDebt = new WorkDateDebt();
            workDateDebt.getCountDayInMonth(AppData.DATE_PAY);
            Double over = arithmetic.getOverpaymentOneMonth(Double.valueOf(AppData.DEBT_BALANCE));
            if (currentPayment > Double.valueOf(AppData.DEBT_BALANCE) + over)
                currentPayment = Double.valueOf(AppData.DEBT_BALANCE) + over;
            Double newDebt = Double.valueOf(AppData.DEBT_BALANCE) - (currentPayment - over);/*arithmetic.getBalance(currentPayment, Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE)*/;
            Double deltaAfter;
            Double nextPayment = currentPayment;
            if (AppData.UP_PAYMENT == 1 || currentPayment > Double.valueOf(AppData.PAYMENT))
                nextPayment =  arithmetic.getPayment(newDebt, AppData.TERM_BALANCE - 1);
            if (AppData.TERM_BALANCE > 1){
                deltaAfter = arithmetic.getDeltaNew(AppData.TERM_BALANCE - 1, newDebt, nextPayment);
            }
            else
                deltaAfter = 0.0;
            Double overPaymentNew = getOverPayment() + /*arithmetic.getOverpaymentOneMonth(Double.valueOf(AppData.DEBT_BALANCE), AppData.COUNT_DAY_OF_MONTH)*/ AppData.OVER_PAYMENT + deltaAfter;

            int overInPercent = arithmetic.getOverInPercent(overPaymentNew, Double.valueOf(AppData.SUM_DEBT));
            tvDeltaOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew));
            tvTotalOnePay.setText(new DecimalFormat("###,###,###,###").format(overPaymentNew + Double.valueOf(AppData.SUM_DEBT)));
            tvOverInPercentOne.setText(String.valueOf(overInPercent) + "%");
        }
    }  //класс для вывода информации по переплате
}
