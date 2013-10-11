package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class ResultForm extends Activity {
    static Double newPayment;
    public static Arithmetic arithmetic;
    static boolean paymentUpdate;
    static boolean overPayment;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_result);

        final String[] params = null;//AppDate.param;
        final NumberFormat numberFormat = new DecimalFormat("###,###,###,###,###,###,##0.##");
        final View view = (View)findViewById(R.id.graphView);

        final TextView textView = (TextView)findViewById(R.id.valuePayment);

        paymentUpdate = true;
        overPayment = false;

        arithmetic = new Arithmetic(Double.valueOf(params[0]), Double.valueOf(params[2]), Integer.valueOf(params[1]));
        newPayment = Double.valueOf(Arithmetic.allResult.get(4));
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        textView.setText(numberFormat.format(newPayment));
        seekBar.setMax(Integer.valueOf(params[1]));
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        overPayment = i != 0;
                        if (i == seekBar.getMax())
                            i--;
                        newPayment = arithmetic.getPayment(Double.valueOf(params[0]), Integer.valueOf(params[1]) - i);
                        textView.setText(arithmetic.setMask(arithmetic.getPayment(Double.valueOf(params[0]), Integer.valueOf(params[1]) - i)));
                        arithmetic.getDeltaDefault(arithmetic.getPayment(Double.valueOf(params[0]), Integer.valueOf(params[1]) - i), Integer.valueOf(params[1]) - i);
                        Arithmetic.allResult.set(6, String.valueOf(Integer.valueOf(params[1]) - i));
                        view.invalidate();
                        paymentUpdate = true;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
    }

}

