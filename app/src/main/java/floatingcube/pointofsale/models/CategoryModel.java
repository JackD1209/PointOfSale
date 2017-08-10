package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Category")
public class CategoryModel extends Model {
    // Active android cannot Store arrayList, so we use String instead
    // Format be like, coffee,tea,cake
    @Column(name = "category_column")
    public String category;
}
