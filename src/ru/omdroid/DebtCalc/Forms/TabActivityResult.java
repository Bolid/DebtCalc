package ru.omdroid.DebtCalc.Forms;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import ru.omdroid.DebtCalc.R;

public class TabActivityResult extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        TabHost tabHost = getTabHost();
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setContent(new Intent(this, ResultForm.class));
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_bar_style));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_table_style));
        tabSpec.setContent(new Intent(this, ListResultForm.class));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

    }
}
