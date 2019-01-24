package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import appsnova.com.mybuydeals.models.HomeProductsModel;
import appsnova.com.mybuydeals.models.LoginDetailsModel;
import appsnova.com.mybuydeals.utilities.DatabaseHelper;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    //Utils Creation
    ProgressDialog progressDialog;
    NetworkUtils networkUtils;
    SharedPref sharedPref;

    ArrayList<HomeProductsModel> relatedProductsList = new ArrayList<HomeProductsModel>();
    ArrayList<String> sizesLists = new ArrayList<String>();
    ArrayList<String> colorsList = new ArrayList<String>();

    //createViewObjects
    LinearLayout relatedProductsLinearLayout, productInfoLinearLayout, addToCartLinearLayout,
            addWishListLinearLayout, shareLinearLayout, sizesLinearLayout, colorsLinearLayout, buyNowLinearLayout;
    RecyclerView relatedProductsRecyclerView;
    TextView vendorNameTextView, productNameTextView, priceTextView, regularPriceTextView,
            stockAvailabilityTextView, galleryCountTextView, buyNowTextView;
    ImageView productDetailSlider, wishListImageView;
    Button pinCheckButton;
    EditText quantitiesEdittext,pinCodeCheckEdittext ;
    Spinner sizesListSpinner, colorsListSpinner;
    //header Views
    RelativeLayout headerRelativeLayout, backallRelativeLayout;
    TextView headerTitleTextView, titleSubTVID;
    Button backButton, cartButton;

    String  productId = "", productName = "", productPrice = "", productDescription="",
            pinCode="", productImageUrl="", productRegularPrice="", productRating = "", productStock="",
            productAvailability="",
            productVendorName = "",productVendorDescription="" , vendorEmail = "", vendorId = "", vendorNumber="";


    DatabaseHelper dbHelper = new DatabaseHelper(ProductDetailsActivity.this);
    LoginDetailsModel loginDetails;
    Bundle bundle=null;

    InputMethodManager keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_product_details);

      //  if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                String fromScreenStacks = sharedPref.getStringValue( "PRODUCT_FROM_SCREEN");
                Intent bookingDoneIntent = null;
                if (fromScreenStacks != null && fromScreenStacks.equalsIgnoreCase("HOME_MAIN")) {
                    bookingDoneIntent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                } else {
                    bookingDoneIntent = new Intent(ProductDetailsActivity.this, ProductListActivity.class);
                    /*else if (fromScreenStacks != null && fromScreenStacks.equalsIgnoreCase("GRID_PRODUCTS")) {
                    bookingDoneIntent = new Intent(ProductDetailsActivity.this, ProductsListActivity.class);
                }*/
                }
                if (bookingDoneIntent != null) {
                    bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bookingDoneIntent.putExtra("EXIT", true);
                    startActivity(bookingDoneIntent);
                }
                onBackPressedAnimationByCHK();
            } else {
                StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                        .permitDiskWrites()
                        .detectAll()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .permitAll()
                        .penaltyDeath()
                        .build());
                StrictMode.setThreadPolicy(old);

                UrlUtility.setDimensions(this);

                //getIntent values from homePage
//                bundle = getIntent().getExtras();
//                if (bundle !=null){
//                    if (bundle.getString("PRODUCT_FROM_SCREEN").equalsIgnoreCase("HOME_MAIN")){
//                        productId =bundle.getString("PRODUCT_ID");
//                        productName = bundle.getString("PRODUCT_NAME");
//                        productDescription = bundle.getString("PRODUCT_DESCRIPTION");
//                        productPrice = bundle.getString("PRODUCT_PRICE");
//                        productRegularPrice = bundle.getString("PRODUCT_REGULAR_PRICE");
//                        productImageUrl = bundle.getString("PRODUCT_IMAGE_URL");
//                        productVendorName = bundle.getString("PRODUCT_VENDOR_NAME");
//                        productVendorDescription = bundle.getString("VENDOR_DESCRIPTION");
//
//                    }
//                }

                //initialize header views
                headerRelativeLayout = findViewById(R.id.headerRelativeLayout);
                backallRelativeLayout = findViewById(R.id.backallRelativeLayout);
                headerTitleTextView = findViewById(R.id.headerTitleTextView);
                titleSubTVID =  findViewById(R.id.titleSubTVID);
                backButton =  findViewById(R.id.backButton);
                cartButton =  findViewById(R.id.cartButton);

                setupNavigation();

                keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                //initialize productdetails views
                productDetailSlider = findViewById(R.id.productDetailSlider);
                productNameTextView=  findViewById(R.id.productNameTextView);
                priceTextView = findViewById(R.id.priceTextView);
                regularPriceTextView =  findViewById(R.id.regularPriceTextView);
                pinCheckButton = findViewById(R.id.pinCheckButton);
                pinCodeCheckEdittext = findViewById(R.id.pinCodeCheckEdittext);
                buyNowTextView = findViewById(R.id.buyNowTextView);
                buyNowLinearLayout = findViewById(R.id.buyNowLinearLayout);
                sizesLinearLayout =  findViewById(R.id.sizesLinearLayout);
                colorsLinearLayout = findViewById(R.id.colorsLinearLayout);
                sizesListSpinner =  findViewById(R.id.sizesListSpinner);
                colorsListSpinner =  findViewById(R.id.colorsListSpinner);
                vendorNameTextView =  findViewById(R.id.vendorNameTextView);
                stockAvailabilityTextView = findViewById(R.id.stockAvailabilityTextView);
                galleryCountTextView = findViewById(R.id.galleryCountTextView);
                productInfoLinearLayout =  findViewById(R.id.productInfoLinearLayout);
                quantitiesEdittext = findViewById(R.id.quantitiesEdittext);
                addToCartLinearLayout =  findViewById(R.id.addToCartLinearLayout);
                wishListImageView =  findViewById(R.id.wishListImageView);
                addWishListLinearLayout =  findViewById(R.id.addWishListLinearLayout);
                shareLinearLayout =  findViewById(R.id.shareLinearLayout);
                relatedProductsLinearLayout = (LinearLayout) findViewById(R.id.relatedProductsLinearLayout);
                relatedProductsRecyclerView = (RecyclerView) findViewById(R.id.relatedProductsRecyclerView);

                relatedProductsLinearLayout.setVisibility(View.GONE);
                relatedProductsRecyclerView.setHasFixedSize(true);
                LinearLayoutManager dealslm = new LinearLayoutManager(ProductDetailsActivity.this);
                dealslm.setOrientation(LinearLayoutManager.HORIZONTAL);
                relatedProductsRecyclerView.setLayoutManager(dealslm);

              //  String pinCode = MySharedPreference.getPreferences(ProductDetailsActivity.this, "PIN_CODE");
                if (pinCode!= null && pinCode.trim().length()>2) {
                    pinCodeCheckEdittext.setText(""+pinCode);
                    pinCheckButton.setText("CHANGE");
                }

                productDetailSlider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UrlUtility.galleriesList!=null && UrlUtility.galleriesList.size()>0) {
                            startActivity(new Intent(ProductDetailsActivity.this, FullScreenViewActivity.class));
                        }
                    }
                });


                pinCheckButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pinCheckButton.getText().toString().equalsIgnoreCase("CHANGE")) {
                            pinCodeCheckEdittext.setText("");
                            pinCheckButton.setText("CHECK");
                          //  MySharedPreference.setPreference(ProductDetailsActivity.this, "PIN_CODE", "");
                        } else {
                            String pinCodeStr = pinCodeCheckEdittext.getText().toString();
                            if (pinCodeStr.trim().length()>2){
                                boolean isNetAvailable = networkUtils.checkConnection();
                                if (isNetAvailable) {
                                    try {
                                        progressDialog = ProgressDialog.show(ProductDetailsActivity.this, "Please wait ...", "Checking your pin code...", true);
                                        progressDialog.setCancelable(false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //POST PARAMETERS FOR CHECK PINCODE REQUEST
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("pincode", pinCodeStr);
                                    JSONObject jsonObject = new JSONObject(params);
                                    checkingPinCode(UrlUtility.PIN_CODE_CHECKING_URL, jsonObject, pinCodeStr);
                                } else {
                                    UrlUtility.showCustomToast(getString(R.string.no_connection), ProductDetailsActivity.this);
                                }
                            } else {
                                UrlUtility.showCustomToast("Please enter pin code!", ProductDetailsActivity.this);
                            }
                            keyboard.hideSoftInputFromWindow(pinCodeCheckEdittext.getWindowToken(), 0);
                        }
                    }
                });


                vendorNameTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
//                        Intent sportIntent = new Intent(ProductDetailsActivity.this, VendorProductsListActivity.class);
//                        sportIntent.putExtra("VENDOR_NAME", vendor_name);
//                        startActivity(sportIntent);
                    }
                });

                regularPriceTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
//                        if (regularPriceTV.getText().toString().equalsIgnoreCase("ASK FOR PRICE/COLLECT AT STORE")){
//                            loginDetails = dbHelper.getLoginDetails();
//                            if (loginDetails != null && loginDetails.getuserEmail() != null) {
//                                Intent sportIntent = new Intent(ProductDetailsActivity.this, ActivityInquiryPage.class);
//                                sportIntent.putExtra("PRODUCT_NAME", product_name);
//                                sportIntent.putExtra("TO_MAIL", vendor_email);
//                                sportIntent.putExtra("FROM_MAIL", loginDetails.getuserEmail());
//                                startActivity(sportIntent);
//                            } else {
//                                Intent resultss = new Intent(ProductDetailsActivity.this, ActivityLoginPage.class);
//                                startActivityForResult(resultss, 1111);
//                            }
//
//                        }
                    }
                });

                galleryCountTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (UrlUtility.galleriesList!=null && UrlUtility.galleriesList.size()>0) {
                            startActivity(new Intent(ProductDetailsActivity.this, FullScreenViewActivity.class));
                        }
                    }
                });

                addToCartLinearLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (stockAvailabilityTextView.getText().toString().equalsIgnoreCase("In stock")) {
                            String quantitiesETStr = quantitiesEdittext.getText().toString();
                            if (!quantitiesETStr.isEmpty() && quantitiesETStr.trim().length()>0) {
                                if (sizesLists.size()>0){
                                    String pickedSize = sizesListSpinner.getSelectedItem().toString();
                                    if (pickedSize!=null && !pickedSize.equalsIgnoreCase("Select")) {
                                   //     loginDetails= dbHelper.getLoginDetails();
                                   //     addToCartData(loginDetails, pickedSize);
                                    } else {
                                        UrlUtility.showCustomToast("Please select size!", ProductDetailsActivity.this);
                                    }
                                } else {
//                                    loginDetails= dbHelper.getLoginDetails();
//                                    addToCartData(loginDetails, "");
                                }
                            } else {
                                UrlUtility.showCustomToast("Please enter quantity!", ProductDetailsActivity.this);
                            }
                        } else {
                            UrlUtility.showCustomToast("Out of stock!", ProductDetailsActivity.this);
                        }
                    }
                });

                addWishListLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        loginDetails= dbHelper.getLoginDetails();
//                        addToWishListData(loginDetails);
                    }
                });

                shareLinearLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        try {
                            String shareBody = "I recommend to View '" + productName
                                    + "' in MyBuyDeals APP. \nI Hope you get at best price in comparison with the market. \n "
                                    + "Download Android App:\n"+"https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();

                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MyBuyDeals (Open it in Google Play Store to Download the Application)");

                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        } catch (ArrayIndexOutOfBoundsException ee) {
                            ee.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                //get Intent Values
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    String PRODUCT_FROM_SCREEN = b.getString("PRODUCT_FROM_SCREEN");
                    sharedPref.setStringValue("PRODUCT_FROM_SCREEN", PRODUCT_FROM_SCREEN);
                    productId = b.getString("PRODUCT_ID");
                    if (networkUtils.checkConnection()) {
                        try {
                            progressDialog = ProgressDialog.show(ProductDetailsActivity.this, "Please wait ...", "Loading data...", true);
                            progressDialog.setCancelable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getProductFullDeatils(UrlUtility.PRODUCT_FULL_DETAILS_URL + "" + productId);
                        //realtedProductsLoading(AppConstants.RELATED_PRODUCTS_URL + "" + productId);
                    } else {
                        UrlUtility.showCustomToast(getResources().getString(R.string.no_connection), ProductDetailsActivity.this);
                        finish();
                    }
                }

                buyNowTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (stockAvailabilityTextView.getText().toString().equalsIgnoreCase("In stock")) {
                           // String pinCode = MySharedPreference.getPreferences(ProductDetailsActivity.this, "PIN_CODE");
                            if (pinCode!= null && pinCode.trim().length()>2) {
                                String quantitiesETStr = quantitiesEdittext.getText().toString();
                                if (quantitiesETStr != null && !quantitiesETStr.isEmpty() && quantitiesETStr.trim().length()>0) {
                                    if (sizesLists.size()>0){
                                        String pickedSize = sizesListSpinner.getSelectedItem().toString();
                                        if (pickedSize!=null && !pickedSize.equalsIgnoreCase("Select")) {
//                                            loginDetails= dbHelper.getLoginDetails();
//                                            addToCartData(loginDetails, pickedSize);
//                                            MySharedPreference.setPreference(ProductDetailsActivity.this, "FROM_SCREEN_USER", "CART");
//                                            if (loginDetails != null) {
//                                                startActivity(new Intent(ProductDetailsActivity.this, ShippingAddressScreenActivity.class));
//                                            } else {
//                                                Intent resultss = new Intent(ProductDetailsActivity.this, ActivityLoginPage.class);
//                                                startActivityForResult(resultss, 1112);
//                                            }
                                        } else {
                                            UrlUtility.showCustomToast("Please select size!", ProductDetailsActivity.this);
                                        }
                                    } else {
//                                        loginDetails= dbHelper.getLoginDetails();
//                                        addToCartData(loginDetails, "");
//                                        MySharedPreference.setPreference(ProductDetailsActivity.this, "FROM_SCREEN_USER", "CART");
//                                        if (loginDetails != null) {
//                                            startActivity(new Intent(ProductDetailsActivity.this, ShippingAddressScreenActivity.class));
//                                        } else {
//                                            Intent resultss = new Intent(ProductDetailsActivity.this, ActivityLoginPage.class);
//                                            startActivityForResult(resultss, 1112);
//                                        }
                                    }
                                } else {
                                    UrlUtility.showCustomToast("Please enter quantity!", ProductDetailsActivity.this);
                                }
                            } else {
                                UrlUtility.showCustomToast("Please check pin code availability.", ProductDetailsActivity.this);
                            }
                        } else {
                            UrlUtility.showCustomToast("Out of stock!", ProductDetailsActivity.this);
                        }
                    }
                });
            }
       // }//end of If(getIntent())
    }//end of onCreate()

    private void getProductFullDeatils(String produtDetailsUrl){
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, produtDetailsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("ProductDetails", "onResponse: "+response.toString());
                for (int i=0; i<response.length(); i++){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = response.getJSONObject(i);
                        productId = jsonObject.getString("product_id");
                        productName = jsonObject.getString("product_name");
                        productDescription = jsonObject.getString("product_desc");
                        productPrice = jsonObject.getString("price");
                        productRegularPrice = jsonObject.getString("regular_price");
                        productImageUrl = jsonObject.getString("image");
                        productRating = jsonObject.getString("rating");
                        productStock = jsonObject.getString("stock");
                        productAvailability = jsonObject.getString("availability");
                        productVendorName = jsonObject.getString("vendor_name");
                        productVendorDescription = jsonObject.getString("vendor_description");
                        vendorEmail = jsonObject.getString("vendor_email");
                        vendorId = jsonObject.getString("vendor_id");
                        vendorNumber = jsonObject.getString("vendor_mobile");
                        JSONArray imagesArray = jsonObject.getJSONArray("gallary_images");
                        UrlUtility.galleriesList.clear();
                        UrlUtility.galleriesList = null;
                        UrlUtility.galleriesList = new ArrayList<String>();

                        for (int j = 0; j < imagesArray.length(); j++) {
                            String str_image_url = imagesArray.getString(j);
                            if (str_image_url!=null && !str_image_url.isEmpty() && !str_image_url.equalsIgnoreCase("null")){
                                UrlUtility.galleriesList.add(str_image_url);
                                Log.d("ImageUrl", "onResponse: "+str_image_url);
                                Picasso.get()
                                        .load(str_image_url)
                                        .into(productDetailSlider);
                            }
                        }

                        if (UrlUtility.galleriesList.size()>0){
                            galleryCountTextView.setVisibility(View.VISIBLE);
                            galleryCountTextView.setText(""+UrlUtility.galleriesList.size());
                        } else {
                            galleryCountTextView.setVisibility(View.GONE);
                        }

                        JSONArray product_attributes = jsonObject.getJSONArray("product_attributes");
                        Log.v("GaleeryImages", "" + imagesArray.length());

//                        if (product_attributes!=null) {
//                            JSONArray sizesJson = new JSONArray();
//                            JSONArray colorsJson = new JSONArray();
//
//                            if (product_attributes.toString().contains("pa_size")){
//                                sizesJson = product_attributes.getJSONArray("pa_size");
//                            } else {
//                                if (product_attributes.toString().contains("size")){
//                                    sizesJson= product_attributes.getJSONArray("size");
//                                }
//                            }
//                            for (int k = 0; k < sizesJson.length(); k++) {
//                                String str_size = sizesJson.getString(k);
//                                if (str_size!=null && !str_size.isEmpty() && !str_size.equalsIgnoreCase("null")){
//                                    if (k == 0){
//                                        sizesLists.add("Select");
//                                    } else {
//                                        sizesLists.add(str_size);
//                                    }
//                                }
//                            }
//
//                            Log.v("ProductDetails", "Item Sizes Count: " + sizesLists.size());
//                            if (sizesLists!=null && sizesLists.size() >0){
//                                sizesLinearLayout.setVisibility(View.VISIBLE);
//                                // Creating adapter for spinner
//                                ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_spinner_item, sizesLists);
//                                // Drop down layout style - list view with radio button
//                                monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                // attaching data adapter to spinner
//                                sizesListSpinner.setAdapter(monthAdapter);
//                            } else {
//                                sizesLinearLayout.setVisibility(View.GONE);
//                            }
//
//                            if (product_attributes.toString().contains("pa_colour")){
//                                colorsJson = product_attributes.getJSONArray("pa_colour");
//                            } else if (product_attributes.toString().contains("colour")){
//                                colorsJson= product_attributes.getJSONArray("colour");
//                            } else if (product_attributes.toString().contains("color")){
//                                colorsJson= product_attributes.getJSONArray("color");
//                            } else if (product_attributes.toString().contains("pa_color")){
//                                colorsJson= product_attributes.getJSONArray("pa_color");
//                            }
//
//                            for (int l = 0; l < colorsJson.length(); l++) {
//                                String str_color = colorsJson.getString(l);
//                                if (str_color!=null && !str_color.isEmpty() && !str_color.equalsIgnoreCase("null")){
//                                    //if (i == 0){
//                                    //    colorsList.add("Select");
//                                    //} else {
//                                    colorsList.add(str_color);
//                                    //}
//                                }
//                            }
//
//                            Log.v("ColorsCount", "Item Colors Count : " + colorsList.size());
//                            if (colorsList!=null && colorsList.size() >0){
//                                colorsLinearLayout.setVisibility(View.VISIBLE);
//                                // Creating adapter for spinner
//                                ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_spinner_item, colorsList);
//                                // Drop down layout style - list view with radio button
//                                monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                // attaching data adapter to spinner
//                                colorsListSpinner.setAdapter(monthAdapter);
//                            } else {
//                                colorsLinearLayout.setVisibility(View.GONE);
//                            }
//                        }

                        try {
                            if (productAvailability!=null && !productAvailability.isEmpty() && !productAvailability.equalsIgnoreCase("null")){
                                if (productAvailability.equalsIgnoreCase("instock")){
                                    Log.v("productAvailability", "" + productAvailability);
                                    stockAvailabilityTextView.setText("In stock");
                                } else {
                                    Log.v("productAvailability", "" + productAvailability);
                                    stockAvailabilityTextView.setText("Out of stock");
                                    stockAvailabilityTextView.setTextColor(Color.parseColor("#FF9900"));
                                }
                            } else {
                                Log.v("productAvailability", "" + productAvailability);
                                stockAvailabilityTextView.setText("Out of stock");
                                stockAvailabilityTextView.setTextColor(Color.parseColor("#FF9900"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            stockAvailabilityTextView.setText("Out of stock");
                            stockAvailabilityTextView.setTextColor(Color.parseColor("#FF9900"));
                            Log.v("productAvailability", "" + e);
                        }

                        if (!productName.isEmpty() || productName != null  ) {
                            productNameTextView.setText(Html.fromHtml(productName));
                        }

                        if (productVendorName != null && !productVendorName.isEmpty()) {
                            vendorNameTextView.setText("By " + Html.fromHtml(productVendorName));
                        }

                        WebView descriptionTV = (WebView) findViewById(R.id.descriptionTextView);
                        //WebView vendorDescriptionWV = (WebView) findViewById(R.id.vendorDescriptionTVID);

                        if (productVendorDescription != null && !productVendorDescription.isEmpty() && !productVendorDescription.equalsIgnoreCase("null")) {
                            // loadInWebView(vendor_description, vendorDescriptionWV, "Seller");
                        } else {
                            //  aboutSellerInfoLL.setVisibility(View.GONE);
                        }

                        if (productDescription != null && !productDescription.isEmpty() && !productDescription.equalsIgnoreCase("null")) {
                            loadInWebView(productDescription, descriptionTV, "Product");
                        } else {
                            productInfoLinearLayout.setVisibility(View.GONE);
                        }

                        if (productPrice != null &&
                                !productPrice.isEmpty() &&
                                !productPrice.equalsIgnoreCase("null") &&
                                !productPrice.equalsIgnoreCase("0")) {

                            priceTextView.setVisibility(View.VISIBLE);
                            buyNowLinearLayout.setVisibility(View.VISIBLE);
                            priceTextView.setText(getResources().getString(R.string.rupees) + Html.fromHtml(productPrice));
                            if (productRegularPrice != null && !productRegularPrice.isEmpty() && !productRegularPrice.equalsIgnoreCase("null")) {
                                regularPriceTextView.setText(getResources().getString(R.string.rupees) + Html.fromHtml(productRegularPrice));
                                regularPriceTextView.setPaintFlags(regularPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            } else {
                                regularPriceTextView.setVisibility(View.GONE);
                            }
                        } else {
                            priceTextView.setVisibility(View.GONE);
                            if (vendorEmail!=null && !vendorEmail.isEmpty() && vendorEmail.contains("@")) {
                                regularPriceTextView.setText("ASK FOR PRICE/COLLECT AT STORE");
                                regularPriceTextView.setVisibility(View.VISIBLE);
                                buyNowLinearLayout.setVisibility(View.GONE);
                            } else {
                                regularPriceTextView.setVisibility(View.GONE);
                                buyNowLinearLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        if (productImageUrl != null && !productImageUrl.isEmpty()) {
                          //  Picasso.with(ProductDetailsScreenActivity.this).load(imageUrl).placeholder(R.drawable.placeholder_pro).into(mDemoSlider);
                        } else {
                          //  productDetailSlider.setBackgroundResource(R.drawable.placeholder_pro);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ProductDetailsActivity.this, "OOPS!! Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().getmRequestQueue().add(jsonArrayRequest);

    }//end of getProductFullDetails

    private void loadInWebView(String vendor_description, WebView _webview, String fileName) {
        _webview.getSettings().setJavaScriptEnabled(true);
        _webview.getSettings().setAllowFileAccess(true);
        _webview.getSettings().setLoadsImagesAutomatically(true);
        WebSettings settings = _webview.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        _webview.getSettings().setUserAgentString(Locale.getDefault().getLanguage());
        _webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.startsWith("tel:")) {
//                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
//                    startActivity(intent);
//                } else if (url.startsWith("http:") || url.startsWith("https:")) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                } else if (url.startsWith("mailto:")) {
//                    MailTo mt = MailTo.parse(url);
//                    Intent i = Utility.hariEmailIntent(ProductDetailsActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
//                    startActivity(i);
//                    view.reload();
//                    return true;
//                } else {
//                    view.loadUrl(url);
//                }
                return true;
            }
        });

        String summary = "<!DOCTYPE html><head> <meta http-equiv=\"Content-Type\" \" +\n" +
                "\"content=\"text/html; charset=utf-8\"><html> <body align=\"justify\" style=\"font-family:Roboto;line-height:20px\">" + vendor_description + "</body></html>";
        summary = summary.replaceAll("//", "");

        _webview.loadData(summary, "text/html", "UTF-8");
    }



    public void setupNavigation() {
        headerRelativeLayout.getLayoutParams().height = (int) (UrlUtility.screenHeight / 10.2);
        backallRelativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });


        // titleTV.setTypeface(Utility.font_bold);
        headerTitleTextView.setText("Details");
        titleSubTVID.setVisibility(View.GONE);
        // if (sub_cat_name != null && !sub_cat_name.isEmpty() && sub_cat_name.trim().length() > 2) {
        //    subTitleTV.setVisibility(View.VISIBLE);
        //    subTitleTV.setText("" + Html.fromHtml(sub_cat_name));
        // }

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
                startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
            }
        });
        cartButton.setBackgroundResource(R.drawable.search_black_icon);//shopping cart image 24X24
    }//end of setUpNavigation

    private void checkingPinCode(String pinCodeCheckUrl, JSONObject jsonObject, final String pinCodeStr) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,pinCodeCheckUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("checkPinCode", "onResponse: "+response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("pincode_status");
                    if (jsonArray.length()>0){
                        JSONObject nodeChild = jsonArray.getJSONObject(0);
                        String result = nodeChild.getString("result");
                        //String delivery_date = jsonObject.getString("delivery_date");

                        if (result!=null && result.equalsIgnoreCase("success")){
                            pinCodeCheckEdittext.setText(""+pinCodeStr);
                          //  MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "PIN_CODE", ""+pinCodeStr);
                            pinCheckButton.setText("CHANGE");
                        } else {
                            UrlUtility.showCustomToast("Oops! We are not currently servicing your area.", ProductDetailsActivity.this);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("checkPinCode", "onErrorResponse: "+error.toString());
                Toast.makeText(ProductDetailsActivity.this, "OOPS!! Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().getmRequestQueue().add(jsonObjectRequest);
    } //check pinCode

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.left_pull_in, R.anim.right_push_out);
    }

}
