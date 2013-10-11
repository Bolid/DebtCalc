package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class ListDebs extends Activity {
    Cursor cursor = null;
    Cursor cursorForPayment = null;
    View tableRow = null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debt_list);
    }


    @Override
    public void onResume(){
        super.onResume();

        final WorkDB workDB = new WorkDB(getBaseContext());

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.llContainer);
        linearLayout.removeAllViews();

        TextView tvTotalPayment = (TextView)findViewById(R.id.valueTotalPayment);
        cursorForPayment = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS +
                " FROM " + DebtCalcDB.TABLE_NAME_PAYMENTS);
        BigDecimal valueTotalPayment = BigDecimal.valueOf(0.0);
        while (cursorForPayment.moveToNext()){
            valueTotalPayment = valueTotalPayment.add(BigDecimal.valueOf(cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS))));
        }
        tvTotalPayment.setText(String.valueOf(new DecimalFormat("###,###,###,###.##").format(valueTotalPayment)));
        cursorForPayment.close();

        tableRow = null;
        cursor = workDB.readValueFromDataBase("SELECT " +
                DebtCalcDB.FIELD_ID + ", " +
                DebtCalcDB.FIELD_ID_DEBT + ", " +
                DebtCalcDB.FIELD_SUM_DEBT  + ", " +
                DebtCalcDB.FIELD_PERCENT_DEBT +", " +
                DebtCalcDB.FIELD_TERM_DEBT +", " +
                DebtCalcDB.FIELD_DATE_STR_START_DEBT +", " +
                DebtCalcDB.FIELD_TYPE_DEBT +", " +
                DebtCalcDB.FIELD_BALANCE_DEBT +
                " FROM " + DebtCalcDB.TABLE_NAME_CREDITS);
        while (cursor.moveToNext()){
            int numCredit = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT));
            cursorForPayment = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS +
                                                            " FROM " + DebtCalcDB.TABLE_NAME_PAYMENTS +
                                                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + numCredit + "'");
            cursorForPayment.moveToNext();
            Log.v("Сохраненные кредиты: ", cursorForPayment.getPosition() + ") сумма: " +
                    cursorForPayment.getString(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
            addPlate(layoutInflater,
                    linearLayout,
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_BALANCE_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)),
                    cursorForPayment.getString(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
            Log.v("Сохраненные кредиты: ", cursor.getPosition() + ") сумма: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_BALANCE_DEBT)) + " процент: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)) + " срок: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)) + " дата: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)) + " тип: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));
            cursorForPayment.close();
        }
        workDB.disconnectDataBase();
    }
    public void addPlate(LayoutInflater layoutInflater,
                         LinearLayout linearLayout,
                         final String balanceCredit,
                         final String percentCredit,
                         final String termCredit,
                         final String typeCredit,
                         final String dateCredit,
                         final String paymentCredit){
        tableRow = layoutInflater.inflate(R.layout.debt_conteiner, linearLayout, false);
        TextView tvTypeCredit = (TextView)tableRow.findViewById(R.id.creditType);
        TextView tvDebtCredit = (TextView)tableRow.findViewById(R.id.containerBalance);
        TextView tvDateCredit = (TextView)tableRow.findViewById(R.id.containerDate);
        Button butPayment = (Button)tableRow.findViewById(R.id.containerBut);
        tvTypeCredit.setText(typeCredit);
        tvDebtCredit.setText(new DecimalFormat("###,###,###,###.##").format(Double.valueOf(balanceCredit)));
        tvDateCredit.setText(dateCredit);
        butPayment.setText(new DecimalFormat("###,###,###,###.##").format(Double.valueOf(paymentCredit)));
        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData appData = new AppData();
                appData.addSumCredit(balanceCredit);
                appData.addPercentCredit(percentCredit);
                appData.addTermCredit(termCredit);
                appData.addTypeCredit("\n" + typeCredit);
                startActivity(new Intent(getBaseContext(), MainForm.class));
            }
        });
        linearLayout.addView(tableRow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater actionMenu = getMenuInflater();
        actionMenu.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_menu_addCredit_item:
                AppData appData = new AppData();
                appData.addSumCredit("1000000");
                appData.addPercentCredit("12");
                appData.addTermCredit("120");
                appData.addTypeCredit("\nНовый");
                startActivity(new Intent(getBaseContext(), MainForm.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
