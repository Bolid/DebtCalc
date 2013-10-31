package ru.omdroid.DebtCalc.Forms;import android.app.Activity;import android.content.Intent;import android.database.Cursor;import android.os.Bundle;import android.util.Log;import android.view.*;import android.widget.*;import ru.omdroid.DebtCalc.AppData;import ru.omdroid.DebtCalc.Arithmetic;import ru.omdroid.DebtCalc.DB.DebtCalcDB;import ru.omdroid.DebtCalc.DB.WorkDB;import ru.omdroid.DebtCalc.R;import java.math.BigDecimal;import java.text.DecimalFormat;import java.util.ArrayList;import java.util.Calendar;public class ListDebt extends Activity{    private WorkDB workDB = null;    Cursor cursor, cursorForPayment = null;    View viewContainer = null;    LinearLayout linearLayout = null;    TextView tvTotalPayment = null;    Menu menu = null;    boolean longClick;    boolean visiblePopupArchive = true;    ArrayList<String> selectForDelete = null;    int selectRecord;    @Override    public void onCreate(Bundle savedInstanceState){        super.onCreate(savedInstanceState);        setContentView(R.layout.debt_list);        tvTotalPayment = (TextView)findViewById(R.id.valueTotalPayment);    }    @Override    public void onResume(){        super.onResume();        selectForDelete = new ArrayList<String>();        selectRecord = 0;        workDB = new WorkDB(getBaseContext());        longClick = false;        linearLayout = (LinearLayout)findViewById(R.id.llContainer);        if (visiblePopupArchive)            createListDebt();        else            createListDebtArchive();    }    public void addPlate(LayoutInflater layoutInflater,                         final LinearLayout linearLayout,                         final Double balanceCredit,                         final int balanceTermCredit,                         final String percentCredit,                         final String termCredit,                         final String typeCredit,                         final String dateCredit,                         final Long dateCreditLong,                         final String idDebt,                         final Double paymentCredit){        final Calendar calendar = Calendar.getInstance();        final Arithmetic arithmetic = new Arithmetic(Double.valueOf(percentCredit));        viewContainer = layoutInflater.inflate(R.layout.debt_conteiner, linearLayout, false);        TextView tvTypeCredit = (TextView) viewContainer.findViewById(R.id.creditType);        TextView tvDebtCredit = (TextView) viewContainer.findViewById(R.id.containerBalance);        TextView tvDateCredit = (TextView) viewContainer.findViewById(R.id.containerDate);        TextView tvBalanceTermCredit = (TextView) viewContainer.findViewById(R.id.containerBalanceTerm);        Button butPayment = (Button) viewContainer.findViewById(R.id.containerBut);        int dateMonth = calendar.get(Calendar.MONTH);        int dateYear = calendar.get(Calendar.YEAR);        calendar.setTimeInMillis(dateCreditLong);        butPayment.setEnabled(dateMonth - calendar.get(Calendar.MONTH) >= 0 || dateYear > calendar.get(Calendar.YEAR));        final RelativeLayout relativeLayout = (RelativeLayout) viewContainer.findViewById(R.id.llDebtContainer);        tvTypeCredit.setText(typeCredit);        tvDebtCredit.setText(new DecimalFormat("###,###,###,###").format(balanceCredit));        tvDateCredit.setText(dateCredit);        tvBalanceTermCredit.setText(String.valueOf(balanceTermCredit));        butPayment.setText(new DecimalFormat("###,###,###,###").format(paymentCredit));        butPayment.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));                Double balance = arithmetic.getBalance(paymentCredit, balanceCredit, balanceTermCredit);                int newBalanceTermCredit = 0;                newBalanceTermCredit = balanceTermCredit - 1;                Double feePayment = 0.0;                Cursor sumPaymentCursor = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.FIELD_SUM_PAYMENTS + ") " +                                                                        "AS " + DebtCalcDB.FIELD_SUM_PAYMENTS +                                                                        " FROM " + DebtCalcDB.TABLE_PAYMENTS +                                                                        " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + idDebt);                sumPaymentCursor.moveToNext();                feePayment = arithmetic.getPayment(balance, newBalanceTermCredit) + sumPaymentCursor.getDouble(sumPaymentCursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS));                sumPaymentCursor.close();                Cursor cursorOver = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.F_OVER_PAY + ") " +                                                                 "AS " + DebtCalcDB.F_OVER_PAY +                                                                 " FROM " + DebtCalcDB.TABLE_PAYMENTS +                                                                 " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + idDebt);                cursorOver.moveToNext();                Double overDebt = cursorOver.getDouble(cursorOver.getColumnIndex(DebtCalcDB.F_OVER_PAY));                overDebt = overDebt + arithmetic.getPaymentInPercent(balanceCredit);                cursorOver.close();                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +                                  " SET " +                                        DebtCalcDB.FIELD_DEBT_PAYMENTS +" = '"+arithmetic.getPaymentInDebt(paymentCredit, balanceCredit) +"', " +                                        DebtCalcDB.FIELD_PERCENT_PAYMENTS +" = '"+arithmetic.getPaymentInPercent(balanceCredit) +"', " +                                        DebtCalcDB.FIELD_SUM_PAYMENTS  +" = '" + feePayment +"', " +                                        DebtCalcDB.F_OVER_PAY  +" = '" + overDebt +"', " +                                        DebtCalcDB.FIELD_PAID_PAYMENTS + " = '1'" +                                  " WHERE (" +                                        DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0'" +                                  " AND " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + idDebt + "')");                if (balance <= 0)                    workDB.updateData("UPDATE " + DebtCalcDB.TABLE_CREDITS +                            " SET " + DebtCalcDB.FIELD_PAID_DEBT + " = '1'" +                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + idDebt +"'");                else                    workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_PAYMENTS + " (" +                        DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + ", " +                        DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +                        DebtCalcDB.F_BALANCE_DEBT_PAY + ", " +                        DebtCalcDB.F_BALANCE_TERM_PAY + ", " +                        DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + ", " +                        DebtCalcDB.FIELD_SUM_PAYMENTS + ", " +                        DebtCalcDB.FIELD_PAID_PAYMENTS + ")" +                        " VALUES ('" +                        idDebt + "', '" +                        arithmetic.getPayment(balance, newBalanceTermCredit) + "', '" +                        balance + "', '" +                        newBalanceTermCredit + "', '" +                        calendar.getTimeInMillis() + "', '" +                        feePayment + "', '" +                        "0')");                createListDebt();            }        });        viewContainer.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                if (!longClick){                    AppData appData = new AppData();                    appData.setPayment(String.valueOf(paymentCredit), String.valueOf(arithmetic.getPayment(balanceCredit, balanceTermCredit)));                    appData.setDate(dateCreditLong);                    appData.setDebt(String.valueOf(balanceCredit));                    appData.setIdDebt(idDebt);                    appData.setPercent(percentCredit);                    appData.setTerm(termCredit);                    appData.addTypeCredit("\n" + typeCredit);                    appData.setOverPayment(arithmetic.getPaymentInPercent(balanceCredit));                    //workDB.disconnectDataBase();                    startActivity(new Intent(getBaseContext(), InfoDebt.class));                }                longClick = false;            }        });        viewContainer.setOnLongClickListener(new View.OnLongClickListener() {            @Override            public boolean onLongClick(View view) {                longClick = true;                for (int i = 0; i < selectForDelete.size(); i++)                    if (selectForDelete.get(i).equals(idDebt)){                        selectForDelete.remove(i);                        relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.container_press));                        selectRecord--;                        menu.getItem(0).setVisible(selectForDelete.size() != 0);                        return false;                    }                selectForDelete.add(selectRecord, idDebt);                selectRecord++;                menu.getItem(0).setVisible(selectForDelete.size() != 0);                relativeLayout.setBackgroundColor(getBaseContext().getResources().getColor(R.color.containerDeleteCredit));                return false;            }        });        linearLayout.addView(viewContainer);    }    @Override    public boolean onCreateOptionsMenu(Menu menu){        MenuInflater actionMenu = getMenuInflater();        actionMenu.inflate(R.menu.dl_action_menu, menu);        menu.getItem(0).setVisible(false);        this.menu = menu;        return super.onCreateOptionsMenu(menu);    }    @Override    public boolean onOptionsItemSelected(MenuItem item){        switch (item.getItemId()){            case R.id.action_menu_addCredit_item:                /*AppData appData = new AppData();                appData.addSumCredit("1000000");                appData.setPercent("12");                appData.setTerm("120");                appData.addTypeCredit("Новый");*/                startActivity(new Intent(getBaseContext(), MainNew.class));                Log.v("Всего в меню:", String.valueOf(item.getItemId()));                return true;            case R.id.action_menu_deleteCredit_item:                for (int i=0; i < selectForDelete.size(); i++){                    workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_CREDITS +                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + selectForDelete.get(i) + "'");                    workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_PAYMENTS +                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + selectForDelete.get(i) + "'");                }                Toast.makeText(getBaseContext(), "Записи удалены!", Toast.LENGTH_LONG).show();                createListDebt();                menu.getItem(0).setVisible(false);                return true;            case R.id.popup:                View v = findViewById(R.id.popup);                showPopupMenu(v);                return true;            default:                return super.onOptionsItemSelected(item);        }    }    private void createListDebt(){        linearLayout.removeAllViews();        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);        cursorForPayment = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " + DebtCalcDB.FIELD_DATE_LONG_PAYMENTS +                " FROM " + DebtCalcDB.TABLE_PAYMENTS +                " WHERE " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0'");        BigDecimal valueTotalPayment = BigDecimal.valueOf(0.0);        Calendar mCurrent = Calendar.getInstance();        Calendar mPayment = Calendar.getInstance();        while (cursorForPayment.moveToNext()){            mPayment.setTimeInMillis(cursorForPayment.getLong(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));            if (mCurrent.get(Calendar.MONTH) >= mPayment.get(Calendar.MONTH) || mCurrent.get(Calendar.YEAR) > mPayment.get(Calendar.YEAR)){                BigDecimal value = BigDecimal.valueOf(cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));                valueTotalPayment = valueTotalPayment.add(value);            }        }        tvTotalPayment.setText(String.valueOf(new DecimalFormat("###,###,###,###.##").format(valueTotalPayment)));        cursorForPayment.close();        viewContainer = null;        cursor = workDB.readValueFromDataBase("SELECT " +                DebtCalcDB.FIELD_ID + ", " +                DebtCalcDB.FIELD_ID_DEBT + ", " +                DebtCalcDB.FIELD_SUM_DEBT  + ", " +                DebtCalcDB.FIELD_PERCENT_DEBT +", " +                DebtCalcDB.FIELD_TERM_DEBT +", " +                DebtCalcDB.FIELD_DATE_STR_START_DEBT +", " +                DebtCalcDB.FIELD_TYPE_DEBT +                " FROM " + DebtCalcDB.TABLE_CREDITS +                " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '0'");        while (cursor.moveToNext()){            int numCredit = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT));            cursorForPayment = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +                    DebtCalcDB.F_BALANCE_DEBT_PAY +", " +                    DebtCalcDB.FIELD_DATE_LONG_PAYMENTS +", " +                    DebtCalcDB.F_BALANCE_TERM_PAY +                    " FROM " + DebtCalcDB.TABLE_PAYMENTS +                    " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + numCredit + "'" +                    " AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");            cursorForPayment.moveToNext();            addPlate(layoutInflater,                    linearLayout,                    cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY)),                    cursorForPayment.getInt(cursorForPayment.getColumnIndex(DebtCalcDB.F_BALANCE_TERM_PAY)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)),                    cursorForPayment.getLong(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)),                    cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));            cursorForPayment.close();        }        cursor.close();    }    private void createListDebtArchive(){        linearLayout.removeAllViews();        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);        viewContainer = null;        cursor = workDB.readValueFromDataBase("SELECT " +                DebtCalcDB.FIELD_ID + ", " +                DebtCalcDB.FIELD_ID_DEBT + ", " +                DebtCalcDB.FIELD_SUM_DEBT  + ", " +                DebtCalcDB.FIELD_PERCENT_DEBT +", " +                DebtCalcDB.FIELD_TERM_DEBT +", " +                DebtCalcDB.FIELD_DATE_STR_START_DEBT +", " +                DebtCalcDB.FIELD_TYPE_DEBT +", " +                DebtCalcDB.FIELD_OVER_DEBT +                " FROM " + DebtCalcDB.TABLE_CREDITS +                " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '1'");        while (cursor.moveToNext()){            Cursor sumPaymentCursor = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.FIELD_SUM_PAYMENTS + ") " +                "AS " + DebtCalcDB.FIELD_SUM_PAYMENTS +                " FROM " + DebtCalcDB.TABLE_PAYMENTS +                " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)));            sumPaymentCursor.moveToNext();            addPlateArchive(layoutInflater,                    linearLayout,                    cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)),                    sumPaymentCursor.getDouble(sumPaymentCursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)));            sumPaymentCursor.close();        }        cursor.close();    }    private void addPlateArchive(LayoutInflater inflater,                                 LinearLayout layout,                                 Double sumDebt,                                 String percentDebt,                                 String termDebt,                                 String typeDebt,                                 Double paidDebt,                                 String dateStartDebt,                                 String idDebt){        viewContainer = inflater.inflate(R.layout.debt_container_archive, linearLayout, false);        TextView tvTypeCredit = (TextView) viewContainer.findViewById(R.id.creditType);        TextView tvDateCredit = (TextView) viewContainer.findViewById(R.id.containerDate);        TextView tvPaidCredit = (TextView) viewContainer.findViewById(R.id.containerPaid);        TextView tvSumCredit = (TextView) viewContainer.findViewById(R.id.containerSum);        TextView tvPercentCredit = (TextView) viewContainer.findViewById(R.id.containerPercent);        TextView tvTermCredit = (TextView) viewContainer.findViewById(R.id.containerTerm);        tvTypeCredit.setText(typeDebt);        tvDateCredit.setText(dateStartDebt);        tvPaidCredit.setText(new DecimalFormat("###,###,###,###").format(paidDebt));        tvSumCredit.setText(new DecimalFormat("###,###,###,###").format(sumDebt));        tvPercentCredit.setText(percentDebt + "%");        tvTermCredit.setText(termDebt);        layout.addView(viewContainer);    }    private void showPopupMenu(View v){        final PopupMenu pMenu = new PopupMenu(getBaseContext(), v);        MenuInflater mInflater = pMenu.getMenuInflater();        mInflater.inflate(R.menu.pm_di, pMenu.getMenu());        pMenu.getMenu().getItem(0).setVisible(visiblePopupArchive);        pMenu.getMenu().getItem(1).setVisible(!visiblePopupArchive);        pMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {            @Override            public boolean onMenuItemClick(MenuItem menuItem) {                switch (menuItem.getItemId()){                    case R.id.f1:                        createListDebtArchive();                        visiblePopupArchive = !visiblePopupArchive;                        return true;                    case R.id.f2:                        createListDebt();                        visiblePopupArchive = !visiblePopupArchive;                        return true;                }                return false;            }        });        pMenu.show();    }}