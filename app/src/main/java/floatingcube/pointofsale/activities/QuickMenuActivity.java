package floatingcube.pointofsale.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.SimpleProductAdapter;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.QuickMenuModel;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class QuickMenuActivity extends AppCompatActivity {

    private ViewPager quickKeyPager;
    private PagerAdapter quickKeyPagerAdapter;
    private SearchView productSearchView;
    private ListView productListView;
    private TextView quickKeyGuide;
    private SimpleProductAdapter simpleProductAdapter;
    ArrayList<SimpleProduct> selectedProducts = new ArrayList<>();
    List<QuickMenuModel> savedQuickKeyList;
    private CirclePageIndicator pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quick Menu");
        SharedPreferences get_preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

        // Set up the pager to slide b/w 2 pages with 25 products on each
        quickKeyPager = (ViewPager) findViewById(R.id.vpQuickKeys);
        refreshPager();

        //Set up indicator
        pageIndicator = (CirclePageIndicator) findViewById(R.id.pagerIndicator);
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(7 * density);
        pageIndicator.setStrokeColor(Color.BLACK);
        pageIndicator.setFillColor(Color.BLACK);
        pageIndicator.setViewPager(quickKeyPager);

        // Set up the search view to search for the products
        // Note the if the products has already added to the quick key list, it will not be appeared on the searching
        productSearchView = (SearchView) findViewById(R.id.svProducts);
        productSearchView.setIconifiedByDefault(false);
        productSearchView.setQueryHint("Search Products");
        productSearchView.setFocusableInTouchMode(true);
        setupSearchView();

        // Set up selected filtered products in List view
        productListView = (ListView) findViewById(R.id.lvProducts);
        quickKeyGuide = (TextView) findViewById(R.id.tvQuickKeyGuide);

        simpleProductAdapter = new SimpleProductAdapter(this, selectedProducts);
        productListView.setAdapter(simpleProductAdapter);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
                //Toast.makeText(QuickMenuActivity.this, selectedProducts.get(i).ProductName, Toast.LENGTH_SHORT).show();
                final String productName = selectedProducts.get(index).ProductName;
                final String productId = selectedProducts.get(index).Id;

                LayoutInflater layoutInflater = LayoutInflater.from(QuickMenuActivity.this);
                final View addQuickKeyView = layoutInflater.inflate(R.layout.add_quick_key_dialog, null);

                TextView productNameTv = (TextView) addQuickKeyView.findViewById(R.id.tvProductName);
                TextView productIdTv = (TextView) addQuickKeyView.findViewById(R.id.tvProductId);

                productNameTv.setText(productNameTv.getText() + productName);
                productIdTv.setText(productIdTv.getText() + productId);

                // Prepare the dialog builder
                final AlertDialog.Builder builder = new AlertDialog.Builder(QuickMenuActivity.this);
                builder.setView(addQuickKeyView);
                builder.setTitle("Add Quick Key");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText addQuickKeyEt = (EditText) addQuickKeyView.findViewById(R.id.etQuickKey);
                        ProductModel product = PointOfSaleDAO.getProductById(Integer.valueOf(productId));
                        QuickMenuModel quickMenuModel = new QuickMenuModel(product, addQuickKeyEt.getText().toString());
                        quickMenuModel.save();

                        refreshPager();

                        Log.i("id = ", String.valueOf(index));
                        selectedProducts.remove(index);
                        simpleProductAdapter.notifyDataSetChanged();

                        // Clear focus on search view
                        productSearchView.clearFocus();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        // Clear focus on search view
                        productSearchView.clearFocus();
                    }
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        simpleProductAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(selectedProducts.size() != 0) {
                    productListView.setVisibility(View.VISIBLE);
                    quickKeyGuide.setVisibility(View.GONE);
                } else {
                    productListView.setVisibility(View.GONE);
                    quickKeyGuide.setVisibility(View.VISIBLE);
                }
            }
        });
        //keep unclock when idle
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

    private class PagerAdapter extends FragmentStatePagerAdapter{
        private List<QuickMenuFragment> fragments;

        public PagerAdapter(FragmentManager fm, List<QuickMenuFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        public void setPagerFragment(List<QuickMenuFragment> fragments) {
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public class SimpleProduct {
        public String Id;
        public String ProductName;

        public SimpleProduct(String id, String productName) {
            Id = id;
            ProductName = productName;
        }
    }

    private void refreshPager() {
        List<QuickMenuFragment> quickMenuFragmentList = new ArrayList<>();
        savedQuickKeyList = PointOfSaleDAO.getQuickKeys();

        // There are 2 pages in total
        int pages = 2;

        // There are 2 Quick Key list for each page
        List<QuickMenuModel> quickKeyListPage1 = new ArrayList<>();
        List<QuickMenuModel> quickKeyListPage2 = new ArrayList<>();

        // Each page will contains 25 items
        int itemCount = 25;

        if(savedQuickKeyList.size() <= 0) {
            for(int currentPage = 1; currentPage <= pages; currentPage++) {
                for(int i = 0; i < itemCount; i++) {
                    QuickMenuModel quickKey = new QuickMenuModel(null, "");
                    if(currentPage == 1) {
                        quickKeyListPage1.add(quickKey);
                    } else {
                        quickKeyListPage2.add(quickKey);
                    }
                }
            }

        } else {
            if(savedQuickKeyList.size() <= 25) {
                // Set up page 1
                for(int i = 0; i < itemCount; i++) {
                    QuickMenuModel quickKey;
                    if(i < savedQuickKeyList.size()) {
                        quickKey = savedQuickKeyList.get(i);
                    } else {
                        quickKey = new QuickMenuModel(null, "");
                    }
                    quickKeyListPage1.add(quickKey);
                }
                // Set up page 2
                for (int i = 0; i < itemCount; i++) {
                    quickKeyListPage2.add(new QuickMenuModel(null, ""));
                }
            } else {
                // Set up page 1
                for(int i = 0; i < itemCount; i++) {
                    quickKeyListPage1.add(savedQuickKeyList.get(i));
                }
                // Set up page 2
                for (int i = 0; i < itemCount; i++) {
                    QuickMenuModel quickKey;
                    if(i + 25 < savedQuickKeyList.size()) {
                        quickKey = savedQuickKeyList.get(i);
                    } else {
                        quickKey = new QuickMenuModel(null, "");
                    }
                    quickKeyListPage2.add(quickKey);
                }
            }
        }

        quickMenuFragmentList.add(new QuickMenuFragment(quickKeyListPage1, this));
        quickMenuFragmentList.add(new QuickMenuFragment(quickKeyListPage2, this));

        if(quickKeyPagerAdapter == null) {
            quickKeyPagerAdapter = new PagerAdapter(getSupportFragmentManager(), quickMenuFragmentList);
        } else {
            quickKeyPagerAdapter.setPagerFragment(quickMenuFragmentList);
        }
        quickKeyPager.setAdapter(quickKeyPagerAdapter);
        quickKeyPagerAdapter.notifyDataSetChanged();
    }

    public void removeQuickKey(final long id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Quick Key")
                .setMessage("Are you sure to delete quick key for product id = " + String.valueOf(id))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PointOfSaleDAO.deleteQuickKeyById(id);
                        refreshPager();
                        // Clear focus on search view
                        productSearchView.clearFocus();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        // Clear focus on search view
                        productSearchView.clearFocus();
                    }
                }).show();
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        productSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if(searchManager != null) {
            // Get the cursor
            Cursor cursor = getFilteredCursor("");
            String[] from = {"product"};
            int[] to = {android.R.id.text1};
            final CursorAdapter adapter = new SimpleCursorAdapter(QuickMenuActivity.this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
            productSearchView.setSuggestionsAdapter(adapter);

            productSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.setFilterQueryProvider(new FilterQueryProvider() {
                        @Override
                        public Cursor runQuery(CharSequence charSequence) {
                            return getFilteredCursor(charSequence.toString());
                        }
                    });
                    return false;
                }
            });

            productSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    Cursor cursor = adapter.getCursor();
                    if(cursor.moveToPosition(position)) {
                        String index = cursor.getString(0);
                        String productName = cursor.getString(1);

                        selectedProducts.add(new SimpleProduct(index, productName));
                        simpleProductAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });
        }
    }

    public Cursor getFilteredCursor(String query) {
        List<ProductModel> products = PointOfSaleDAO.getProductList();

        List<ProductModel> tempProducts = new ArrayList<>();
        if(query != "") {
            for(ProductModel product : products) {
                if(product.productName.toLowerCase().contains(query.toLowerCase())
                        && product.quickKeys().size() == 0 && !isProductExistedInSelectedList(product.getId()))
                    tempProducts.add(product);
            }
        } else {
            tempProducts = products;
        }

        String[] columnNames = {"_id","product"};
        MatrixCursor cursor = new MatrixCursor(columnNames);
        String[] temp = new String[2];
        for(ProductModel product : tempProducts){
            temp[0] = product.getId().toString();
            temp[1] = product.productName;
            cursor.addRow(temp);
        }
        return cursor;
    }

    public boolean isProductExistedInSelectedList(long id) {
        for(SimpleProduct product : selectedProducts) {
            if(product.Id == String.valueOf(id)) {
                return true;
            }
        }
        return false;
    }

}
