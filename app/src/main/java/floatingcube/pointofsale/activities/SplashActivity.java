package floatingcube.pointofsale.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import floatingcube.pointofsale.R;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    Intent mainActivity = new Intent("android.intent.action.SELL_SCREEN");
                    startActivity(mainActivity);
                }
            }
        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}