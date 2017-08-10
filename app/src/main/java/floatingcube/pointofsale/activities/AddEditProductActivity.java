package floatingcube.pointofsale.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.Calendar;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.adapters.InventoryHistoryAdapter;
import floatingcube.pointofsale.adapters.ProductDetailSupplierAdapter;
import floatingcube.pointofsale.adapters.PromotionAdapter;
import floatingcube.pointofsale.adapters.SpinnerCustomAdapter;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.InventoryHistoryModel;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.PromotionModel;
import floatingcube.pointofsale.models.SchedulePriceModel;
import floatingcube.pointofsale.models.SupplierModel;


public class AddEditProductActivity extends AppCompatActivity {

    Context context = this;

    Spinner categorySpinner;
    Spinner supplierSpinner;
    EditText productNameEdit;
    EditText productBarcodeEdit;
    EditText sellingPriceEdit;
    EditText coldPriceEdit;
    EditText supplierCodeEdit;
    EditText costPriceEdit;
    EditText inventoryEdit;

    ListView supplierListView;
    ListView inventoryHistoryListView;
    ListView promotionListView;

    ProductDetailSupplierAdapter supplierAdapter;
    InventoryHistoryAdapter inventoryHistoryAdapter;
    PromotionAdapter promotionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_edit);

        ActiveAndroid.initialize(this);

        // get data passed by ProductActivity, and fill it into edittext
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // get component from xml file
            productNameEdit = (EditText) findViewById(R.id.product_details_name_edit);
            productBarcodeEdit = (EditText) findViewById(R.id.product_details_code_edit);
            sellingPriceEdit = (EditText) findViewById(R.id.product_details_sellingprice_edit);
            coldPriceEdit = (EditText) findViewById(R.id.product_details_coldprice_edit);
            supplierCodeEdit = (EditText) findViewById(R.id.product_details_suppliercode_edit);
            costPriceEdit = (EditText) findViewById(R.id.product_details_costprice_edit);
            inventoryEdit = (EditText) findViewById(R.id.product_details_inventory_edit);

            Boolean isAddAction = extras.getBoolean("action");  // if not add, it will be edit

            if (isAddAction) { // when add
                long productId = extras.getLong("productId");
                ProductModel product = PointOfSaleDAO.getProductById(productId);

//                 set up Dropdown list of category
                categorySpinner = (Spinner) findViewById(R.id.product_details_category_spinner_edit);
                SpinnerCustomAdapter categoryAdapter = new SpinnerCustomAdapter(this, productId, "Category");
                categorySpinner.setAdapter(categoryAdapter);
//                 set up Dropdown list for active supplier

                supplierSpinner = (Spinner) findViewById(R.id.product_details_activesupplier_spinner);
                SpinnerCustomAdapter supplierCategory = new SpinnerCustomAdapter(this, productId, "Supplier", supplierCodeEdit, costPriceEdit);
                supplierSpinner.setAdapter(supplierCategory);

                supplierCodeEdit.setFocusable(false);

                // fill data into textview
                productNameEdit.setText(product.productName);
                productBarcodeEdit.setText(product.barcode);

                setUpAddEditButton(isAddAction, productId);
                setUpCancelButton(isAddAction, productId);
            } else { // when Edit  ->>>> get current product then show information
                // get product model
                long id = extras.getLong("productId");
                ProductModel model = PointOfSaleDAO.getProductById(id);

                // then fill information into edittexts

                productNameEdit.setText(model.productName);
                productBarcodeEdit.setText(model.barcode);
                sellingPriceEdit.setText(String.valueOf(model.sellingPrice));
                coldPriceEdit.setText(String.valueOf(model.coldPrice));

                // auto updated when supplier updated
                supplierCodeEdit.setText(model.supplierCode);
                supplierCodeEdit.setFocusable(false);

                costPriceEdit.setText(String.valueOf(model.costPrice));
                inventoryEdit.setText(String.valueOf(model.inventory));

                // set up Dropdown list of category
                categorySpinner = (Spinner) findViewById(R.id.product_details_category_spinner_edit);
                SpinnerCustomAdapter categoryAdapter = new SpinnerCustomAdapter(this, id, "Category");
                categorySpinner.setAdapter(categoryAdapter);

                // set up Dropdown list for active supplier

                supplierSpinner = (Spinner) findViewById(R.id.product_details_activesupplier_spinner);
                SpinnerCustomAdapter supplierCategory = new SpinnerCustomAdapter(this, id, "Supplier", supplierCodeEdit, costPriceEdit);
                supplierSpinner.setAdapter(supplierCategory);

                // Set up button
                setUpCancelButton(isAddAction, id);
                setUpAddEditButton(isAddAction, id);
                setUpAddPromotionButton(id);
                setUpAddSchedulePriceButton(id, findViewById(android.R.id.content));
                setUpSchedulePriceEditButton(id, findViewById(android.R.id.content));

                // Set up listview
                setUpSupplierListView(id);
                setUpInventoryHistoryListView(id);
                setUpPromotionListView(id);

                // set up schedule price table
                ProductDetailActivity.setUpSchedulePriceTable(id, findViewById(android.R.id.content));
            }

            View current = getCurrentFocus();
            if (current != null) current.clearFocus();
        }
    }


    // if the action is ADD, go back to product activity
    // if              EDIT, go back to prodcut details activity
    void setUpAddEditButton(final boolean isAddAction, final long productId) {
        Button addEditButton = (Button) findViewById(R.id.product_details_edit_add_edit_button);

        addEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a product model, put all data inside.. save, then go back to product activity

                if (isAddAction) {  // when add
                    ProductModel model = PointOfSaleDAO.getProductById(productId);

                    model.productName = productNameEdit.getText().toString();
                    model.barcode = productBarcodeEdit.getText().toString();

                    // because of the new spinner structure (have a button inside)
                    // we don't just simply use .getSelectedItem().toString() like before
                    int selectedPos = categorySpinner.getFirstVisiblePosition();
                    model.category = StaticFunctions.getSelectedStringInCategorySpinner(selectedPos, model.category);

                    model.sellingPrice = StaticFunctions.wrongInputPolice(sellingPriceEdit.getText().toString());
                    model.coldPrice = StaticFunctions.wrongInputPolice(coldPriceEdit.getText().toString());
                    model.activeSupplier = "";
                    model.supplierCode = "";
                    model.costPrice = StaticFunctions.wrongInputPolice(costPriceEdit.getText().toString());
                    model.inventory = Math.round(StaticFunctions.wrongInputPolice(inventoryEdit.getText().toString()));

                    model.save();

                    Intent intent = new Intent(context, ProductActivity.class);
                    startActivity(intent);
                } else { // edit action
                    // get editting product model, then update data into it
                    ProductModel product = PointOfSaleDAO.getProductById(productId);

                    product.productName = productNameEdit.getText().toString();
                    product.barcode = productBarcodeEdit.getText().toString();

                    // because of the new spinner structure (have a button inside)
                    // we don't just simply use .getSelectedItem().toString() like before
                    int selectedPos = categorySpinner.getFirstVisiblePosition();
                    product.category = StaticFunctions.getSelectedStringInCategorySpinner(selectedPos, product.category);

                    product.sellingPrice = StaticFunctions.wrongInputPolice(sellingPriceEdit.getText().toString());

                    product.coldPrice = StaticFunctions.wrongInputPolice(coldPriceEdit.getText().toString());

                    // when supplier added, supplierCode will be automatically updated
                    selectedPos = supplierSpinner.getFirstVisiblePosition();
                    String supplierName = StaticFunctions.getSelectedStringInSupplierSpinner(selectedPos, product.activeSupplier, productId);
                    product.activeSupplier = supplierName;

                    SupplierModel supplier = PointOfSaleDAO.getSupplierByName(supplierName);
                    if (supplier != null) {
                        product.supplierCode = supplier.code;
                    }
                    product.costPrice = StaticFunctions.wrongInputPolice(costPriceEdit.getText().toString());

                    product.inventory = Math.round(StaticFunctions.wrongInputPolice(inventoryEdit.getText().toString()));
                    product.save();

                    // Update Inventory history
                    InventoryHistoryModel inventoryHistory = new InventoryHistoryModel();

                    inventoryHistory.supplier = product.activeSupplier;
                    inventoryHistory.remark = product.activeSupplier;
                    inventoryHistory.cost = product.costPrice;
                    inventoryHistory.quantity = product.inventory;
                    inventoryHistory.profitMargin = "";
                    inventoryHistory.addedBy = "";

//                     get supplier GST
                    if (supplier != null) {
                        inventoryHistory.applyGST = supplier.applyGST;
                    }

                    inventoryHistory.updatedOn = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
                    inventoryHistory.productId = productId;

                    inventoryHistory.save();

                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("productId", productId);
                    startActivity(intent);
                }
            }
        });
    }

    void setUpCancelButton(final boolean isAddAction, final long productId) {
        Button cancelButton = (Button) findViewById(R.id.product_details_edit_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAddAction) { // if add, go back to product activity
                    // also, delete the product created half way
                    ProductModel product = PointOfSaleDAO.getProductById(productId);
                    product.delete();

                    Intent intent = new Intent(context, ProductActivity.class);
                    startActivity(intent);
                } else { // edit, go back to product details
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("productId", productId);

                    context.startActivity(intent);
                }
            }
        });
    }

    void setUpAddPromotionButton(final long productId){
        Button addPromotionButton = (Button) findViewById(R.id.edit_product_details_add_promotions);
        addPromotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call a dialog
                LayoutInflater li = LayoutInflater.from(context);
                final View dialogXML = li.inflate(R.layout.add_promotion_dialog, null);

                // Create custom dialog object
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setView(dialogXML);

                final EditText nameInput = (EditText) dialogXML.findViewById(R.id.promotion_name_input);
                final EditText typeInput = (EditText) dialogXML.findViewById(R.id.promotion_type_input);
                final EditText startDateInput = (EditText) dialogXML.findViewById(R.id.promotion_start_date);
                final EditText endDateInput = (EditText) dialogXML.findViewById(R.id.promotion_end_date);

                // Set dialog title
                dialog.setTitle("Add Promotion");
                dialog
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        PromotionModel promotion = new PromotionModel();

                                        promotion.name = nameInput.getText().toString();
                                        promotion.type = typeInput.getText().toString();
                                        promotion.startDate = startDateInput.getText().toString();
                                        promotion.endDate = endDateInput.getText().toString();
                                        promotion.productId = productId;
                                        promotion.createdBy = "";

                                        promotion.save();
                                        promotionAdapter.notifyDataSetChanged();
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

    void setUpAddSchedulePriceButton(final long productId, final View mainView) {
        Button addSchedulePriceButton = (Button) findViewById(R.id.edit_product_details_add_scheduleprice);

        addSchedulePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call a dialog
                LayoutInflater li = LayoutInflater.from(context);
                final View dialogView = li.inflate(R.layout.add_edit_schedule_price_dialog, null);

                // Create custom dialog object
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setView(dialogView);

                // fill in existed data
                final ProductModel product = PointOfSaleDAO.getProductById(productId);

                TextView currentSellingText = (TextView) dialogView.findViewById(R.id.current_selling_price_text);
                TextView currentColdText = (TextView) dialogView.findViewById(R.id.current_cold_price_text);

                currentSellingText.setText(String.valueOf(product.sellingPrice));
                currentColdText.setText(String.valueOf(product.coldPrice));

                // get Edittexts
                final EditText newSellingPriceInput = (EditText) dialogView.findViewById(R.id.new_selling_price_input);
                final EditText newColdPriceInput = (EditText) dialogView.findViewById(R.id.new_cold_price_input);
                final EditText startOnInput = (EditText) dialogView.findViewById(R.id.schedule_price_start_on_input);

                dialog.setTitle("Add Schedule Price");
                dialog
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SchedulePriceModel model = PointOfSaleDAO.getSchedulePriceThatBelongToProduct(productId);

                                        if (model == null) {
                                            model = new SchedulePriceModel();
                                        }

                                        String newSellingPrice = newSellingPriceInput.getText().toString();
                                        String newColdPrice = newColdPriceInput.getText().toString();

                                        if (!newSellingPrice.equals("")) {
                                            model.currentSellingPrice = product.sellingPrice;
                                            model.newSellingPrice = Float.valueOf(newSellingPrice);
                                            model.sellingStartDate = startOnInput.getText().toString();
                                            model.sellingCreatedBy = "";
                                        }

                                        if (!newColdPriceInput.getText().toString().equals("")) {
                                            model.currentColdPrice = product.coldPrice;
                                            model.newColdPrice = Float.valueOf(newColdPrice);
                                            model.coldStartDate = startOnInput.getText().toString();
                                            model.coldCreatedBy = "";
                                        }

                                        model.productId = productId;
                                        model.save();

                                        ProductDetailActivity.setUpSchedulePriceTable(productId, mainView);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                alertDialog.show();
            }
        });
    }

    void setUpSchedulePriceEditButton(final long productId, final View mainView){
        Button editButton = (Button) findViewById(R.id.schedule_price_edit_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call a dialog
                LayoutInflater li = LayoutInflater.from(context);
                final View dialogView = li.inflate(R.layout.add_edit_schedule_price_dialog, null);

                // Create custom dialog object
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setView(dialogView);

                // fill in existed data
                final ProductModel product = PointOfSaleDAO.getProductById(productId);

                TextView currentSellingText = (TextView) dialogView.findViewById(R.id.current_selling_price_text);
                TextView currentColdText = (TextView) dialogView.findViewById(R.id.current_cold_price_text);

                currentSellingText.setText(String.valueOf(product.sellingPrice));
                currentColdText.setText(String.valueOf(product.coldPrice));


                SchedulePriceModel schedulePrice = PointOfSaleDAO.getSchedulePriceThatBelongToProduct(productId);

                // get Edittexts
                final EditText newSellingPriceInput = (EditText) dialogView.findViewById(R.id.new_selling_price_input);
                final EditText newColdPriceInput = (EditText) dialogView.findViewById(R.id.new_cold_price_input);
                final EditText startOnInput = (EditText) dialogView.findViewById(R.id.schedule_price_start_on_input);

                newSellingPriceInput.setText(String.valueOf(schedulePrice.newSellingPrice));
                newColdPriceInput.setText(String.valueOf(schedulePrice.newColdPrice));
                if (!schedulePrice.sellingStartDate.equals("")) {
                    startOnInput.setText(schedulePrice.sellingStartDate);
                }
                if (!schedulePrice.coldStartDate.equals("")) {
                    startOnInput.setText(schedulePrice.coldStartDate);
                }

                dialog.setTitle("Edit Schedule Price");
                dialog
                        .setCancelable(false)
                        .setPositiveButton("Update",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SchedulePriceModel model = PointOfSaleDAO.getSchedulePriceThatBelongToProduct(productId);

                                        if (model == null) {
                                            model = new SchedulePriceModel();
                                        }

                                        String newSellingPrice = newSellingPriceInput.getText().toString();
                                        String newColdPrice = newColdPriceInput.getText().toString();

                                        if (!newSellingPrice.equals("")) {
                                            model.currentSellingPrice = product.sellingPrice;
                                            model.newSellingPrice = Float.valueOf(newSellingPrice);
                                            model.sellingStartDate = startOnInput.getText().toString();
                                            model.sellingCreatedBy = "";
                                        }

                                        if (!newColdPriceInput.getText().toString().equals("")) {
                                            model.currentColdPrice = product.coldPrice;
                                            model.newColdPrice = Float.valueOf(newColdPrice);
                                            model.coldStartDate = startOnInput.getText().toString();
                                            model.coldCreatedBy = "";
                                        }

                                        model.productId = productId;
                                        model.save();

                                        ProductDetailActivity.setUpSchedulePriceTable(productId, mainView);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                alertDialog.show();
            }
        });

    }

    void setUpSupplierListView(long productId) {
        supplierAdapter = new ProductDetailSupplierAdapter(context, productId, "EditActivity");
        supplierListView = (ListView) findViewById(R.id.product_details_suppliers_listview);
        supplierListView.setAdapter(supplierAdapter);
        StaticFunctions.setListViewHeightBasedOnChildren(supplierListView);
    }

    void setUpInventoryHistoryListView(long productId) {
        inventoryHistoryAdapter = new InventoryHistoryAdapter(context, productId, "EditActivity");
        inventoryHistoryListView = (ListView) findViewById(R.id.product_details_iventoryhistory_listview);
        inventoryHistoryListView.setAdapter(inventoryHistoryAdapter);
        StaticFunctions.setListViewHeightBasedOnChildren(inventoryHistoryListView);
    }

    void setUpPromotionListView(long productId) {
        promotionAdapter = new PromotionAdapter(context, productId, true);
        promotionListView = (ListView) findViewById(R.id.product_details_promotions_listview);
        promotionListView.setAdapter(promotionAdapter);
        StaticFunctions.setListViewHeightBasedOnChildren(promotionListView);
    }


}
