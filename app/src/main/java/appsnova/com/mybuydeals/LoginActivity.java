 package appsnova.com.mybuydeals;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import appsnova.com.mybuydeals.models.LoginDetailsModel;
import appsnova.com.mybuydeals.utilities.DatabaseHelper;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

 public class LoginActivity extends AppCompatActivity {

     //create Utils objects
     NetworkUtils networkUtils;
     SharedPref sharedPref;
     ProgressDialog progressDialog;

    //create view objects
    CoordinatorLayout coordinatorLayout;
    EditText mobileNumberLoginET, passwordLoginET;
    Button loginButton;
    TextView signUpTv;

    String mobileNumberStr="", passwordStr="", deviceId="";

     InputMethodManager keyboard;
     DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        //create Utils objects
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);
        progressDialog = UrlUtility.showProgressDialog(this);
        UrlUtility.setDimensions(this);

        //get DeviceId
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Login", "onCreate: "+deviceId);

        setContentView(R.layout.activity_login);
        keyboard =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        //intialize Views
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        mobileNumberLoginET = findViewById(R.id.mobileNumberLoginET);
        passwordLoginET = findViewById(R.id.passwordLoginET);
        loginButton = findViewById(R.id.loginButton);
        signUpTv = findViewById(R.id.signUpTv);

        mobileNumberLoginET.getLayoutParams().height = (int) (UrlUtility.screenWidth / 7.3);
        mobileNumberLoginET.getLayoutParams().height = (int) (UrlUtility.screenWidth / 7.3);
        loginButton.getLayoutParams().height = (int) (UrlUtility.screenWidth / 7.5);
        signUpTv.getLayoutParams().height = (int) (UrlUtility.screenWidth / 7.5);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumberStr = mobileNumberLoginET.getText().toString();
                passwordStr = passwordLoginET.getText().toString();
                if (!mobileNumberStr.isEmpty() && Patterns.PHONE.matcher(mobileNumberStr).matches()){
                    if (!passwordStr.isEmpty()){
                        if (networkUtils.checkConnection()){
                            progressDialog.show();
                            HashMap<String, String> params = new HashMap<>();
                            params.put("email", mobileNumberStr);
                            params.put("password", passwordStr);
                            params.put("deviceid", "" +deviceId);
                            sendRequestForLogin(params);
                        }else {
                            UrlUtility.showCustomToast(getString(R.string.no_connection), LoginActivity.this);
                        }
                    }else{
                        UrlUtility.showCustomToast("Enter valid passowrd", LoginActivity.this);
                    }

                }else{
                   UrlUtility.showCustomToast("Enter valid Mobile Number", LoginActivity.this);
                }

            }
        });

        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class), 1);
            }
        });

    } // end of onCreate
    private void sendRequestForLogin(final HashMap<String, String> params){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlUtility.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length()>0) {
                        JSONObject jsonResponse = jsonArray.getJSONObject(0);
                        String status = jsonResponse.optString("response");
                        if (status != null && status.equalsIgnoreCase("no user found")) {
                            UrlUtility.showCustomToast("Mobile and password incorrect!", LoginActivity.this);
                        } else {
                            String user_id = jsonResponse.optString("ID");
                            String emailStr = jsonResponse.optString("user_email");
                            String username = jsonResponse.optString("user_login");
                            String mobileStr = jsonResponse.optString("mobile");

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Calendar cal = Calendar.getInstance();

                                databaseHelper.addLoginDetails(new LoginDetailsModel(user_id, username, mobileStr, emailStr, dateFormat.format(cal.getTime())));
                                /*String str = MySharedPreference.getPreferences(ActivityLoginPage.this, "FROM_SCREEN_USER");
                                if (str != null && str.equalsIgnoreCase("MY_WISHLIST")){
                                    //Wish list here
                                } else {
                                    startActivity(new Intent(ActivityLoginPage.this, ShippingAddressScreenActivity.class));
                                }
                                finish();*/
                                onBackPressedAnimationByCHK();
                            }
                        } else {
                            UrlUtility.showCustomToast("User does'nt exit!", LoginActivity.this);
                        }
                    } catch (JSONException e) {
                        UrlUtility.showCustomToast("Sorry failed. Please try again!", LoginActivity.this);
                        e.printStackTrace();
                    } catch (Exception e) {
                        UrlUtility.showCustomToast("Sorry failed. Please try again!", LoginActivity.this);
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
             }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
            VolleySingleton.getmApplication().getmRequestQueue().add(stringRequest);

    }//end of sendRequestForLogin

     private void onBackPressedAnimationByCHK() {
         if (databaseHelper.getLoginDetails() != null) {
             Intent intent = new Intent();
             intent.putExtra("USER_MOBILE", databaseHelper.getLoginDetails().getUserMobile());
             setResult(RESULT_OK, intent);
             finish();//finishing activity
         } else {
             Intent intent = new Intent();
             intent.putExtra("USER_MOBILE", "Not_Login");
             setResult(RESULT_OK, intent);
             finish();//finishing activity
         }
         overridePendingTransition(R.anim.left_pull_in, R.anim.right_push_out);
     } //end of onBackPressedAnimation

     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
             onBackPressedAnimationByCHK();
             return false;
         }
         return super.onKeyDown(keyCode, event);
     } //end of onKeyDown

     @Override
     protected void onResume() {
         super.onResume();
         overridePendingTransition(R.anim.right_pull_in, R.anim.left_push_out);
     }
}
