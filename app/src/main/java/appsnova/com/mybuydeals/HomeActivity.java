package appsnova.com.mybuydeals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.SliderLayout;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    //ArrayList<HomeProductsData> homeProductsHomesList = new ArrayList<HomeProductsData>();
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, UrlUtility.HOME_PRODUCTS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: "+response.toString());
                        try {
                            if (response.has("deals_products")){
                                JSONArray dealsJsonArray = response.getJSONArray("deals_products");
                                for (int i=0; i<dealsJsonArray.length(); i++){

                                }

                            }

                        } catch (JSONException e) {
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
