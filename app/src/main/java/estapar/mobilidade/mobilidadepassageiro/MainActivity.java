package estapar.mobilidade.mobilidadepassageiro;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    //Timer da splash screen
    private static int SPLASH_TIME_OUT = 2000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //    getSupportActionBar().hide();

        //obs:  esse codigo para a página seguinte !!!!!!!!


        new Handler ().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal

                Intent i = new Intent(MainActivity.this, Login .class);
                startActivity(i);

                // Fecha esta activity

                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}