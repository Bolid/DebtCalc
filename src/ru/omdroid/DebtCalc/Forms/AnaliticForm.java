package ru.omdroid.DebtCalc.Forms;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.Parser.DescriptionCredits;
import ru.omdroid.DebtCalc.R;

import java.util.ArrayList;


public class AnaliticForm extends Activity{

    final String TAG = "com.CC.DebtCalc.Form.AnaliticForm";

    ArrayList<String> allResultAnalitic = new ArrayList<String>();

    int i = 0;

    boolean paramAdding = false;

    private EditText etSumCredit;
    private EditText etTermCredit;
    private EditText etPercend;
    private EditText etDopPlatej;

    private String notify = "Пожалуйста, введите следующие параметры для кредита:";


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analitic_form);

        Button butStart = (Button)findViewById(R.id.afbutStart);
        Button butAdd = (Button)findViewById(R.id.afbutAdd);

        etSumCredit = (EditText)findViewById(R.id.afedSumCr);
        etTermCredit = (EditText)findViewById(R.id.afetTermCredit);
        etPercend = (EditText)findViewById(R.id.afedPercend);
        etDopPlatej = (EditText)findViewById(R.id.afetDopPlatej);

        final DescriptionCredits descriptionCredits = new DescriptionCredits();

        butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!controlEdittext()){
                    descriptionCredits.setParamCredit(String.valueOf(i++), etSumCredit.getText().toString(), etTermCredit.getText().toString(), etPercend.getText().toString(), etDopPlatej.getText().toString());
                    paramAdding = true;
                }
                else
                    Toast.makeText(getBaseContext(), notify, Toast.LENGTH_LONG).show();
            }
        });

        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int listIndex = 0;
                if (!controlEdittext()){
                    for (int j = 0; j < i; j++){
                        ArrayList<String> inData = new ArrayList<String>(descriptionCredits.getParamCredit(String.valueOf(j)));
                        Arithmetic arithmetic = new Arithmetic(Double.valueOf(inData.get(0)), Double.valueOf(inData.get(2)), Integer.valueOf(inData.get(1)));
                        allResultAnalitic.add(listIndex++, "Кредит");
                        allResultAnalitic.add(listIndex++, String.valueOf(j+1));
                        allResultAnalitic.add(listIndex++, "_______");
                        allResultAnalitic.add(listIndex++, "");
                        allResultAnalitic.add(listIndex++, "Сумма");
                        allResultAnalitic.add(listIndex++, arithmetic.allResult.get(1)); //исходные данные - СУММА КРЕДИТА
                    allResultAnalitic.add(listIndex++, "Срок");
                    allResultAnalitic.add(listIndex++, arithmetic.allResult.get(9)); //исходные данные - СРОК КРЕДИТА
                    allResultAnalitic.add(listIndex++, "Процент");
                    allResultAnalitic.add(listIndex++, arithmetic.allResult.get(3)); //исходные данные - ПРОЦЕНТ КРЕДИТА
                    allResultAnalitic.add(listIndex++, "Доп. платеж");
                    allResultAnalitic.add(listIndex++, arithmetic.allResult.get(0)); //исходные данные - ДОП. ПЛАТЕЖ
                    allResultAnalitic.add(listIndex++, "Платеж");
                    allResultAnalitic.add(listIndex++, arithmetic.allResult.get(7)); //результат - ПЛАТЕЖ
                    allResultAnalitic.add(listIndex++, "Переплата");
                    allResultAnalitic.add(listIndex++, arithmetic.allResult.get(8)); //результат - ПЕРЕПЛАТА
                    allResultAnalitic.add(listIndex++, "_______");
                    allResultAnalitic.add(listIndex++, "");
                }

                Intent intentAnaliticFormView = new Intent(getBaseContext(), AnaliticFormView.class);
                intentAnaliticFormView.putExtra(TAG, allResultAnalitic);
                intentAnaliticFormView.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentAnaliticFormView);
                allResultAnalitic.clear();
                Log.d(TAG, "Данные: " + descriptionCredits.getParamCredit(String.valueOf(0)));
                }
                else
                    Toast.makeText(getBaseContext(), notify, Toast.LENGTH_LONG).show();
            }
        });

    }

    private Boolean controlEdittext(){
        notify = "Пожалуйста, введите следующие параметры для кредита:";
        if (etSumCredit.getText().toString().equals("") || etSumCredit.getText().toString().equals("0"))
            notify = notify + "\nСумму кредита.";
        if (etTermCredit.getText().toString().equals("") || etTermCredit.getText().toString().equals("0"))
            notify = notify + "\nСрок кредита.";
        if (etPercend.getText().toString().equals("") || etPercend.getText().toString().equals("0"))
            notify = notify + "\nПроцентную ставку.";
        if (notify.length() > 55)
            return true;
        return false;
    }
}
