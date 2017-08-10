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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.ProductAdapter;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.SupplierModel;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class ProductActivity extends AppCompatActivity {
    public static List<ProductModel> searchedList = new ArrayList<>();
    public static boolean isSearchedOn = false; // used to tell adapter that use search data instead

    private ProductAdapter productAdapter;
    private LinearLayout searchBar;
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ActiveAndroid.initialize(this);

        //setting up listview and Adapter

        ListView productListView = (ListView) findViewById(R.id.product_listview);
        productAdapter = new ProductAdapter(this);
        productListView.setAdapter(productAdapter);

        // search bar will be visible when click search Button
        setUpSearchBar();

        // setting toolbar section, no Entry
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product");

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
        if(get_preferences.getBoolean("checked_unlocked",false)==true){
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else  this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);

        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_product:
                setUpAddProductButton(this, productAdapter);
                return true;

            case R.id.action_search_product:
                searchBar.setVisibility(View.VISIBLE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setUpSearchBar() {
        searchBar = (LinearLayout) findViewById(R.id.search_bar);

        final EditText productName = (EditText) findViewById(R.id.productName_search);
        final EditText productBarcode = (EditText) findViewById(R.id.productBarcode_search);
        final Spinner spinnerCategory = (Spinner) findViewById(R.id.category_spinner);

        String[] items = new String[0];
        if (PointOfSaleDAO.getCategory() != null) {
            String category = PointOfSaleDAO.getCategory().category;
            if (category.contains(",")) {
                items = category.split(",");
            } else {
                items = new String[]{category};
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerCategory.setAdapter(adapter);

        // set up listener for button

        Button search = (Button) findViewById(R.id.search_button);
        Button cancel = (Button) findViewById(R.id.cancel_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = productName.getText().toString();
                String barcode = productBarcode.getText().toString();
                String category = spinnerCategory.getSelectedItem().toString();

                searchedList = PointOfSaleDAO.SearchProduct(name, barcode, category);
                isSearchedOn = true;
                productAdapter.notifyDataSetChanged();

                InputMethodManager imm = (InputMethodManager) getSystemService(ProductActivity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        // when user click cancel, they also dismiss the search result
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setVisibility(View.GONE);
                isSearchedOn = false;
                searchedList = new ArrayList<>();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void setUpAddProductButton(final Context context, final ProductAdapter productAdapter) {
        // call a dialog
        LayoutInflater li = LayoutInflater.from(context);
        // Include dialog.xml file
        final View promptsView = li.inflate(R.layout.add_product_dialog, null);

        // Create custom dialog object

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setView(promptsView);

        final EditText productNameInput = (EditText) promptsView.findViewById(R.id.product_name_input);
        final EditText barcodeInput = (EditText) promptsView.findViewById(R.id.barcode_input);

        // Set dialog title
        dialog.setTitle("Add Product");
        dialog
                .setCancelable(false)
                .setPositiveButton("SUBMIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ProductModel product = new ProductModel();

                                product.productName = productNameInput.getText().toString();
                                product.barcode = barcodeInput.getText().toString();

                                product.save();

                                Intent intent = new Intent(ProductActivity.this, AddEditProductActivity.class);
                                intent.putExtra("action", true);  // mean add
                                intent.putExtra("productId", product.getId());
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
