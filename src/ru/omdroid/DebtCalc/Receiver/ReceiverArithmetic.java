package ru.omdroid.DebtCalc.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.omdroid.DebtCalc.Arithmetic;


public class ReceiverArithmetic extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String[] data = intent.getExtras().getStringArray("DATA");
        if (action.equals("GO"))
            Log.v("Ресивер: ", "Круто");
        if (action.equals("START")) {
        }
    }
}
