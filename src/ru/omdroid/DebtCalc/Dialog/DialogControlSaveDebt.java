package ru.omdroid.DebtCalc.Dialog;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.omdroid.DebtCalc.Forms.MainNew;
import ru.omdroid.DebtCalc.R;

public class DialogControlSaveDebt extends DialogFragment implements View.OnClickListener {
    MainNew.ControlSaveDebt controlSaveDebt;

    public DialogControlSaveDebt(MainNew.ControlSaveDebt controlSaveDebt){
       this.controlSaveDebt = controlSaveDebt;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        View view = inflater.inflate(R.layout.dialog_control_save_debt, null);
        view.findViewById(R.id.butYes).setOnClickListener(this);
        view.findViewById(R.id.butNo).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.butYes:
                controlSaveDebt.saveDebt();
                dismiss();
                break;
            case R.id.butNo:
                controlSaveDebt.notSaveDebt();
                dismiss();
                break;
        }
    }
}
