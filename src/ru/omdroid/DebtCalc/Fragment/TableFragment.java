package ru.omdroid.DebtCalc.Fragment;


import android.app.DialogFragment;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ru.omdroid.DebtCalc.*;
import ru.omdroid.DebtCalc.Adapter.AdapterViewListResult;

import java.util.ArrayList;
import java.util.HashMap;

public class TableFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.list_result_form, null);
        new AsyncTask<Void, Void, ArrayList>() {
            AdapterViewListResult simpleAdapter;
            ListView listView;
            ArrayList <HashMap<String, String>> listResult = Arithmetic.listResult;

            @Override
            protected ArrayList doInBackground(Void... voids) {
                if (ResultFragment.paymentUpdate){
                    ResultFragment.arithmetic.getOverpaymentAllMonth(ResultFragment.newPayment, ResultFragment.overPayment);
                    listResult = Arithmetic.listResult;
                    ResultFragment.paymentUpdate = false;
                }
                else {
                    if (!ErrorMessage.nullSumCredit.equals("") || !ErrorMessage.nullPercentCredit.equals("") || !ErrorMessage.nullTermCredit.equals(""))
                        Toast.makeText(getActivity().getBaseContext(), ErrorMessage.errorTitle + ErrorMessage.nullSumCredit + ErrorMessage.nullPercentCredit + ErrorMessage.nullTermCredit, Toast.LENGTH_LONG).show();
                    Arithmetic arithmetic = new Arithmetic(Double.valueOf(AppData.param[0]), Integer.valueOf(AppData.param[1]), Double.valueOf(AppData.param[2]));
                    arithmetic.getOverpaymentAllMonth(Double.valueOf(Arithmetic.allResult.get(4)), false);
                    listResult = Arithmetic.listResult;
                }
                return Arithmetic.listResult;
            }

            protected void onPostExecute(final ArrayList element){
                Log.v("Сработал onResume", "");
                listView = (ListView)getActivity().findViewById(R.id.list);
                String[] from = new String[]{"Number", "Payment", "Image", "Dolg", "Delta"};
                int to[] = new int[]{R.id.tv1, R.id.tv2, R.id.iv1, R.id.tv3, R.id.tv4};
                simpleAdapter = new AdapterViewListResult(getActivity().getBaseContext(), element, R.layout.list_content, from, to, getResources().getDrawable(R.drawable.arrow), getFragmentManager(), listView);
                listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        try {
                            DialogFragment dialogFragment =  new DialogPayment(listView, getActivity().getBaseContext(), Arithmetic.listDefaultPayment.get(position), position + 1);
                            dialogFragment.show(getFragmentManager(), "dlg1");
                        }catch (IndexOutOfBoundsException ioobe)
                        {

                        }

                    }
                });
            }
        }.execute();

        return v;

    }
}