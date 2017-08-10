package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "InventoryHistory")
public class InventoryHistoryModel extends Model {

    @Column(name = "supplier")
    public String supplier;

    @Column(name = "remark")
    public String remark;

    @Column(name = "cost")
    public float cost;

    @Column(name = "quantity")
    public int quantity;

    @Column(name = "profit_margin")
    public String profitMargin;

    @Column(name = "updated_on")
    public String updatedOn;

    @Column(name = "added_by")
    public String addedBy;

    // 0 means false, 1 means true
    // i afraid that active android cannot store boolean probably, so use int instead
    @Column(name = "apply_gst")
    public Integer applyGST;

    @Column(name = "product_id")
    public long productId;
}

