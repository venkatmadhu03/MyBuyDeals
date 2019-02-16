package appsnova.com.mybuydeals;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import appsnova.com.mybuydeals.adapters.HomeProductsAdapter;
import appsnova.com.mybuydeals.models.HomeChildModel;
import appsnova.com.mybuydeals.models.HomeProductsModel;
import appsnova.com.mybuydeals.models.SliderImageModel;
import appsnova.com.mybuydeals.ownlibraries.MaterialProgressWheel;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getName();

    //create utility objects
    NetworkUtils networkUtils;
    SharedPref sharedPref;

    //Slider Object creation
    private static ViewPager homeViewPager;
    private CirclePageIndicator slider;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<SliderImageModel> sliderImageModelArrayList;
    private int[] myImageList = new int[]{R.drawable.banner1, R.drawable.banner2,
            R.drawable.banner3,R.drawable.banner4};

    //create View Objects
    RecyclerView homeProductsRecyclerView;
    RelativeLayout homeProductsRelativeLayout;
    LinearLayout homeProgressLinearLayout;

    ScrollView homeMainScrollView;
    TextView homeSearch;

    MaterialProgressWheel progressVIew;

    int scrollY;
    static Button notifCount;
    static int mNotifCount = 5;

    HomeChildModel homeChildModel;
    HomeProductsModel homeProductsModel ;
    HomeProductsAdapter homeProductsAdapter;
    List<HomeProductsModel> homeProductsModelList;
    ArrayList<HomeChildModel> homeChildModelArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize utils objects
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);
        //lists object instantiate
        sliderImageModelArrayList = new ArrayList<>();
        homeProductsModelList = new ArrayList<>();

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

        //initialize ViewObjects
        homeSearch = (TextView) findViewById(R.id.homeSearch);
        homeProductsRecyclerView = findViewById(R.id.homeProductsRecyclerView);
        progressVIew = findViewById(R.id.progressVIew);

        homeMainScrollView = (ScrollView) findViewById(R.id.homeMainScrollView);
        homeProductsRelativeLayout = findViewById(R.id.homeProductsRelativeLayout);
        homeProgressLinearLayout = findViewById(R.id.homeProgressLinearLayout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        homeProductsRecyclerView.setLayoutManager(linearLayoutManager);
        homeProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());


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
    } //end of Initialization

    private ArrayList<SliderImageModel> populateImagesSliding(){

        ArrayList<SliderImageModel> list = new ArrayList<>();

        for(int i = 0; i < myImageList.length; i++){
            SliderImageModel imageModel = new SliderImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    } //end of populateImageSliding

    private void getProductsFromServer(){
        StringRequest stringRequest =new StringRequest(Request.Method.GET, UrlUtility.HOME_PRODUCTS1_URL, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.d("HomeProducts", "onResponse: "+response);
                if (response !=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int count = jsonObject.getInt("count");
                        homeProductsRelativeLayout.setVisibility(View.VISIBLE);
                        for (int i=0; i<count; i++){
                            JSONArray jsonArray = jsonObject.getJSONArray(String.valueOf(i));
                            Log.d(TAG, "onResponse: "+jsonArray.length());
                            if (jsonArray.length() !=0){
                                homeChildModelArrayList = new ArrayList<>();
                                for (int j=0; j<jsonArray.length(); j++){
                                    Log.d("SubCats", "onResponse: "+jsonArray.length());
                                    homeProductsModel = new HomeProductsModel();
                                    homeChildModel = new HomeChildModel();
                                    JSONObject subcatObjects = jsonArray.getJSONObject(j);

                                    homeProductsModel.setCategory_name(subcatObjects.getString("category_name"));
                                    homeProductsModel.setCategory_slug(subcatObjects.getString("category_slug"));
                                    homeProductsModel.setCat_id(subcatObjects.getString("category_id"));
                                    homeChildModel.setCategory_name(subcatObjects.getString("category_name"));
                                    homeChildModel.setCategory_id(subcatObjects.getString("category_id"));
                                    homeChildModel.setCategory_slug(subcatObjects.getString("category_slug"));
                                    homeChildModel.setDiscount_in_per(subcatObjects.getString("discount_in_per"));
                                    homeChildModel.setId(subcatObjects.getString("id"));
                                    homeChildModel.setImage(subcatObjects.getString("image"));
                                    homeChildModel.setImage_extension(subcatObjects.getString("image_extension"));
                                    homeChildModel.setName(subcatObjects.getString("name"));
                                    homeChildModel.setOffer_end(subcatObjects.getString("offer_end"));
                                    homeChildModel.setOffer_start(subcatObjects.getString("offer_start"));
                                    homeChildModel.setSpecial_offer(subcatObjects.getString("special_offer"));
                                    homeChildModel.setPrice(subcatObjects.getString("price"));
                                    homeChildModel.setRegular_price(subcatObjects.getString("regular_price"));
                                    homeChildModel.setPrice_type(subcatObjects.getString("price_type"));
                                    homeChildModel.setSlug(subcatObjects.getString("slug"));
                                    homeChildModel.setPrice_per_primeuser(subcatObjects.getString("price_per_primeuser"));
                                    homeChildModel.setPrime_user_discount(subcatObjects.getString("prime_user_discount"));
                                    homeChildModelArrayList.add(homeChildModel);
                                }
                                homeProductsModel.setChildModelArrayList(homeChildModelArrayList);
                                Log.d("SubCats", "onResponse: ChildSize "+homeProductsModel.getChildModelArrayList().size());
                                homeProductsModelList.add(homeProductsModel);
                            }
                        }
                        homeProductsAdapter = new HomeProductsAdapter(HomeActivity.this,homeProductsModelList);
                        homeProductsRecyclerView.setAdapter(homeProductsAdapter);
                        homeProductsAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    homeProgressLinearLayout.setVisibility(View.GONE);

                }else{
                    homeProgressLinearLayout.setVisibility(View.GONE);
                    homeProductsRelativeLayout.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HomeProducts", "onErrorResponse: "+error.toString());
                homeProgressLinearLayout.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "OOPS!! Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().addToRequestQueue(stringRequest);
    } //end of getProductsFromServer

    private void initSlider() {

        homeViewPager = findViewById(R.id.homeViewPager);
        homeViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return sliderImageModelArrayList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup view, int position) {
                LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
                View imageLayout = layoutInflater.inflate(R.layout.sliding_images_layout, view, false);

                assert imageLayout != null;
                final ImageView imageView = imageLayout.findViewById(R.id.sliding_imageview);
                imageView.setImageResource(sliderImageModelArrayList.get(position).getImage_drawable());
                view.addView(imageLayout, 0);
                return imageLayout;
            }

            @Override
            public void restoreState(Parcelable state, ClassLoader loader) {
            }

            @Override
            public Parcelable saveState() {
                return null;
            }
        });

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

    } //end of initSlider

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