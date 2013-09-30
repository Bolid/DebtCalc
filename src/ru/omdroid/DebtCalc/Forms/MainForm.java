package ru.omdroid.DebtCalc.Forms;

import android.app.ActionBar;
import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;

import android.widget.*;
import ru.omdroid.DebtCalc.Fragment.MainFragment;
import ru.omdroid.DebtCalc.Fragment.ResultFragment;
import ru.omdroid.DebtCalc.Listener.InControlFieldPercendCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldTermCredit;
import ru.omdroid.DebtCalc.Listener.TabListener;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;

public class MainForm extends Activity{
    public static final String TAG = "ru.omdroid.DebtCalc.MainForm";

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.main);
       // createActionBar();
        Button butStart = (Button)findViewById(R.id.butStart);
        final EditText etSumCredit = (EditText)findViewById(R.id.etCreditSum);
        final EditText etTermCredit = (EditText)findViewById(R.id.etTermCredit);
        final EditText etPercend = (EditText)findViewById(R.id.etPercentCredit);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        final String[] param = new String[3];

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Log.v("Размер экрана: ", displayMetrics.widthPixels + " на " + displayMetrics.heightPixels);

        //etSumCredit.addTextChangedListener(new InControlFieldSumCredit((ImageView)findViewById(R.id.markerSumCredit), etSumCredit, ));
        etPercend.addTextChangedListener(new InControlFieldPercendCredit((ImageView)findViewById(R.id.markerPercentCredit)));
        etTermCredit.addTextChangedListener(new InControlFieldTermCredit((ImageView)findViewById(R.id.markerTermCredit)));

        etSumCredit.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(etSumCredit.getText().toString())));

        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String notify = "";
                if (!etPercend.getText().toString().equals(""))
                    if (Double.valueOf(etPercend.getText().toString().replace(",",".")) > 100){
                        notify = "Процентная ставка может быть не более 100%\n";
                        notify = notify + "Пожалуйста, введите следующие параметры для кредита:";
                    }
                    else
                        notify = notify + "Пожалуйста, введите следующие параметры для кредита:";

                if (etSumCredit.getText().toString().equals("") || etSumCredit.getText().toString().equals("0"))
                    notify = notify + "\nСумму кредита.";
                if (etTermCredit.getText().toString().equals("") || etTermCredit.getText().toString().equals("0"))
                    notify = notify + "\nСрок кредита.";
                if (etPercend.getText().toString().equals("") || etPercend.getText().toString().equals("0"))
                    notify = notify + "\nПроцентную ставку.";
                if (notify.length() > 55)
                    Toast.makeText(getBaseContext(), notify, Toast.LENGTH_LONG).show();
                else {
                    param[0] = "";
                    for (int j = etSumCredit.getText().length(); j > 0; j--) {
                        if ("1234567890".contains(String.valueOf(etSumCredit.getText().toString().charAt(j-1))))
                            param[0] = etSumCredit.getText().toString().charAt(j-1) + param[0];
                    }
                    param[1] = etTermCredit.getText().toString();
                    param[2] = etPercend.getText().toString();
                    //new AppDate(param);
                    Intent intent = new Intent(getBaseContext(), TabActivityResult.class);
                    //intent.putExtra(TAG, param);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_menu_analitic_item:
                Intent intent = new Intent(this, AnaliticForm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }
    public void createActionBar(){
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab = actionBar.newTab();
        tab.setText("Закладка 1");
        tab.setTabListener(new TabListener<MainFragment>(
                this, "main", MainFragment.class, actionBar));
        actionBar.addTab(tab);
        tab = actionBar.newTab();
        tab.setText("Закладка 2");
        tab.setTabListener(new TabListener<ResultFragment>(
                this, "main", ResultFragment.class, actionBar));
        actionBar.addTab(tab);

    }
}

/*public class AppDate{
    static String[] param;
    AppDate(String[] param){
        this.param = param;
    }
}*/
