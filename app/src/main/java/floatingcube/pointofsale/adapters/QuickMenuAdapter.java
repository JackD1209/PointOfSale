package floatingcube.pointofsale.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.models.QuickMenuModel;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class QuickMenuAdapter extends BaseAdapter {
    private List<QuickMenuModel> quickKeyList;
    private Context context;

    public QuickMenuAdapter(List<QuickMenuModel> quickKeyList, Context context) {
        this.quickKeyList = quickKeyList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return quickKeyList.size();
    }

    @Override
    public Object getItem(int i) {
        return quickKeyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        ViewHolder viewHolder;

        if(view == null) {
            view = inflater.inflate(R.layout.quick_key_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.QuickKeyTextView = (TextView) view.findViewById(R.id.tvQuickKey);
            viewHolder.ProductNameTextView = (TextView) view.findViewById(R.id.tvProductName);
            viewHolder.QuickKeyItemLayout = (RelativeLayout) view.findViewById(R.id.rlQuickKey);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        QuickMenuModel quickMenuModel = quickKeyList.get(pos);

        if(quickMenuModel.Product != null) {
            viewHolder.QuickKeyTextView.setText(quickMenuModel.QuickKey);
            viewHolder.ProductNameTextView.setText(quickMenuModel.Product.productName);
            viewHolder.QuickKeyItemLayout.setBackgroundColor(Color.GRAY);
        }
        // Set the height based on current parent size
        view.getLayoutParams().height = (parent.getHeight() / 5) - 10;
        return view;
    }

    public class ViewHolder {
        public TextView QuickKeyTextView;
        public TextView ProductNameTextView;
        public RelativeLayout QuickKeyItemLayout;
    }
}
