package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import appsnova.com.mybuydeals.models.HomeProductsModel;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

    String vendorId = "", productId = "", productName = "", price = "", vendorName = "",
            vendorEmail = "", pinCode="";


//    AppDataBaseHelper dbHelper = new AppDataBaseHelper(ProductDetailsActivity.this);
//    LoginDetails loginDetails;

    InputMethodManager keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_product_details);

      //  if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
//                String fromScreenStacks = MySharedPreference.getPreferences(ProductDetailsActivity.this, "PRODUCT_FROM_SCREEN");
//                Intent bookingDoneIntent = null;
//                if (fromScreenStacks != null && fromScreenStacks.equalsIgnoreCase("HOME_MAIN")) {
//                    bookingDoneIntent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
//                } else {
//                    bookingDoneIntent = new Intent(ProductDetailsActivity.this, ProductsListActivity.class);
//                    /*else if (fromScreenStacks != null && fromScreenStacks.equalsIgnoreCase("GRID_PRODUCTS")) {
//                    bookingDoneIntent = new Intent(ProductDetailsActivity.this, ProductsListActivity.class);
//                }*/
//                }
//                if (bookingDoneIntent != null) {
//                    bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    bookingDoneIntent.putExtra("EXIT", true);
//                    startActivity(bookingDoneIntent);
//                }
               // onBackPressedAnimationByCHK();
            } else {
                sizesLists = new ArrayList<String>();
                colorsList = new ArrayList<String>();

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
               // setupNavigation();
                keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

                Bundle b = getIntent().getExtras();
                if (b != null) {
//                    String PRODUCT_FROM_SCREEN = b.getString("PRODUCT_FROM_SCREEN");
//                    MySharedPreference.setPreference(ProductDetailsActivity.this, "PRODUCT_FROM_SCREEN", ""+PRODUCT_FROM_SCREEN);
//                    productId = b.getString("PRODUCT_ID");
//                    if (Utility.isOnline(ProductDetailsActivity.this)) {
//                        try {
//                            progressDialog = ProgressDialog.show(ProductDetailsActivity.this, "Please wait ...", "Loading data...", true);
//                            progressDialog.setCancelable(false);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        fullDetailsSingleProductData(AppConstants.PRODUCT_FULL_DETAILS_URL + "" + PRODUCT_ID);
//                        realtedProductsLoading(AppConstants.RELATED_PRODUCTS_URL + "" + PRODUCT_ID);
//                    } else {
//                        Utility.showCustomToast("Please connect your internet!", ProductDetailsActivity.this);
//                        finish();
//                    }
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



}
