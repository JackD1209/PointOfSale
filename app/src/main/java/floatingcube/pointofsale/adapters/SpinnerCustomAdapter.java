package floatingcube.pointofsale.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.activities.StaticFunctions;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.CategoryModel;
import floatingcube.pointofsale.models.InventoryHistoryModel;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.SupplierModel;


public class SpinnerCustomAdapter extends BaseAdapter {
    String[] itemList;
    long productId;
    Context context;
    String type;
    EditText supplierCodeEdit;
    EditText costPriceEdit;

    public SpinnerCustomAdapter(Context context, long productId, String type) {
        this.context = context;
        this.productId = productId;
        this.type = type;

        if (type.equals("Category")) {
            itemList = getFormattedCategoryItemList(productId);
        } else { // Supplier
            itemList = getFormattedSupplierItemList(productId);
        }
    }

    public SpinnerCustomAdapter(Context context, long productId, String type, EditText supplierCodeEdit, EditText costPriceEdit) {
        this.context = context;
        this.productId = productId;
        this.type = type;
        this.supplierCodeEdit = supplierCodeEdit;
        this.costPriceEdit = costPriceEdit;

        if (type.equals("Category")) {
            itemList = getFormattedCategoryItemList(productId);
        } else { // Supplier
            itemList = getFormattedSupplierItemList(productId);
        }
    }

    String[] getFormattedCategoryItemList(long productId){
        ProductModel product = PointOfSaleDAO.getProductById(productId);
        String[] items = new String[0];

        if (PointOfSaleDAO.getCategory() != null) {
            String category = PointOfSaleDAO.getCategory().category;
            if (!category.contains(",")) {
                items = new String[]{"", category};
            } else {
                items = category.split(",");
            }
        }

        // bring current activeSupplier to first position, so when we edit other things,
        // this product's current active Supplier will always selected by default.
        items = StaticFunctions.reArrangeStringArray(items, product.category);

        return items;
    }

    String[] getFormattedSupplierItemList(long productId){
        List<SupplierModel> supplierList = PointOfSaleDAO.getSupplierListThatBelongToProduct(productId);
        ProductModel product = PointOfSaleDAO.getProductById(productId);

        // create an array of supplier name
        String[] items = new String[supplierList.size()];
        int count = 0;
        for (SupplierModel supplier: supplierList) {
            items[count] = supplier.name;
            count++;
        }

        // bring current activeSupplier to first position, so when we edit other things,
        // this product's current active Supplier will always selected by default.
        items = StaticFunctions.reArrangeStringArray(items, product.activeSupplier);
        return items;
    }

    @Override
    public int getCount() {
        return itemList.length + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_dropdown_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.spinner_item);
        if ((position == 1 || itemList.length == 0 ) && type.equals("Category")) {

            item.setBackgroundResource(R.color.cardview_shadow_start_color);
            item.setText("+ Add Category");
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Add New Category");

                    // Set up the input
                    final EditText categoryEditText = new EditText(context);
                    dialog.setView(categoryEditText);

                    // Set up the buttons
                    dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newCategory = categoryEditText.getText().toString();

                            // get existing category list
                            CategoryModel model = PointOfSaleDAO.getCategory();
                            if (model == null) {
                                model = new CategoryModel();
                                model.category = newCategory;
                            } else {
                                model.category = model.category + "," + newCategory;
                            }

                            if (model.category.charAt(0) == ',') {
                                model.category = model.category.substring(1);
                            }

                            model.save();

                            notifyDataSetChanged();
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            });
        } else if ((position == 1 || itemList.length == 0 ) && type.equals("Supplier")) {
            item.setText("+ Add Supplier");
            item.setBackgroundResource(R.color.cardview_shadow_start_color);
            item.setOnClickListener(new View.OnClickListener() {
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

                                            notifyDataSetChanged();
                                            supplierCodeEdit.setText(supplier.code);
                                            costPriceEdit.setText(String.valueOf(supplier.costPrice));

                                            // if true, it means this is the first time supplier is added
                                            // before it, there are no supplier, we count it as an product edit too
                                            // so we will save inventory history as well
                                            if (currentSupplier == null || currentSupplier.equals("")) {
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
        } else if (position == 0) {
            item.setText(itemList[position]);
        } else {
            item.setText(itemList[position - 1]);
        }

        return convertView;
    }

}