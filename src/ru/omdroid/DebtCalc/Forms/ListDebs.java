package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;


public class ListDebs extends Activity {
    Cursor cursor = null;
    /*TableRow*/ View tableRow = null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debts_2);
        Button butAddDebt = (Button)findViewById(R.id.butAddDebt);
        butAddDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData appData = new AppData();
                appData.addSumCredit("1000000");
                appData.addPercentCredit("12");
                appData.addTermCredit("120");
                appData.addTypeCredit("\nНовый");
                startActivity(new Intent(getBaseContext(), MainForm.class));
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
        //LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayoutPlate);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.llConteiner);
        linearLayout.removeAllViews();
        tableRow = null;
        final WorkDB workDB = new WorkDB(getBaseContext());
        cursor = workDB.readValueFromDataBase("SELECT " +
                DebtCalcDB.FIELD_ID + ", " +
                DebtCalcDB.FIELD_SUM_DEBT  + ", " +
                DebtCalcDB.FIELD_PERCENT_DEBT +", " +
                DebtCalcDB.FIELD_TERM_DEBT +", " +
                DebtCalcDB.FIELD_DATE_STR_START_DEBT +", " +
                DebtCalcDB.FIELD_TYPE_DEBT +
                " FROM " + DebtCalcDB.TABLE_NAME_CREDITS);
        while (cursor.moveToNext()){
            addPlate(layoutInflater,
                    linearLayout,
                    cursor.getPosition()%3,
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)),
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));
            Log.v("Сохраненные кредиты: ", cursor.getPosition() + ") сумма: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_DEBT)) + " процент: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_PERCENT_DEBT)) + " срок: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TERM_DEBT)) + " дата: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_STR_START_DEBT)) + " тип: " +
                    cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));
        }
        workDB.disconnectDataBase();
        /*if (tableRow != null)
            linearLayout.addView(tableRow);*/
    }

    public void addPlateq(LayoutInflater layoutInflater, LinearLayout linearLayout, int i, final String sumCredit, final String percentCredit, final String termCredit, final String typeCredit,
                         final String dateCredit){
        boolean longClick = false;
        if (i == 0) {
            if (tableRow != null)
                linearLayout.addView(tableRow);
            tableRow = (TableRow)layoutInflater.inflate(R.layout.plate_debts, null);
            tableRow.setPadding(0, 5, 0, 0);
        }
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        ll.setPadding(0, 0, 0, 15);
        ll.setBackgroundResource(R.drawable.ico_background);

        ImageView iv = new ImageView(this);
        //iv.setPadding(5, 5, 5, 5);
        iv.setImageResource(R.drawable.ico_home);
        iv.setColorFilter(getResources().getColor(R.color.colorIconDebt));
        Log.v("Картинка: ", String.valueOf(iv.getResources()));
        ll.addView(iv);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Appetite.ttf");

        TextView tvSumCredit = new TextView(this);
        tvSumCredit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvSumCredit.setText(sumCredit + "\n" + percentCredit + "%  " + termCredit);
        tvSumCredit.setTextColor(getResources().getColor(R.color.colorIconText));
        tvSumCredit.setShadowLayer((float) (1.5), 0, 0, getResources().getColor(R.color.colorIconTextShadow));
        tvSumCredit.setTextSize(11);
        tvSumCredit.setTypeface(typeface);
        tvSumCredit.setGravity(Gravity.CENTER);
        ll.addView(tvSumCredit);

        TextView tvPerTermCredit = new TextView(this);
        tvPerTermCredit.setText(percentCredit + "%  " + termCredit);
        tvPerTermCredit.setTextColor(getResources().getColor(R.color.colorIconText));
        tvPerTermCredit.setShadowLayer((float) (1.5), 0, 0, getResources().getColor(R.color.colorIconTextShadow));
        tvPerTermCredit.setTextSize(9);
        tvPerTermCredit.setTypeface(typeface);
        tvPerTermCredit.setGravity(Gravity.CENTER_HORIZONTAL);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData appData = new AppData();
                appData.addSumCredit(sumCredit);
                appData.addPercentCredit(percentCredit);
                appData.addTermCredit(termCredit);
                appData.addTypeCredit("\n" + typeCredit);
                startActivity(new Intent(getBaseContext(), MainForm.class));
            }
        });

        ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.v("onLongClick", "Большой клик)))))");
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        //tableRow.addView(ll);
    }

    public void addPlate(LayoutInflater layoutInflater,
                         LinearLayout linearLayout, int i,
                         final String sumCredit,
                         final String percentCredit,
                         final String termCredit,
                         final String typeCredit,
                         final String dateCredit){
        tableRow = layoutInflater.inflate(R.layout.debt_conteiner, linearLayout, false);
        TextView tvType = (TextView)tableRow.findViewById(R.id.conteinerType);
        TextView tvDolg = (TextView)tableRow.findViewById(R.id.conteinerDolg);
        TextView tvDate = (TextView)tableRow.findViewById(R.id.conteinerDate);
        tvType.setText(typeCredit);
        tvDolg.setText(sumCredit);
        tvDate.setText(dateCredit);
        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData appData = new AppData();
                appData.addSumCredit(sumCredit);
                appData.addPercentCredit(percentCredit);
                appData.addTermCredit(termCredit);
                appData.addTypeCredit("\n" + typeCredit);
                startActivity(new Intent(getBaseContext(), MainForm.class));
            }
        });
        linearLayout.addView(tableRow);
    }
}
