package floatingcube.pointofsale.dataaccess;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import floatingcube.pointofsale.models.CategoryModel;
import floatingcube.pointofsale.models.CustomerModel;
import floatingcube.pointofsale.models.InventoryHistoryModel;
import floatingcube.pointofsale.models.ProductModel;
import floatingcube.pointofsale.models.PromotionModel;
import floatingcube.pointofsale.models.QuickMenuModel;
import floatingcube.pointofsale.models.SaleHistoryModel;
import floatingcube.pointofsale.models.SchedulePriceModel;
import floatingcube.pointofsale.models.SupplierModel;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
public class PointOfSaleDAO {

    public static List<CustomerModel> getAllCustomer() {
        return new Select().from(CustomerModel.class).execute();
    }

    public static List<ProductModel> getProductList() {
        return new Select().from(ProductModel.class).execute();
    }

    public static List<SaleHistoryModel> getSaleHistoryList() {
        return new Select().from(SaleHistoryModel.class).execute();
    }

    public static List<CategoryModel> getCategoryList() {
        return new Select().from(CategoryModel.class).execute();
    }

    // There are only one category model, Its id will be 1.
    public static CategoryModel getCategory() {
        return CategoryModel.load(CategoryModel.class, 1);
    }

    public static List<SaleHistoryModel> FilterSaleHistoryList(String filterBy){

        if (filterBy.equals("Filter by date")) {
            return new Select().from(SaleHistoryModel.class).execute();
        }

        if (filterBy.equals("Earliest")) {
            return new Select()
                    .from(SaleHistoryModel.class)
                    .orderBy("date_in_millis ASC")
                    .execute();
        } else {  // latest
            return new Select()
                    .from(SaleHistoryModel.class)
                    .orderBy("date_in_millis DESC")
                    .execute();
        }
    }


    public static ProductModel getProductById(long id) {
        return ProductModel.load(ProductModel.class, id);
    }

    public static InventoryHistoryModel getInventoryHistoryById(long id) {
        return InventoryHistoryModel.load(InventoryHistoryModel.class, id);
    }

    public static SupplierModel getSupplierById(long id) {
        return SupplierModel.load(SupplierModel.class, id);
    }

    public static PromotionModel getPromotionById(long id) {
        return PromotionModel.load(PromotionModel.class, id);
    }

    public static List<ProductModel> searchProductWithCategory(String category){
        List<ProductModel> productModelList = new Select().from(ProductModel.class).execute();
        List<ProductModel> returnList = new ArrayList<>();

        for (ProductModel model : productModelList) {
            if (model.category != null) {
                if (model.category.equals(category)) {
                    returnList.add(model);
                }
            }
        }

        return returnList;
    }

    public static List<SupplierModel> getSupplierListThatBelongToProduct(long productId){
        return new Select()
                .from(SupplierModel.class)
                .where("product_id = ?", productId)
                .execute();
    }

    public static List<InventoryHistoryModel> getInventoryHistoryListThatBelongToProduct(long productId){
        return new Select()
                .from(InventoryHistoryModel.class)
                .where("product_id = ?", productId)
                .execute();
    }

    public static List<PromotionModel> getPromotionListThatBelongToProduct(long productId){
        return new Select()
                .from(PromotionModel.class)
                .where("product_id = ?", productId)
                .execute();
    }

    public static SchedulePriceModel getSchedulePriceThatBelongToProduct(long productId){
        return new Select()
                .from(SchedulePriceModel.class)
                .where("product_id = ?", productId)
                .executeSingle();
    }

    public static SupplierModel getSupplierByName(String supplierName){
        return new Select()
                .from(SupplierModel.class)
                .where("supplier_name = ?", supplierName)
                .executeSingle();
    }


    public static List<SupplierModel> getSupplierList() {
        return new Select().from(SupplierModel.class).execute();
    }

    public static List<ProductModel> SearchProduct(String name, String barcode, String category){
        List<ProductModel> productModelList = new Select().from(ProductModel.class).execute();
        List<ProductModel> returnList = new ArrayList<>();

        boolean haveName = name.length() != 0;
        boolean haveBarcode = barcode.length() != 0;
        boolean haveCategory = category.length() != 0;


        // Name is highest priority, if there are name, barcode and category, name will be the first
        // thing to be search, and if match, return immediately
        if (haveName) {
            for(ProductModel model : productModelList) {
                if(model.productName.contains(name)) {
                    returnList.add(model);
                }
            }
            if (returnList.size() > 0) {
                return returnList;
            }
        }

        // barCode is the second highest priority
        if (haveBarcode) {
            for(ProductModel model : productModelList) {
                if(model.barcode.contains(barcode)) {
                    returnList.add(model);
                }
            }
            if (returnList.size() > 0) {
                return returnList;
            }
        }

        if (haveCategory) {
            for(ProductModel model : productModelList) {
                if(model.category.contains(category)) {
                    returnList.add(model);
                }
            }
        }
        return returnList;
    }

    public static List<InventoryHistoryModel> getInventoryHistoryList() {
        return new Select().from(InventoryHistoryModel.class).execute();
    }

    public static ProductModel getProductById(int id) {
        return new Select().from(ProductModel.class).where("Id = ?", id).executeSingle();
    }

    public static List<QuickMenuModel> getQuickKeys() {
        return new Select().from(QuickMenuModel.class).execute();
    }


    public static void deleteQuickKeyById(long id) {
        new Delete().from(QuickMenuModel.class).where("Id = ?", id).execute();
    }

    public static List<ProductModel> searchProductWithKeyword(String keyword){
        List<ProductModel> productModelList = new Select().from(ProductModel.class).execute();
        List<ProductModel> returnList = new ArrayList<>();

        for (ProductModel model : productModelList) {
            if (model.productName.toLowerCase().contains(keyword.toLowerCase())) {
                returnList.add(model);
            }
        }

        return returnList;
    }

}
