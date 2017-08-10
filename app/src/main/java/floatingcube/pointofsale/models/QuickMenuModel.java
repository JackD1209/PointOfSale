package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */
@Table(name = "QuickKeys")
public class QuickMenuModel extends Model {
    @Column(name = "Product")
    public ProductModel Product;

    @Column(name = "QuickKey")
    public String QuickKey;

    public QuickMenuModel() {
        super();
        //To be implemented
    }

    public QuickMenuModel(ProductModel product, String quickKey) {
        super();
        Product = product;
        QuickKey = quickKey;
    }
}
