package ru.omdroid.DebtCalc;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import ru.omdroid.DebtCalc.Listener.InControlFieldPercentCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.Listener.InControlFieldTermCredit;

import java.text.DecimalFormat;

public class DialogInputData extends DialogFragment implements OnClickListener {

    TextView tvLabel, tvSumPre, tvOverPre, tvTotal, tvOverPercent;
    String label, value;
    EditText etData;
    View view;
    PreCalc preCalc;
    int res;

    public DialogInputData(String value, TextView tvLabel, String label, PreCalc preCalc, TextView tvSumPre, TextView tvOverPre, TextView tvTotal, TextView tvOverPercent, int res){
        this.tvLabel = tvLabel;
        this.label = label;
        this.preCalc = preCalc;
        this.tvSumPre = tvSumPre;
        this.tvOverPre = tvOverPre;
        this.tvTotal = tvTotal;
        this.tvOverPercent = tvOverPercent;
        this.res = res;
        this.value = value;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        view = inflater.inflate(res, null);
        view.findViewById(R.id.butDialogApplyData).setOnClickListener(this);

        AppData appData = new AppData();

        etData = (EditText)view.findViewById(R.id.etDialogField);
        TextView tvLabel = (TextView)view.findViewById(R.id.tvDialogLabel);

        InControlFieldSumCredit fSumCredit = new InControlFieldSumCredit(null, etData, null, appData, null);
        InControlFieldTermCredit fTermCredit = new InControlFieldTermCredit(null, null, appData, null);
        InControlFieldPercentCredit fPercentCredit = new InControlFieldPercentCredit(null, null, appData, null);

        if (label.equals(getResources().getString(R.string.main_layout_label_credit_sum)))
            etData.addTextChangedListener(fSumCredit);
        if (label.equals(getResources().getString(R.string.main_layout_label_credit_term)))
            etData.addTextChangedListener(fTermCredit);
        if (label.equals(getResources().getString(R.string.main_layout_label_credit_percent)))
            etData.addTextChangedListener(fPercentCredit);

        etData.setText(value);
        etData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                etData.selectAll();
            }
        });
        return view;
    }

    @Override
    public void onClick(final View view) {
        AppData appData = new AppData();
        switch (view.getId()){
            case R.id.butDialogApplyData:
                TextView tvDialogLabel = (TextView)this.view.findViewById(R.id.tvDialogLabel);
                tvDialogLabel.setText(label);
                tvLabel.setText(etData.getText().toString());
                Arithmetic arithmetic;
                switch (res){
                    case R.layout.dialog_input_sum: appData.setDebt(formatValue(etData.getText().toString()));
                        break;
                    case R.layout.dialog_input_term: appData.setTerm(formatValue(etData.getText().toString()));
                        break;
                    case R.layout.dialog_input_percent: appData.setPercent(formatValue(etData.getText().toString()));
                        break;
                    case R.layout.dialog_input_goal: appData.setGoal(etData.getText().toString());
                        break;
                }
                if (preCalc.preCalc()){
                    arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT), AppData.PERCENT, AppData.TERM);
                    tvSumPre.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(1))));
                    tvOverPre.setText("+ " + new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(5))));
                    tvTotal.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(1)) + Double.valueOf(Arithmetic.allResult.get(5))));
                    tvOverPercent.setText(String.valueOf(arithmetic.getOverInPercent()) + "%");
                }
                dismiss();
                break;
        }
    }
    @Override
    public void onDismiss(android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(android.content.DialogInterface dialog){
        super.onCancel(dialog);
    }

    private String formatValue(String addPayment){
        String value = "";
        for (int j = 0; j < addPayment.length(); j++) {
            if ("1234567890".contains(String.valueOf(addPayment.charAt(j)))){
                value = value + addPayment.charAt(j);
            }
            else if (j == addPayment.length() - 3 || j == addPayment.length() - 2){
                value = value + ".";
            }
        }
        if (value.equals(""))
            return "0.0";
        else
            return value;
    }
}
