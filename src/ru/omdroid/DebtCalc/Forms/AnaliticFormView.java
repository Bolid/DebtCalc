package ru.omdroid.DebtCalc.Forms;


import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import ru.omdroid.DebtCalc.Adapter.AdapterViewData;
import ru.omdroid.DebtCalc.R;

import java.util.ArrayList;

public class AnaliticFormView extends Activity{
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analitic_form_view);
        GridView gridViewData = (GridView)findViewById(R.id.afvGridViewData);
       // GridView gridViewLabel = (GridView)findViewById(R.id.afvGridViewLabel);
        ArrayList<String> paramCredit = (ArrayList<String>) getIntent().getExtras().get("com.CC.DebtCalc.Form.AnaliticForm");
        AdapterViewData adapterViewData = new AdapterViewData(getBaseContext(), paramCredit);
        gridViewData.setAdapter(adapterViewData);
       // gridViewLabel.setAdapter(new AdapterViewLabel(getBaseContext(), paramCredit.size()));

    }
}
