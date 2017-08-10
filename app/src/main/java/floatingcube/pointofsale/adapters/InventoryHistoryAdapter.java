package floatingcube.pointofsale.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import floatingcube.pointofsale.R;
import floatingcube.pointofsale.activities.StaticFunctions;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.InventoryHistoryModel;
import floatingcube.pointofsale.models.SupplierModel;


public class InventoryHistoryAdapter extends BaseAdapter {
    List<InventoryHistoryModel> invetoryList;  // this list will store Customers list after we got them from Active Android
    long productId;
    Context context;
    String type = "";

    public InventoryHistoryAdapter(Context context,long productId) {
        this.context = context;
        this.productId = productId;
    }

    public InventoryHistoryAdapter(Context context,long productId, String type) {
        this.context = context;
        this.productId = productId;
        this.type = type;
    }

        @Override
    public int getCount() {
        this.invetoryList = PointOfSaleDAO.getInventoryHistoryListThatBelongToProduct(productId);
        return invetoryList.size();
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
            convertView = inflater.inflate(R.layout.inventory_history_listview_item, null);
        }

        InventoryHistoryModel inventory = invetoryList.get(position);

        TextView supplierText = (TextView) convertView.findViewById(R.id.inventory_supplier_text);
        TextView remarkText = (TextView) convertView.findViewById(R.id.inventory_remark_text);
        TextView costText = (TextView) convertView.findViewById(R.id.inventory_cost_text);
        TextView profitMarginText = (TextView) convertView.findViewById(R.id.inventory_profitmargin_text);
        TextView updatedOnText = (TextView) convertView.findViewById(R.id.inventory_updatedon_text);
        TextView quantityText = (TextView) convertView.findViewById(R.id.inventory_quantity_text);

        supplierText.setText(inventory.supplier);
        remarkText.setText(inventory.remark);
        costText.setText(String.valueOf(inventory.cost));
        profitMarginText.setText(inventory.profitMargin);
        updatedOnText.setText(inventory.updatedOn);
        quantityText.setText(String.valueOf(inventory.quantity));

        if (type.equals("EditActivity")) {
            Button editButton = (Button) convertView.findViewById(R.id.edit_button);
            editButton.setVisibility(View.VISIBLE);

            final long inventoryId = inventory.getId();

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // call a dialog
                    LayoutInflater li = LayoutInflater.from(context);
                    final View dialogXML = li.inflate(R.layout.edit_inventory_history_dialog, null);

                    // Create custom dialog object
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setView(dialogXML);

                    final EditText supplierInput = (EditText) dialogXML.findViewById(R.id.inventory_supplier_input);
                    final EditText dateInput = (EditText) dialogXML.findViewById(R.id.inventory_date_input);
                    final EditText quantityInput = (EditText) dialogXML.findViewById(R.id.inventory_quantity_input);
                    final EditText costPriceInput = (EditText) dialogXML.findViewById(R.id.inventory_costprice_input);
                    final CheckBox checkBox = (CheckBox) dialogXML.findViewById(R.id.inventory_checkbox);
                    final EditText remarkInput = (EditText) dialogXML.findViewById(R.id.inventory_remark_input);

                    InventoryHistoryModel inventory = PointOfSaleDAO.getInventoryHistoryById(inventoryId);

                    supplierInput.setText(inventory.supplier);
                    dateInput.setText(inventory.updatedOn);
                    quantityInput.setText(String.valueOf(inventory.quantity));
                    costPriceInput.setText(String.valueOf(inventory.cost));

                    if (inventory.applyGST == 1) {
                        checkBox.setChecked(true);
                    }
                    remarkInput.setText(inventory.remark);

                    // Set dialog title
                    dialog.setTitle("Edit Inventory History");
                    dialog
                            .setCancelable(false)
                            .setPositiveButton("Update",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            InventoryHistoryModel inventory = PointOfSaleDAO.getInventoryHistoryById(inventoryId);

                                            inventory.supplier = supplierInput.getText().toString();
                                            inventory.updatedOn = dateInput.getText().toString();
                                            inventory.quantity = Math.round(StaticFunctions.wrongInputPolice(quantityInput.getText().toString()));
                                            inventory.cost = StaticFunctions.wrongInputPolice(costPriceInput.getText().toString());
                                            inventory.remark = remarkInput.getText().toString();

                                            if (checkBox.isChecked()) {
                                                inventory.applyGST = 1;
                                            } else {
                                                inventory.applyGST = 0;
                                            }

                                            inventory.save();
                                            notifyDataSetChanged();
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

        return convertView;
    }

}