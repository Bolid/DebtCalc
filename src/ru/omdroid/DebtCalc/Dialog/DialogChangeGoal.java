package ru.omdroid.DebtCalc.Dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Forms.ListDebt;
import ru.omdroid.DebtCalc.R;

public class DialogChangeGoal extends DialogFragment implements View.OnClickListener {
    Context context;
    WorkDB workDB;
    EditText editText;
    ListDebt.RefreshGUIThis refreshGUIListDebt;
    InputMethodManager imm;

    public DialogChangeGoal(Context context, ListDebt.RefreshGUIThis refreshGUIListDebt){
        this.context = context;
        this.refreshGUIListDebt = refreshGUIListDebt;
        workDB = new WorkDB(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        View view = inflater.inflate(R.layout.dialog_change_goal, null);
        editText = (EditText)view.findViewById(R.id.etNewGoal);
        view.findViewById(R.id.butOK).setOnClickListener(this);
        view.findViewById(R.id.butCancel).setOnClickListener(this);

        imm = (InputMethodManager)getActivity().getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1);

        editText.selectAll();
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
                    workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_CHANGED + "(" +
                            DebtCalcDB.F_ID_DEBT_CHANGED + ", " +
                            DebtCalcDB.F_PERCENT_CHANGED + ", " +
                            DebtCalcDB.F_GOAL_CHANGED + ") " +
                            "VALUES ('" +
                            AppData.ID_DEBT + "', '" +
                            editText.getText() + "', '')");
                    workDB.updateData("UPDATE " + DebtCalcDB.TABLE_CREDITS +
                            " SET " +
                            DebtCalcDB.FIELD_TYPE_DEBT + " = '" + editText.getText() + "'" +
                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + AppData.ID_DEBT + "'");
                    workDB.disconnectDataBase();
                    dismiss();

                    if (refreshGUIListDebt != null){
                        refreshGUIListDebt.refreshGUI();
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
}
