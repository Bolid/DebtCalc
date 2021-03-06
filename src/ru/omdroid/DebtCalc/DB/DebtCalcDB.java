package ru.omdroid.DebtCalc.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DebtCalcDB extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "debt_calc_database.db";
    /*TABLE DEBT_BALANCE*/
    public static final String TABLE_CREDITS = "debts_table";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_ID_DEBT = "id_debt";
    public static final String FIELD_SUM_DEBT = "sum_debt";
    public static final String FIELD_PERCENT_DEBT = "percent_debt";
    public static final String FIELD_TERM_DEBT = "term_debt";
    public static final String FIELD_TYPE_DEBT = "type_debt";
    public static final String FIELD_OVER_DEBT = "over_debt";
    public static final String FIELD_DATE_STR_START_DEBT = "date_str_start_debt";
    public static final String FIELD_DATE_LONG_START_DEBT = "date_long_start_debt";
    public static final String FIELD_PAID_DEBT = "paid_debt";
            /*new field*/
    public static final String F_PAY_DEFAULT_DEBT = "pay_default_debt"; /*version 2*/
    /*TABLE PAYMENTS*/
    public static final String TABLE_PAYMENTS = "payments_table";
    public static final String FIELD_ID_NUM = "_id";
    public static final String FIELD_ID_DEBT_PAYMENTS = "id_debt_payments";
    public static final String FIELD_PAYMENT_PAYMENTS = "payments_payments";
    public static final String FIELD_DEBT_PAYMENTS = "debt_payments";
    public static final String FIELD_PERCENT_PAYMENTS = "percent_payments";
    public static final String F_BALANCE_DEBT_PAY = "balance_debt";
    public static final String F_BALANCE_TERM_PAY = "balance_term_debt";
    public static final String FIELD_SUM_PAYMENTS = "sum_payments";
    public static final String F_OVER_PAY = "over_payments";
    public static final String FIELD_DATE_LONG_PAYMENTS = "date_long_payments";
    public static final String FIELD_PAID_PAYMENTS = "paid_payments";
    public static final String F_PAYMENT_UP_PAY = "up_payment";
    public static final String F_COUNT_DAY_PAY = "count_day_payment";
    public static final String MOUTH_WIDGET_PAY = "mouth_widget";
    /*TABLE SETTING*/
    public static final String TABLE_SETTING = "setting_table";
    public static final String F_ID_SET = "_id";
    public static final String F_RATING_SHOW_SET = "RATING_SHOW";
    public static final String F_NUMBER_START_APP_SET = "NUM_START_APP";
    /*TABLE WIDGET add version 5 (проверить правильность указания версии, сравнить с гитхаб)*/
    /*public static final String TABLE_WIDGET = "widget_table";
    public static final String F_ID_WIDGET = "_id";
    public static final String F_ID_ID_WIDGET = "id_widget";*/
    /*TABLE NOTIFICATION add version 3*/
    public static final String TABLE_NOTIFY = "notify_table";
    public static final String F_ID_NOTIFY = "_id";
    public static final String F_ID_ALARM_NOTIFY = "id_alarm_notify";
    public static final String F_ID_DEBT_NOTIFY = "id_debt_notify";
    public static final String F_COUNT_DAY_NOTIFY = "count_day_notify";
    public static final String F_TIME_NOTIFY = "time_notify";
    public static final String F_TIME_START_NOTIFY = "time_start_notify";
    /*TABLE CHANGED DETAILS version 4*/
    public static final String TABLE_CHANGED = "changed_table";
    public static final String F_ID_CHANGED = "_id";
    public static final String F_ID_DEBT_CHANGED = "id_debt_changed";
    public static final String F_PERCENT_CHANGED = "percent_changed";
    public static final String F_GOAL_CHANGED = "goal_changed";

    public final String REQUEST_CREATE_TABLE_DEBTS = "CREATE TABLE " +
                                                                    TABLE_CREDITS + " (" +
                                                                    FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    FIELD_ID_DEBT + " INTEGER, " +
                                                                    FIELD_SUM_DEBT + " INTEGER, " +
                                                                    FIELD_PERCENT_DEBT + " DOUBLE, " +
                                                                    FIELD_TERM_DEBT + " INTEGER, " +
                                                                    FIELD_TYPE_DEBT + " STRING, " +
                                                                    FIELD_DATE_LONG_START_DEBT + " LONG, " +
                                                                    FIELD_DATE_STR_START_DEBT + " STRING, " +
                                                                    FIELD_PAID_DEBT + " STRING, " +
                                                                    F_PAY_DEFAULT_DEBT + " DOUBLE);";

    public final String REQUEST_CREATE_TABLE_PAYMENTS = "CREATE TABLE " +
                                                                    TABLE_PAYMENTS + " (" +
                                                                    FIELD_ID_NUM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    FIELD_ID_DEBT_PAYMENTS + " INTEGER, " +
                                                                    FIELD_PAYMENT_PAYMENTS + " DOUBLE, " +
                                                                    FIELD_DEBT_PAYMENTS + " DOUBLE, " +
                                                                    FIELD_PERCENT_PAYMENTS + " DOUBLE, " +
                                                                    F_BALANCE_DEBT_PAY + " STRING, " +
                                                                    F_BALANCE_TERM_PAY + " STRING, " +
                                                                    FIELD_SUM_PAYMENTS + " DOUBLE, " +
                                                                    F_OVER_PAY + " DOUBLE, " +
                                                                    FIELD_DATE_LONG_PAYMENTS + " INTEGER, " +
                                                                    FIELD_PAID_PAYMENTS + " INTEGER, " +
                                                                    F_PAYMENT_UP_PAY + " INTEGER, " +
                                                                    //MOUTH_WIDGET_PAY + " STRING, " +
                                                                    F_COUNT_DAY_PAY + " INTEGER);";



    public final String REQUEST_CREATE_TABLE_SETTING = "CREATE TABLE " +
                                                                TABLE_SETTING + " (" +
                                                                F_ID_SET + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                F_RATING_SHOW_SET + " INTEGER, " +
                                                                F_NUMBER_START_APP_SET + " INTEGER);";

    public final String REQUEST_CREATE_TABLE_NOTIFY = "CREATE TABLE " +
                                                                TABLE_NOTIFY + " (" +
                                                                F_ID_NOTIFY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                F_ID_ALARM_NOTIFY + " INTEGER, " +
                                                                F_ID_DEBT_NOTIFY + " STRING, " +
                                                                F_COUNT_DAY_NOTIFY + " INTEGER, "+
                                                                F_TIME_NOTIFY + " STRING, "+
                                                                F_TIME_START_NOTIFY + " LONG);";

    /*public final String REQUEST_CREATE_TABLE_WIDGET = "CREATE TABLE " +
                                                                TABLE_WIDGET + " (" +
                                                                F_ID_WIDGET+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                F_ID_ID_WIDGET + " INTEGER);";*/


    public final String REQUEST_CREATE_TABLE_CHANGED = "CREATE TABLE " +
                                                                TABLE_CHANGED + " (" +
                                                                F_ID_CHANGED + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                F_ID_DEBT_CHANGED + " INTEGER, " +
                                                                F_PERCENT_CHANGED + " DOUBLE, " +
                                                                F_GOAL_CHANGED + " STRING);";

    public DebtCalcDB(Context context) {
        super(context, DATABASE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUEST_CREATE_TABLE_DEBTS);
        db.execSQL(REQUEST_CREATE_TABLE_PAYMENTS);
        db.execSQL(REQUEST_CREATE_TABLE_SETTING);
        db.execSQL(REQUEST_CREATE_TABLE_NOTIFY);
        db.execSQL("INSERT INTO " + DebtCalcDB.TABLE_SETTING + "(" +
                                    DebtCalcDB.F_RATING_SHOW_SET + ", " +
                                    DebtCalcDB.F_NUMBER_START_APP_SET + ") VALUES ('0', '0')");
        db.execSQL(REQUEST_CREATE_TABLE_CHANGED);
        //db.execSQL(REQUEST_CREATE_TABLE_WIDGET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2)
            db.execSQL("ALTER TABLE " + DebtCalcDB.TABLE_CREDITS + " ADD " + DebtCalcDB.F_PAY_DEFAULT_DEBT + " DOUBLE"); /*version 2*/
        if (oldVersion < 3)
            db.execSQL(REQUEST_CREATE_TABLE_NOTIFY);
        if (oldVersion < 4)
            db.execSQL(REQUEST_CREATE_TABLE_CHANGED); /*version 4*/
        /*if (oldVersion < 5)
            db.execSQL(REQUEST_CREATE_TABLE_WIDGET); *//*version 5*/
    }
}
