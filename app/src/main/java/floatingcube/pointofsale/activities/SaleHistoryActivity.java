package floatingcube.pointofsale.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.SaleHistoryAdapter;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class SaleHistoryActivity extends AppCompatActivity {
    public static String filterBy = "";
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sale History");
        }

        // set up adapter
        final SaleHistoryAdapter adapter = new SaleHistoryAdapter(this);
        ListView listView = (ListView) findViewById(R.id.sale_history_listview);
        listView.setAdapter(adapter);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_history2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterBy = spinner.getSelectedItem().toString();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // Set up Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Intent intent;

                if (id == R.id.nav_sale_screen) {
                    intent = new Intent("android.intent.action.SELL_SCREEN");
                } else if (id == R.id.nav_sale_history) {
                    intent = new Intent("android.intent.action.SALE_HISTORY");
                } else if (id == R.id.nav_close_register) {
                    intent = new Intent("android.intent.action.CLOSE_REGISTER");
                } else if (id == R.id.nav_products) {
                    intent = new Intent("android.intent.action.PRODUCT");
                } else if (id == R.id.nav_customers) {
                    intent = new Intent("android.intent.action.CUSTOMER");
                } else if (id == R.id.nav_quick_menu) {
                    intent = new Intent("android.intent.action.QUICK_MENU");
                } else if (id == R.id.nav_settings) {
                    intent = new Intent("android.intent.action.SETTING");
                } else {
                    intent = new Intent("android.intent.action.STOCK_CONTROL");
                }

                startActivity(intent);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //Return after 10 second if no touch
        LinearLayout all_layout =(LinearLayout)findViewById(R.id.all_layout);
        SharedPreferences get_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), SellScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };
        if(get_preferences.getBoolean("checked_return_10",false)==true) {
           countDownTimer.start();
            all_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    countDownTimer.cancel();
                    countDownTimer.start();
                    return false;
                }
            });
        }
        //Keep unclock while idle
        if(get_preferences.getBoolean("checked_unlocked",false)==true){
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else  this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }
}
