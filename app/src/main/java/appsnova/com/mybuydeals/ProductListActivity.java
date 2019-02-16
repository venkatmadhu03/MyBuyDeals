package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import appsnova.com.mybuydeals.adapters.GridViewHomeAdapter;
import appsnova.com.mybuydeals.models.HomeChildModel;
import appsnova.com.mybuydeals.models.HomeProductsModel;
import appsnova.com.mybuydeals.models.ProductListModel;
import appsnova.com.mybuydeals.ownlibraries.MaterialProgressWheel;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductListActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private static final String TAG = ProductListActivity.class.getName();
    //create Utils Projects
    SharedPref sharedPref;
    UrlUtility urlUtility;
    NetworkUtils networkUtils;

    //create View Objects
    RelativeLayout productListMainRelativeLayout, gridViewRelativeLayout, loadingMoreRelativeLayout
            ,headerRelativeLayout, backallRelativeLayout;
    RecyclerView productListRecyclerView;
    GridView productListGridView;
    MaterialProgressWheel progressWheel1, progressWheel ;
    TextView noDataFoundTextView, loadingMoreProductsTextView,headerTitleTextView, subTitleTV;
    Button backButton, cartButton;

    //create List Objects
    List<ProductListModel> categoriesWiseProductsListHome = new ArrayList<ProductListModel>();
    List<ProductListModel> categoriesWiseProductsListHomeTemp = new ArrayList<ProductListModel>();

    //Adapters Object Creation

    GridViewHomeAdapter gridViewHomeAdapter;

    static String catId = "";
    static String subCategoryId = "";
    static String categoryName = "";
    static String subCategoryName = "";
    static String fromScreen = "";

    static String HOME_KEY = "";
    static String SEARCH_KEYWORD = "";

    //Pagination here
    private int mvisibleItemCount = -1;
    private String fetchDirectionUP = UrlUtility.FETCH_DIRECTION_UP;
    private String fetchDirection = "";
    private int visibleThreshold = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private int mfirstVisibleItem;
    int productsCount = 0, startCount = 0;
    int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialie utils objects
        sharedPref = new SharedPref(this);
        networkUtils = new NetworkUtils(this);

        if (getIntent() != null && getIntent().getBooleanExtra("EXIT", false)) {
            Intent bookingDoneIntent = new Intent(ProductListActivity.this, HomeActivity.class);
            bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bookingDoneIntent.putExtra("EXIT", true);
            startActivity(bookingDoneIntent);
            onBackPressedAnimationByCHK();
        } else {
            HOME_KEY = "";
            SEARCH_KEYWORD = "";
            catId = "";
            subCategoryId = "";
            categoryName = "";
            subCategoryName = "";
            fromScreen = "";
            categoriesWiseProductsListHome = new ArrayList<ProductListModel>();
            categoriesWiseProductsListHomeTemp = new ArrayList<ProductListModel>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            setContentView(R.layout.activity_product_list);

            UrlUtility.setDimensions(this);
            overridePendingTransition(R.anim.right_pull_in, R.anim.left_push_out);

            //initialie View Objects
            headerRelativeLayout = findViewById(R.id.headerRelativeLayout);
            backallRelativeLayout =findViewById(R.id.backallRelativeLayout);
            headerTitleTextView = findViewById(R.id.headerTitleTextView);
            subTitleTV = findViewById(R.id.titleSubTVID);
            backButton = findViewById(R.id.backButton);
            cartButton = (Button) findViewById(R.id.cartButton);

            progressWheel1 =  findViewById(R.id.progressWheel1);
            loadingMoreRelativeLayout =findViewById(R.id.loadingMoreRelativeLayout);
            noDataFoundTextView = findViewById(R.id.noDataFoundTextView);
            productListGridView = findViewById(R.id.productListGridView);
            productListRecyclerView = findViewById(R.id.productListRecyclerView);

            loadingMoreRelativeLayout.setVisibility(View.GONE);
            progressWheel1.setBarColor(getResources().getColor(R.color.colorPrimary));
            progressWheel1.setRimColor(Color.LTGRAY);


            productListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent sportIntent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "GRID_PRODUCTS");
                    sportIntent.putExtra("PRODUCT_ID", categoriesWiseProductsListHome.get(position).getProduct_id());
                    sportIntent.putExtra("PRODUCT_NAME", categoriesWiseProductsListHome.get(position).getProduct_name());
                    sportIntent.putExtra("PRODUCT_PRICE", categoriesWiseProductsListHome.get(position).getPrice());
                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", categoriesWiseProductsListHome.get(position).getRegular_price());
                    sportIntent.putExtra("PRODUCT_IMAGE_URL", categoriesWiseProductsListHome.get(position).getImage());
                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", categoriesWiseProductsListHome.get(position).getVendor_name());
                    sportIntent.putExtra("PRODUCT_DESCRIPTION", "");
                    sportIntent.putExtra("VENDOR_DESCRIPTION", "");
                    startActivity(sportIntent);
                }
            });

            productListGridView.setOnScrollListener(this);

            if (networkUtils.checkConnection()) {
                if (getIntent() != null) {
                    Bundle b = getIntent().getExtras();
                    if (b != null) {
                        String productsURL = null;
                        fromScreen = b.getString("FROM_HOME");
                        if (fromScreen != null && fromScreen.equalsIgnoreCase("HOME")) {
                            HOME_KEY = b.getString("HOME_KEY");
                            categoryName = b.getString("CAT_NAME");
                            catId = b.getString("CAT_ID");
                            setupNavigation("" + categoryName);
                           // productsURL = UrlUtility.HOME_PRODUCTS_VIEW_ALL_URL + "" + HOME_KEY;
                            productsURL = UrlUtility.CATEGORIES_WISE_PRODUCTS_LIST_URL + "cat_id=" +catId;
                            loadProductsListData(productsURL, productsCount, limit);
                        } else if (fromScreen != null && fromScreen.equalsIgnoreCase("SEARCH")) {
                            SEARCH_KEYWORD= b.getString("SEARCH_KEYWORD");
                            setupNavigation("Search Results");
                            productsURL = UrlUtility.SEARCH_RESULTS_URL + "" + SEARCH_KEYWORD;
                            loadProductsListData(productsURL, productsCount, limit);
                        } else {
                            catId = b.getString("CAT_ID");
                            subCategoryId = b.getString("SUB_CAT_ID");
                            categoryName = b.getString("CAT_NAME");
                            subCategoryName = b.getString("SUB_CAT_NAME");
                            setupNavigation("" + categoryName);
                            if (catId != null && !catId.isEmpty()) {
                                if (subCategoryId != null && !subCategoryId.isEmpty() && subCategoryId.trim().length() > 0) {
                                    productsURL = UrlUtility.CATEGORIES_WISE_PRODUCTS_LIST_URL + "sub_category_id=" + subCategoryId+ "&start="+startCount+"&limit=10";
                                } else {
                                    productsURL = UrlUtility.CATEGORIES_WISE_PRODUCTS_LIST_URL + "cat_id=" + catId + "&start="+startCount+"&limit=10";
                                }
                            }
                            loadProductsListData(productsURL, productsCount, limit);
                        }
                    }
                }
            } else {
                UrlUtility.showCustomToast("Please connect your Internet!", this);
                finish();
            }
        }
    } //end of onCreate

    private void loadProductsListData(String allCategoriesUrl, int start, int limit) {
        String productsURL = allCategoriesUrl + "&start=" + start + "&limit=" + limit;
        getAllCategoriesList(productsURL);
        Log.w("ProductsURL", "" + productsURL);
    } //end of loadProductsListData

    private void getAllCategoriesList(final String allCategoriesUrl) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, allCategoriesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse: "+allCategoriesUrl);
                    if (!response.equalsIgnoreCase("fail")){
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length();i++){
                            ProductListModel productListModel = new ProductListModel();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            productListModel.setProduct_id(jsonObject.getString("product_id"));
                            productListModel.setProduct_name(jsonObject.getString("product_name"));
                            productListModel.setVendor_id(jsonObject.getString("vendor_id"));
                            productListModel.setPrice(jsonObject.getString("price"));
                            productListModel.setRegular_price(jsonObject.getString("regular_price"));
                            productListModel.setStock(jsonObject.getString("stock"));
                            productListModel.setImage(jsonObject.getString("image"));
                            productListModel.setVendor_name(jsonObject.getString("vendor_name"));
                            productListModel.setRating(jsonObject.getString("rating"));

                            categoriesWiseProductsListHome.add(productListModel);
                        }
                    }

                    gridViewHomeAdapter = new GridViewHomeAdapter(categoriesWiseProductsListHome, ProductListActivity.this);
                    productListGridView.setAdapter(gridViewHomeAdapter);
                    startCount = gridViewHomeAdapter.getCount();
                    progressWheel1.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheel1.setVisibility(View.GONE);
                Log.d(TAG, "onErrorResponse: "+error.toString());
                Toast.makeText(ProductListActivity.this, "OOPS!! Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().getmRequestQueue().add(stringRequest);
    }

    public void setupNavigation(String _title) {

        headerRelativeLayout.getLayoutParams().height = (int) (UrlUtility.screenHeight / 10.2);
        backallRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });
        // titleTV.setTypeface(Utility.font_bold);
        headerTitleTextView.setText("" + Html.fromHtml(_title));
        subTitleTV.setVisibility(View.GONE);
        if (subCategoryName != null && !subCategoryName.isEmpty() && subCategoryName.trim().length() > 2) {
            subTitleTV.setVisibility(View.VISIBLE);
            subTitleTV.setText("" + Html.fromHtml(subCategoryName));
        }

        backButton.getLayoutParams().width = (int) (UrlUtility.screenHeight / 28.0);
        backButton.getLayoutParams().height = (int) (UrlUtility.screenHeight / 28.0);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });

        cartButton.getLayoutParams().width = (int) (UrlUtility.screenHeight / 24.0);
        cartButton.getLayoutParams().height = (int) (UrlUtility.screenHeight / 24.0);
        cartButton.setVisibility(View.VISIBLE);
        cartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, CartActivity.class));
            }
        });
        cartButton.setBackgroundResource(R.drawable.ic_menu_send);//shopping cart
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.left_pull_in, R.anim.right_push_out);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
