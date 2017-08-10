package floatingcube.pointofsale.adapters;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import floatingcube.pointofsale.R;
import floatingcube.pointofsale.activities.StaticFunctions;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.InventoryHistoryModel;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.SupplierModel;


public class ProductDetailSupplierAdapter extends BaseAdapter {
    List<SupplierModel> supplierList;
    Context context;
    long productId;
    String type = "";

    public ProductDetailSupplierAdapter(Context context, long id) {
        this.context = context;
        this.productId = id;
    }

    public ProductDetailSupplierAdapter(Context context, long id, String type) {
        this.context = context;
        this.productId = id;
        this.type = type;
    }

    @Override
    public int getCount() {
        this.supplierList = PointOfSaleDAO.getSupplierListThatBelongToProduct(productId);
        return supplierList.size();
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
            convertView = inflater.inflate(R.layout.supplier_listview_item, null);
        }

        SupplierModel supplier = supplierList.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.name_variable);
        TextView info = (TextView) convertView.findViewById(R.id.info_variable);
        TextView cost = (TextView) convertView.findViewById(R.id.cost_variable);
        TextView lastUpdated = (TextView) convertView.findViewById(R.id.lastupdatedon_variable);
        TextView addedBy = (TextView) convertView.findViewById(R.id.addedby_variable);

        name.setText(supplier.name);
        info.setText(supplier.info);
        cost.setText(String.valueOf(supplier.costPrice));
        lastUpdated.setText(supplier.lastUpdated);
        addedBy.setText(supplier.addedBy);

        if (type.equals("EditActivity")) {  // if called from edit activity, there will be a edit button extra
            Button editButton = (Button) convertView.findViewById(R.id.edit_button);
            editButton.setVisibility(View.VISIBLE);

            final long supplierId = supplier.getId();

            editButton.setOnClickListener(new View.OnClickListener() {
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

                    // fill existed data in

                    SupplierModel supplier = PointOfSaleDAO.getSupplierById(supplierId);

                    nameInput.setText(supplier.name);
                    codeInput.setText(supplier.code);
                    costPriceInput.setText(String.valueOf(supplier.costPrice));
                    infoInput.setText(String.valueOf(supplier.info));

                    if (supplier.applyGST == 1) {
                        checkBox.setChecked(true);
                    }

                    // Set dialog title
                    dialog.setTitle("Edit Supplier");
                    dialog
                            .setCancelable(false)
                            .setPositiveButton("Update",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            SupplierModel supplier = PointOfSaleDAO.getSupplierById(supplierId);

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

                                            supplier.save();
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



