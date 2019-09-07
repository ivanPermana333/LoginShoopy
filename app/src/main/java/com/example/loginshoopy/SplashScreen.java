package com.example.loginshoopy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        imageView =(ImageView) findViewById(R.id.txtshopee);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.anim);
        imageView.setAnimation(myanim);
        final Intent i = new Intent (this, MainActivity.class);
        Thread time = new Thread(){
            public  void run(){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };
           time.start();

    }
}
