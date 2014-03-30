package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.R;

public class FormAbout extends Activity {
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.form_about);
        ImageView imFace = (ImageView)findViewById(R.id.ivAboutFace);
        ImageView imTwi = (ImageView)findViewById(R.id.ivAboutTwi);
        ImageView imVK = (ImageView)findViewById(R.id.ivAboutVK);
        imFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Test");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        imTwi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");
                // Кому
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[] { "dmitriy.grigorev@gmail.com" });
                // Зачем
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test");
                // О чём
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Test");
                // С чем
                emailIntent.putExtra(
                        android.content.Intent.EXTRA_STREAM,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory()
                                + "/Photos/0142939.jpg"));

                emailIntent.setType("text/video");
                // Поехали!
                startActivity(Intent.createChooser(emailIntent,
                        "Отправка письма..."));
            }
        });

        imVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=ru.omdroid.DebtCalc"));
                startActivity(intent);
            }
        });
    }
}
