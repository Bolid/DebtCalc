package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ContextWindow extends Activity {
    public static String TAG = "ContextWindow";

    TextView tvPlatej;
    TextView tvDelta;
    TextView tvDopPereplata;
    TextView tvDopTerm;
    TextView tvTerm;
    TextView tvDopPlatej;
    TextView tvEconomy;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.context_window);

        tvPlatej = (TextView)findViewById(R.id.tvPlatej);
        tvDelta = (TextView)findViewById(R.id.tvDelta);
        tvDopPlatej = (TextView)findViewById(R.id.tvDopPlatej);
        tvDopTerm = (TextView)findViewById(R.id.tvDopTerm);
        tvTerm = (TextView)findViewById(R.id.tvTerm);
        tvDopPereplata = (TextView)findViewById(R.id.tvDopPereplata);
        tvEconomy = (TextView)findViewById(R.id.tvEconomy);


        final Button butDopPereplata = (Button)findViewById(R.id.butPer);

        final EditText etDopPlatej = (EditText)findViewById(R.id.etDopPlatej);

        final ArrayList<String> result = new ArrayList<String>(getIntent().getExtras().getStringArrayList("MainForm"));

        resultShow(result);

        butDopPereplata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Arithmetic arithmetic = null;
                if (etDopPlatej.getText().toString().equals("") || Double.valueOf(etDopPlatej.getText().toString()) == 0)
                    arithmetic = new Arithmetic(Double.valueOf(result.get(1)), Integer.valueOf(result.get(2)), Double.valueOf(result.get(3)));
                else
                    arithmetic = new Arithmetic(Double.valueOf(result.get(1)), Integer.valueOf(result.get(2)), Double.valueOf(result.get(3)));
                            resultShow(arithmetic.allResult);
            }
        });
    }

    private void resultShow(ArrayList<String> listResult){
        NumberFormat patternMoney = new DecimalFormat("###,###,###,###,###,###,##0.00");
        NumberFormat patternMounth = new DecimalFormat("###,###");
        String stringResultControlLength = "";

        stringResultControlLength = patternMoney.format(Double.valueOf(listResult.get(4))) + " руб";
        tvPlatej.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
        tvPlatej.setText(stringResultControlLength);

        stringResultControlLength = patternMoney.format(Double.valueOf(listResult.get(5))) + " руб";
        tvDelta.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
        tvDelta.setText(stringResultControlLength);

        stringResultControlLength = patternMounth.format(Double.valueOf(listResult.get(6))) + " месяцев";
        tvTerm.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
        tvTerm.setText(stringResultControlLength);

        if (listResult.size() == 10){
            try{
                stringResultControlLength = patternMoney.format(Double.valueOf(listResult.get(7))) + " руб";
                tvDopPlatej.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
                tvDopPlatej.setText(stringResultControlLength);
            }catch (Exception e) {
                stringResultControlLength = listResult.get(7);
                tvDopPlatej.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
                tvDopPlatej.setText(stringResultControlLength);
            }

            stringResultControlLength = patternMoney.format(Double.valueOf(listResult.get(8))) + " руб";
            tvDopPereplata.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
            tvDopPereplata.setText(stringResultControlLength);

            stringResultControlLength = patternMounth.format(Double.valueOf(listResult.get(9))) + " месяцев";
            tvDopTerm.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
            tvDopTerm.setText(stringResultControlLength);

            stringResultControlLength = patternMoney.format(Double.valueOf(listResult.get(5)) - Double.valueOf(listResult.get(8))) + " руб";
            tvEconomy.setTextSize((float) (480 * 0.6/stringResultControlLength.length()));
            tvEconomy.setText(stringResultControlLength);
        }
    }
}
