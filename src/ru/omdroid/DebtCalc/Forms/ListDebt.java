package ru.omdroid.DebtCalc.Forms;import android.app.Activity;import android.content.Intent;import android.database.Cursor;import android.os.Bundle;import android.util.Log;import android.view.*;import android.widget.*;import ru.omdroid.DebtCalc.*;import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;import ru.omdroid.DebtCalc.DB.DebtCalcDB;import ru.omdroid.DebtCalc.DB.WorkDB;import ru.omdroid.DebtCalc.Dialog.*;import ru.omdroid.DebtCalc.Widget.WidgetProvider;import java.math.BigDecimal;import java.text.DecimalFormat;import java.util.ArrayList;import java.util.Calendar;public class ListDebt extends Activity{    private WorkDB workDB = null;    Cursor cursor, cursorForPayment = null;    View viewContainer = null;    LinearLayout linearLayout = null;    TextView tvTotalPayment = null;    Menu menu = null;    boolean longClick;    boolean visiblePopupArchive = true;    ArrayList<String> selectForDelete = null;    int selectRecord;    AppData appData = new AppData();    @Override    public void onCreate(Bundle savedInstanceState){        super.onCreate(savedInstanceState);        setContentView(R.layout.debt_list);        tvTotalPayment = (TextView)findViewById(R.id.valueTotalPayment);        workDB = new WorkDB(getBaseContext());        if (workDB.countDataInDataBase("SELECT " + DebtCalcDB.FIELD_ID_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS + " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '0'") == 0){            startActivity(new Intent(getBaseContext(), MainNew.class));        }        ratingShow();    }    @Override    public void onResume(){        super.onResume();        selectForDelete = new ArrayList<String>();        selectRecord = 0;        longClick = false;        linearLayout = (LinearLayout)findViewById(R.id.llContainer);        if (visiblePopupArchive)            createListDebt();        else            createListDebtArchive();    }    public void addPlate(LayoutInflater layoutInflater,                         final LinearLayout linearLayout,                         final Double balanceCredit,                         final int balanceTermCredit,                         final String percentCredit,                         final String sumCredit,                         final String termCredit,                         final String typeCredit,                         final Long dateCredit,                         final Long datePaymentLong,                         final String idDebt,                         final Double paymentCredit,                         final Double paymentCreditDefault,                         final int upPayment,                         final int countPaymentPaid){        final Calendar calendar = Calendar.getInstance();        final Arithmetic arithmetic = new Arithmetic(Double.valueOf(percentCredit));        final WorkDateDebt workDateDebt = new WorkDateDebt();        viewContainer = layoutInflater.inflate(R.layout.debt_conteiner, linearLayout, false);        TextView tvTypeCredit = (TextView) viewContainer.findViewById(R.id.creditType);        TextView tvDebtCredit = (TextView) viewContainer.findViewById(R.id.containerBalance);        TextView tvDateCredit = (TextView) viewContainer.findViewById(R.id.containerDate);        TextView tvBalanceTermCredit = (TextView) viewContainer.findViewById(R.id.containerBalanceTerm);        final ImageButton ibpmContainer = (ImageButton) viewContainer.findViewById(R.id.ivpmContainer);        ibpmContainer.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                View v = (ibpmContainer);                showPopupMenuForTable(v, idDebt, false, paymentCredit, paymentCreditDefault, datePaymentLong, balanceCredit, balanceTermCredit);            }        });        Button butPayment = (Button) viewContainer.findViewById(R.id.containerBut);        int dateMonth = calendar.get(Calendar.MONTH);        int dateYear = calendar.get(Calendar.YEAR);        calendar.setTimeInMillis(datePaymentLong);        butPayment.setEnabled((dateMonth >= calendar.get(Calendar.MONTH) & dateYear >= calendar.get(Calendar.YEAR)) || dateYear > calendar.get(Calendar.YEAR));        final RelativeLayout relativeLayout = (RelativeLayout) viewContainer.findViewById(R.id.llDebtContainer);        tvTypeCredit.setText(typeCredit);        tvDebtCredit.setText(new DecimalFormat("###,###,###,###").format(balanceCredit));        //tvDateCredit.setText(dateCredit);        tvDateCredit.setText(workDateDebt.getDate(datePaymentLong));        tvBalanceTermCredit.setText(String.valueOf(balanceTermCredit));        butPayment.setText(new DecimalFormat("###,###,###,###").format(paymentCredit));        butPayment.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                Intent updateWidgetIntent = new Intent(getApplicationContext(), WidgetProvider.class);                updateWidgetIntent.setAction("UPDATE_WIDGET");                sendBroadcast(updateWidgetIntent);/*                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, updateWidgetIntent, 0);                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget);                remoteViews.setOnClickPendingIntent(R.id.containerBut, pendingIntent);*/                //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE_PAY));                workDateDebt.getCountDayInMonth(datePaymentLong);                Double overDebt = sumOverPaymentInPercent(balanceCredit, Integer.valueOf(idDebt), arithmetic);                Double newBalanceCredit = arithmetic.getBalance(paymentCredit, balanceCredit);                int newBalanceTermCredit = 0;                newBalanceTermCredit = balanceTermCredit - 1;                Cursor sumPaymentCursor = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.FIELD_SUM_PAYMENTS + ") " +                                                                        "AS " + DebtCalcDB.FIELD_SUM_PAYMENTS +                                                                        " FROM " + DebtCalcDB.TABLE_PAYMENTS +                                                                        " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + idDebt);                sumPaymentCursor.moveToNext();                Double feePayment = sumPaymentCursor.getDouble(sumPaymentCursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS));                sumPaymentCursor.close();                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +                                  " SET " +                                        /*DebtCalcDB.FIELD_DEBT_PAYMENTS +" = '"+arithmetic.getPaymentInDebt(paymentCredit, balanceCredit) +"', " +*/                                        /*DebtCalcDB.FIELD_PERCENT_PAYMENTS +" = '"+arithmetic.getOverpaymentOneMonth(balanceCredit, AppData.COUNT_DAY_OF_MONTH) +"', " +*/                                        //DebtCalcDB.FIELD_SUM_PAYMENTS  +" = '" + feePayment +"', " +                                        DebtCalcDB.F_OVER_PAY  +" = '" + overDebt +"', " +                                        DebtCalcDB.FIELD_PAID_PAYMENTS + " = '1'" +                                  " WHERE (" +                                        DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0'" +                                  " AND " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + idDebt + "')");                if (Rounding(newBalanceCredit) <= 0){                    workDB.updateData("UPDATE " + DebtCalcDB.TABLE_CREDITS +                            " SET " + DebtCalcDB.FIELD_PAID_DEBT + " = '1'" +                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + idDebt +"'");                    Toast.makeText(getBaseContext(), "Платеж совершен. Кредит выплачен!", Toast.LENGTH_LONG).show();                    WorkNotification wNotification = new WorkNotification(getBaseContext());                    wNotification.delNotify(idDebt);                }                else{                    Double nextPayment = paymentCredit;                    Double defaultPayment = paymentCreditDefault;                    if (upPayment == 1)                        defaultPayment = nextPayment = arithmetic.getPayment(newBalanceCredit, newBalanceTermCredit);                    calendar.setTimeInMillis(workDateDebt.createNextDatePayment(datePaymentLong, dateCredit));                    workDateDebt.getCountDayInMonth(calendar.getTimeInMillis());                    if (newBalanceTermCredit == 1)                        nextPayment = newBalanceCredit + arithmetic.getOverpaymentOneMonth(newBalanceCredit);                    feePayment = feePayment + nextPayment;                    workDB.updateData("UPDATE " + DebtCalcDB.TABLE_CREDITS +                            " SET " + DebtCalcDB.F_PAY_DEFAULT_DEBT + " = '" + defaultPayment +"'" +                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + idDebt +"'");                    workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_PAYMENTS + " (" +                            DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + ", " +                            DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +                            DebtCalcDB.F_BALANCE_DEBT_PAY + ", " +                            DebtCalcDB.F_BALANCE_TERM_PAY + ", " +                            DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + ", " +                            DebtCalcDB.FIELD_DEBT_PAYMENTS + ", " +                            DebtCalcDB.FIELD_PERCENT_PAYMENTS + ", " +                            DebtCalcDB.FIELD_SUM_PAYMENTS + ", " +                            DebtCalcDB.FIELD_PAID_PAYMENTS + /*", " +                            DebtCalcDB.MOUTH_WIDGET_PAY + */")" +                        " VALUES ('" +                            idDebt + "', '" +                            nextPayment + "', '" +                            newBalanceCredit + "', '" +                            newBalanceTermCredit + "', '" +                            calendar.getTimeInMillis() + "', '" +                            arithmetic.getPaymentInDebt(nextPayment, newBalanceCredit) + "', '" +                            arithmetic.getOverpaymentOneMonth(newBalanceCredit) + "', '" +                            feePayment + "', '0'" +/*", " +                            calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR) +*/")");                    Toast.makeText(getBaseContext(), "Платеж совершен!", Toast.LENGTH_LONG).show();                }                createListDebt();            }        });        viewContainer.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                workDateDebt.getCountDayInMonth(datePaymentLong);                if (!longClick){                    AppData appData = new AppData();                    appData.setPayment(String.valueOf(paymentCredit), String.valueOf(paymentCreditDefault));                    appData.setUpPayment(upPayment);                    appData.setDate(datePaymentLong);                    appData.setDateDebtStart(dateCredit);                    appData.setDebtBalance(String.valueOf(balanceCredit));                    appData.setIdDebt(idDebt);                    appData.setPercent(percentCredit);                    appData.setTermBalance(String.valueOf(balanceTermCredit));                    appData.addTypeCredit("\n" + typeCredit);                    appData.setOverPayment(arithmetic.getOverpaymentOneMonth(balanceCredit));                    appData.setTerm(Integer.parseInt(termCredit));                    appData.setSumDebt(sumCredit);                    appData.setCountPayPaid(countPaymentPaid);                    //workDB.disconnectDataBase();                    startActivity(new Intent(getBaseContext(), InfoDebt.class));                }                longClick = false;            }        });        viewContainer.setOnLongClickListener(new View.OnLongClickListener() {            @Override            public boolean onLongClick(View view) {                longClick = true;                for (int i = 0; i < selectForDelete.size(); i++)                    if (selectForDelete.get(i).equals(idDebt)){                        selectForDelete.remove(i);                        relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.container_press));                        selectRecord--;                        menu.getItem(0).setVisible(selectForDelete.size() != 0);                        return false;                    }                selectForDelete.add(selectRecord, idDebt);                selectRecord++;                menu.getItem(0).setVisible(selectForDelete.size() != 0);                relativeLayout.setBackgroundColor(getBaseContext().getResources().getColor(R.color.containerDeleteCredit));                return false;            }        });        linearLayout.addView(viewContainer);    }    @Override    public boolean onCreateOptionsMenu(Menu menu){        MenuInflater actionMenu = getMenuInflater();        actionMenu.inflate(R.menu.dl_action_menu, menu);        menu.getItem(0).setVisible(false);        this.menu = menu;        return super.onCreateOptionsMenu(menu);    }    @Override    public boolean onOptionsItemSelected(MenuItem item){        switch (item.getItemId()){            case R.id.action_menu_addCredit_item:                startActivity(new Intent(getBaseContext(), MainNew.class));                Log.v("Всего в меню:", String.valueOf(item.getItemId()));                return true;            case R.id.action_menu_deleteCredit_item:                /*DialogDeleteDebt dialogDeleteDebt = new DialogDeleteDebt(selectForDelete);                dialogDeleteDebt.show(getFragmentManager(), "");*/                for (int i=0; i < selectForDelete.size(); i++){                    workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_CREDITS +                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + selectForDelete.get(i) + "'");                    workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_PAYMENTS +                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + selectForDelete.get(i) + "'");                }                Toast.makeText(getBaseContext(), "Записи удалены!", Toast.LENGTH_LONG).show();                createListDebt();                menu.getItem(0).setVisible(false);                return true;            case R.id.popup:                View v = findViewById(R.id.popup);                showPopupMenu(v);                return true;            default:                return super.onOptionsItemSelected(item);        }    }    public void createListDebt(){        linearLayout.removeAllViews();        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);        cursorForPayment = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " + DebtCalcDB.FIELD_DATE_LONG_PAYMENTS +                " FROM " + DebtCalcDB.TABLE_PAYMENTS +                " WHERE " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0'");        BigDecimal valueTotalPayment = BigDecimal.valueOf(0.0);        Calendar mCurrent = Calendar.getInstance();        Calendar mPayment = Calendar.getInstance();        while (cursorForPayment.moveToNext()){            mPayment.setTimeInMillis(cursorForPayment.getLong(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));            if (mCurrent.get(Calendar.MONTH) >= mPayment.get(Calendar.MONTH) || mCurrent.get(Calendar.YEAR) > mPayment.get(Calendar.YEAR)){                BigDecimal value = BigDecimal.valueOf(cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));                valueTotalPayment = valueTotalPayment.add(value);            }        }        tvTotalPayment.setText(String.valueOf(new DecimalFormat("###,###,###,###.##").format(valueTotalPayment)));        cursorForPayment.close();        viewContainer = null;        cursor = workDB.readValueFromDataBase("SELECT " +                DebtCalcDB.FIELD_ID + ", " +                DebtCalcDB.FIELD_ID_DEBT + ", " +                DebtCalcDB.FIELD_SUM_DEBT  + ", " +                DebtCalcDB.FIELD_PERCENT_DEBT +", " +                DebtCalcDB.FIELD_TERM_DEBT +", " +                DebtCalcDB.FIELD_DATE_LONG_START_DEBT +", " +                DebtCalcDB.FIELD_TYPE_DEBT +", " +                DebtCalcDB.F_PAY_DEFAULT_DEBT +                " FROM " + DebtCalcDB.TABLE_CREDITS +                " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '0'");        int countPaymentPaid = 0;        while (cursor.moveToNext()){            int numCredit = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT));            cursorForPayment = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +                    DebtCalcDB.F_BALANCE_DEBT_PAY +", " +                    DebtCalcDB.FIELD_DATE_LONG_PAYMENTS +", " +                    DebtCalcDB.F_BALANCE_TERM_PAY + ", " +                    DebtCalcDB.F_COUNT_DAY_PAY + ", " +                    DebtCalcDB.F_PAYMENT_UP_PAY +                    " FROM " + DebtCalcDB.TABLE_PAYMENTS +                    " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + numCredit + "'" +                    " AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");            cursorForPayment.moveToNext();            countPaymentPaid = workDB.countDataInDataBase("SELECT " + DebtCalcDB.FIELD_PAYMENT_PAYMENTS + " FROM " + DebtCalcDB.TABLE_PAYMENTS + " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + numCredit + "'" +                    " AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '1')");            appData.setCountDayOfMonth(cursorForPayment.getInt(cursorForPayment.getColumnIndex(DebtCalcDB.F_COUNT_DAY_PAY)), typeYear(cursorForPayment.getLong(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS))));            addPlate(layoutInflater,                    linearLayout,                    cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.F_BALANCE_DEBT_PAY)),                    cursorForPayment.getInt(cursorForPayment.getColumnIndex(DebtCalcDB.F_BALANCE_TERM_PAY)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)),                    cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_START_DEBT)),                    cursorForPayment.getLong(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)),                    cursorForPayment.getDouble(cursorForPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)),                    cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.F_PAY_DEFAULT_DEBT)),                    cursorForPayment.getInt(cursorForPayment.getColumnIndex(DebtCalcDB.F_PAYMENT_UP_PAY)),                    countPaymentPaid);            cursorForPayment.close();        }        cursor.close();    }    private void createListDebtArchive(){        linearLayout.removeAllViews();        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);        viewContainer = null;        cursor = workDB.readValueFromDataBase("SELECT " +                DebtCalcDB.FIELD_ID + ", " +                DebtCalcDB.FIELD_ID_DEBT + ", " +                DebtCalcDB.FIELD_SUM_DEBT  + ", " +                DebtCalcDB.FIELD_PERCENT_DEBT +", " +                DebtCalcDB.FIELD_TERM_DEBT +", " +                DebtCalcDB.FIELD_DATE_STR_START_DEBT +", " +                DebtCalcDB.FIELD_TYPE_DEBT +                " FROM " + DebtCalcDB.TABLE_CREDITS +                " WHERE " + DebtCalcDB.FIELD_PAID_DEBT + " = '1'");        while (cursor.moveToNext()){            Cursor sumPaymentCursor = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.FIELD_SUM_PAYMENTS + ") " +                    "AS " + DebtCalcDB.FIELD_SUM_PAYMENTS + ", " + DebtCalcDB.FIELD_DATE_LONG_PAYMENTS +                    " FROM " + DebtCalcDB.TABLE_PAYMENTS +                    " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)));            sumPaymentCursor.moveToNext();            addPlateArchive(layoutInflater,                    linearLayout,                    cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)),                    sumPaymentCursor.getDouble(sumPaymentCursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)),                    sumPaymentCursor.getLong(sumPaymentCursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)),                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)));            sumPaymentCursor.close();        }        cursor.close();    }    private void addPlateArchive(LayoutInflater inflater,                                 LinearLayout layout,                                 Double sumDebt,                                 final String percentCredit,                                 String termDebt,                                 String typeDebt,                                 Double paidDebt,                                 String dateStartDebt,                                 Long dateStopDebt,                                 final String idDebt){        Calendar calendar = Calendar.getInstance();        calendar.setTimeInMillis(dateStopDebt);        String dateStop = String.valueOf(calendar.get(Calendar.DATE))+"."+String.valueOf(calendar.get(Calendar.MONTH) + 1)+"."+String.valueOf(calendar.get(Calendar.YEAR));        viewContainer = inflater.inflate(R.layout.debt_container_archive, linearLayout, false);        TextView tvTypeCredit = (TextView) viewContainer.findViewById(R.id.creditType);        TextView tvDateCredit = (TextView) viewContainer.findViewById(R.id.containerDate);        TextView tvPaidCredit = (TextView) viewContainer.findViewById(R.id.containerPaid);        TextView tvSumCredit = (TextView) viewContainer.findViewById(R.id.containerSum);        TextView tvPercentCredit = (TextView) viewContainer.findViewById(R.id.containerPercent);        TextView tvTermCredit = (TextView) viewContainer.findViewById(R.id.containerTerm);        final ImageButton ibpmContainer = (ImageButton) viewContainer.findViewById(R.id.ivpmContainer);        ibpmContainer.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                View v = (ibpmContainer);                showPopupMenuForTable(v, idDebt, true, 0.0, 0.0, 0, 0.0, 0);            }        });        tvTypeCredit.setText(typeDebt);        tvDateCredit.setText(dateStartDebt + " - " + dateStop);        tvPaidCredit.setText(new DecimalFormat("###,###,###,###").format(paidDebt));        tvSumCredit.setText(new DecimalFormat("###,###,###,###").format(sumDebt));        tvPercentCredit.setText(percentCredit + "%");        tvTermCredit.setText(termDebt);        layout.addView(viewContainer);    }    private void showPopupMenu(View v){        final PopupMenu pMenu = new PopupMenu(getBaseContext(), v);        MenuInflater mInflater = pMenu.getMenuInflater();        mInflater.inflate(R.menu.pm_di, pMenu.getMenu());        pMenu.getMenu().getItem(0).setVisible(visiblePopupArchive);        pMenu.getMenu().getItem(1).setVisible(!visiblePopupArchive);        pMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {            @Override            public boolean onMenuItemClick(MenuItem menuItem) {                switch (menuItem.getItemId()){                    case R.id.itemArchive:                        createListDebtArchive();                        visiblePopupArchive = !visiblePopupArchive;                        return true;                    case R.id.itemCurrent:                        createListDebt();                        visiblePopupArchive = !visiblePopupArchive;                        return true;                }                return false;            }        });        pMenu.show();    }    private void showPopupMenuForTable(View v, final String idDebt, final Boolean archiveShow, final Double payment, final Double paymentDefault, final long datePayment, final Double balanceDebt, final int balanceTerm){        final PopupMenu pMenu = new PopupMenu(getBaseContext(), v);        MenuInflater mInflater = pMenu.getMenuInflater();        mInflater.inflate(R.menu.pm_debt_card, pMenu.getMenu());        int i = workDB.countDataInDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_ID_DEBT_NOTIFY + " = '" + idDebt + "'");        pMenu.getMenu().getItem(1).setVisible(i == 0 & !archiveShow);        pMenu.getMenu().getItem(2).setVisible(i > 0 & !archiveShow);        pMenu.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {            @Override            public boolean onMenuItemClick(MenuItem menuItem) {                appData.setIdDebt(idDebt);                Intent intent = new Intent(getBaseContext(), TablePaymentSavedDebt.class);                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                startActivity(intent);                return false;            }        });        pMenu.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {            @Override            public boolean onMenuItemClick(MenuItem menuItem) {                DialogAddNotify dialogNotify = new DialogAddNotify(getBaseContext(), idDebt);                dialogNotify.show(getFragmentManager(), "");                return false;            }        });        pMenu.getMenu().getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {            @Override            public boolean onMenuItemClick(MenuItem menuItem) {                appData.setIdDebt(idDebt);                DialogDelNotify dialogNotify = new DialogDelNotify(getBaseContext());                dialogNotify.show(getFragmentManager(), "");                return false;            }        });        pMenu.getMenu().getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {            @Override            public boolean onMenuItemClick(MenuItem menuItem) {                appData.setIdDebt(idDebt);                appData.setPayment(String.valueOf(payment), String.valueOf(paymentDefault));                appData.setDate(datePayment);                appData.setDebtBalance(String.valueOf(balanceDebt));                appData.setTermBalance(String.valueOf(balanceTerm));                DialogChangePercent dialogChangePercent = new DialogChangePercent(getBaseContext(), new RefreshGUIThis());                dialogChangePercent.show(getFragmentManager(), "");                return false;            }        });        pMenu.getMenu().getItem(4).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {            @Override            public boolean onMenuItemClick(MenuItem menuItem) {                DialogChangeGoal dialogChangeGoal = new DialogChangeGoal(getBaseContext(), new RefreshGUIThis());                dialogChangeGoal.show(getFragmentManager(), "");                return false;            }        });        pMenu.show();    }    private int typeYear(Long aLong){        Calendar calendar = Calendar.getInstance();        calendar.setTimeInMillis(aLong);        if ((calendar.get(Calendar.YEAR) % 4 == 0 & calendar.get(Calendar.YEAR) % 100 != 0) || (calendar.get(Calendar.YEAR) % 400 == 0))            return 366;        else            return 365;    }    private Double sumOverPaymentInPercent(Double balanceCredit, int idDebt, Arithmetic arithmetic){        Cursor cursorOver = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.F_OVER_PAY + ") " +            "AS " + DebtCalcDB.F_OVER_PAY +            " FROM " + DebtCalcDB.TABLE_PAYMENTS +            " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + idDebt);        cursorOver.moveToNext();        Double overDebt = cursorOver.getDouble(cursorOver.getColumnIndex(DebtCalcDB.F_OVER_PAY));        overDebt = overDebt + arithmetic.getOverpaymentOneMonth(balanceCredit);        cursorOver.close();        return overDebt;    }    public Double Rounding(Double value){        BigDecimal roundValue = BigDecimal.valueOf(value);        roundValue = roundValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);        return Double.valueOf(roundValue.toString());    }    private void ratingShow(){        DialogRating dialogRating = null;        Cursor ratingShow = workDB.readValueFromDataBase("SELECT * FROM " + DebtCalcDB.TABLE_SETTING);        ratingShow.moveToNext();        int ratingShowStart = ratingShow.getInt(ratingShow.getColumnIndex(DebtCalcDB.F_RATING_SHOW_SET));        if (ratingShowStart == 0){            int ratingStartNum = ratingShow.getInt(ratingShow.getColumnIndex(DebtCalcDB.F_NUMBER_START_APP_SET));            if (ratingStartNum == 2){                dialogRating = new DialogRating();                dialogRating.show(getFragmentManager(), "");                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_SETTING + " SET " + DebtCalcDB.F_NUMBER_START_APP_SET + " = '0'");            }            else{                ratingStartNum++;                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_SETTING + " SET " + DebtCalcDB.F_NUMBER_START_APP_SET + " = '" + ratingStartNum + "'");            }        }        ratingShow.close();    }    public class RefreshGUIThis{        public void refreshGUI(){            createListDebt();        }        public void applyNewPercent(String percent){            appData.setPercent(String.valueOf(percent));        }        public void applyNewGoal(String percent){            appData.setPercent(String.valueOf(percent));        }    }}