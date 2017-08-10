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
import floatingcube.pointofsale.models.PromotionModel;


public class PromotionAdapter extends BaseAdapter {
    List<PromotionModel> promotionList;
    long productId;
    Context context;
    boolean isEditActivity;

    public PromotionAdapter(Context context, long productId) {
        this.context = context;
        this.productId = productId;
    }

    public PromotionAdapter(Context context, long productId, boolean type) {
        this.context = context;
        this.productId = productId;
        this.isEditActivity = type;
    }

    @Override
    public int getCount() {
        this.promotionList = PointOfSaleDAO.getPromotionListThatBelongToProduct(productId);
        return promotionList.size();
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
            convertView = inflater.inflate(R.layout.promotion_listview_item, null);
        }

        final PromotionModel promotion = promotionList.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.promotion_name);
        TextView type = (TextView) convertView.findViewById(R.id.promotion_type);
        TextView startDate = (TextView) convertView.findViewById(R.id.promotion_startdate);
        TextView endDate = (TextView) convertView.findViewById(R.id.promotion_enddate);
        TextView createdBy = (TextView) convertView.findViewById(R.id.promotion_createdby);

        name.setText(promotion.name);
        type.setText(promotion.type);
        startDate.setText(promotion.startDate);
        endDate.setText(promotion.endDate);
        createdBy.setText(promotion.createdBy);

        if (isEditActivity){
            Button editButton = (Button) convertView.findViewById(R.id.edit_button);
            editButton.setVisibility(View.VISIBLE);
            System.out.println("Checkpoint");

            final long promotionId = promotion.getId();

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // call a dialog
                    LayoutInflater li = LayoutInflater.from(context);
                    final View dialogXML = li.inflate(R.layout.add_promotion_dialog, null);

                    // Create custom dialog object
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setView(dialogXML);

                    final EditText name = (EditText) dialogXML.findViewById(R.id.promotion_name_input);
                    final EditText type = (EditText) dialogXML.findViewById(R.id.promotion_type_input);
                    final EditText startDate = (EditText) dialogXML.findViewById(R.id.promotion_start_date);
                    final EditText endDate = (EditText) dialogXML.findViewById(R.id.promotion_end_date);

                    // fill information in
                    PromotionModel promotion = PointOfSaleDAO.getPromotionById(promotionId);

                    name.setText(promotion.name);
                    type.setText(promotion.type);
                    startDate.setText(promotion.startDate);
                    endDate.setText(promotion.endDate);

                    // Set dialog title
                    dialog.setTitle("Edit Promotion");
                    dialog
                            .setCancelable(false)
                            .setPositiveButton("Update",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            PromotionModel promotion = PointOfSaleDAO.getPromotionById(promotionId);

                                            promotion.name = name.getText().toString();
                                            promotion.type = type.getText().toString();
                                            promotion.startDate = startDate.getText().toString();
                                            promotion.endDate = endDate.getText().toString();

                                            promotion.save();
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
