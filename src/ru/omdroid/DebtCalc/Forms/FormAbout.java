package ru.omdroid.DebtCalc.Forms;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import ru.omdroid.DebtCalc.R;

public class FormAbout extends Activity {
    public void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.form_about);
        ImageView imMail = (ImageView)findViewById(R.id.ivAboutMail);
        ImageView imRating = (ImageView)findViewById(R.id.ivAboutRating);
        ImageView imShare = (ImageView)findViewById(R.id.ivAboutShare);
        imMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[]{"dmitriy.grigorev@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Кредитный калькулятор v." + getResources().getString(R.string.app_version_major) + "." + getResources().getString(R.string.app_version_minor));
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Здравствуйте!\n\n");

                emailIntent.setType("text/video");
                startActivity(Intent.createChooser(emailIntent,
                        "Отправка письма..."));
            }
        });

        imRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=ru.omdroid.DebtCalc"));
                startActivity(intent);
            }
        });

        imShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Test");
                startActivity(Intent.createChooser(sharingIntent, "Рекомендовать"));
            }
        });
    }
}
