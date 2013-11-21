package ru.omdroid.DebtCalc.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.omdroid.DebtCalc.Arithmetic;


public class Receiver extends BroadcastReceiver {
    String ADD_NOTIFY = "ADD_NOTIFY";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ADD_NOTIFY))
            Log.v("Ресивер: ", "Круто");
    }
}
