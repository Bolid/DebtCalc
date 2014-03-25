package ru.omdroid.DebtCalc.Widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;


public class WidgetProvider extends AppWidgetProvider{
    public static final String UPDATE_WIDGET = "UPDATE_WIDGET";
    boolean createWidget = false;
    WorkDB workDB;
    @Override
    public void onEnabled(Context context){
        createWidget = true;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            onUpdateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public void onUpdateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        RemoteViews rView = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rView.setRemoteAdapter(R.id.lViewInWidget, intent);
        appWidgetManager.updateAppWidget(appWidgetId, rView);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lViewInWidget);
    }

    private void updateData(AppWidgetManager appWidgetManager, int[] ids){
        for (int id : ids)
            appWidgetManager.notifyAppWidgetViewDataChanged(id, R.id.lViewInWidget);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action.equals(UPDATE_WIDGET)){
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName cName = new ComponentName(context, WidgetProvider.class);
            int[] ids = manager.getAppWidgetIds(cName);
            onUpdate(context, manager, ids);
        }
    }

    private void saveIdWidget(int[] ids){
        for (int id : ids)
            workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_WIDGET + "(" + DebtCalcDB.F_ID_ID_WIDGET + ") VALUES ('" + id + "')");
        }

    private int[] getSavedIdWidget(Context context){
        workDB = new WorkDB(context);
        Cursor cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.F_ID_ID_WIDGET + " FROM " + DebtCalcDB.TABLE_WIDGET);
        int[] ids = new int[cursor.getCount()];
        while (cursor.moveToNext()){
            ids[cursor.getPosition()] = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_ID_ID_WIDGET));
            Log.i("Проверки из БД ", String.valueOf(cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_ID_ID_WIDGET))));
        }
        workDB.disconnectDataBase();
        return ids;
    }
}
