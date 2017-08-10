package floatingcube.pointofsale.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Calendar;

/**
 * Created by Steven Nguyen on 8/4/2016.
 */

@Table(name = "SaleHistory")
public class SaleHistoryModel extends Model {
    @Column(name = "date")
    public String date;  // in human Readable 11 Aug 2016

    @Column(name = "date_in_millis")
    public long dateInMillis;  // for comparation

    @Column(name = "user_name")
    public String userName;

    @Column(name = "customer_name")
    public String customer;

    @Column(name = "note")
    public String note;

    @Column(name = "status")
    public String status;

    @Column(name = "total_price")
    public float total;
}

// date, user, note, status, total