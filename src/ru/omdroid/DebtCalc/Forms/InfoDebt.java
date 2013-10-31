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

       // final View viewGraph = (View)findViewById(R.id.viewGraph);

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
                tvPayment.setText(etPayment.getText().toString());
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
}
