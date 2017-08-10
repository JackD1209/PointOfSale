package floatingcube.pointofsale.adapters;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.activities.SaleHistoryActivity;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.SaleHistoryModel;

public class SaleHistoryAdapter extends BaseAdapter {
    List<SaleHistoryModel> saleHistoryList;
    Context context;

    public SaleHistoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        this.saleHistoryList = PointOfSaleDAO.FilterSaleHistoryList(SaleHistoryActivity.filterBy);
        return saleHistoryList.size();
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
            convertView = inflater.inflate(R.layout.sale_history_list_item, null);
        }

        TextView dateTextView = (TextView) convertView.findViewById(R.id.date_name_variable);
        dateTextView.setText(saleHistoryList.get(position).date);  // this getId() is provided by AA

        TextView userName = (TextView) convertView.findViewById(R.id.user_name_variable);
        userName.setText(saleHistoryList.get(position).userName);

        TextView customerName = (TextView) convertView.findViewById(R.id.customer_name_variable);
        customerName.setText(saleHistoryList.get(position).customer);

        TextView noteTextView = (TextView) convertView.findViewById(R.id.note_name_variable);
        noteTextView.setText(saleHistoryList.get(position).note);

        TextView statusTextView = (TextView) convertView.findViewById(R.id.status_name_variable);
        statusTextView.setText(saleHistoryList.get(position).status);

        TextView totalTextView = (TextView) convertView.findViewById(R.id.total_name_variable);
        totalTextView.setText(String.valueOf(saleHistoryList.get(position).total));

        return convertView;
    }

}
