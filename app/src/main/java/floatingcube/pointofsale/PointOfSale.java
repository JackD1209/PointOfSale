package floatingcube.pointofsale;

import android.app.Application;
import com.activeandroid.ActiveAndroid;

/**
 * Created by Steven Nguyen on 8/6/2016.
 */
public class PointOfSale extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
