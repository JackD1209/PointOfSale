package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "SchedulePrices")
public class SchedulePriceModel extends Model {
    @Column(name = "current_selling_price")
    public float currentSellingPrice;

    @Column(name = "new_selling_price")
    public float newSellingPrice;

    @Column(name = "selling_start_date")
    public String sellingStartDate;

    @Column(name = "selling_created_by")
    public String sellingCreatedBy;

    @Column(name = "current_cold_price")
    public float currentColdPrice;

    @Column(name = "new_cold_price")
    public float newColdPrice;

    @Column(name = "cold_start_date")
    public String coldStartDate;

    @Column(name = "cold_created_by")
    public String coldCreatedBy;

    @Column(name = "product_id")  // product id which this model belong to
    public long productId;
}