package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.CustomView.DataForGraph;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddOverallPayment;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddPayment;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class ResultForm extends Activity {
    static Double newPayment;
    public static Arithmetic arithmetic;
    static boolean paymentUpdate;
    static boolean overPayment;

    boolean changeListener = false;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_result);
        final DataForGraph dataForGraph = new DataForGraph();
        dataForGraph.createOver(true);
        dataForGraph.createTerm(true);

        final NumberFormat numberFormat = new DecimalFormat("###,###,###,###,###,###,##0.##");
        final View view = (View)findViewById(R.id.graphViewTermDebt);

        arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);

        final EditText etPayment = (EditText)findViewById(R.id.valuePayment);
        final InControlFieldAddOverallPayment inControlFieldAddPayment = new InControlFieldAddOverallPayment(etPayment, Double.valueOf(AppData.PAYMENT_DEFAULT), view, arithmetic);

        etPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!changeListener){
                    etPayment.addTextChangedListener(inControlFieldAddPayment);
                    changeListener = !changeListener;
                }
            }
        });

        paymentUpdate = true;
        overPayment = false;
        newPayment = Double.valueOf(Arithmetic.allResult.get(4));
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        etPayment.setText(numberFormat.format(newPayment));
        seekBar.setMax(AppData.TERM_BALANCE);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        etPayment.removeTextChangedListener(inControlFieldAddPayment);
                        changeListener = !changeListener;
                        overPayment = i != 0;
                        if (i == seekBar.getMax())
                            i--;
                        newPayment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE - i);
                        etPayment.setText(arithmetic.setMask(arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE - i)));
                        arithmetic.getDeltaDefault(arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE - i), AppData.TERM_BALANCE - i);
                        Arithmetic.allResult.set(6, String.valueOf(AppData.TERM_BALANCE - i));
                        dataForGraph.setParamNew(AppData.TERM_BALANCE - i);
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

