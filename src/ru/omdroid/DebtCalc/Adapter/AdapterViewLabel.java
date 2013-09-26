package ru.omdroid.DebtCalc.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class AdapterViewLabel extends BaseAdapter {

    Context context;
    TextView view;
    int size = 0;
    int item = 0;

    public AdapterViewLabel(Context context, int size){
        this.context = context;
        this.size = size;
    }
    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view1, ViewGroup viewGroup) {
        view = new TextView(context);
        view.setTextSize(20);
        if (item == 0)
            view.setText("Сумма кредита");
        else if (item == 1)
            view.setText("Срок кредита");
        else if (item == 2)
            view.setText("Процент кредита");
        else if (item == 4)
            view.setText("Платеж");
        else if (item == 5){
            view.setText("Переплата");
            item= -1;
        }
        item++;
        return view;
    }
}
