package appsnova.com.mybuydeals;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;

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
import appsnova.com.mybuydeals.adapters.NavMenuAdapter;
import appsnova.com.mybuydeals.adapters.ViewPagerAdapter;
import appsnova.com.mybuydeals.models.HomeChildModel;
import appsnova.com.mybuydeals.models.HomeProductsModel;
import appsnova.com.mybuydeals.models.LoginDetailsModel;
import appsnova.com.mybuydeals.models.NavMenuModel;
import appsnova.com.mybuydeals.models.SliderImageModel;
import appsnova.com.mybuydeals.ownlibraries.MaterialProgressWheel;
import appsnova.com.mybuydeals.utilities.DatabaseHelper;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getName();

    //create utility objects
    NetworkUtils networkUtils;
    SharedPref sharedPref;
    DatabaseHelper databaseHelper;


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
    NestedScrollView homeMainScrollView;
    TextView homeSearch;
    MaterialProgressWheel progressVIew;

    //create NavMenu View Object
    RecyclerView navHomeRecyclerView;
    NavMenuAdapter navMenuAdapter;
    List<NavMenuModel> navMenuModelList;
    NavMenuModel navMenuModel;

    int scrollY;
    static Button notifCount;
    static int mNotifCount = 5;

    private static final int TIME_DELAY = 5000;
    private static long back_pressed;

    HomeChildModel homeChildModel;
    HomeProductsModel homeProductsModel ;
    HomeProductsAdapter homeProductsAdapter;
    List<HomeProductsModel> homeProductsModelList;
    ArrayList<HomeChildModel> homeChildModelArrayList;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize utils objects
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);
        //lists object instantiate
        sliderImageModelArrayList = new ArrayList<>();
        homeProductsModelList = new ArrayList<>();

        navMenuModelList = new ArrayList<>();

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
        sliderImageModelArrayList = populateImagesSliding();
        initSlider();
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(sliderImageModelArrayList,this);
        homeViewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //get Slider Images list

    } //end of onCreate

    private void initialization(){

        //initialize ViewObjects
        homeSearch = (TextView) findViewById(R.id.homeSearch);
        homeProductsRecyclerView = findViewById(R.id.homeProductsRecyclerView);
        progressVIew = findViewById(R.id.progressVIew);

        homeMainScrollView = (NestedScrollView) findViewById(R.id.homeMainScrollView);
        homeProductsRelativeLayout = findViewById(R.id.homeProductsRelativeLayout);
        homeProgressLinearLayout = findViewById(R.id.homeProgressLinearLayout);
        navHomeRecyclerView = findViewById(R.id.navHomeRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        homeProductsRecyclerView.setLayoutManager(linearLayoutManager);
        homeProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager navMenuLLM = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        navHomeRecyclerView.setLayoutManager(navMenuLLM);
        navHomeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navHomeRecyclerView.setHasFixedSize(true);
        navHomeRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        homeMainScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                scrollY = homeMainScrollView.getScrollY(); //for verticalScrollView
                invalidateOptionsMenu();
                //DO SOMETHING WITH THE SCROLL COORDINATES
            }
        });

        if (networkUtils.checkConnection()) {
            getNavMenus();
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

    private void getNavMenus(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, UrlUtility.ALL_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response !=null){
                    Log.d(TAG, "onResponse: "+response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++){
                            navMenuModel = new NavMenuModel();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            navMenuModel.setCategoryName(jsonObject.getString("name"));
                            navMenuModel.setCatId(jsonObject.getString("cat_id"));
                            navMenuModel.setImageUrl(jsonObject.getString("image_url"));

                            navMenuModelList.add(navMenuModel);

                        }
                        navMenuAdapter = new NavMenuAdapter(navMenuModelList, HomeActivity.this);
                        navHomeRecyclerView.setAdapter(navMenuAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().addToRequestQueue(stringRequest);
    }

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
       /* homeViewPager.setAdapter(new PagerAdapter() {
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
        });*/

      /*  CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.slider);

        indicator.setViewPager(homeViewPager);*/

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
      //  indicator.setRadius(5 * density);

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

       /* // Pager listener over indicator
        indicator.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

    } //end of initSlider

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1112) {
            if (data != null) {
                String userEmailStr = data.getStringExtra("USER_EMAIL");
                if (userEmailStr != null && !userEmailStr.equalsIgnoreCase("Not_Login")) {
                    sharedPref.setStringValue("FROM_SCREEN_USER", "MY_ACCOUNT");
                    startActivity(new Intent(HomeActivity.this, ShippingAddressActivity.class));
                }
            }
        }
    }

    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_account:
                 databaseHelper = new DatabaseHelper(HomeActivity.this);
                LoginDetailsModel loginDetails = databaseHelper.getLoginDetails();
                if (loginDetails != null && loginDetails.getUserEmail() != null) {
                    sharedPref.setStringValue( "FROM_SCREEN_USER", "MY_ACCOUNT");
                    startActivity(new Intent(HomeActivity.this, ShippingAddressActivity.class));
                } else {
                    Intent resultss = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivityForResult(resultss, 1112);
                }
                return true;
            case R.id.action_cart:
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
                return true;
            case R.id.action_my_wishlist:
                startActivity(new Intent(HomeActivity.this, WishListActivity.class));
                return true;
            case R.id.action_search:
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}