package ru.omdroid.DebtCalc.Forms;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;
import android.app.Activity;
import android.os.Bundle;

import java.text.DecimalFormat;

public class ListPayment extends Activity {
    View view = null;
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.payment_info);
        LayoutInflater inflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)findViewById(R.id.llPaymentInfo);
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
            numPayment++;
            addRecord(inflater,
                    layout,
                    numPayment,
                    cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)),
                    cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_DEBT_PAYMENTS)),
                    cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PERCENT_PAYMENTS)),
                    cursorInPayment.getDouble(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)));
            Log.v("Платежи", cursorInPayment.getString(cursorInPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
        }
    }

    public void addRecord(LayoutInflater inflater,
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

        layout.addView(view);
    }
}
