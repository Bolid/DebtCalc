package ru.omdroid.DebtCalc.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class ResultFragment extends Fragment {
    static Double newPayment;
    public static Arithmetic arithmetic;
    static boolean paymentUpdate;
    static boolean overPayment;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.layout_result, null);

        final String[] params = AppData.param;
        final NumberFormat numberFormat = new DecimalFormat("###,###,###,###,###,###,##0.##");
        final View view = (View)v.findViewById(R.id.graphView);
        final TextView textView = (TextView)v.findViewById(R.id.valuePayment);

        if (!ErrorMessage.nullSumCredit.equals("") || !ErrorMessage.nullPercentCredit.equals("") || !ErrorMessage.nullTermCredit.equals(""))
            Toast.makeText(getActivity().getBaseContext(), ErrorMessage.errorTitle + ErrorMessage.nullSumCredit + ErrorMessage.nullPercentCredit + ErrorMessage.nullTermCredit, Toast.LENGTH_LONG).show();

        paymentUpdate = true;
        overPayment = false;

        arithmetic = new Arithmetic(Double.valueOf(AppData.param[0]), Integer.valueOf(AppData.param[1]), Double.valueOf(AppData.param[2]));
        newPayment = Double.valueOf(Arithmetic.allResult.get(4));
        SeekBar seekBar = (SeekBar)v.findViewById(R.id.seekBar);
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
        return v;

    }
}
