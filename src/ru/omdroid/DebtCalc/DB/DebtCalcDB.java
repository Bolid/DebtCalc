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
    public static final String F_PAY_DEFAULT_DEBT = "pay_default_debt";
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
    /*TABLE SETTING*/
    public static final String TABLE_SETTING = "setting_table";
    public static final String F_ID_SET = "_id";
    public static final String F_RATING_SHOW_SET = "RATING_SHOW";
    public static final String F_NUMBER_START_APP_SET = "NUM_START_APP";

    public static final String REQUEST_CREATE_TABLE_DEBTS = "CREATE TABLE " +
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

    public static final String REQUEST_CREATE_TABLE_PAYMENTS = "CREATE TABLE " +
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
                                                                    F_COUNT_DAY_PAY + " INTEGER);";

    public static final String REQUEST_CREATE_TABLE_SETTING = "CREATE TABLE " +
            TABLE_SETTING + " (" +
            F_ID_SET + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            F_RATING_SHOW_SET + " INTEGER, " +
            F_NUMBER_START_APP_SET + " INTEGER);";

    public DebtCalcDB(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUEST_CREATE_TABLE_DEBTS);
        db.execSQL(REQUEST_CREATE_TABLE_PAYMENTS);
        db.execSQL(REQUEST_CREATE_TABLE_SETTING);
        db.execSQL("INSERT INTO " + DebtCalcDB.TABLE_SETTING + "(" +
                                    DebtCalcDB.F_RATING_SHOW_SET + ", " +
                                    DebtCalcDB.F_NUMBER_START_APP_SET + ") VALUES ('0', '0')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* alter table table1 add A3 varchar(30);*/
        db.execSQL("ALTER TABLE " + DebtCalcDB.TABLE_CREDITS + " ADD " + DebtCalcDB.F_PAY_DEFAULT_DEBT + " DOUBLE");
    }


}
