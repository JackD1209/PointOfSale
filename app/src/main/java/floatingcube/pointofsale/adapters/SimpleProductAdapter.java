package floatingcube.pointofsale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import org.w3c.dom.Text;

import java.util.ArrayList;

import floatingcube.pointofsale.activities.QuickMenuActivity;

/**
 * Created by Steven Nguyen on 9/1/2016.
 */
public class SimpleProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<QuickMenuActivity.SimpleProduct> selectedProducts;

    public SimpleProductAdapter(Context context, ArrayList<QuickMenuActivity.SimpleProduct> selectedProducts) {
        this.context = context;
        this.selectedProducts = selectedProducts;
    }

    @Override
    public int getCount() {
        return selectedProducts.size();
    }

    @Override
    public Object getItem(int i) {
        return selectedProducts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

        text1.setText("Product name: " + selectedProducts.get(i).ProductName);
        text2.setText("Product ID: " + selectedProducts.get(i).Id);

        return view;
    }
}
