package appsnova.com.mybuydeals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import appsnova.com.mybuydeals.adapters.HomeProductsAdapter;
import appsnova.com.mybuydeals.adapters.SliderPageImageAdapter;
import appsnova.com.mybuydeals.models.HomeAllProductsModel;
import appsnova.com.mybuydeals.models.HomeProductsModel;
import appsnova.com.mybuydeals.models.SliderImageModel;
import appsnova.com.mybuydeals.ownlibraries.MaterialProgressWheel;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getName();

    //create utility objects
        NetworkUtils networkUtils;
        SharedPref sharedPref;

    //create and Initialize ArrayList Objects
    ArrayList<HomeProductsModel> homeProductsDealsList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsBeveragesList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsKitchenDinesList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsKitchenStoragesList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsDiningServingList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsFashionList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsfruitVegList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsRecommendList = new ArrayList<HomeProductsModel>();
    //ArrayList<HomeProductsModel> homeProductsHomesList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeAllProductsModel> homeProductsHomeAll = new ArrayList<HomeAllProductsModel>();


    //Slider Object creation
    private static ViewPager homeViewPager;
    private CirclePageIndicator slider;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<SliderImageModel> sliderImageModelArrayList;
    private int[] myImageList = new int[]{R.drawable.banner1, R.drawable.banner2,
            R.drawable.banner3,R.drawable.banner4};

    //create View Objects
    RecyclerView dealsListRecyclerView, beveragesProductsRecyclerView, kitchenDineProductsRecyclerView,
            kitchenStorageProductsRecyclerView, diningServingProductsRecyclerView, fashionProductsRecyclerVIew,
            fruitVegProductsRecyclerView, recommendedProductsRecyclerView; // homeNeedsRV

    LinearLayout homeProgressLinearLayout, homeProductsMainLinearLayout, dealsLinearLayout,
            beveragesLinearLayout, kitchenDineLinearLayout, kitchenStorageLinearLayout, diningServingLinearLayout,
            fashionLinearLayout, fruitVegLinearLayout, recommendedLinearLayout; //homeNeedsLL

    ScrollView homeMainScrollView;
    TextView homeSearch, loadingTextView, dealsTextView, viewAllDealsTextView, beveragesTextView,
            viewAllbeveragesTextView,kitchenDineTextView, viewAllKitchenDineTextView,kitchenStorageTextView,
            viewAllKitchenStorageTextView, diningServingTextView, viewAlldiningServingTextView,fashionTextView, viewAllFashionTextView,
            fruitVegTextView, viewAllfruitVegsTextView, recommendedTextView, viewAllrecommendedTextView;

    MaterialProgressWheel progressVIew;

    int scrollY;
    static Button notifCount;
    static int mNotifCount = 5;

    RelativeLayout searchByCategoryRL;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize utils objects
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);
        //lists object instantiate
        sliderImageModelArrayList = new ArrayList<>();

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initialization();

        //get Slider Images list
        sliderImageModelArrayList = populateImagesSliding();
        initSlider();
    } //end of onCreate

    private void initialization(){
        dealsListRecyclerView = (RecyclerView) findViewById(R.id.dealsListRecyclerView);
        dealsListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager dealsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        dealsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dealsListRecyclerView.setLayoutManager(dealsLinearLayoutManager);

        fruitVegProductsRecyclerView = (RecyclerView) findViewById(R.id.fruitVegProductsRecyclerView);
        fruitVegProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager fruitVegLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        fruitVegLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fruitVegProductsRecyclerView.setLayoutManager(fruitVegLinearLayoutManager);

        beveragesProductsRecyclerView = (RecyclerView) findViewById(R.id.beveragesProductsRecyclerView);
        beveragesProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager beveragesLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        beveragesLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        beveragesProductsRecyclerView.setLayoutManager(beveragesLinearLayoutManager);

        recommendedProductsRecyclerView = (RecyclerView) findViewById(R.id.recommendedProductsRecyclerView);
        recommendedProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager recommendedLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        recommendedLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendedProductsRecyclerView.setLayoutManager(recommendedLinearLayoutManager);

        kitchenDineProductsRecyclerView = (RecyclerView) findViewById(R.id.kitchenDineProductsRecyclerView);
        kitchenDineProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager kitchenDineProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        kitchenDineProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        kitchenDineProductsRecyclerView.setLayoutManager(kitchenDineProductsLinearLayoutManager);

        fashionProductsRecyclerVIew = (RecyclerView) findViewById(R.id.fashionProductsRecyclerVIew);
        fashionProductsRecyclerVIew.setHasFixedSize(true);
        LinearLayoutManager fashionProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        fashionProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fashionProductsRecyclerVIew.setLayoutManager(fashionProductsLinearLayoutManager);

        diningServingProductsRecyclerView = (RecyclerView) findViewById(R.id.diningServingProductsRecyclerView);
        diningServingProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager diningServingProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        diningServingProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        diningServingProductsRecyclerView.setLayoutManager(diningServingProductsLinearLayoutManager);

        kitchenStorageProductsRecyclerView = (RecyclerView) findViewById(R.id.kitchenStorageProductsRecyclerView);
        kitchenStorageProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager kitchenStorageProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        kitchenStorageProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        kitchenStorageProductsRecyclerView.setLayoutManager(kitchenStorageProductsLinearLayoutManager);

        homeProgressLinearLayout = (LinearLayout) findViewById(R.id.homeProgressLinearLayout);
        dealsLinearLayout = (LinearLayout) findViewById(R.id.dealsLinearLayout);
        fruitVegLinearLayout = (LinearLayout) findViewById(R.id.fruitVegLinearLayout);
        beveragesLinearLayout = (LinearLayout) findViewById(R.id.beveragesLinearLayout);

        //homeNeedsLL = (LinearLayout)findViewById(R.id.homeNeedsLLID);
        recommendedLinearLayout = (LinearLayout) findViewById(R.id.recommendedLinearLayout);
        kitchenDineLinearLayout = (LinearLayout) findViewById(R.id.kitchenDineLinearLayout);
        kitchenStorageLinearLayout = (LinearLayout) findViewById(R.id.kitchenStorageLinearLayout);
        fashionLinearLayout = (LinearLayout) findViewById(R.id.fashionLinearLayout);
        diningServingLinearLayout = (LinearLayout) findViewById(R.id.diningServingLinearLayout);

        viewAllfruitVegsTextView = findViewById(R.id.viewAllfruitVegsTextView);
        viewAllbeveragesTextView = findViewById(R.id.viewAllbeveragesTextView);
        viewAllDealsTextView = findViewById(R.id.viewAllDealsTextView);
        viewAlldiningServingTextView = findViewById(R.id.viewAllDiningServingTextView);
        viewAllKitchenDineTextView = findViewById(R.id.viewAllKitchenDineTextView);
        viewAllKitchenStorageTextView = findViewById(R.id.viewAllKitchenStorageTextView);


        homeSearch = (TextView) findViewById(R.id.homeSearch);

        homeMainScrollView = (ScrollView) findViewById(R.id.homeMainScrollView);
        homeMainScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                scrollY = homeMainScrollView.getScrollY(); //for verticalScrollView
                invalidateOptionsMenu();
                //DO SOMETHING WITH THE SCROLL COORDINATES
            }
        });

        if (networkUtils.checkConnection()) {
            getProductsFromServer();
        } else {
            UrlUtility.showCustomToast(getResources().getString(R.string.no_connection), HomeActivity.this);
            finish();
        }

        homeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });

        viewAllDealsTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", "deals_products");
                sportIntent.putExtra("CAT_NAME", "ALL DEALS");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllKitchenStorageTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", "kitchen_storage_products");
                sportIntent.putExtra("FROM_HOME", "HOME");
                sportIntent.putExtra("CAT_NAME", "ALL KITCHEN STORAGE");
                startActivity(sportIntent);
            }
        });

        viewAllKitchenDineTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", "kitchen_dine_products");
                sportIntent.putExtra("CAT_NAME", "ALL KITCHEN DINE");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAlldiningServingTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", "dining_serving_products");
                sportIntent.putExtra("CAT_NAME", "ALL DINE SERVING");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });


        viewAllbeveragesTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", "beverages_products");
                sportIntent.putExtra("CAT_NAME", "ALL BEVERAGES");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllfruitVegsTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", "fruits_veg_products");
                sportIntent.putExtra("CAT_NAME", "ALL FRUITS & VEGETABLES");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });
    }

    private ArrayList<SliderImageModel> populateImagesSliding(){

        ArrayList<SliderImageModel> list = new ArrayList<>();

        for(int i = 0; i < myImageList.length; i++){
            SliderImageModel imageModel = new SliderImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }

    private void getProductsFromServer(){
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, UrlUtility.HOME_PRODUCTS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response);
                            String OutputData = "";
                            Log.d("HomeProducts", "onSuccess: "+response);
                            JSONObject jsonResponse;

                            try {

                                /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                                /*******  Returns null otherwise.  *******/
                                JSONArray deals_productsJson = response.getJSONArray("deals_products");
                                  JSONArray fruitsVeg_productsJson = response.getJSONArray("fruits-vegetables");
                                  JSONArray beverages_productsJson = response.getJSONArray("Beverages");
                                  JSONArray kitchenDine_productsJson = response.getJSONArray("Kitchen_&_Dining");
                                  JSONArray kitchenStorage_productsJson = response.getJSONArray("Kitchen_Storage");
                                  JSONArray diningServing_productsJson = response.getJSONArray("Dining_&_Serving");
                                  //JSONArray fashion_productsJson = jsonResponse.getJSONArray("fashion_products");
                                //    JSONArray furniture_productsJson = jsonResponse.getJSONArray("furniture_products");


                                /****** diningServing PRODUCTS ******* Process each JSON node ************/
                            if (diningServing_productsJson.length() > 0) {
                                homeProductsDiningServingList.clear();
                                homeProductsDiningServingList = null;
                                homeProductsDiningServingList = new ArrayList<HomeProductsModel>();

                                for (int i = 0; i < diningServing_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = diningServing_productsJson.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    //String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    //String product_desc = jsonChildNode.optString("product_desc");
                                    homeProductsDiningServingList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsDiningServingList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                            /****** KitchenStorage PRODUCTS ******* Process each JSON node ************/
                            if (kitchenStorage_productsJson.length() > 0) {
                                homeProductsKitchenStoragesList.clear();
                                homeProductsKitchenStoragesList = null;
                                homeProductsKitchenStoragesList = new ArrayList<HomeProductsModel>();

                                for (int i = 0; i < kitchenStorage_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = kitchenStorage_productsJson.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    //String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    //String product_desc = jsonChildNode.optString("product_desc");
                                    homeProductsKitchenStoragesList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsKitchenStoragesList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                                /****** DEALS PRODUCTS ******* Process each JSON node ************/
                            if (deals_productsJson.length() > 0) {
                                homeProductsDealsList.clear();
                                homeProductsDealsList = null;
                                homeProductsDealsList = new ArrayList<HomeProductsModel>();

                                for (int i = 0; i < deals_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = deals_productsJson.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    //String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    //String product_desc = jsonChildNode.optString("product_desc");
                                    homeProductsDealsList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsDealsList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                            if (fruitsVeg_productsJson.length() > 0) {
                                homeProductsfruitVegList.clear();
                                homeProductsfruitVegList = null;
                                homeProductsfruitVegList = new ArrayList<HomeProductsModel>();

                                for (int i = 0; i < fruitsVeg_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = fruitsVeg_productsJson.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    //String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    //String product_desc = jsonChildNode.optString("product_desc");
                                    homeProductsfruitVegList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsfruitVegList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                            if (beverages_productsJson.length() > 0) {
                                homeProductsBeveragesList.clear();
                                homeProductsBeveragesList = null;
                                homeProductsBeveragesList = new ArrayList<HomeProductsModel>();

                                for (int i = 0; i < beverages_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = beverages_productsJson.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    //String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    //String product_desc = jsonChildNode.optString("product_desc");
                                    homeProductsBeveragesList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsBeveragesList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                            if (kitchenDine_productsJson.length() > 0) {
                                homeProductsKitchenDinesList.clear();
                                homeProductsKitchenDinesList = null;
                                homeProductsKitchenDinesList = new ArrayList<HomeProductsModel>();

                                for (int i = 0; i < kitchenDine_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = kitchenDine_productsJson.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    //String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    //String product_desc = jsonChildNode.optString("product_desc");
                                    homeProductsKitchenDinesList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsKitchenDinesList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                                homeProductsHomeAll.add(new HomeAllProductsModel(homeProductsDealsList, homeProductsfruitVegList, homeProductsRecommendList, homeProductsBeveragesList, homeProductsKitchenDinesList));

                                /************ Show Output on screen/activity **********/
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (homeProductsDealsList.size() > 0) {
                                            HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsDealsList);
                                            dealsListRecyclerView.setAdapter(adapter1);
                                            dealsLinearLayout.setVisibility(View.VISIBLE);
                                            adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsDealsList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsDealsList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsDealsList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsDealsList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsDealsList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsDealsList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsDealsList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsDealsList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }

                                        if (homeProductsBeveragesList.size() > 0) {
                                            HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsBeveragesList);
                                            beveragesProductsRecyclerView.setAdapter(adapter1);
                                            beveragesLinearLayout.setVisibility(View.VISIBLE);
                                            adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsBeveragesList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsBeveragesList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsBeveragesList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsBeveragesList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsBeveragesList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsBeveragesList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsBeveragesList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsBeveragesList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }

                                        if (homeProductsDiningServingList.size() > 0) {
                                            HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsDiningServingList);
                                            diningServingProductsRecyclerView.setAdapter(adapter1);
                                            diningServingLinearLayout.setVisibility(View.VISIBLE);
                                            adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsDiningServingList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsDiningServingList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsDiningServingList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsDiningServingList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsDiningServingList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsDiningServingList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsDiningServingList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsDiningServingList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }

                                        if (homeProductsfruitVegList.size() > 0) {
                                            HomeProductsAdapter adapter2 = new HomeProductsAdapter(HomeActivity.this, homeProductsfruitVegList);
                                            fruitVegProductsRecyclerView.setAdapter(adapter2);
                                            fruitVegLinearLayout.setVisibility(View.VISIBLE);
                                            adapter2.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsDealsList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsDealsList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsDealsList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsDealsList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsDealsList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsDealsList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsDealsList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsDealsList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }

                                        if (homeProductsRecommendList.size() > 0) {
                                            HomeProductsAdapter adapter4 = new HomeProductsAdapter(HomeActivity.this, homeProductsRecommendList);
                                            recommendedProductsRecyclerView.setAdapter(adapter4);
                                            recommendedLinearLayout.setVisibility(View.VISIBLE);

                                            adapter4.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsDealsList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsDealsList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsDealsList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsDealsList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsDealsList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsDealsList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsDealsList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsDealsList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }

                                        if (homeProductsKitchenDinesList.size() > 0) {
                                            HomeProductsAdapter adapter4 = new HomeProductsAdapter(HomeActivity.this, homeProductsKitchenDinesList);
                                            kitchenDineProductsRecyclerView.setAdapter(adapter4);
                                            kitchenDineLinearLayout.setVisibility(View.VISIBLE);

                                            adapter4.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsDealsList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsDealsList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsDealsList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsDealsList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsDealsList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsDealsList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsDealsList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsDealsList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }
                                        homeProgressLinearLayout.setVisibility(View.GONE);
                                    }
                                }, 200);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this,"Home Product P"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().getmRequestQueue().add(jsonObjectRequest);
    }

    private void initSlider() {

        homeViewPager = (ViewPager) findViewById(R.id.homeViewPager);
        homeViewPager.setAdapter(new SliderPageImageAdapter(sliderImageModelArrayList, HomeActivity.this));

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.slider);

        indicator.setViewPager(homeViewPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =sliderImageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                homeViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}