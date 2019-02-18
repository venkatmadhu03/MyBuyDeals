package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    //utils object creation
    NetworkUtils networkUtils;
    SharedPref sharedPref;

    private ImageView logoIV;
    private EditText unameET;
    private EditText eemailET;
    private EditText phoneNumET;
    private EditText passwordsET;
    private EditText reEnterPasswordsET;

    public String nameStr = "";
    public String phoneStr = "";
    public String emailStr = "";
    public String passwordStr = "";
    public String conFirmPasswordStr = "";

    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_registration);

        overridePendingTransition(R.anim.right_pull_in, R.anim.left_push_out);

        UrlUtility.setDimensions(this);

        logoIV = (ImageView) findViewById(R.id.logo);
        logoIV.getLayoutParams().height = (int) (UrlUtility.screenHeight / 5.2);

        unameET = (EditText) findViewById(R.id.unameETID);
        unameET.getLayoutParams().height = (int) (UrlUtility.screenWidth / 9.0);
        unameET.setHintTextColor(getResources().getColor(R.color.colorWhite));

        eemailET = (EditText) findViewById(R.id.eemailETID);
        eemailET.getLayoutParams().height = (int) (UrlUtility.screenWidth / 9.0);
        eemailET.setHintTextColor(getResources().getColor(R.color.colorWhite));

        phoneNumET = (EditText) findViewById(R.id.mobileETID);
        phoneNumET.getLayoutParams().height = (int) (UrlUtility.screenWidth / 9.0);
        phoneNumET.setHintTextColor(getResources().getColor(R.color.colorWhite));

        passwordsET = (EditText) findViewById(R.id.passwordsETID);
        passwordsET.getLayoutParams().height = (int) (UrlUtility.screenWidth / 9.0);
        passwordsET.setHintTextColor(getResources().getColor(R.color.colorWhite));

        reEnterPasswordsET = (EditText) findViewById(R.id.reEnterPasswordsETID);
        reEnterPasswordsET.getLayoutParams().height = (int) (UrlUtility.screenWidth / 9.0);
        reEnterPasswordsET.setHintTextColor(getResources().getColor(R.color.colorWhite));

        Button registerBtn = (Button) findViewById(R.id.registerBtnID);
        registerBtn.getLayoutParams().height = (int) (UrlUtility.screenWidth / 7.5);

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nameStr = unameET.getText().toString();
                emailStr = eemailET.getText().toString();
                phoneStr = phoneNumET.getText().toString();
                passwordStr = passwordsET.getText().toString();
                conFirmPasswordStr = reEnterPasswordsET.getText().toString();

                if (networkUtils.checkConnection()) {
                    if (nameStr.trim().length() > 0 && emailStr.trim().length() > 0) {
                        if (UrlUtility.isEmailValid(emailStr)) {
                            if (passwordStr.equalsIgnoreCase(conFirmPasswordStr)) {
                                if (phoneStr.length()==10) {
                                    try {
                                        ringProgressDialog = ProgressDialog.show(RegistrationActivity.this, "Please wait ...", "Creating your account...", true);
                                        ringProgressDialog.setCancelable(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("uname", nameStr);
                                    params.put("email", emailStr);
                                    params.put("mobile", phoneStr);
                                    params.put("password", passwordStr);
                                    sendOtpRequesttoServer(UrlUtility.REGISTER_URL, params);
                                } else {
                                    UrlUtility.showCustomToast("Invalid mobile number!", RegistrationActivity.this);
                                }
                            } else {
                                UrlUtility.showCustomToast("Password and confirm password not match!", RegistrationActivity.this);
                            }
                        } else {
                            UrlUtility.showCustomToast("Invalid email!", RegistrationActivity.this);
                        }
                    } else {
                        UrlUtility.showCustomToast("All fields are mandatory!", RegistrationActivity.this);
                    }
                } else {
                    UrlUtility.showCustomToast("Please connect your Internet!", RegistrationActivity.this);
                }
            }
        });

        Button loginBtn = (Button) findViewById(R.id.loginBtnID);
        loginBtn.getLayoutParams().height = (int) (UrlUtility.screenWidth / 7.5);

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendOtpRequesttoServer(String registerUrl, final HashMap<String, String> params) {

        StringRequest  stringRequest = new StringRequest(Request.Method.POST, registerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.optString("response");
                        if (status.contains("success")) {
                            finish();
                            UrlUtility.showCustomToast("Thank you for registering!", RegistrationActivity.this);
                        } else {
                            UrlUtility.showCustomToast("Already exits your email!", RegistrationActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        UrlUtility.showCustomToast("Sorry failed. Please try again!", RegistrationActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        UrlUtility.showCustomToast("Sorry failed. Please try again!", RegistrationActivity.this);
                    }
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
                if (error.networkResponse.statusCode == 404) {
                    UrlUtility.showCustomToast("Requested resource not found", RegistrationActivity.this);
                } else if (error.networkResponse.statusCode == 500) {
                    UrlUtility.showCustomToast("Something went wrong at server end", RegistrationActivity.this);
                } else {
                    UrlUtility.showCustomToast("Unexpected Error occurred! Try again.", RegistrationActivity.this);
                }
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
    }
}
