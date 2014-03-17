package ru.omdroid.DebtCalc.Dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Forms.InfoDebt;
import ru.omdroid.DebtCalc.Forms.ListDebt;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkDateDebt;

public class DialogChangePercent extends DialogFragment implements View.OnClickListener {
    Context context;
    WorkDB workDB;
    EditText editText;
    Double newPayment;
    Double paymentInPercent;
    Double paymentInDebt;
    ListDebt.RefreshGUIThis refreshGUIListDebt;
    InfoDebt.RefreshGUIThis refreshGUIInfoDebt;

    InputMethodManager imm;
    public DialogChangePercent(Context context, ListDebt.RefreshGUIThis refreshGUIListDebt){
        this.context = context;
        this.refreshGUIListDebt = refreshGUIListDebt;
        workDB = new WorkDB(context);
    }

    public DialogChangePercent(Context context, InfoDebt.RefreshGUIThis refreshGUIInfoDebt){
        this.context = context;
        this.refreshGUIInfoDebt = refreshGUIInfoDebt;
        workDB = new WorkDB(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        View view = inflater.inflate(R.layout.dialog_change_percend, null);
        editText = (EditText)view.findViewById(R.id.etNewPercent);
        view.findViewById(R.id.butOK).setOnClickListener(this);
        view.findViewById(R.id.butCancel).setOnClickListener(this);

        imm = (InputMethodManager)getActivity().getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);
        return view;
    }

    @Override
    public void onClick(View view) {
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        switch (view.getId()){
            case R.id.butOK:
                if (editText.getText().toString().equals(""))
                    Toast.makeText(context, "Введите новое значение ставки", Toast.LENGTH_LONG).show();
                else{
                    applyNewPercent();

                    workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_CHANGED + "(" +
                            DebtCalcDB.F_ID_DEBT_CHANGED + ", " +
                            DebtCalcDB.F_PERCENT_CHANGED + ", " +
                            DebtCalcDB.F_GOAL_CHANGED + ") " +
                            "VALUES ('" +
                            AppData.ID_DEBT + "', '" +
                            editText.getText() + "', '')");
                    workDB.updateData("UPDATE " + DebtCalcDB.TABLE_CREDITS +
                            " SET " +
                            DebtCalcDB.F_PAY_DEFAULT_DEBT + " = '" + newPayment + "', " +
                            DebtCalcDB.FIELD_PERCENT_DEBT +" = '" + editText.getText() +"'" +
                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + AppData.ID_DEBT + "'");

                    workDB.updateData("UPDATE " + DebtCalcDB.TABLE_PAYMENTS +
                            " SET " +
                            DebtCalcDB.FIELD_PAYMENT_PAYMENTS + " = '" + newPayment + "', " +
                            DebtCalcDB.FIELD_DEBT_PAYMENTS + " = '" + paymentInDebt + "', " +
                            DebtCalcDB.FIELD_PERCENT_PAYMENTS + " = '" + paymentInPercent + "', " +
                            DebtCalcDB.FIELD_SUM_PAYMENTS + " = '" + changeSumPayments(newPayment) + "'" +
                            " WHERE (" + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + AppData.ID_DEBT + "'" +
                            " AND " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");

                    workDB.disconnectDataBase();
                    dismiss();
                    if (refreshGUIListDebt != null){
                        refreshGUIListDebt.applyNewPercent(editText.getText().toString());
                        refreshGUIListDebt.refreshGUI();
                    }
                    if (refreshGUIInfoDebt != null){
                        refreshGUIInfoDebt.applyNewPercent(editText.getText().toString(), newPayment, newPayment, paymentInPercent);
                        refreshGUIInfoDebt.refreshGUI();
                    }
                }

                break;
            case R.id.butCancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void applyNewPercent(){
        WorkDateDebt workDateDebt = new WorkDateDebt();
        Arithmetic arithmetic = new Arithmetic(Double.valueOf(editText.getText().toString()));

        workDateDebt.getCountDayInMonth(AppData.DATE_PAY);
        newPayment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE);
        paymentInPercent = arithmetic.getOverpaymentOneMonth(Double.valueOf(AppData.DEBT_BALANCE));
        paymentInDebt = newPayment - paymentInPercent;
    }

    private Double changeSumPayments(Double newPayment){
        Cursor cursor = workDB.readValueFromDataBase("SELECT MAX(" + DebtCalcDB.FIELD_SUM_PAYMENTS + ") " +
                                                     "AS " + DebtCalcDB.FIELD_SUM_PAYMENTS +
                                                     " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                                                     " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = " + AppData.ID_DEBT);
        cursor.moveToNext();
        Double newSumPayments = cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_SUM_PAYMENTS));
        cursor.close();
        return newSumPayments - Double.valueOf(AppData.PAYMENT) + newPayment;
    }
}
