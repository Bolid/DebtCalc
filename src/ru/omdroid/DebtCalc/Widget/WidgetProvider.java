package ru.omdroid.DebtCalc.Widget;


import android.app.PendingIntent;
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
import ru.omdroid.DebtCalc.Forms.ListDebt;
import ru.omdroid.DebtCalc.R;


public class WidgetProvider extends AppWidgetProvider{
    public static final String UPDATE_WIDGET = "UPDATE_WIDGET";
    boolean createWidget = false;
    WorkDB workDB;
    final String OPEN_CALC = "OPEN_CALC";
    Intent intentOpenCalc;
    PendingIntent pIntentOpenCalc;
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

        setItemClick(rView, context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, rView);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lViewInWidget);
    }

    private void setItemClick(RemoteViews rv, Context context, int appWidgetId){
        Intent intentOpenCalc = new Intent(context.getApplicationContext(), WidgetProvider.class);
        intentOpenCalc.setAction(OPEN_CALC);
        intentOpenCalc.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntentOpenCalc = PendingIntent.getBroadcast(context, 0, intentOpenCalc, 0);
        rv.setPendingIntentTemplate(R.id.lViewInWidget, pIntentOpenCalc);
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

        if (action.equals(OPEN_CALC)){
            Intent intentFormStart = new Intent(context, ListDebt.class);
            intentFormStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentFormStart);
        }
    }
}
