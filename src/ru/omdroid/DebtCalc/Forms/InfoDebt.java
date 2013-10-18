package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddPayment;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.util.Calendar;


public class InfoDebt extends Activity {
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.credit_info);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final TextView tvPayment = (TextView)findViewById(R.id.infoPayment);
        TextView tvDate = (TextView)findViewById(R.id.infoDate);
        TextView tvDebt = (TextView)findViewById(R.id.infoDebt);
        TextView tvDateDay = (TextView)findViewById(R.id.tvInfoDate);
        final EditText etPayment = (EditText)findViewById(R.id.etPayment);
        ImageView ivControlWritePayment = (ImageView)findViewById(R.id.ivControlWritePayment);
        Button butWritePayment = (Button)findViewById(R.id.butWritePayment);
        Button butInfoPayments = (Button)findViewById(R.id.butInfoPayments);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(AppData.DATE);
        InControlFieldAddPayment inControlFieldAddPayment = new InControlFieldAddPayment(etPayment, ivControlWritePayment, butWritePayment, Double.valueOf(AppData.PAYMENT_DEFAULT));

        tvPayment.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.PAYMENT)));
        tvDate.setText(getResources().getStringArray(R.array.month)[calendar.get(Calendar.MONTH)]);
        tvDebt.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(AppData.DEBT)));
        tvDateDay.setText(String.valueOf(calendar.get(Calendar.DATE)));
        etPayment.setText(new DecimalFormat("###,###,###,###.00").format(Double.valueOf(AppData.PAYMENT)));

        etPayment.addTextChangedListener(inControlFieldAddPayment);

        butWritePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        butInfoPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), ListPayment.class));
            }
        });
    }

    private String formatValue(String addPayment){
        String newPay = "";
        for (int i = 0; i < addPayment.length(); i++){
            if ("1234567890".contains(String.valueOf(addPayment.charAt(i))))
                newPay = newPay + String.valueOf(addPayment.charAt(i));
        }
        return newPay;
    }
}
