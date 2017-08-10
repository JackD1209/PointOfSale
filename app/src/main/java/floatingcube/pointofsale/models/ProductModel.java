package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */

@Table(name = "Products")
public class ProductModel extends Model{
    @Column(name = "name")
    public String productName;

    @Column(name = "barcode")
    public String barcode;

    @Column(name = "category")
    public String category;

    @Column(name = "selling_price")
    public float sellingPrice;

    @Column(name = "cost_price")
    public float costPrice;

    @Column(name = "cold_price")
    public float coldPrice;

    @Column(name = "active_supplier")
    public String activeSupplier;

    @Column(name = "supplier_code")
    public String supplierCode;

    @Column(name = "inventory")
    public int inventory;

    public List<QuickMenuModel> quickKeys() {
        return getMany(QuickMenuModel.class, "Product");
    }
}
