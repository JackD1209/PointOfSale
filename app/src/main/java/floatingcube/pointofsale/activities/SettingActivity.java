package floatingcube.pointofsale.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import floatingcube.pointofsale.R;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    Button generalSettingButton;
    Button hardwareButton;
    Button exitButton;
    SharedPreferences get_preferences;
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setting");
        // Set up Navigation Drawer

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
//
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

        generalSettingButton = (Button) findViewById(R.id.btn_general_setting);
        hardwareButton = (Button) findViewById(R.id.btn_hardware);
        exitButton = (Button) findViewById(R.id.btn_exit);
        generalSettingButton.setOnClickListener(this);
        hardwareButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        //Return after 10 second if no touch
        LinearLayout all_layout =(LinearLayout)findViewById(R.id.all_layout);
        get_preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            countDownTimer.cancel();
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void onClick(View view) {

        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new Fragment();

        int id = view.getId();
        if (id == R.id.btn_general_setting) {
            fragment = new Fragment_2();
        }
        if (id == R.id.btn_hardware) {
            Intent intent= new Intent(Intent.ACTION_MAIN);;
            intent.setComponent(new ComponentName("com.chd.rs232lib","com.chd.rs232lib.MainActivity"));
            intent.putExtra("total_price",get_preferences.getString("printer",null));
            startActivity(intent);
//            PackageManager manager = getPackageManager();
//            try {
//                i = manager.getLaunchIntentForPackage("com.chd.rs232lib");
//                if (i == null)
//                    throw new PackageManager.NameNotFoundException();
//                i.addCategory(Intent.CATEGORY_LAUNCHER);
//                startActivity(i);
//
//
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
        }
        if(id == R.id.btn_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("EXIT");
            builder.setMessage("Do you want to Exit ?");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}

