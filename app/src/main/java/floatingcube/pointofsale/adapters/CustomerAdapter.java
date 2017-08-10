package floatingcube.pointofsale.adapters;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import floatingcube.pointofsale.R;
import floatingcube.pointofsale.activities.CustomerActivity;
import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.CustomerModel;


public class CustomerAdapter extends BaseAdapter {
    List<CustomerModel> customerList;  // this list will store Customers list after we got them from Active Android
    Context context;
    CustomerAdapter adapter = this;

    public CustomerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        this.customerList = PointOfSaleDAO.getAllCustomer();
        return customerList.size();
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
            convertView = inflater.inflate(R.layout.customer_list_listview_item, null);
        }

        TextView customerID = (TextView) convertView.findViewById(R.id.customerID_variable);
        customerID.setText((String) customerList.get(position).getId().toString());  // this getId() is provided by AA

        // Setting data for textViews
        TextView customerName = (TextView) convertView.findViewById(R.id.user_name_variable);
        customerName.setText((String) customerList.get(position).customerName);

        TextView email = (TextView) convertView.findViewById(R.id.email_variable);
        email.setText((String) customerList.get(position).customerEmail);

        final long customerId = customerList.get(position).getId();

        Button deleteBtn = (Button) convertView.findViewById(R.id.delete_button);
        deleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long customerId = customerList.get(position).getId();

                CustomerModel.delete(CustomerModel.class, customerId);
                notifyDataSetChanged();
            }
        });

        // edit button listener
        Button editButton = (Button) convertView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerActivity.createInputDialog(context, "Edit Customer", "edit", customerId , adapter);
            }
        });

        return convertView;
    }

}
