package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Suppliers")
public class SupplierModel extends Model {

    @Column(name = "supplier_name")
    public String name;

    @Column(name = "supplier_code")
    public String code;

    @Column(name = "cost_price")
    public Float costPrice;

    // 0 means false, 1 means true
    // i afraid that active android cannot store boolean probably, so use int instead
    @Column(name = "apply_gst")
    public Integer applyGST;

    @Column(name = "supplier_info")
    public String info;

    @Column(name = "last_updated_on")
    public String lastUpdated;

    @Column(name = "added_by")
    public String addedBy;

    // id of product that this supplier belong to
    @Column(name = "product_id")
    public long productId;
}
