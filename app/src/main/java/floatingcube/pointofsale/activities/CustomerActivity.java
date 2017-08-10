package floatingcube.pointofsale.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.CustomerAdapter;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.CustomerModel;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class CustomerActivity extends AppCompatActivity {
    CustomerAdapter customerAdapter;
    final Context context = this;
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        // set up adapter and listView
        ActiveAndroid.initialize(this);

        // all the data will be get inside adapter itself. So we dont need to prepare its data here
        customerAdapter = new CustomerAdapter(context);
        ListView listView = (ListView) findViewById(R.id.customer_list);
        listView.setAdapter(customerAdapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Customer");

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

        Button addBtn = (Button) findViewById(R.id.AddButton_Customer);
        assert addBtn != null;
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                createInputDialog(CustomerActivity.this, "Add Customer", "add", -1, customerAdapter);
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

    // parameter action will be either add or edit
    // if it is add, customerId will be -1
    public static void createInputDialog(Context context, String title, final String action, final long customerId, final CustomerAdapter adapter){
        // call a dialog
        LayoutInflater li = LayoutInflater.from(context);
        // Include dialog.xml file
        final View promptsView = li.inflate(R.layout.customer_list_add_dialog, null);
        // Create custom dialog object

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setView(promptsView);

        final EditText CustomerName = (EditText) promptsView.findViewById(R.id.customer_name_input);
        final EditText Email = (EditText) promptsView.findViewById(R.id.email_input);

        if (customerId != -1) {
            CustomerModel existCustomer = CustomerModel.load(CustomerModel.class, customerId);
            CustomerName.setText(existCustomer.customerName);
            Email.setText(existCustomer.customerEmail);
        } else {
            CustomerName.setText("");
            Email.setText("");
        }

        // Set dialog title
        dialog.setTitle(title);

        dialog
                .setCancelable(false)
                .setPositiveButton("SUBMIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result

                                String CustomerNametext = CustomerName.getText().toString();
                                String CustomerEmailtext = Email.getText().toString();

                                if (action.equals("edit")) {
                                    CustomerModel existCustomer = CustomerModel.load(CustomerModel.class, customerId);
                                    existCustomer.customerName = CustomerNametext;
                                    existCustomer.customerEmail = CustomerEmailtext;
                                    existCustomer.save();
                                } else { // add
                                    CustomerModel newCustomer = new CustomerModel();
                                    newCustomer.customerName = CustomerNametext;
                                    newCustomer.customerEmail = CustomerEmailtext;
                                    newCustomer.save();
                                }

                                // reload listview to show new data
                                adapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
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