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
    ArrayList<HomeProductsModel> homeProductsMobilesList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsFoodsList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsFurnituresList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsBeautyList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsFashionList = new ArrayList<HomeProductsModel>();
    ArrayList<HomeProductsModel> homeProductsLatestList = new ArrayList<HomeProductsModel>();
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
    RecyclerView dealsListRecyclerView, mobilesProductsRecyclerView, foodProductsRecyclerView,
            furnitureProductsRecyclerView, beautyProductsRecyclerView, fashionProductsRecyclerVIew,
            latestProductsRecyclerView, recommendedProductsRecyclerView; // homeNeedsRV

    LinearLayout homeProgressLinearLayout, homeProductsMainLinearLayout, dealsLinearLayout,
            mobilesLinearLayout, foodLinearLayout, furnitureLinearLayout, beautyLinearLayout, fashionLinearLayout, latestLinearLayout,
            recommendedLinearLayout; //homeNeedsLL

    ScrollView homeMainScrollView;
    TextView homeSearch, loadingTextView, dealsTextView, viewAllDealsTextView, mobilesTextView,
            viewAllmobilesTextView,foodTextView, viewAllFoodTextView,furnitureTextView,
            viewAllFurnitureTextView, beautyTextView, viewAllBeautyTextView,fashionTextView, viewAllFashionTextView,
            latestTextView, viewAllLatestsTextView, recommendedTextView, viewAllrecommendedTextView;

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
    }

    private void initialization(){
        dealsListRecyclerView = (RecyclerView) findViewById(R.id.dealsListRecyclerView);
        dealsListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager dealsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        dealsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dealsListRecyclerView.setLayoutManager(dealsLinearLayoutManager);

        latestProductsRecyclerView = (RecyclerView) findViewById(R.id.latestProductsRecyclerView);
        latestProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager latestLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        latestLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        latestProductsRecyclerView.setLayoutManager(latestLinearLayoutManager);

        mobilesProductsRecyclerView = (RecyclerView) findViewById(R.id.mobilesProductsRecyclerView);
        mobilesProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mobilesLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        mobilesLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mobilesProductsRecyclerView.setLayoutManager(mobilesLinearLayoutManager);

        recommendedProductsRecyclerView = (RecyclerView) findViewById(R.id.recommendedProductsRecyclerView);
        recommendedProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager recommendedLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        recommendedLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendedProductsRecyclerView.setLayoutManager(recommendedLinearLayoutManager);

        foodProductsRecyclerView = (RecyclerView) findViewById(R.id.foodProductsRecyclerView);
        foodProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager foodProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        foodProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        foodProductsRecyclerView.setLayoutManager(foodProductsLinearLayoutManager);

        fashionProductsRecyclerVIew = (RecyclerView) findViewById(R.id.fashionProductsRecyclerVIew);
        fashionProductsRecyclerVIew.setHasFixedSize(true);
        LinearLayoutManager fashionProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        fashionProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fashionProductsRecyclerVIew.setLayoutManager(fashionProductsLinearLayoutManager);

        beautyProductsRecyclerView = (RecyclerView) findViewById(R.id.beautyProductsRecyclerView);
        beautyProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager beautyProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        beautyProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        beautyProductsRecyclerView.setLayoutManager(beautyProductsLinearLayoutManager);

        furnitureProductsRecyclerView = (RecyclerView) findViewById(R.id.furnitureProductsRecyclerView);
        furnitureProductsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager furnitureProductsLinearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        furnitureProductsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        furnitureProductsRecyclerView.setLayoutManager(furnitureProductsLinearLayoutManager);

        homeProgressLinearLayout = (LinearLayout) findViewById(R.id.homeProgressLinearLayout);
        dealsLinearLayout = (LinearLayout) findViewById(R.id.dealsLinearLayout);
        latestLinearLayout = (LinearLayout) findViewById(R.id.latestLinearLayout);
        mobilesLinearLayout = (LinearLayout) findViewById(R.id.mobilesLinearLayout);

        //homeNeedsLL = (LinearLayout)findViewById(R.id.homeNeedsLLID);
        recommendedLinearLayout = (LinearLayout) findViewById(R.id.recommendedLinearLayout);
        foodLinearLayout = (LinearLayout) findViewById(R.id.foodLinearLayout);
        furnitureLinearLayout = (LinearLayout) findViewById(R.id.furnitureLinearLayout);
        fashionLinearLayout = (LinearLayout) findViewById(R.id.fashionLinearLayout);
        beautyLinearLayout = (LinearLayout) findViewById(R.id.beautyLinearLayout);

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
                                  JSONArray latest_productsJson = response.getJSONArray("fruits-vegetables");
                                  JSONArray recommend_productsJson = response.getJSONArray("Beverages");
                                  JSONArray mobiles_productsJson = response.getJSONArray("Kitchen_&_Dining");
                                  JSONArray food_productsJson = response.getJSONArray("Kitchen_Storage");
                                  JSONArray beauty_productsJson = response.getJSONArray("Dining_&_Serving");
                                  //JSONArray fashion_productsJson = jsonResponse.getJSONArray("fashion_products");
                                //    JSONArray furniture_productsJson = jsonResponse.getJSONArray("furniture_products");

                                /****** FASHION PRODUCTS ******* Process each JSON node ************/
//                            if (fashion_productsJson.length() > 0) {
//                                homeProductsFashionList.clear();
//                                homeProductsFashionList = null;
//                                homeProductsFashionList = new ArrayList<HomeProductsModel>();
//
//                                for (int i = 0; i < fashion_productsJson.length(); i++) {
//                                    /****** Get Object for each JSON node.***********/
//                                    JSONObject jsonChildNode = fashion_productsJson.getJSONObject(i);
//
//                                    /******* Fetch node values **********/
//                                    String product_id = jsonChildNode.optString("product_id");
//                                    String product_name = jsonChildNode.optString("product_name");
//                                    String price = jsonChildNode.optString("price");
//                                    String regular_price = jsonChildNode.optString("regular_price");
//                                    String imageUrl = jsonChildNode.optString("image");
//                                    String vendor_name = jsonChildNode.optString("vendor_name");
//                                    //String vendor_description = jsonChildNode.optString("vendor_description");
//                                    String rating = jsonChildNode.optString("rating");
//                                    //String product_desc = jsonChildNode.optString("product_desc");
//                                    homeProductsFashionList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                                }
//
//                                OutputData = "" + homeProductsFashionList.size();
//                                Log.i("JSON parse Item Count", OutputData);
//                            }
//
                            /****** BEAUTY PRODUCTS ******* Process each JSON node ************/
                            if (beauty_productsJson.length() > 0) {
                                homeProductsBeautyList.clear();
                                homeProductsBeautyList = null;
                                homeProductsBeautyList = new ArrayList<HomeProductsModel>();

                                for (int i = 0; i < beauty_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = beauty_productsJson.getJSONObject(i);

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
                                    homeProductsBeautyList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsBeautyList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }
//
//                            /****** FURNITURE PRODUCTS ******* Process each JSON node ************/
//                            if (furniture_productsJson.length() > 0) {
//                                homeProductsFurnituresList.clear();
//                                homeProductsFurnituresList = null;
//                                homeProductsFurnituresList = new ArrayList<HomeProductsModel>();
//
//                                for (int i = 0; i < furniture_productsJson.length(); i++) {
//                                    /****** Get Object for each JSON node.***********/
//                                    JSONObject jsonChildNode = furniture_productsJson.getJSONObject(i);
//
//                                    /******* Fetch node values **********/
//                                    String product_id = jsonChildNode.optString("product_id");
//                                    String product_name = jsonChildNode.optString("product_name");
//                                    String price = jsonChildNode.optString("price");
//                                    String regular_price = jsonChildNode.optString("regular_price");
//                                    String imageUrl = jsonChildNode.optString("image");
//                                    String vendor_name = jsonChildNode.optString("vendor_name");
//                                    //String vendor_description = jsonChildNode.optString("vendor_description");
//                                    String rating = jsonChildNode.optString("rating");
//                                    //String product_desc = jsonChildNode.optString("product_desc");
//                                    homeProductsFurnituresList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                                }
//
//                                OutputData = "" + homeProductsFurnituresList.size();
//                                Log.i("JSON parse Item Count", OutputData);
//                            }

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

                                if (latest_productsJson.length() > 0) {
                                    homeProductsLatestList.clear();
                                    homeProductsLatestList = null;
                                    homeProductsLatestList = new ArrayList<HomeProductsModel>();

                                    for (int i = 0; i < latest_productsJson.length(); i++) {
                                        /****** Get Object for each JSON node.***********/
                                        JSONObject jsonChildNode = latest_productsJson.getJSONObject(i);

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
                                        homeProductsLatestList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                    }

                                    OutputData = "" + homeProductsLatestList.size();
                                    Log.i("JSON parse Item Count", OutputData);
                                }

                                if (recommend_productsJson.length() > 0) {
                                    homeProductsRecommendList.clear();
                                    homeProductsRecommendList = null;
                                    homeProductsRecommendList = new ArrayList<HomeProductsModel>();

                                    for (int i = 0; i < recommend_productsJson.length(); i++) {
                                        /****** Get Object for each JSON node.***********/
                                        JSONObject jsonChildNode = recommend_productsJson.getJSONObject(i);

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
                                        homeProductsRecommendList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                    }

                                    OutputData = "" + homeProductsRecommendList.size();
                                    Log.i("JSON parse Item Count", OutputData);
                                }
                                if (mobiles_productsJson.length() > 0) {
                                    homeProductsMobilesList.clear();
                                    homeProductsMobilesList = null;
                                    homeProductsMobilesList = new ArrayList<HomeProductsModel>();

                                    for (int i = 0; i < mobiles_productsJson.length(); i++) {
                                        /****** Get Object for each JSON node.***********/
                                        JSONObject jsonChildNode = mobiles_productsJson.getJSONObject(i);

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
                                        homeProductsMobilesList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                    }

                                    OutputData = "" + homeProductsMobilesList.size();
                                    Log.i("JSON parse Item Count", OutputData);
                                }

                                if (food_productsJson.length() > 0) {
                                    homeProductsFoodsList.clear();
                                    homeProductsFoodsList = null;
                                    homeProductsFoodsList = new ArrayList<HomeProductsModel>();

                                    for (int i = 0; i < food_productsJson.length(); i++) {
                                        /****** Get Object for each JSON node.***********/
                                        JSONObject jsonChildNode = food_productsJson.getJSONObject(i);

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
                                        homeProductsFoodsList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                    }

                                    OutputData = "" + homeProductsFoodsList.size();
                                    Log.i("JSON parse Item Count", OutputData);
                                }


                                /****** LATEST PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < latest_productsJson.length(); i++) {
//                                /****** Get Object for each JSON node.***********/
//                                JSONObject jsonChildNode = latest_productsJson.getJSONObject(i);
//
//                                /******* Fetch node values **********/
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsLatestList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsLatestList.size();
//                            Log.i("JSON parse Item Count", OutputData);
//
//                            /****** RECOMMENDED PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < recommend_productsJson.length(); i++) {
//                                /****** Get Object for each JSON node.***********/
//                                JSONObject jsonChildNode = recommend_productsJson.getJSONObject(i);
//
//                                /******* Fetch node values **********/
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsRecommendList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsRecommendList.size();
//                            Log.i("JSON parse Item Count", OutputData);
//
//                            /****** MOBILES PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < mobiles_productsJson.length(); i++) {
//                                /****** Get Object for each JSON node.***********/
//                                JSONObject jsonChildNode = mobiles_productsJson.getJSONObject(i);
//
//                                /******* Fetch node values **********/
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsMobilesList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsMobilesList.size();
//                            Log.i("JSON parse Item Count", OutputData);
//
//                            /****** FOOD PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < food_productsJson.length(); i++) {
//                                //****** Get Object for each JSON node.***********//*
//                                JSONObject jsonChildNode = food_productsJson.getJSONObject(i);
//
//                                //******* Fetch node values **********//*
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsFoodsList.add(new HomeProductsModel(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsFoodsList.size();
//                            Log.i("JSON parse Item Count", OutputData);

                                homeProductsHomeAll.add(new HomeAllProductsModel(homeProductsDealsList, homeProductsLatestList, homeProductsRecommendList, homeProductsMobilesList, homeProductsFoodsList));

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

                                        if (homeProductsMobilesList.size() > 0) {
                                            HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsMobilesList);
                                            mobilesProductsRecyclerView.setAdapter(adapter1);
                                            mobilesLinearLayout.setVisibility(View.VISIBLE);
                                            adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsMobilesList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsMobilesList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsMobilesList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsMobilesList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsMobilesList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsMobilesList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsMobilesList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsMobilesList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }

                                        if (homeProductsBeautyList.size() > 0) {
                                            HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsBeautyList);
                                            beautyProductsRecyclerView.setAdapter(adapter1);
                                            beautyLinearLayout.setVisibility(View.VISIBLE);
                                            adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                                    sportIntent.putExtra("PRODUCT_ID", homeProductsBeautyList.get(position).getProductId());
                                                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                    sportIntent.putExtra("PRODUCT_NAME", homeProductsBeautyList.get(position).getProductName());
                                                    sportIntent.putExtra("PRODUCT_PRICE", homeProductsBeautyList.get(position).getPrice());
                                                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsBeautyList.get(position).getRegularPrice());
                                                    sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsBeautyList.get(position).getImageUrl());
                                                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsBeautyList.get(position).getVendorName());
                                                    sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsBeautyList.get(position).getProductDesc());
                                                    sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsBeautyList.get(position).getVendorDescription());
                                                    startActivity(sportIntent);
                                                }
                                            });
                                        }

                                        if (homeProductsLatestList.size() > 0) {
                                            HomeProductsAdapter adapter2 = new HomeProductsAdapter(HomeActivity.this, homeProductsLatestList);
                                            latestProductsRecyclerView.setAdapter(adapter2);
                                            latestLinearLayout.setVisibility(View.VISIBLE);
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

                                        if (homeProductsFoodsList.size() > 0) {
                                            HomeProductsAdapter adapter4 = new HomeProductsAdapter(HomeActivity.this, homeProductsFoodsList);
                                            foodProductsRecyclerView.setAdapter(adapter4);
                                            foodLinearLayout.setVisibility(View.VISIBLE);

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
