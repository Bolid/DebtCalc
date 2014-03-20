package ru.omdroid.DebtCalc.Widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;


public class WidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i("Сработало", " Сработало");
        return new WidgetAdapter(getApplicationContext());
    }
}
