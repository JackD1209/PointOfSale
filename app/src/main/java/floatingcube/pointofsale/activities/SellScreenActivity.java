package floatingcube.pointofsale.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.SellScreenAdapter;
import floatingcube.pointofsale.adapters.SellScreenAdapter1;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.CategoryModel;
import floatingcube.pointofsale.models.CustomerModel;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.PromotionModel;
import floatingcube.pointofsale.models.SaleHistoryModel;

public class SellScreenActivity extends AppCompatActivity {
    SellScreenAdapter sellScreenAdapter;
    SellScreenAdapter1 sellScreenAdapter1; // listview for selected items

    // display total money and pay button
    TextView subTotalAmountTextview;
    TextView totalAmountTextview;
    SharedPreferences get_preferences,preferences;
    SharedPreferences.Editor editor;
    public static int level = 1;  // when we click to a grid Item(level 1), it open a new gridItem list(level 2)
    public static String categorySelected = "";
    public static List<ProductModel> selectedItems = new ArrayList<>();  // data for listView
    public static List<ProductModel> searcheditems = new ArrayList<>();  // searched data for listView
    public static boolean isSearchOn = false;
    public static int discountAmount = 0;
    public static int taxAmount = 0;
    Context context = this;
    String product_name="";
    String selling_price="";
    String product_name0="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sell Screen");

        // hide keyboard when activity open
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ActiveAndroid.initialize(this);
        preferences=PreferenceManager.getDefaultSharedPreferences(this);
        get_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LinearLayout layout = (LinearLayout)findViewById(R.id.io_layout);
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.left_layout);
        TextView hint = (TextView) findViewById(R.id.hint);
        hint.setGravity(Gravity.CENTER);
        if(get_preferences.getBoolean("checked_4",false)==true){
            layout.setVisibility(View.GONE);
            layout2.setVisibility(View.INVISIBLE);
            hint.setVisibility(View.VISIBLE);
        }
        else if(get_preferences.getBoolean("checked_4",true)==false){
            layout.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.VISIBLE);
            hint.setVisibility(View.GONE);
        }

        // get all textView that display amount of money
        subTotalAmountTextview = (TextView) findViewById(R.id.amount_of_sub_total);
        totalAmountTextview = (TextView) findViewById(R.id.amount_of_total);
        Button payButton = (Button) findViewById(R.id.pay_button);

        // this is a textView that show name of item we just click
        TextView menuNameTextview = (TextView) findViewById(R.id.selectedMenuName_textView);
        setUpBackButtonListener(menuNameTextview);

        // set up and display adapter
        sellScreenAdapter1 = new SellScreenAdapter1(this, subTotalAmountTextview, totalAmountTextview, payButton);
        ListView listView = (ListView) findViewById(R.id.sale_transactions_listview);
        listView.setAdapter(sellScreenAdapter1);

        // pass sellScreenAdapter1 into sellScreenAdapter, so when item in sellScreenAdapter touched
        // it will update the sellScreenAdapter1 adapter
        TextView backTextView = (TextView) findViewById(R.id.back_textView);
        sellScreenAdapter = new SellScreenAdapter(this, sellScreenAdapter1,
                subTotalAmountTextview, totalAmountTextview, payButton, menuNameTextview, backTextView);
        GridView gridView = (GridView) findViewById(R.id.sellscreen_gridview);
        gridView.setAdapter(sellScreenAdapter);

        setUpPayButton();
        setUpSearchProductBar(sellScreenAdapter);
        setUpAddDiscountAndTax(subTotalAmountTextview, totalAmountTextview, payButton);
        updatePayAmount(subTotalAmountTextview, totalAmountTextview, payButton);

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

    public void setUpAddDiscountAndTax(final TextView subTotalAmount, final TextView totalAmount, final Button payButton){
        final TextView addDiscount = (TextView) findViewById(R.id.sell_screen_add_discount);
        final TextView addTax = (TextView) findViewById(R.id.sell_screen_add_tax);

        final TextView displayDiscount = (TextView) findViewById(R.id.sell_screen_display_discount);
        final TextView displayTax = (TextView) findViewById(R.id.sell_screen_display_tax);

        addDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SellScreenActivity.this);

                final EditText discountInput = new EditText(SellScreenActivity.this);
                discountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                alert.setTitle("Add Discount In Percentage");
                alert.setView(discountInput);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        discountAmount = Math.round(StaticFunctions.wrongInputPolice(discountInput.getText().toString()));
                        if (discountAmount < 0) { // Means user input wrong number
                            discountAmount = 0;
                        }
                        updatePayAmount(subTotalAmount, totalAmount, payButton);
                        displayDiscount.setText(String.valueOf(discountAmount) + "%");
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });

        addTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SellScreenActivity.this);

                final EditText taxInput = new EditText(SellScreenActivity.this);
                taxInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                alert.setTitle("Add tax In Percentage");
                alert.setView(taxInput);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        taxAmount = Math.round(StaticFunctions.wrongInputPolice(taxInput.getText().toString()));
                        if (taxAmount < 0) { // Means user input wrong number
                            taxAmount = 0;
                        }
                        updatePayAmount(subTotalAmount, totalAmount, payButton);
                        displayTax.setText(String.valueOf(taxAmount) + "%");
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
            }
        });
    }

    public static void updatePayAmount(TextView subTotalAmountTextview, TextView totalAmountTextview, Button payButton) {
        float totalPrice = 0;

        for (ProductModel product : selectedItems) {
            totalPrice += product.sellingPrice;
        }

        float discount = totalPrice * SellScreenActivity.discountAmount / 100;
        float tax = totalPrice * SellScreenActivity.taxAmount / 100;

        totalPrice -= discount;
        totalPrice += tax;

        String totalAmount = String.valueOf(totalPrice);
        subTotalAmountTextview.setText(totalAmount);
        totalAmountTextview.setText(totalAmount);

        String pay = " Pay                                        " + totalAmount;
        payButton.setText(pay);
    }

    private void setUpBackButtonListener(final TextView selectedMenuNameText) {
        final TextView backTextView = (TextView) findViewById(R.id.back_textView);
        backTextView.setVisibility(View.INVISIBLE);
        if (level == 2) {
            backTextView.setVisibility(View.VISIBLE);
        }

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backTextView.setVisibility(View.INVISIBLE);

                selectedMenuNameText.setText("Category");
                level = 1;
                sellScreenAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setUpPayButton() {
        final Button payButton = (Button) findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.size() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Wait !!!");
                    alertDialog.setMessage("You have not select anything to pay for.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    LayoutInflater li = LayoutInflater.from(context);
                    final View dialogXML = li.inflate(R.layout.sell_screen_add_note_dialog, null);

                    // Create custom dialog object
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setView(dialogXML);

                    dialog.setTitle("Anything You want to add ?");

                    final EditText noteInput = (EditText) dialogXML.findViewById(R.id.dialog_note_input);
                    final EditText userInput = (EditText) dialogXML.findViewById(R.id.dialog_note_input);

                    // set up customer spinner
                    final Spinner customerSpinner = (Spinner) dialogXML.findViewById(R.id.dialog_customer_spinner);

                    List<CustomerModel> customerModelList = PointOfSaleDAO.getAllCustomer();
                    String[] items = new String[customerModelList.size() + 1];

                    if (customerModelList.size() == 0) { // means there are no customers to display
                        items = new String[]{"No Customer"};
                    } else {
                        int count = 1;
                        items[0] = "";
                        for (CustomerModel customer: customerModelList) {
                            items[count] = customer.customerName;
                            count++;
                        }
                    }

                    ArrayAdapter<String> customerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
                    customerSpinner.setAdapter(customerAdapter);

                    final ArrayList<String> name_product = new ArrayList<String>();
                    final ArrayList<String> price_product = new ArrayList<String>();
                    dialog
                            .setCancelable(false)
                            .setPositiveButton("Pay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            float totalPrice = 0;
                                            for (ProductModel product : selectedItems) {
                                                totalPrice += product.sellingPrice;
                                                name_product.add(product.productName);
                                                price_product.add(String.valueOf(product.sellingPrice));
                                            }
                                            // get current time
                                            Calendar c = Calendar.getInstance();

                                            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                                            String formattedDate = dateFormatter.format(c.getTime());

                                            SaleHistoryModel saleHistory = new SaleHistoryModel();

                                            saleHistory.total = totalPrice;
                                            saleHistory.date = formattedDate;
                                            saleHistory.dateInMillis = c.getTimeInMillis();
                                            saleHistory.status = "Closed";
                                            saleHistory.note = noteInput.getText().toString();
                                            saleHistory.userName = userInput.getText().toString();
                                            // get customer from spinner
                                            String customer = customerSpinner.getSelectedItem().toString();
                                            if (customer.equals("No Customer")) {
                                                saleHistory.customer = "";
                                            } else {
                                                saleHistory.customer = customer;
                                            }
                                            saleHistory.save();
                                            String price = String.valueOf(totalPrice);
                                            editor = preferences.edit();
                                            String floating = "FLOATING CUBE POS";
                                            int count =0;
                                            int count_float =32-11-price.length();
                                            String tab_float ="";
                                            String blank="";
                                            for(int i =0;i<count_float;i++){
                                                tab_float+="\t";
                                            }
                                            for (int i =0;i<32;i++){
                                                blank+="-";
                                            }
                                            for (int i=0;i<name_product.size();i++){
                                                selling_price = price_product.get(i);
                                                product_name0 = name_product.get(i);
                                                count = 32-(product_name0.length()+selling_price.length());

                                                String tab = "";
                                                for(int k =0;k<count;k++){
                                                    tab+="\t";
                                                }
                                                product_name+=name_product.get(i)+tab+price_product.get(i);

                                            }
                                            editor.putString("printer","       "+floating+"\n"+blank+product_name+blank+"Total Price"+tab_float+price);
                                            editor.apply();
                                            editor.putString("display", "Total Price\n"+price);
                                            editor.apply();
                                            // reduce items inventory respectively with the number of products bought
                                            for(ProductModel product: selectedItems) {
                                                product.inventory -= 1;
                                                product.save();
                                            }

                                            // refresh views
                                            selectedItems.clear();
                                            sellScreenAdapter1.notifyDataSetChanged();

                                            // After pay, reset the payAmount display, also return discount and tax to 0%

                                            TextView displayDiscount = (TextView) findViewById(R.id.sell_screen_display_discount);
                                            TextView displayTax = (TextView) findViewById(R.id.sell_screen_display_tax);

                                            displayDiscount.setText("");
                                            displayTax.setText("");
                                            SellScreenActivity.discountAmount = 0;
                                            SellScreenActivity.taxAmount = 0;

                                            subTotalAmountTextview.setText("");
                                            totalAmountTextview.setText("");
                                            payButton.setText("Pay");

                                            updatePayAmount(subTotalAmountTextview, totalAmountTextview, payButton);

                                            //return to sale screen after sell
                                            if(get_preferences.getBoolean("checked_return_sale",false)==true){
                                                LinearLayout menuBar = (LinearLayout) findViewById(R.id.menu_name_bar);
                                                menuBar.setVisibility(View.INVISIBLE);
                                                level = 1;
                                                sellScreenAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = dialog.create();

                    alertDialog.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                    alertDialog.show();
                }
            }
        });
    }

    void setUpSearchProductBar(final SellScreenAdapter adapter){
        final EditText searchInput = (EditText) findViewById(R.id.sell_screen_search_edtitext);
        searchInput.addTextChangedListener(new SearchBarListener(adapter));

        Button cancelButton = (Button) findViewById(R.id.sell_screen_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level = 1;
                isSearchOn = false;
                adapter.notifyDataSetChanged();
                searchInput.setText("");
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }
}

class SearchBarListener implements TextWatcher {
    SellScreenAdapter adapter;

    public SearchBarListener(SellScreenAdapter adapter){
        this.adapter = adapter;
    }

    public void afterTextChanged(Editable s) {

    }
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().equals("")) {
            SellScreenActivity.isSearchOn = false;
            adapter.notifyDataSetChanged();
        } else {
            SellScreenActivity.isSearchOn = true;
            SellScreenActivity.searcheditems = PointOfSaleDAO.searchProductWithKeyword(s.toString());
            adapter.notifyDataSetChanged();
        }
    }
}