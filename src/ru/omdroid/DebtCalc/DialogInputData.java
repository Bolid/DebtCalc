package ru.omdroid.DebtCalc;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;

import java.text.DecimalFormat;
import java.util.Calendar;

public class DialogInputData extends DialogFragment implements OnClickListener {

    TextView tvLabel, tvSumPre, tvOverPre, tvTotal, tvOverPercent;
    String value;
    EditText etData;
    View view;
    PreCalc preCalc;
    int res;

    InputMethodManager imm;

    Calendar calendar, calendarConst;

    public DialogInputData(String value, TextView tvLabel, PreCalc preCalc, TextView tvSumPre, TextView tvOverPre, TextView tvTotal, TextView tvOverPercent, int res, Calendar calendar,  Calendar calendarConst){
        this.tvLabel = tvLabel;
        this.preCalc = preCalc;
        this.tvSumPre = tvSumPre;
        this.tvOverPre = tvOverPre;
        this.tvTotal = tvTotal;
        this.tvOverPercent = tvOverPercent;
        this.res = res;
        this.value = value;
        this.calendar = calendar;
        this.calendarConst = calendarConst;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        view = inflater.inflate(res, null);
        etData = (EditText)view.findViewById(R.id.etDialogField);
        etData.requestFocus();

        imm = (InputMethodManager)getActivity().getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
        view.findViewById(R.id.butDialogApplyData).setOnClickListener(this);
        view.findViewById(R.id.butNext).setOnClickListener(this);

        AppData appData = new AppData();

        if (res == R.layout.dialog_input_sum){
            InControlFieldSumCredit fSumCredit = new InControlFieldSumCredit(null, etData, null, appData, null);
            etData.addTextChangedListener(fSumCredit);
        }

        etData.setText(value);
        return view;
    }

    @Override
    public void onClick(final View view) {
        Arithmetic arithmetic;
        AppData appData = new AppData();
        switch (view.getId()){
            case R.id.butDialogApplyData:
                tvLabel.setText(etData.getText().toString());
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
            case R.id.butNext:
                tvLabel.setText(etData.getText().toString());
                switch (res){
                    case R.layout.dialog_input_sum:
                        appData.setDebt(formatValue(etData.getText().toString()));
                        nextDialog((TextView)getActivity().findViewById(R.id.tvLabTerm), String.valueOf(AppData.TERM), R.layout.dialog_input_term);
                        break;
                    case R.layout.dialog_input_term:
                        appData.setTerm(formatValue(etData.getText().toString()));
                        nextDialog((TextView)getActivity().findViewById(R.id.tvLabPercent), String.valueOf(AppData.PERCENT), R.layout.dialog_input_percent);
                        break;
                    case R.layout.dialog_input_percent:
                        imm.hideSoftInputFromWindow(etData.getWindowToken(), 0);
                        appData.setPercent(formatValue(etData.getText().toString()));
                        DialogFragment dialogFragment = new DatePickerFragment((TextView)getActivity().findViewById(R.id.tvDateStartCredit), calendarConst, calendar);
                        dialogFragment.show(getFragmentManager(), getResources().getString(R.string.app_name));
                        dismiss();
                        break;
                    case R.layout.dialog_input_goal:
                        appData.setGoal(etData.getText().toString());
                        nextDialog((TextView)getActivity().findViewById(R.id.tvLabSum), String.valueOf(AppData.DEBT), R.layout.dialog_input_sum);
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

    private void nextDialog(TextView resOut, String value, int res){
        DialogFragment dFragment = new DialogInputData(value, resOut, preCalc, tvSumPre, tvOverPre, tvTotal, tvOverPercent, res, calendar, calendarConst);
        dFragment.show(getFragmentManager(), "");
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