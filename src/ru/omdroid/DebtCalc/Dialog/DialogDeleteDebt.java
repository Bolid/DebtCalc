package ru.omdroid.DebtCalc.Dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;

import java.util.ArrayList;

public class DialogDeleteDebt extends DialogFragment implements View.OnClickListener {
    ArrayList<String> selectForDelete = null;
    Activity activity;

    public DialogDeleteDebt(ArrayList<String> selectForDelete){
        this.selectForDelete = selectForDelete;
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        View view = inflater.inflate(R.layout.dialog_delete, null);
        view.findViewById(R.id.butDelYes).setOnClickListener(this);
        view.findViewById(R.id.butDelNo).setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.butDelYes:
                for (int i=0; i < selectForDelete.size(); i++){
                    WorkDB workDB = new WorkDB(getActivity().getBaseContext());
                    workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_CREDITS +
                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT + " = '" + selectForDelete.get(i) + "'");
                    workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_PAYMENTS +
                            " WHERE " + DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + selectForDelete.get(i) + "'");
                }
                Toast.makeText(getActivity().getBaseContext(), "Записи удалены!", Toast.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.butDelNo:
                dismiss();
                break;
        }
    }
}
