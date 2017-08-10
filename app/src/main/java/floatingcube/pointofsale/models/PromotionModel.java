package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Promotions")
public class PromotionModel extends Model {

    @Column(name = "name")
    public String name;

    @Column(name = "type")
    public String type;

    @Column(name = "start_date")
    public String startDate;

    @Column(name = "end_date")
    public String endDate;

    @Column(name = "created_by")
    public String createdBy;

    @Column(name = "product_id")
    public long productId;
}