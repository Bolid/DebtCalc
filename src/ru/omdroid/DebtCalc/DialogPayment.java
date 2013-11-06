package ru.omdroid.DebtCalc;

import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import ru.omdroid.DebtCalc.Adapter.AdapterViewListResult;

import ru.omdroid.DebtCalc.Fragment.MainFragment;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddPayment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class DialogPayment extends DialogFragment implements OnClickListener {

    EditText textView;
    ImageView imageView;
    ListView listView;
    Context context;
    ProgressBar progressBar;
    Button button;

    String defaultPayment, updateValuePayment;
    int position;

    boolean updatePayment = false, listener = false;

    public DialogPayment(ListView listView, Context context, String defaultPayment, int position){
        this.listView = listView;
        this.context = context;
        this.defaultPayment = defaultPayment;
        this.position = position;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.list_dialog_label_title));
        View view = inflater.inflate(R.layout.dialog_form, null);
        view.findViewById(R.id.but).setOnClickListener(this);

        progressBar = (ProgressBar)view.findViewById(R.id.pBarDialog);
        SeekBar seekBar = (SeekBar)view.findViewById(R.id.sb1);
        button = (Button)view.findViewById(R.id.but);
        textView = (EditText) view.findViewById(R.id.valueDopPaymentMouth);
        imageView = (ImageView)view.findViewById(R.id.ivMarkerDefaulPayment);

        final NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
        final InControlFieldAddPayment inControlFieldAddPayment = new InControlFieldAddPayment(textView, formatAddPayment(defaultPayment), null, null);

        textView.setText(numberFormat.format(Double.valueOf(Arithmetic.listDefaultPayment.get(position))));
        updateValuePayment = Arithmetic.listDefaultPayment.get(position);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listener){
                    textView.addTextChangedListener(inControlFieldAddPayment);
                    listener = true;
                }
            }
        });
        textView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                updatePayment = true;
                updateValuePayment = String.valueOf(formatAddPayment(textView.getText().toString()));
                Log.v("Платеж: ", updateValuePayment);
                return false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (formatAddPayment(Arithmetic.listDefaultPayment.get(position)) > formatAddPayment(textView.getText().toString())){
                    button.setEnabled(false);
                    imageView.setImageResource(R.drawable.marker_red_addpayment);
                }
                else{
                    button.setEnabled(true);
                    imageView.setImageResource(R.drawable.marker_green_addpayment);
                }
                textView.removeTextChangedListener(inControlFieldAddPayment);
                listener = false;
                textView.setText(String.valueOf(numberFormat.format(formatAddPayment(updateValuePayment) + 1000 * i)));
                updatePayment = !numberFormat.format(Double.valueOf(Arithmetic.allResult.get(4))).equals(textView.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return view;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.but:
                new AsyncTask<Void, Void, ArrayList>() {
                    @Override
                    protected void onPreExecute(){
                       super.onPreExecute();
                        progressBar.setVisibility(View.VISIBLE);
                        button.setEnabled(false);
                    }

                    @Override
                    protected ArrayList doInBackground(Void... voids) {
                        if (updatePayment)
                            MainFragment.arithmetic.getOverpaymentSomeMonth(formatAddPayment(textView.getText().toString()), formatAddPayment(defaultPayment), position);
                        return Arithmetic.listResult;
                    }

                    protected void onPostExecute(ArrayList element){
                        String[] from = new String[]{"Number", "Payment", "Image", "Dolg", "Delta"};
                        int to[] = new int[]{R.id.tv1, R.id.tv2, R.id.iv1, R.id.tv3, R.id.tv4};
                        AdapterViewListResult simpleAdapter = new AdapterViewListResult(context, element, R.layout.list_content, from, to, getResources().getDrawable(R.drawable.arrow), getFragmentManager(), listView);
                        listView.setAdapter(simpleAdapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        dismiss();
                    }
                }.execute();
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

    private Double formatAddPayment(String addPayment){
        Log.v("Аргумент: ", addPayment);
        String newAddPayment = "";
        for (int j = 0; j < addPayment.length(); j++) {
            if ("1234567890".contains(String.valueOf(addPayment.charAt(j)))){
                newAddPayment = newAddPayment + addPayment.charAt(j);
            }
            else if (j == addPayment.length() - 3 || j == addPayment.length() - 2){
                newAddPayment = newAddPayment + ".";
            }
        }
        Log.v("Аргумент после: ", newAddPayment);
        if (newAddPayment.equals(""))
            return 0.00;
        else
            return Double.valueOf(newAddPayment);
    }
}
