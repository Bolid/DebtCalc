package ru.omdroid.DebtCalc.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.CustomView.DataForGraph;
import ru.omdroid.DebtCalc.ErrorMessage;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddOverallPayment;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class ResultFragment extends Fragment {
    static public Double newPayment;
    static boolean paymentUpdate = true;
    static boolean overPayment;
    boolean listener = false;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.layout_result, null);
        MainFragment.arithmetic = new Arithmetic(Double.valueOf(AppData.param[0]), Double.valueOf(AppData.param[1]), Integer.valueOf(AppData.param[2]));
        newPayment = MainFragment.arithmetic.getPayment(Double.valueOf(AppData.param[0]), Integer.valueOf(AppData.param[2]));

        final String[] params = AppData.param;
        final NumberFormat numberFormat = new DecimalFormat("###,###,###,###,###,###,##0.##");
        final View view = (View)v.findViewById(R.id.graphView);
        final EditText editText = (EditText)v.findViewById(R.id.valuePayment);
        editText.setText("");
        editText.setText(numberFormat.format(newPayment));

        final InControlFieldAddOverallPayment inControlFieldAddOverallPayment = new InControlFieldAddOverallPayment(editText, newPayment, view);

        if (!ErrorMessage.nullSumCredit.equals("") || !ErrorMessage.nullPercentCredit.equals("") || !ErrorMessage.nullTermCredit.equals(""))
            Toast.makeText(getActivity().getBaseContext(), ErrorMessage.errorTitle + ErrorMessage.nullSumCredit + ErrorMessage.nullPercentCredit + ErrorMessage.nullTermCredit, Toast.LENGTH_LONG).show();



        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listener){
                    editText.addTextChangedListener(inControlFieldAddOverallPayment);
                    listener = true;
                }
            }
        });

        overPayment = false;

        SeekBar seekBar = (SeekBar)v.findViewById(R.id.seekBar);
        seekBar.setMax(Integer.valueOf(params[2]));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DataForGraph dataForGraph = new DataForGraph();
                editText.removeTextChangedListener(inControlFieldAddOverallPayment);
                listener = false;
                overPayment = i != 0;
                if (i == seekBar.getMax())
                    i--;
                newPayment = MainFragment.arithmetic.getPayment(Double.valueOf(params[0]), Integer.valueOf(params[2]) - i);
                editText.setText(MainFragment.arithmetic.setMask(MainFragment.arithmetic.getPayment(Double.valueOf(params[0]), Integer.valueOf(params[2]) - i)));
                MainFragment.arithmetic.getDeltaDefault(MainFragment.arithmetic.getPayment(Double.valueOf(params[0]), Integer.valueOf(params[2]) - i), Integer.valueOf(params[2]) - i);
                Arithmetic.allResult.set(6, String.valueOf(Integer.valueOf(params[2]) - i));
                dataForGraph.setNewTerm(Double.valueOf(Integer.valueOf(params[2]) - i));
                view.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                paymentUpdate = true;
            }
        });

        return v;

    }
}
