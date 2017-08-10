package floatingcube.pointofsale.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import floatingcube.pointofsale.activities.ProductActivity;
import floatingcube.pointofsale.activities.ProductDetailActivity;
import floatingcube.pointofsale.activities.StaticFunctions;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.InventoryHistoryModel;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.PromotionModel;


public class ProductAdapter extends BaseAdapter {
    List<ProductModel> productList;
    Context context;

    public ProductAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (ProductActivity.isSearchedOn) {
            productList = ProductActivity.searchedList;
        } else {
            productList = PointOfSaleDAO.getProductList();
        }
        return productList.size();
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
            convertView = inflater.inflate(R.layout.product_list_row, null);
        }
        TextView product,category,supplier,cost,selling,inventory;

        product = (TextView) convertView.findViewById(R.id.productName_variable);
        category = (TextView) convertView.findViewById(R.id.category_variable);
        supplier = (TextView) convertView.findViewById(R.id.supplier_variable);
        cost = (TextView) convertView.findViewById(R.id.cost_variable);
        selling = (TextView) convertView.findViewById(R.id.selling_variable);
        inventory = (TextView) convertView.findViewById(R.id.count_variable);

        final ProductModel currentProduct = productList.get(position);

        product.setText(currentProduct.productName);
        category.setText(currentProduct.category);
        supplier.setText(currentProduct.activeSupplier);
        cost.setText(String.valueOf(currentProduct.costPrice));
        selling.setText(String.valueOf(currentProduct.sellingPrice));
        inventory.setText(String.valueOf(currentProduct.inventory));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);

                long id = currentProduct.getId();
                intent.putExtra("productId", id);

                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
