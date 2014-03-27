package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.Arithmetic.Arithmetic;
import ru.omdroid.DebtCalc.Arithmetic.ExactArithmetic;
import ru.omdroid.DebtCalc.CustomView.DataForGraph;
import ru.omdroid.DebtCalc.Listener.InControlFieldAddOverallPayment;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.TablePaymentNotSavedDebt;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class ResultForm extends Activity {
    static Double newPayment;
    public static Arithmetic arithmetic;
    private ExactArithmetic exactArithmetic;
    static boolean paymentUpdate;
    static boolean overPayment;
    boolean changeListener = false;

    AppData appData = new AppData();
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_result);
        final DataForGraph dataForGraph = new DataForGraph();
        dataForGraph.createOver(true);
        dataForGraph.createTerm(true);
        final NumberFormat numberFormat = new DecimalFormat("###,###,###,###,###,###,##0.00");
        final View view = findViewById(R.id.graphViewTermDebt);

        arithmetic = new Arithmetic(Double.valueOf(AppData.DEBT_BALANCE), AppData.PERCENT, AppData.TERM_BALANCE);
        exactArithmetic = new ExactArithmetic(AppData.PERCENT);

        final EditText etPayment = (EditText)findViewById(R.id.valuePayment);
        final InControlFieldAddOverallPayment inControlFieldAddPayment = new InControlFieldAddOverallPayment(etPayment, Double.valueOf(AppData.PAYMENT_DEFAULT), view, exactArithmetic);

        etPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!changeListener){
                    etPayment.addTextChangedListener(inControlFieldAddPayment);
                    changeListener = !changeListener;
                }
            }
        });

        paymentUpdate = true;
        overPayment = false;
        newPayment = Double.valueOf(Arithmetic.allResult.get(4));
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        etPayment.setText(numberFormat.format(newPayment));
        seekBar.setMax(AppData.TERM_BALANCE);
        dataForGraph.setOver(arithmetic.getDeltaDefault(newPayment, AppData.TERM_BALANCE));
        dataForGraph.setSum(Double.valueOf(AppData.DEBT_BALANCE));
        dataForGraph.setNewTerm(AppData.TERM_BALANCE);
        dataForGraph.setOldTerm(AppData.TERM_BALANCE);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    WorkDateDebt workDateDebt = new WorkDateDebt();
                    long date = workDateDebt.createNextDatePayment(AppData.DATE_PAY, AppData.DATE_DEBT_START);

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        etPayment.removeTextChangedListener(inControlFieldAddPayment);
                        changeListener = !changeListener;
                        overPayment = i != 0;
                        if (i == seekBar.getMax())
                            i--;
                        newPayment = arithmetic.getPayment(Double.valueOf(AppData.DEBT_BALANCE), AppData.TERM_BALANCE - i);
                        etPayment.setText(numberFormat.format(newPayment));

                        Arithmetic.allResult.set(6, String.valueOf(AppData.TERM_BALANCE - i));

                        appData.setPayment(String.valueOf(newPayment), AppData.PAYMENT_DEFAULT);


                        dataForGraph.setOver(arithmetic.getDeltaDefault(newPayment, AppData.TERM_BALANCE - i));
                        dataForGraph.setSum(Double.valueOf(AppData.DEBT_BALANCE));
                        dataForGraph.setNewTerm(AppData.TERM_BALANCE - i);
                        dataForGraph.setOldTerm(AppData.TERM_BALANCE);

                        view.invalidate();
                        paymentUpdate = true;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Log.d("ResultForm", "------------------Форма результата------------------");
                        dataForGraph.setOver(exactArithmetic.getOverpaymentAllMonth(Double.valueOf(AppData.DEBT_BALANCE), newPayment, AppData.TERM_BALANCE, date, 0));

                        Log.d("ResultForm", "==================Форма результата==================");
                        dataForGraph.setSum(Double.valueOf(AppData.DEBT_BALANCE));
                        dataForGraph.setNewTerm(Integer.valueOf(exactArithmetic.getTotalTerm()));
                        dataForGraph.setOldTerm(AppData.TERM_BALANCE);
                        view.invalidate();
                        paymentUpdate = true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater actionMenu = getMenuInflater();
        actionMenu.inflate(R.menu.di_action_menu, menu);
        menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.popup:
                View v = findViewById(R.id.popup);
                showPopupMenu(v);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopupMenu(View v){
        final PopupMenu pMenu = new PopupMenu(getBaseContext(), v);
        MenuInflater mInflater = pMenu.getMenuInflater();
        mInflater.inflate(R.menu.pm_main_form, pMenu.getMenu());
        pMenu.getMenu().getItem(1).setVisible(false);
        pMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(getBaseContext(), TablePaymentNotSavedDebt.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });
        pMenu.show();
    }

}

