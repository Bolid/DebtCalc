package ru.omdroid.DebtCalc.Dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.Listener.InControlFieldSumCredit;
import ru.omdroid.DebtCalc.PreCalc;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.util.Calendar;

public class DialogInputData extends DialogFragment implements OnClickListener {

    TextView tvLabel, tvSumPre, tvOverPre, tvTotal, tvOverPercent, tvPayment;
    String value;
    EditText etData;
    View view;
    PreCalc preCalc;
    int res;

    InputMethodManager imm;

    Calendar calendar, calendarConst;

    public DialogInputData(String value, TextView tvLabel, PreCalc preCalc, TextView tvSumPre, TextView tvOverPre, TextView tvTotal, TextView tvOverPercent, TextView tvPayment, int res, Calendar calendar,  Calendar calendarConst){
        this.tvLabel = tvLabel;
        this.preCalc = preCalc;
        this.tvSumPre = tvSumPre;
        this.tvOverPre = tvOverPre;
        this.tvTotal = tvTotal;
        this.tvOverPercent = tvOverPercent;
        this.tvPayment = tvPayment;
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

        etData.selectAll();

        view.findViewById(R.id.butDialogApplyData).setOnClickListener(this);
        view.findViewById(R.id.butNext).setOnClickListener(this);
        view.findViewById(R.id.butCancel).setOnClickListener(this);

        AppData appData = new AppData();

        if (res == R.layout.dialog_input_sum){
            InControlFieldSumCredit fSumCredit = new InControlFieldSumCredit(etData, appData, null);
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
                imm.hideSoftInputFromWindow(etData.getWindowToken(), 0);
                tvLabel.setText(etData.getText().toString());
                switch (res){
                    case R.layout.dialog_input_sum: appData.setDebtBalance(formatValue(etData.getText().toString()));
                        break;
                    case R.layout.dialog_input_term: appData.setTermBalance(formatValue(etData.getText().toString()));
                        break;
                    case R.layout.dialog_input_percent: appData.setPercent(formatValue(etData.getText().toString()));
                        break;
                    case R.layout.dialog_input_goal: appData.setGoal(etData.getText().toString());
                        break;
                }
                if (preCalc.preCalc()){
                    arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);
                    tvSumPre.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(1))));
                    tvOverPre.setText("+ " + new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(5))));
                    tvTotal.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(1)) + Double.valueOf(Arithmetic.allResult.get(5))));
                    tvOverPercent.setText(String.valueOf(arithmetic.getOverInPercent(Double.valueOf(Arithmetic.allResult.get(5)), Double.valueOf(AppData.DEBT_BALANCE), 1)) + "%");
                    tvPayment.setText(new DecimalFormat("###,###,###,###.##").format(arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE)));
                    appData.setPayment("", String.valueOf(arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE)));
                }
                dismiss();
                break;
            case R.id.butNext:
                tvLabel.setText(etData.getText().toString());
                switch (res){
                    case R.layout.dialog_input_sum:
                        appData.setDebtBalance(formatValue(etData.getText().toString()));
                        nextDialog((TextView)getActivity().findViewById(R.id.tvLabTerm), String.valueOf(AppData.TERM_BALANCE), R.layout.dialog_input_term);
                        break;
                    case R.layout.dialog_input_term:
                        appData.setTermBalance(formatValue(etData.getText().toString()));
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
                        nextDialog((TextView)getActivity().findViewById(R.id.tvLabSum), String.valueOf(AppData.DEBT_BALANCE), R.layout.dialog_input_sum);
                        break;
                }
                if (preCalc.preCalc()){
                    arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);
                    tvSumPre.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(1))));
                    tvOverPre.setText("+ " + new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(5))));
                    tvTotal.setText(new DecimalFormat("###,###,###,###").format(Double.valueOf(Arithmetic.allResult.get(1)) + Double.valueOf(Arithmetic.allResult.get(5))));
                    tvOverPercent.setText(String.valueOf(arithmetic.getOverInPercent(Double.valueOf(Arithmetic.allResult.get(5)), Double.valueOf(AppData.DEBT_BALANCE), 1)) + "%");
                    tvPayment.setText(new DecimalFormat("###,###,###,###.##").format(arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE)));
                    appData.setPayment("", String.valueOf(arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE)));
                }
                dismiss();
                break;
            case R.id.butCancel:
                imm.hideSoftInputFromWindow(etData.getWindowToken(), 0);
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
        DialogFragment dFragment = new DialogInputData(value, resOut, preCalc, tvSumPre, tvOverPre, tvTotal, tvOverPercent, tvPayment, res, calendar, calendarConst);
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
