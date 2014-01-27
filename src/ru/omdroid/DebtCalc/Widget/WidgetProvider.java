package ru.omdroid.DebtCalc.Widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import ru.omdroid.DebtCalc.R;


public class WidgetProvider extends AppWidgetProvider{
    final String UPDATE_WIDGET = "UPDATE_WIDGET";
    @Override
    public  void onEnabled(Context context){
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        /*RemoteViews rView = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
        rView.setRemoteAdapter(R.id.lViewInWidget, intent);
        appWidgetManager.updateAppWidget(appWidgetIds, rView);*/
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
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action.equals(UPDATE_WIDGET)){
            ComponentName componentName = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName));
        }

    }
}
