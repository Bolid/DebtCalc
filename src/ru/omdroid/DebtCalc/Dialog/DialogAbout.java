package ru.omdroid.DebtCalc.Dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.omdroid.DebtCalc.R;

public class DialogAbout  extends DialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.dialog_about_title));
        View view = inflater.inflate(R.layout.dialog_about, null);
        TextView tv1 = (TextView)view.findViewById(R.id.tv2DialogAbout);
        tv1.append(getResources().getString(R.string.app_version_major));
        view.findViewById(R.id.dAboutOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }
}
