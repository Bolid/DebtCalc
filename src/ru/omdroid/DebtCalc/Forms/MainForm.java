package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Fragment.MainFragment;
import ru.omdroid.DebtCalc.Fragment.ResultFragment;
import ru.omdroid.DebtCalc.Fragment.TableFragment;
import ru.omdroid.DebtCalc.Listener.TabListener;
import ru.omdroid.DebtCalc.R;

public class MainForm extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main);
        createActionBar();
    }

    public void createActionBar(){
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab = actionBar.newTab();
        tab.setText("Закладка 1");
        tab.setTabListener(new TabListener<MainFragment>(
                this, "main", MainFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab();
        tab.setText("Закладка 2");
        tab.setTabListener(new TabListener<ResultFragment>(
                this, "main", ResultFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab();
        tab.setText("Закладка 3");
        tab.setTabListener(new TabListener<TableFragment>(
                this, "main", TableFragment.class));
        actionBar.addTab(tab);
    }
}
