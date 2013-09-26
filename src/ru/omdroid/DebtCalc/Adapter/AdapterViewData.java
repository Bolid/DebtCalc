package ru.omdroid.DebtCalc.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterViewData extends BaseAdapter{
    TextView view;
    ArrayList<String> paramCredit;
    Context context;

    int item = 0;
    public AdapterViewData(Context context, ArrayList<String> paramCredit){
        this.context = context;
        this.paramCredit = paramCredit;
    }
    @Override
    public int getCount() {
        return paramCredit.size();
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
        view.setText(paramCredit.get(i));
        view.setTextSize(24);
        view.setTypeface(null, Typeface.BOLD);
        return view;
    }
}
