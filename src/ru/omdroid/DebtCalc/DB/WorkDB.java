package ru.omdroid.DebtCalc.DB;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WorkDB {
    DebtCalcDB debtCalcDB;
    SQLiteDatabase sql;
    public WorkDB(Context context){
        debtCalcDB = new DebtCalcDB(context);
        sql = debtCalcDB.getWritableDatabase();
    }

    public void insertValueToDataBase(String requestIns){
        sql.execSQL(requestIns);
        sql.close();
    }

    public Cursor readValueFromDataBase(String requestSelect){
        return sql.rawQuery(requestSelect, null);
    }

    public int countDataInDataBase(String requestCount){
        return sql.rawQuery(requestCount, null).getCount();
    }
}
