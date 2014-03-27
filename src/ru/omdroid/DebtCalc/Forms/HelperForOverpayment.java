package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;
import ru.omdroid.DebtCalc.Arithmetic.ExactArithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;


public class HelperForOverpayment extends Activity {
    final String LABEL = "CREDIT_";

    WorkDB workDB;
    AppData appData = new AppData();
    ExactArithmetic exactArithmetic = new ExactArithmetic(null);

    HashMap <String, String> idDebt = new HashMap<String, String>();

    HashMap <String, Double> overPaymentDefault = new HashMap<String, Double>();
    HashMap <String, Double> overPaymentAdd = new HashMap<String, Double>();
    HashMap <String, Double> balanceDebt = new HashMap<String, Double>();
    HashMap <String, Double> payment = new HashMap<String, Double>();
    HashMap <String, Double> paymentDefault = new HashMap<String, Double>();
    HashMap <String, Double> oldOverpayment = new HashMap<String, Double>();
    HashMap <String, Double> percentDebt = new HashMap<String, Double>();
    HashMap <String, Double> totalOverpayment = new HashMap<String, Double>();
    HashMap <String, String> goalDebt = new HashMap<String, String>();
    HashMap <String, Integer> balanceTerm = new HashMap<String, Integer>();
    HashMap <String, Long> datePayment = new HashMap<String, Long>();
    HashMap <String, Long> dateDebtStart = new HashMap<String, Long>();

    Double addPayment;
    int numberProfitable = 0;

    boolean showInterface = false;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.helper_for_overpayment_form);
        addPayment = getIntent().getExtras().getDouble("ADD_PAYMENT");
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!showInterface){
            workDB = new WorkDB(getBaseContext());
            initData();
            getOverpaymentDefault();
            getOverpaymentAdd();
            getResultTotalOverpay();
            outputData();
            showInterface = !showInterface;
        }
    }

    private void initData(){
        int i = 0;
        Cursor cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_ID_DEBT + ", " + DebtCalcDB.FIELD_TYPE_DEBT + ", " + DebtCalcDB.FIELD_PERCENT_DEBT + ", " + DebtCalcDB.F_PAY_DEFAULT_DEBT + ", " + DebtCalcDB.FIELD_DATE_LONG_START_DEBT + ", " + DebtCalcDB.F_BALANCE_DEBT_PAY + ", " + DebtCalcDB.F_BALANCE_TERM_PAY + ", " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " + DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + " FROM " + DebtCalcDB.TABLE_CREDITS + " INNER JOIN " + DebtCalcDB.TABLE_PAYMENTS + " ON " + DebtCalcDB.FIELD_ID_DEBT + " = " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " WHERE (" + /*DebtCalcDB.FIELD_PAID_DEBT + " = '0' AND " + */DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0' )");
        while (cursor.moveToNext()){
            idDebt.put(LABEL + i, cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)));
            goalDebt.put(LABEL + i, cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));
            balanceDebt.put(LABEL + i, cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY)));
            balanceTerm.put(LABEL + i, cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_BALANCE_TERM_PAY)));
            payment.put(LABEL + i, cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
            paymentDefault.put(LABEL + i, cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_PAY_DEFAULT_DEBT)));
            datePayment.put(LABEL + i, cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
            dateDebtStart.put(LABEL + i, cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_START_DEBT)));
            percentDebt.put(LABEL + i, cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)));
            i++;
        }
        cursor.close();
        for (int j = 0; j < idDebt.size(); j++){
            cursor = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.F_OVER_PAY + ") " +
                    " AS " + DebtCalcDB.F_OVER_PAY  +
                    " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                    " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + idDebt.get(LABEL + j));
            cursor.moveToNext();
            oldOverpayment.put(LABEL + j, cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_OVER_PAY)));
            cursor.close();
        }

    }

    private void getOverpaymentDefault(){
        for (int i = 0; i < idDebt.size(); i++){
            appData.setDateDebtStart(dateDebtStart.get(LABEL + i));
            appData.setTermBalance(String.valueOf(balanceTerm.get(LABEL + i)));
            exactArithmetic = new ExactArithmetic(percentDebt.get(LABEL + i));
            overPaymentDefault.put(LABEL + i, exactArithmetic.getOverpaymentAllMonth(balanceDebt.get(LABEL + i), payment.get(LABEL + i), balanceTerm.get(LABEL + i), datePayment.get(LABEL + i), 0) + oldOverpayment.get(LABEL + i));
            //overPaymentDefault.put(LABEL + i, arithmetic.getDeltaNew(balanceTerm.get(LABEL + i), balanceDebt.get(LABEL + i), payment.get(LABEL + i)) + oldOverpayment.get(LABEL + i));
        }
    }

    private void getOverpaymentAdd(){
        Double currentPayment;
        Arithmetic arithmetic;
        for (int i = 0; i < goalDebt.size(); i++){
            arithmetic = new Arithmetic(percentDebt.get(LABEL + i));
            exactArithmetic = new ExactArithmetic(percentDebt.get(LABEL + i));
            currentPayment = payment.get(LABEL + i) + addPayment;
            WorkDateDebt workDateDebt = new WorkDateDebt();
            workDateDebt.getCountDayInMonth(datePayment.get(LABEL + i));

            Double over = arithmetic.getOverpaymentOneMonth(balanceDebt.get(LABEL + i));
            if (currentPayment > balanceDebt.get(LABEL + i) + over)
                currentPayment = balanceDebt.get(LABEL + i) + over;
            Double newDebt = balanceDebt.get(LABEL + i) - (currentPayment - over);
            Double deltaAfter;
            Double nextPayment = currentPayment;
            if (currentPayment > paymentDefault.get(LABEL + i))
                if (balanceTerm.get(LABEL + i) == 1)
                    nextPayment = paymentDefault.get(LABEL + i);
                else
                    nextPayment =  arithmetic.getPayment(newDebt, balanceTerm.get(LABEL + i) - 1);
            if (balanceTerm.get(LABEL + i) > 1){
                //deltaAfter = arithmetic.getDeltaNew(balanceTerm.get(LABEL + i) - 1, newDebt, nextPayment);
                appData.setDateDebtStart(dateDebtStart.get(LABEL + i));
                appData.setTermBalance(String.valueOf(balanceTerm.get(LABEL + i)));
                deltaAfter = exactArithmetic.getOverpaymentAllMonth(newDebt, nextPayment, balanceTerm.get(LABEL + i), workDateDebt.createNextDatePayment(datePayment.get(LABEL + i), AppData.DATE_DEBT_START), 1);
            }
            else
                deltaAfter = 0.0;
            Double overPaymentNew = oldOverpayment.get(LABEL + i) + over + deltaAfter;
            overPaymentAdd.put(LABEL + i, overPaymentNew);
        }
    }

    private void getResultTotalOverpay(){
        Double total;

        for (int i = 0; i < idDebt.size(); i++){
            total = overPaymentAdd.get(LABEL + i);
            for (int j = 0; j < idDebt.size(); j++){
                if (i != j)
                    total = total + overPaymentDefault.get(LABEL + j);
            }

            totalOverpayment.put(LABEL + i, total);

            if (total < totalOverpayment.get(LABEL + numberProfitable))
                numberProfitable = i;
            Log.i("Вывод данных: ", goalDebt.get(LABEL + i) + ". Долг: " + balanceDebt.get(LABEL + i) + ". Срок: " + balanceTerm.get(LABEL + i) + ". Переплата: " + overPaymentDefault.get(LABEL + i) + ". Новая переплата: " + overPaymentAdd.get(LABEL + i) + ". Доп. платеж: " + addPayment + ". Итог переплаты: " + total);
        }
    }

    private void outputData(){
        TextView tv = (TextView)findViewById(R.id.textView);
        NumberFormat numberFormat = new DecimalFormat("###,###,###,###");
        View containerParent;
        View containerChild;
        LinearLayout llParent = (LinearLayout)findViewById(R.id.listHelper);
        LinearLayout llChild;
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        TextView tvGoalParent;
        TextView tvOverpayParent;
        TextView tvGoalChild;
        TextView tvOverpayChild;
        for (int i = 0; i < idDebt.size(); i++){
            containerParent = layoutInflater.inflate(R.layout.parent_item_helper, llParent, false);
            tvGoalParent = (TextView) containerParent.findViewById(R.id.tvPGoal);
            tvOverpayParent = (TextView) containerParent.findViewById(R.id.tvPOverpay);
            tvGoalParent.setText(goalDebt.get(LABEL + i));
            tvOverpayParent.setText(numberFormat.format(totalOverpayment.get(LABEL + i)));
            llChild = (LinearLayout)containerParent.findViewById(R.id.childListHelper);

            for (int j = 0; j < idDebt.size(); j++){
                containerChild = layoutInflater.inflate(R.layout.child_item_helper, llChild, false);
                tvGoalChild = (TextView)containerChild.findViewById(R.id.tvCGoal);
                tvOverpayChild = (TextView)containerChild.findViewById(R.id.tvCOverpay);
                tvGoalChild.setText(goalDebt.get(LABEL + j));
                if (j == i)
                    tvOverpayChild.setText(numberFormat.format(overPaymentAdd.get(LABEL + j)));
                else
                    tvOverpayChild.setText(numberFormat.format(overPaymentDefault.get(LABEL + j)));
                llChild.addView(containerChild);
            }

            if (i == numberProfitable)
                containerParent.findViewById(R.id.parentItemHelper).setBackground(getResources().getDrawable(R.drawable.helper_good_container_press));
            else
                containerParent.findViewById(R.id.parentItemHelper).setBackground(getResources().getDrawable(R.drawable.helper_bad_container_press));
            final LinearLayout finalLlChild = llChild;
            containerParent.findViewById(R.id.parentItemHelper).setOnClickListener(new View.OnClickListener() {
                boolean showChildItem = false;
                @Override
                public void onClick(View view) {
                    LinearLayout.LayoutParams param;
                    if (!showChildItem){
                        param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    else{
                        param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    }
                    param.setMargins(100, 0, 0, 0);

                    showChildItem = !showChildItem;
                    finalLlChild.setLayoutParams(param);
                }
            });
            llParent.addView(containerParent);
            tv.setText(numberFormat.format(addPayment));
        }
    }



    @Override
    public void onStop(){
        super.onStop();
        overPaymentDefault.clear();
        overPaymentAdd.clear();
        goalDebt.clear();
        balanceDebt.clear();
        balanceTerm.clear();
        payment.clear();
        paymentDefault.clear();
        datePayment.clear();
        oldOverpayment.clear();
    }
}

