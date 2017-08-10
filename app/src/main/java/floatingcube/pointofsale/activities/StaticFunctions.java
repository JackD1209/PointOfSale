package floatingcube.pointofsale.activities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import floatingcube.pointofsale.dataaccess.PointOfSaleDAO;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.SupplierModel;

/**
 * Created by trieulieuf9 on 8/26/16.
 */
public class StaticFunctions {


    public static float wrongInputPolice(String input){
        String acceptedCharacters = "0123456789.";

        int length = input.length();
        if (length == 0) {
            return -1003;
        }

        for(int i = 0 ; i < input.length() ; i++) {
            char c = input.charAt(i);
            if (acceptedCharacters.indexOf(c) == -1) {  // if the input is wrong, return -1000
                return -1000;
            }
        }

        if (input.contains("..")) {  // 2 dot in a row, wrong input
            return - 1001;
        }

        if (input.charAt(0) == '.' || input.charAt(length - 1) == '.') { // if "." appear at the end or beginning, wrong
            return -1002;
        }

        return Float.valueOf(input);
    }


    public static String[] reArrangeStringArray(String[] items, String selectedItem){
        if (items.length == 0 || selectedItem == null) {
            return items;
        }

        int position = 0;
        boolean isSelectedItemAlreadyDeleted = true;

        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(selectedItem)) {
                position = i;
                isSelectedItemAlreadyDeleted = false;
                break;
            }
        }

        if (!isSelectedItemAlreadyDeleted) {
            items[position] = items[0];
            items[0] = selectedItem;
        }
        return items;
    }


    // This function is to fix bug of android, Cannot use listview inside of scrollView
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight() + 5;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String getSelectedStringInCategorySpinner(int position, String currentFirstItem) {

        if (PointOfSaleDAO.getCategory() == null) {
            return "";
        }

        String category = PointOfSaleDAO.getCategory().category;
        if (!category.contains(",")) {
            return category;
        }
        String[] unformattedList = category.split(",");
        String[] list = reArrangeStringArray(unformattedList, currentFirstItem);

        if (position == 0) {
            return list[0];
        }

        // because position 1 is reserved for add Button.
        // so we step over position 1
        if (position > 1) {
            return list[position - 1];
        }

        return "error";  // we will never reach here
    }

    public static String getSelectedStringInSupplierSpinner(int position, String currentActiveSupplier, long productId){

        if (position == 0) { // no item selected yet
            if (currentActiveSupplier == null || currentActiveSupplier.equals("")) {
                return "";
            } else {
                return currentActiveSupplier;
            }
        } else {
            // if user select another supplier
            // construct list of supplier
            List<SupplierModel> supplierList = PointOfSaleDAO.getSupplierListThatBelongToProduct(productId);
            String[] nameList = new String[supplierList.size()];
            int count = 0;
            for (SupplierModel model : supplierList) {
                nameList[count] = model.name;
                count++;
            }

            if (currentActiveSupplier.equals("")) {
                return nameList[position - 1];
            } else {
                nameList = reArrangeStringArray(nameList, currentActiveSupplier);
                return nameList[position - 1];
            }
        }
    }
}
