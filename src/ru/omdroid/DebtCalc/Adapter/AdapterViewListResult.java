package ru.omdroid.DebtCalc.Adapter;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ru.omdroid.DebtCalc.DialogPayment;
import ru.omdroid.DebtCalc.R;

import java.util.HashMap;
import java.util.List;

public class AdapterViewListResult extends SimpleAdapter{
    Context context;
    Drawable image;
    FragmentManager fragmentManager;
    List <HashMap<String, String>> data;
    ListView listView;
    public static String position;
    public AdapterViewListResult(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to, Drawable image, FragmentManager fragmentManager, ListView listView) {
        super(context, data, resource, from, to);
        this.context = context;
        this.data = data;
        this.image = image;
        this.image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        this.fragmentManager = fragmentManager;
        this.listView = listView;
    }

    @Override
    public void setViewImage(android.widget.ImageView v, java.lang.String value){
        super.setViewImage(v, value);
        if (value.equals("1"))
            v.setImageResource(R.drawable.arrow);
    }

    public void notifyDataSetChanged(){
    }
}
