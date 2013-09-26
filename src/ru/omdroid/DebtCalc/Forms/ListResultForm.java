package ru.omdroid.DebtCalc.Forms;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ru.omdroid.DebtCalc.Adapter.AdapterViewListResult;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.DialogPayment;
import ru.omdroid.DebtCalc.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListResultForm extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_result_form);

    }

    @Override
    public void onResume(){
        super.onResume();

        new AsyncTask<Void, Void, ArrayList>() {
            AdapterViewListResult simpleAdapter;
            ListView listView;
            ArrayList <HashMap<String, String>> listResult = Arithmetic.listResult;

            @Override
            protected ArrayList doInBackground(Void... voids) {
                if (ResultForm.paymentUpdate){
                    ResultForm.arithmetic.getOverpaymentAllMonth(ResultForm.newPayment, ResultForm.overPayment);
                    listResult = Arithmetic.listResult;
                    ResultForm.paymentUpdate = false;
                }
                return Arithmetic.listResult;
            }

            protected void onPostExecute(final ArrayList element){
                Log.v("Сработал onResume","");
                listView = (ListView)findViewById(R.id.list);
                String[] from = new String[]{"Number", "Payment", "Image", "Dolg", "Delta"};
                int to[] = new int[]{R.id.tv1, R.id.tv2, R.id.iv1, R.id.tv3, R.id.tv4};
                simpleAdapter = new AdapterViewListResult(getBaseContext(), element, R.layout.list_content, from, to, getResources().getDrawable(R.drawable.arrow), getFragmentManager(), listView);
                listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                     try {
                         DialogFragment dialogFragment =  new DialogPayment(listView, getBaseContext(), Arithmetic.listDefaultPayment.get(position), position + 1);
                         dialogFragment.show(getFragmentManager(), "dlg1");
                     }catch (IndexOutOfBoundsException ioobe)
                     {

                     }

                    }
                });
            }
        }.execute();
    }
}
