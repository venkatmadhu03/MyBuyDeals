package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import appsnova.com.mybuydeals.models.LoginDetailsModel;
import appsnova.com.mybuydeals.ownlibraries.CustomTextView;
import appsnova.com.mybuydeals.utilities.DatabaseHelper;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShippingAddressActivity extends AppCompatActivity {

    //Utils Object Creation
    SharedPref sharedPref;
    NetworkUtils networkUtils;
    ProgressDialog progressDialog;

    //create View Objects
    EditText firstNameEdittext,lastNameEdittext, emailIdEdittext, mobileEdittext, addressLine1Edittext,
            addressLine2Edittext, pinCodeCheckEdittext, cityEdittext, stateEdittext, countryEdittext;
    CustomTextView continueOrderTextView;

    //header Views
    RelativeLayout headerRelativeLayout, backallRelativeLayout;
    TextView headerTitleTextView, titleSubTVID;
    Button backButton, cartButton;
    ViewPager viewPager;

    LoginDetailsModel loginDetails;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //initialize Utils objects
        sharedPref = new SharedPref(this);
        networkUtils = new NetworkUtils(this);

        setContentView(R.layout.activity_shipping_address);
        UrlUtility.setDimensions(this);

        headerRelativeLayout = findViewById(R.id.headerRelativeLayout);
        backallRelativeLayout = findViewById(R.id.backallRelativeLayout);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);
        titleSubTVID =  findViewById(R.id.titleSubTVID);
        backButton =  findViewById(R.id.backButton);
        cartButton =  findViewById(R.id.cartButton);

        setupNavigation();

        if (getIntent()!= null && getIntent().getBooleanExtra("EXIT", false)) {
            Intent bookingDoneIntent = new Intent(ShippingAddressActivity.this, ProductDetailsActivity.class);
            bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bookingDoneIntent.putExtra("EXIT", true);
            startActivity(bookingDoneIntent);
            onBackPressedAnimationByCHK();
        } else {
            loginDetails = databaseHelper.getLoginDetails();

            //intialize View Objects
            firstNameEdittext = findViewById(R.id.firstNameEdittext);
            lastNameEdittext = findViewById(R.id.lastNameEdittext);
            emailIdEdittext = findViewById(R.id.emailIdEdittext);
            mobileEdittext = findViewById(R.id.mobileEdittext);
            addressLine1Edittext = findViewById(R.id.addressLine1Edittext);
            addressLine2Edittext = findViewById(R.id.addressLine2Edittext);
            pinCodeCheckEdittext = findViewById(R.id.pinCodeCheckEdittext);
            cityEdittext = findViewById(R.id.cityEdittext);
            stateEdittext = findViewById(R.id.stateEdittext);
            countryEdittext = findViewById(R.id.countryEdittext);
            continueOrderTextView = findViewById(R.id.continueOrderTextView);


            if (loginDetails!=null){
                firstNameEdittext.setText(""+loginDetails.getUserName());
                emailIdEdittext.setText(""+loginDetails.getUserEmail());
                mobileEdittext.setText(""+loginDetails.getUserMobile());
            }

            lastNameEdittext.setText(sharedPref.getStringValue("LAST_NAME"));
            //mobileET.setText(MySharedPreference.getPreferences(ShippingAddressActivity.this, "MOBILE"));
            addressLine1Edittext.setText(sharedPref.getStringValue("DELIVERY_ADDRESS_LINE1"));
            addressLine2Edittext.setText(sharedPref.getStringValue("DELIVERY_ADDRESS_LINE2"));
            pinCodeCheckEdittext.setText(sharedPref.getStringValue("PIN_CODE"));
            cityEdittext.setText(sharedPref.getStringValue("CITY_NAME"));
            countryEdittext.setText(sharedPref.getStringValue("COUNTRY"));
            stateEdittext.setText(sharedPref.getStringValue("STATE"));

            String startStr = sharedPref.getStringValue("FROM_SCREEN_USER");
            if (startStr != null && startStr.equalsIgnoreCase("MY_ACCOUNT")){
                continueOrderTextView.setVisibility(View.VISIBLE);
                continueOrderTextView.setText("Update Profile");
            } else {
                continueOrderTextView.setVisibility(View.VISIBLE);
            }


            continueOrderTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String firstNameETStr = firstNameEdittext.getText().toString();
                    String lastNameETStr = lastNameEdittext.getText().toString();
                    String emailETStr = emailIdEdittext.getText().toString();
                    String mobileETStr = mobileEdittext.getText().toString();
                    String addressLineOneETStr = addressLine1Edittext.getText().toString();
                    String addressLineTwoETStr = addressLine2Edittext.getText().toString();
                    String pinCodeCheckETStr = pinCodeCheckEdittext.getText().toString();
                    String cityETStr = cityEdittext.getText().toString();
                    String countryETStr = countryEdittext.getText().toString();
                    String stateETStr = stateEdittext.getText().toString();

                    boolean proceed = false;
                    if (firstNameETStr!=null && firstNameETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your first name", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (lastNameETStr!=null && lastNameETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your last name", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (emailETStr!=null && emailETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (mobileETStr!=null && mobileETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your mobile", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (addressLineOneETStr!=null && addressLineOneETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter delivery address line 1", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (addressLineTwoETStr!=null && addressLineTwoETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter delivery address line 2", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (pinCodeCheckETStr!=null && pinCodeCheckETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your postal code", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (cityETStr!=null && cityETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your city", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (stateETStr!=null && stateETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your state", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }

                    if (countryETStr!=null && countryETStr.equals("")) {
                        Toast.makeText(ShippingAddressActivity.this, "Please enter your country", Toast.LENGTH_LONG).show();
                        proceed = true;
                        return;
                    }
                    if (proceed){
                        sharedPref.setStringValue("EMAIL_ID", emailETStr);
                        sharedPref.setStringValue("MOBILE", mobileETStr);
                        sharedPref.setStringValue("FIRST_NAME", firstNameETStr);
                        sharedPref.setStringValue("LAST_NAME", lastNameETStr);
                        sharedPref.setStringValue("DELIVERY_ADDRESS_LINE1", addressLineOneETStr);
                        sharedPref.setStringValue("DELIVERY_ADDRESS_LINE2", addressLineTwoETStr);
                        sharedPref.setStringValue("ZIP_CODE", pinCodeCheckETStr);
                        sharedPref.setStringValue("CITY_NAME", cityETStr);
                        sharedPref.setStringValue("STATE", stateETStr);
                        sharedPref.setStringValue("COUNTRY", countryETStr);

                        if (networkUtils.checkConnection()){
                            try {
                                progressDialog = ProgressDialog.show(ShippingAddressActivity.this, "Please wait ...", "Updating details...", true);
                                progressDialog.setCancelable(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            HashMap<String, String> params = new HashMap<>();
                            if (loginDetails!=null){
                                params.put("user_id", loginDetails.getUserID());
                            }
                            params.put("uname", firstNameETStr);
                            params.put("email", emailETStr);
                            params.put("mobile", mobileETStr);
                            params.put("last_name", lastNameETStr);
                            params.put("address_line1", addressLineOneETStr);
                            params.put("address_line2", addressLineTwoETStr);
                            params.put("pin_code", pinCodeCheckETStr);
                            params.put("city", cityETStr);
                            params.put("state", stateETStr);
                            params.put("country", countryETStr);
                            updateProfile(UrlUtility.UPDATE_PROFILE_URL, params);
                        }else{
                            Toast.makeText(ShippingAddressActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    } //end of onCreate

    private void updateProfile(String updateProfileDataUrl, final HashMap<String, String> params) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateProfileDataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("success")){
                        if (continueOrderTextView.getText().toString().equalsIgnoreCase("Update Profile")) {
                            UrlUtility.showCustomToast("Updated successful", ShippingAddressActivity.this);
                        } else {
                            startActivity(new Intent(ShippingAddressActivity.this, CreateOrderActivity.class));
                        }
                    }
                }
                finish();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ShippingAddress", "onErrorResponse: "+error.toString());
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(ShippingAddressActivity.this, "OOPS!! Something went wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                JSONObject jsonObject = new JSONObject(params);
                Log.d("ShippingAddress", "getParams: update Profile "+jsonObject.toString());
                return params;
            }
        };

        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().getmRequestQueue().add(stringRequest);

    }


    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.right_pull_in, R.anim.left_push_out);
    }

    public void setupNavigation() {
        headerRelativeLayout.getLayoutParams().height = (int) (UrlUtility.screenHeight / 10.2);
        backallRelativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });


        headerTitleTextView.setText("Details");
        titleSubTVID.setVisibility(View.GONE);

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
                startActivity(new Intent(ShippingAddressActivity.this, CartActivity.class));
            }
        });
        cartButton.setBackgroundResource(R.drawable.search_black_icon);//shopping cart image 24X24
    }//end of setUpNavigation

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
