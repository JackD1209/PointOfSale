package floatingcube.pointofsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.activities.SellScreenActivity;
import floatingcube.pointofsale.models.ProductModel;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */

public class SellScreenAdapter1 extends BaseAdapter {
    Context context;
    TextView subAmountText;
    TextView totalAmountText;
    Button payButton;

    public SellScreenAdapter1(Context context, TextView subAmountText, TextView totalAmountText, Button payButton) {
        this.context = context;
        this.subAmountText = subAmountText;
        this.totalAmountText = totalAmountText;
        this.payButton = payButton;
    }

    @Override
    public int getCount() {
        return SellScreenActivity.selectedItems.size();
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
            convertView = inflater.inflate(R.layout.sell_screen_listview_row, null);
        }

        ProductModel product = SellScreenActivity.selectedItems.get(position);

        // Setting data for textViews
        TextView numberOfProduct = (TextView) convertView.findViewById(R.id.number_of_product);
        TextView productName = (TextView) convertView.findViewById(R.id.name_of_product);
        TextView priceOfProduct = (TextView) convertView.findViewById(R.id.price_of_product);


        productName.setText(product.productName);
        numberOfProduct.setText("x1");
        priceOfProduct.setText(String.valueOf(product.sellingPrice));

        // set listener for the "Fake Remove button"
        TextView removeButton = (TextView) convertView.findViewById(R.id.selected_item_listview_remove_button);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SellScreenActivity.selectedItems.remove(position);
                SellScreenActivity.updatePayAmount(subAmountText, totalAmountText, payButton);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

}

