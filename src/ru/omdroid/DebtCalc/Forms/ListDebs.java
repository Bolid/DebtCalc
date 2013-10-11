package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    View viewContainer = null;
    Menu menu = null;
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
            BigDecimal value = BigDecimal.valueOf(cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
            valueTotalPayment = valueTotalPayment.add(value);
        }
        tvTotalPayment.setText(String.valueOf(new DecimalFormat("###,###,###,###.##").format(valueTotalPayment)));
        cursorForPayment.close();

        viewContainer = null;
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
            addPlate(layoutInflater,
                    linearLayout,
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_BALANCE_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)),
                    cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
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
                         final Double paymentCredit){
        viewContainer = layoutInflater.inflate(R.layout.debt_conteiner, linearLayout, false);
        TextView tvTypeCredit = (TextView) viewContainer.findViewById(R.id.creditType);
        TextView tvDebtCredit = (TextView) viewContainer.findViewById(R.id.containerBalance);
        TextView tvDateCredit = (TextView) viewContainer.findViewById(R.id.containerDate);
        Button butPayment = (Button) viewContainer.findViewById(R.id.containerBut);
        tvTypeCredit.setText(typeCredit);
        tvDebtCredit.setText(new DecimalFormat("###,###,###,###.##").format(Double.valueOf(balanceCredit)));
        tvDateCredit.setText(dateCredit);
        butPayment.setText(new DecimalFormat("###,###,###,###.##").format(paymentCredit));

        viewContainer.setOnClickListener(new View.OnClickListener() {
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

        viewContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                viewContainer.setBackgroundColor(getResources().getColor(R.color.containerDeleteCredit));
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ico_action_bar_delete_credit));
                return false;
            }
        });
        linearLayout.addView(viewContainer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater actionMenu = getMenuInflater();
        actionMenu.inflate(R.menu.action_menu, menu);
        this.menu = menu;
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
