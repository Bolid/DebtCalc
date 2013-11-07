package ru.omdroid.DebtCalc.Dialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;

public class DialogRating extends DialogFragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        View view = inflater.inflate(R.layout.dialog_rating, null);
        view.findViewById(R.id.butRatYes).setOnClickListener(this);
        view.findViewById(R.id.butRatNo).setOnClickListener(this);
        view.findViewById(R.id.butRatYesFix).setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.butRatYes:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=ru.omdroid.DebtCalc"));
                startActivity(intent);
                dismiss();
                break;
            case R.id.butRatNo:
                dismiss();
                break;
            case R.id.butRatYesFix:
                WorkDB workDB = new WorkDB(getActivity().getBaseContext());
                workDB.updateData("UPDATE " + DebtCalcDB.TABLE_SETTING + " SET " + DebtCalcDB.F_RATING_SHOW_SET + " = '1'");
                workDB.disconnectDataBase();
                dismiss();
                break;
        }
    }
}
