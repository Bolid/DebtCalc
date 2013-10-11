package ru.omdroid.DebtCalc.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DebtCalcDB extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "debt_calc_database.db";
    public static final String TABLE_NAME_CREDITS = "debts_table";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_ID_DEBT = "id_debt";
    public static final String FIELD_SUM_DEBT = "sum_debt";
    public static final String FIELD_PERCENT_DEBT = "percent_debt";
    public static final String FIELD_TERM_DEBT = "term_debt";
    public static final String FIELD_TYPE_DEBT = "type_debt";
    public static final String FIELD_DATE_STR_START_DEBT = "date_str_start_debt";
    public static final String FIELD_DATE_LONG_START_DEBT = "date_long_start_debt";
    public static final String FIELD_BALANCE_DEBT = "balance_debt";
    public static final String TABLE_NAME_PAYMENTS = "payments_table";
    public static final String FIELD_ID_NUM = "_id";
    public static final String FIELD_ID_DEBT_PAYMENTS = "id_debt_payments";
    public static final String FIELD_PAYMENT_PAYMENTS = "payments_payments";
    public static final String REQUEST_CREATE_TABLE_DEBTS = "CREATE TABLE " +
                                                                TABLE_NAME_CREDITS + " (" +
                                                                    FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    FIELD_ID_DEBT + " INTEGER, " +
                                                                    FIELD_SUM_DEBT + " INTEGER, " +
                                                                    FIELD_PERCENT_DEBT + " DOUBLE, " +
                                                                    FIELD_TERM_DEBT + " INTEGER, " +
                                                                    FIELD_TYPE_DEBT + " STRING, " +
                                                                    FIELD_DATE_LONG_START_DEBT + " LONG, " +
                                                                    FIELD_DATE_STR_START_DEBT + " STRING, " +
                                                                    FIELD_BALANCE_DEBT + " STRING);";

    public static final String REQUEST_CREATE_TABLE_PAYMENTS = "CREATE TABLE " +
                                                                TABLE_NAME_PAYMENTS + " (" +
                                                                    FIELD_ID_NUM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    FIELD_ID_DEBT_PAYMENTS + " INTEGER, " +
                                                                    FIELD_PAYMENT_PAYMENTS + " DOUBLE);";

    public DebtCalcDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUEST_CREATE_TABLE_DEBTS);
        db.execSQL(REQUEST_CREATE_TABLE_PAYMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }
}
