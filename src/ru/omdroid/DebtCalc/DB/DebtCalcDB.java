package ru.omdroid.DebtCalc.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DebtCalcDB extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "debt_calc_database.db";
    private static final String TABLE_NAME_CREDITS = "debts_table";
    private static final String ID = "id";
    private static final String FIELD_SUM_DEBT = "sum_debt";
    private static final String FIELD_PERCENT_DEBT = "percent_debt";
    private static final String FIELD_TERM_DEBT = "term_debt";
    private static final String FIELD_DATE_STR_START_DEBT = "date_str_start_debt";
    private static final String FIELD_DATE_LONG_START_DEBT = "date_long_start_debt";
    private static final String REQUEST_CREATE_DATABASE = "CREATE TABLE " +
                                                        TABLE_NAME_CREDITS + " (" +
                                                            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            FIELD_SUM_DEBT + " INTEGER, " +
                                                            FIELD_PERCENT_DEBT + " DOUBLE, " +
                                                            FIELD_TERM_DEBT + " INTEGER, " +
                                                            FIELD_DATE_STR_START_DEBT + " STRING, " +
                                                            FIELD_DATE_LONG_START_DEBT + " LONG);";

    public DebtCalcDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("Создание ДБ: ", REQUEST_CREATE_DATABASE);
        db.execSQL(REQUEST_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }
}
