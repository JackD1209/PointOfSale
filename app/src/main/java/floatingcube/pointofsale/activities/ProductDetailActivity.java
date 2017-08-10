package floatingcube.pointofsale.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.InventoryHistoryAdapter;
import floatingcube.pointofsale.adapters.ProductDetailSupplierAdapter;
import floatingcube.pointofsale.adapters.PromotionAdapter;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.CategoryModel;
import floatingcube.pointofsale.models.InventoryHistoryModel;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.PromotionModel;
import floatingcube.pointofsale.models.SchedulePriceModel;
import floatingcube.pointofsale.models.SupplierModel;


public class ProductDetailActivity extends AppCompatActivity {
    Context context = this;

    TextView productNameText;
    TextView productBarcodeText;
    TextView categoryText;
    TextView sellingPriceText;
    TextView coldPriceText;
    TextView activeSupllierText;
    TextView supplierCodeText;
    TextView costPriceText;
    TextView inventoryText;

    ProductDetailSupplierAdapter supplierAdapter;
    InventoryHistoryAdapter inventoryHistoryAdapter;
    PromotionAdapter promotionAdapter;

    ListView supplierListView;
    ListView inventoryHistoryListView;
    ListView promotionListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ActiveAndroid.initialize(this);

        // Showing product info here
        productNameText = (TextView) findViewById(R.id.product_details_name);
        productBarcodeText = (TextView) findViewById(R.id.product_details_code);
        categoryText = (TextView) findViewById(R.id.product_details_category);
        sellingPriceText = (TextView) findViewById(R.id.product_details_sellingprice);
        coldPriceText = (TextView) findViewById(R.id.product_details_coldprice);
        activeSupllierText = (TextView) findViewById(R.id.product_details_activesupplier);
        supplierCodeText = (TextView) findViewById(R.id.product_details_suppliercode);
        costPriceText = (TextView) findViewById(R.id.product_details_costprice);
        inventoryText = (TextView) findViewById(R.id.product_details_inventory);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long id = extras.getLong("productId");
            ProductModel model = PointOfSaleDAO.getProductById(id);

            productNameText.setText(model.productName);
            productBarcodeText.setText(model.barcode);
            categoryText.setText(model.category);
            sellingPriceText.setText(String.valueOf(model.sellingPrice));
            coldPriceText.setText(String.valueOf(model.coldPrice));

            activeSupllierText.setText(model.activeSupplier);
            supplierCodeText.setText(model.supplierCode);

            costPriceText.setText(String.valueOf(model.costPrice));
            inventoryText.setText(String.valueOf(model.inventory));

            // set up all listview here
            long productId = model.getId();
            setUpSupplierListView(productId);
            setUpInventoryHistoryListView(productId);
            setUpPromotionListView(productId);

            // set up all button here
            setUpDeleteButton(model);
            setUpEditButton(productId);
            setUpDeleteCategoryButton();
            setUpAddSupplierButton(productId, activeSupllierText, supplierCodeText);

            // set up schedule price table
            View view = findViewById(android.R.id.content);
            setUpSchedulePriceTable(productId, view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_detail_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        context.startActivity(new Intent(context, ProductActivity.class));
        return true;
    }

    void setUpDeleteButton(final ProductModel model) {
        Button deleteButton = (Button) findViewById(R.id.product_details_deleteBtn);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Are you sure ?");

                // Set up the buttons
                dialog.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.delete();

                        // also, delete the Supplier models that belong to this products

                        List<SupplierModel> supplierList = PointOfSaleDAO.getSupplierListThatBelongToProduct(model.getId());

                        for (SupplierModel supplier : supplierList) {
                            supplier.delete();
                        }

                        Intent intent = new Intent(context, ProductActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });
    }

    void setUpEditButton(final long productId) {
        Button editButton = (Button) findViewById(R.id.product_details_editBtn);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddEditProductActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });
    }

    void setUpSupplierListView(long productId) {
        supplierAdapter = new ProductDetailSupplierAdapter(context, productId);
        supplierListView = (ListView) findViewById(R.id.product_details_suppliers_listview);
        supplierListView.setAdapter(supplierAdapter);
        StaticFunctions.setListViewHeightBasedOnChildren(supplierListView);
    }

    void setUpInventoryHistoryListView(long productId) {
        inventoryHistoryAdapter = new InventoryHistoryAdapter(context, productId);
        inventoryHistoryListView = (ListView) findViewById(R.id.product_details_iventoryhistory_listview);
        inventoryHistoryListView.setAdapter(inventoryHistoryAdapter);
        StaticFunctions.setListViewHeightBasedOnChildren(inventoryHistoryListView);
    }

    void setUpPromotionListView(long productId) {
        promotionAdapter = new PromotionAdapter(context, productId);
        promotionListView = (ListView) findViewById(R.id.product_details_promotions_listview);
        promotionListView.setAdapter(promotionAdapter);
        StaticFunctions.setListViewHeightBasedOnChildren(promotionListView);
    }

    void setUpDeleteCategoryButton(){
        Button removeButton = (Button) findViewById(R.id.product_details_delete_category);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call a dialog
                LayoutInflater li = LayoutInflater.from(context);
                final View dialogXML = li.inflate(R.layout.delete_category_dialog, null);

                // Create custom dialog object
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setView(dialogXML);

                // get list of category
                String category = PointOfSaleDAO.getCategory().category;
                String[] items = new String[0];
                // Catch error - but it may never happen, because i caught this bug at the very beginning
                if (category != null) {
                    items = category.split(",");
                }

                final TextView chosenCategoryText = (TextView) dialogXML.findViewById(R.id.chosen_category_display);
                Button leftButton = (Button) dialogXML.findViewById(R.id.go_left_button);
                Button rightButton = (Button) dialogXML.findViewById(R.id.go_right_button);

                final List<String> itemsList = new LinkedList<>(Arrays.asList(items));
                if (items.length == 0) {
                    chosenCategoryText.setText("Nothing To Delete");
                } else {
                    chosenCategoryText.setText(items[0]);
                }

                // click here, for chosenCategorytext to display the previous element in the list
                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String item = chosenCategoryText.getText().toString();
                        int currentPos = itemsList.indexOf(item);

                        if (currentPos > 0) {
                            chosenCategoryText.setText(itemsList.get(currentPos - 1));
                        }
                    }
                });

                // click here, for chosenCategorytext to display the next element in the list
                rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String item = chosenCategoryText.getText().toString();
                        int currentPos = itemsList.indexOf(item);

                        if (currentPos < itemsList.size() - 1) {
                            chosenCategoryText.setText(itemsList.get(currentPos + 1));
                        }
                    }
                });

                // Set dialog title
                dialog.setTitle("Choose Category to Delete");
                dialog
                        .setCancelable(false)
                        .setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String deleteTarget = chosenCategoryText.getText().toString();

                                        if (deleteTarget.equals("Nothing To Delete")) {
                                            // Do nothing here
                                        } else {
                                            CategoryModel category = PointOfSaleDAO.getCategory();
                                            String[] categoryArray = category.category.split(",");
                                            // convert array into list, because list have more function to use
                                            List<String> categoryList = new LinkedList<>(Arrays.asList(categoryArray));
                                            categoryList.remove(deleteTarget);

                                            // catch no category error.
                                            if (categoryList.size() == 0) {
                                                category.category = "";
                                                category.save();
                                            } else {
                                                String newCategory = "";

                                                for (String item: categoryList) {
                                                    newCategory += "," + item;
                                                }

                                                // trim the newCategory
                                                if (newCategory.charAt(0) == ',') {
                                                    newCategory = newCategory.substring(1);
                                                }
                                                category.category = newCategory;
                                                category.save();
                                            }
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
        });
    }

    void setUpAddSupplierButton(final long productId, final TextView activeSupllierText, final TextView supplierCodeText){
        Button addbutton = (Button) findViewById(R.id.product_details_add_supplier);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call a dialog
                LayoutInflater li = LayoutInflater.from(context);
                final View dialogXML = li.inflate(R.layout.add_supplier_dialog, null);

                // Create custom dialog object
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setView(dialogXML);

                final EditText nameInput = (EditText) dialogXML.findViewById(R.id.supplier_name_input);
                final EditText codeInput = (EditText) dialogXML.findViewById(R.id.supplier_code_input);
                final EditText costPriceInput = (EditText) dialogXML.findViewById(R.id.supplier_cost_price_input);
                final EditText infoInput = (EditText) dialogXML.findViewById(R.id.supplier_info_input);
                final CheckBox checkBox = (CheckBox) dialogXML.findViewById(R.id.add_supplier_checkbox);

                // Set dialog title
                dialog.setTitle("Add Supplier");
                dialog
                        .setCancelable(false)
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SupplierModel supplier = new SupplierModel();

                                        supplier.name = nameInput.getText().toString();
                                        supplier.code = codeInput.getText().toString();
                                        supplier.info = infoInput.getText().toString();
                                        supplier.costPrice = StaticFunctions.wrongInputPolice(costPriceInput.getText().toString());
                                        supplier.productId = productId;
                                        supplier.lastUpdated = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

                                        if (checkBox.isChecked()) {
                                            supplier.applyGST = 1;
                                        } else {
                                            supplier.applyGST = 0;
                                        }

                                        // also update product model, set this newly created supplier to be active supplier
                                        ProductModel product = PointOfSaleDAO.getProductById(productId);
                                        // for later checking
                                        String currentSupplier = product.activeSupplier;

                                        product.activeSupplier = supplier.name;
                                        product.supplierCode = supplier.code;

                                        supplier.save();
                                        product.save();

                                        supplierAdapter.notifyDataSetChanged();
                                        StaticFunctions.setListViewHeightBasedOnChildren(supplierListView);
                                        activeSupllierText.setText(supplier.name);
                                        supplierCodeText.setText(supplier.code);

                                        // if true, it means this is the first time supplier is added
                                        // before it, there are no supplier, we count it as an product edit too
                                        // so we will save inventory history as well
                                        if (currentSupplier.equals("")) {
                                            // add Inventory history
                                            InventoryHistoryModel inventoryHistory = new InventoryHistoryModel();

                                            inventoryHistory.supplier = product.activeSupplier;
                                            inventoryHistory.remark = product.activeSupplier;
                                            inventoryHistory.cost = product.costPrice;
                                            inventoryHistory.quantity = product.inventory;
                                            inventoryHistory.profitMargin = "";
                                            inventoryHistory.addedBy = "";
                                            inventoryHistory.applyGST = supplier.applyGST;

                                            inventoryHistory.updatedOn = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                                            inventoryHistory.productId = product.getId();

                                            inventoryHistory.save();
                                            inventoryHistoryAdapter.notifyDataSetChanged();
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
        });


    }

    static void setUpSchedulePriceTable(long productId, View view){
        SchedulePriceModel schedulePrice = PointOfSaleDAO.getSchedulePriceThatBelongToProduct(productId);

        if (schedulePrice != null) {
            TextView sellingCurrent = (TextView) view.findViewById(R.id.product_details_scheduledprice_selling_current);
            TextView sellingNew = (TextView) view.findViewById(R.id.product_details_scheduledprice_selling_new);
            TextView sellingStartDate = (TextView) view.findViewById(R.id.product_details_scheduledprice_selling_startdate);
            TextView sellingCreatedBy = (TextView) view.findViewById(R.id.product_details_scheduledprice_selling_createdby);

            TextView coldCurrent = (TextView) view.findViewById(R.id.product_details_scheduledprice_cold_current);
            TextView coldNew = (TextView) view.findViewById(R.id.product_details_scheduledprice_cold_new);
            TextView coldStartDate = (TextView) view.findViewById(R.id.product_details_scheduledprice_cold_startdate);
            TextView coldCreatedBy = (TextView) view.findViewById(R.id.product_details_scheduledprice_cold_createdby);

            // fill data in
            sellingCurrent.setText(String.valueOf(schedulePrice.currentSellingPrice));
            sellingNew.setText(String.valueOf(schedulePrice.newSellingPrice));
            sellingStartDate.setText(schedulePrice.sellingStartDate);
            sellingCreatedBy.setText(schedulePrice.sellingCreatedBy);

            coldCurrent.setText(String.valueOf(schedulePrice.currentColdPrice));
            coldNew.setText(String.valueOf(schedulePrice.newColdPrice));
            coldStartDate.setText(schedulePrice.coldStartDate);
            coldCreatedBy.setText(schedulePrice.coldCreatedBy);
        }
    }
}