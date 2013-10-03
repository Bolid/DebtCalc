package ru.omdroid.DebtCalc;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WorkDB extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "debt_calc_database.db";
    private static final String TABLE_NAME = "debts_table";
    private static final String ID = "id";
    private static final String FIELD_SUM_DEBT = "sum_debt";
    private static final String FIELD_PERCENT_DEBT = "percent_debt";
    private static final String FIELD_TERM_DEBT = "term_debt";
    private static final String FIELD_DATE_START_DEBT = "date_start_debt";
    private static final String REQUEST_CREATE_DATABASE = "CREATE TABLE " +
                                                        TABLE_NAME + " (" +
                                                            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            FIELD_SUM_DEBT + " INTEGER, " +
                                                            FIELD_PERCENT_DEBT + " DOUBLE);";

    public WorkDB(Context context) {
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
