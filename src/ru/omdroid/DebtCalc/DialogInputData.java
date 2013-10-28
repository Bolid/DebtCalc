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
    String label;
    EditText etData;
    View view;
    PreCalc preCalc;

    public DialogInputData(TextView tvLabel, String label, PreCalc preCalc, TextView tvSumPre, TextView tvOverPre, TextView tvTotal, TextView tvOverPercent){
        this.tvLabel = tvLabel;
        this.label = label;
        this.preCalc = preCalc;
        this.tvSumPre = tvSumPre;
        this.tvOverPre = tvOverPre;
        this.tvTotal = tvTotal;
        this.tvOverPercent = tvOverPercent;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.list_dialog_label_title));
        view = inflater.inflate(R.layout.dialog_input_data, null);
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

        tvLabel.setText(label);
        etData.requestFocus();
        return view;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.butDialogApplyData:
                TextView tvDialogLabel = (TextView)this.view.findViewById(R.id.tvDialogLabel);
                tvDialogLabel.setText(label);
                tvLabel.setText(etData.getText().toString());
                Arithmetic arithmetic;
                if (preCalc.preCalc()){
                    arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT), AppData.PERCENT, AppData.TERM);
                    tvSumPre.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(1))));
                    tvOverPre.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(5))));
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

    private Double formatAddPayment(String addPayment){
        String newAddPayment = "";
        for (int j = 0; j < addPayment.length(); j++) {
            if ("1234567890".contains(String.valueOf(addPayment.charAt(j)))){
                newAddPayment = newAddPayment + addPayment.charAt(j);
            }
            else if (j == addPayment.length() - 3 || j == addPayment.length() - 2){
                newAddPayment = newAddPayment + ".";
            }
        }
        if (newAddPayment.equals(""))
            return 0.00;
        else
            return Double.valueOf(newAddPayment);
    }
}
