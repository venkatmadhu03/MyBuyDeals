package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import appsnova.com.mybuydeals.adapters.CartListAdapter;
import appsnova.com.mybuydeals.models.CartDataModel;
import appsnova.com.mybuydeals.models.LoginDetailsModel;
import appsnova.com.mybuydeals.ownlibraries.MaterialProgressWheel;
import appsnova.com.mybuydeals.utilities.DatabaseHelper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CreateOrderActivity extends AppCompatActivity {

    ArrayList<CartDataModel> cartListDataList = new ArrayList<CartDataModel>();
    DatabaseHelper dataBaseHelper = new DatabaseHelper(CreateOrderActivity.this);
    public CartListAdapter cartListAdapter;
    RelativeLayout cartIsEmptyRL;
    ListView cartListviewLV;
    private MaterialProgressWheel progressWheel;
    TextView totalAmountTV;

    LoginDetailsModel loginDetailsModel;
    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
    }
}
