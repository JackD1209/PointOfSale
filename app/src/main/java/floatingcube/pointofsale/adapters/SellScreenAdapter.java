package floatingcube.pointofsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import floatingcube.pointofsale.PointOfSale;
import floatingcube.pointofsale.R;
import floatingcube.pointofsale.activities.SellScreenActivity;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.CategoryModel;
import floatingcube.pointofsale.models.ProductModel;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class SellScreenAdapter extends BaseAdapter {
    List<String> categoryList;
    List<ProductModel> productList;
    List<ProductModel> searchedList;
    Context context;
    SellScreenAdapter1 sellScreenAdapter1;

    TextView subTotalAmountTextview;
    TextView totalAmountTextview;
    Button payButton;
    TextView menuNameTextview;
    TextView backTextView;

    public SellScreenAdapter(Context context, SellScreenAdapter1 sellScreenAdapter1
    , TextView textView, TextView textView1, Button button, TextView menuNameTextview, TextView backTextView) {
        this.context = context;
        this.sellScreenAdapter1 = sellScreenAdapter1;
        this.subTotalAmountTextview = textView;
        this.totalAmountTextview = textView1;
        this.payButton = button;
        this.menuNameTextview = menuNameTextview;
        this.backTextView = backTextView;
    }

    @Override
    public int getCount() {
        if (SellScreenActivity.isSearchOn) {
            searchedList = SellScreenActivity.searcheditems;
            return searchedList.size();
        } else {
            if (SellScreenActivity.level == 1) {
                if (PointOfSaleDAO.getCategory() == null) {
                    return 0;
                }

                String category = PointOfSaleDAO.getCategory().category;
                if (category.equals("")) {
                    return 0;
                }
                if (category.contains(",")) {
                    categoryList = Arrays.asList(category.split(","));
                } else {
                    categoryList = Arrays.asList(category);
                }
                return categoryList.size();
            } else {
                productList = PointOfSaleDAO.searchProductWithCategory(SellScreenActivity.categorySelected);
                return productList.size();
            }
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sell_screen_gridview_item, null);
        }

        if (SellScreenActivity.isSearchOn) {
            final ProductModel product = searchedList.get(position);

            final TextView productName = (TextView) convertView.findViewById(R.id.product_textview_item);
            productName.setText(product.productName);

            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when user selected some item, this item will be displayed in the pay listView
                    // then update the pay amount for that item

                    int stock = countStock(SellScreenActivity.selectedItems, product.getId());

                    if (product.inventory > stock) {
                        SellScreenActivity.selectedItems.add(product);
                        SellScreenActivity.updatePayAmount(subTotalAmountTextview, totalAmountTextview, payButton);
                        sellScreenAdapter1.notifyDataSetChanged();
                    } else {
                        productName.setText("Out Of Stock");
                    }
                }
            });
            return convertView;
        }

        // Setting data for textViews
        if (SellScreenActivity.level == 1) {
            TextView productName = (TextView) convertView.findViewById(R.id.product_textview_item);
            productName.setText(categoryList.get(position));

            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuNameTextview.setText(categoryList.get(position));
                    SellScreenActivity.categorySelected = categoryList.get(position);

                    SellScreenActivity.level = 2;
                    notifyDataSetChanged();
                    backTextView.setVisibility(View.VISIBLE);
                    // redirect to level 2
                }
            });

            // When we have the real product model, we will check if this product have image
            // if it have, we will display its image and name

        } else { // level 2
            final ProductModel product = productList.get(position);

            final TextView productName = (TextView) convertView.findViewById(R.id.product_textview_item);
            productName.setText(product.productName);

            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when user selected some item, this item will be displayed in the pay listView
                    // then update the pay amount for that item

                    int stock = countStock(SellScreenActivity.selectedItems, product.getId());

                    if (product.inventory > stock) {
                        SellScreenActivity.selectedItems.add(product);
                        SellScreenActivity.updatePayAmount(subTotalAmountTextview, totalAmountTextview, payButton);
                        sellScreenAdapter1.notifyDataSetChanged();
                    } else {
                        productName.setText("Out Of Stock");
                    }
                }
            });
        }
        return convertView;
    }

    // count stock inside the list of product
    int countStock(List<ProductModel> list, long productId){
        int count = 0;
        for (ProductModel product : list) {
            if (product.getId() == productId) {
                count++;
            }
        }

        return count;
    }
}

