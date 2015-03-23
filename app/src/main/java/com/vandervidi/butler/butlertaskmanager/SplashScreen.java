package com.vandervidi.butler.butlertaskmanager;


import com.vandervidi.butler.butlertaskmanager.MainActivity;
import com.vandervidi.butler.butlertaskmanager.R;
import com.vandervidi.butler.butlertaskmanager.SplashScreen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/**
 * Created by Gal on 23/03/2015.
 */
public class SplashScreen extends Activity{

    /**
     * The thread to process splash screen events
     */
    private Thread mSplashThread;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Splash screen view
        setContentView(R.layout.activity_splash);

        final SplashScreen sPlashScreen = this;

        // The thread to wait for splash screen events
        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        // Wait given period of time or exit on touch
                        wait(3000);
                    }
                }
                catch(InterruptedException ex){
                }



                // Run next activity

                Intent intent = new Intent();
                intent.setClass(sPlashScreen, MainActivity.class);
                startActivity(intent);

                finish();
            }
        };

        mSplashThread.start();
    }
}
