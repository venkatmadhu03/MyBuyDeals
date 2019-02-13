package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import appsnova.com.mybuydeals.models.CartDataModel;
import appsnova.com.mybuydeals.models.LoginDetailsModel;
import appsnova.com.mybuydeals.ownlibraries.MaterialProgressWheel;
import appsnova.com.mybuydeals.utilities.DatabaseHelper;
import appsnova.com.mybuydeals.utilities.NetworkUtils;
import appsnova.com.mybuydeals.utilities.SharedPref;
import appsnova.com.mybuydeals.utilities.UrlUtility;
import appsnova.com.mybuydeals.utilities.VolleySingleton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateOrderActivity extends AppCompatActivity {

    //utils object creation
    NetworkUtils networkUtils;
    SharedPref sharedPref;
    ArrayList<CartDataModel> cartListDataList = new ArrayList<CartDataModel>();
    DatabaseHelper databaseHelper = new DatabaseHelper(CreateOrderActivity.this);


    public CartListAdapter cartListAdapter;

    //create Viewobjects
    RelativeLayout cartIsEmptyRealtiveLayout;
    ListView cartListView;
    private MaterialProgressWheel progressWheel;
    TextView totalAmountTextView, nameTextView, addressTextView, paymentTypeTextView, editTextView, shippingTextView;
    Button confirmationOrderButton, shopNowButton;

    //header Views
    RelativeLayout headerRelativeLayout, backallRelativeLayout;
    TextView headerTitleTextView, titleSubTVID;
    Button backButton, cartButton;

    LoginDetailsModel loginDetailsModel;
    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        //Initialie  utils
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);

        //inflate view
        setContentView(R.layout.activity_create_order);

        if (getIntent()!= null && getIntent().getBooleanExtra("EXIT", false)) {
            Intent bookingDoneIntent = new Intent(CreateOrderActivity.this,
                    ShippingAddressActivity.class);
            bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bookingDoneIntent.putExtra("EXIT", true);
            startActivity(bookingDoneIntent);
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
                    .penaltyDeath()
                    .build());
            StrictMode.setThreadPolicy(old);

            loginDetailsModel= databaseHelper.getLoginDetails();

            UrlUtility.setDimensions(this);

            //initialize header views
            headerRelativeLayout = findViewById(R.id.headerRelativeLayout);
            backallRelativeLayout = findViewById(R.id.backallRelativeLayout);
            headerTitleTextView = findViewById(R.id.headerTitleTextView);
            titleSubTVID =  findViewById(R.id.titleSubTVID);
            backButton =  findViewById(R.id.backButton);
            cartButton =  findViewById(R.id.cartButton);


            setupNavigation();

            //Initialise Views
            nameTextView= (TextView)findViewById(R.id.nameTextView);
            addressTextView=findViewById(R.id.addressTextView);
            editTextView = (TextView)findViewById(R.id.editTextView);
            progressWheel =findViewById(R.id.progress_wheel1);
            confirmationOrderButton= findViewById(R.id.confirmationOrderButton);
            shopNowButton = (Button)findViewById(R.id.shopNowButton);
            cartIsEmptyRealtiveLayout = findViewById(R.id.cartIsEmptyRealtiveLayout);
            cartListView = findViewById(R.id.cartListView);
            totalAmountTextView = (TextView) findViewById(R.id.totalAmountTextView);

            nameTextView.setText(""+ sharedPref.getStringValue("FIRST_NAME"));
            addressTextView.setText(""+sharedPref.getStringValue("DELIVERY_ADDRESS_LINE1")+"\n"
                    +sharedPref.getStringValue( "DELIVERY_ADDRESS_LINE2"));

            editTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPref.setStringValue( "FROM_SCREEN_USER", "CART");
                    startActivity(new Intent(CreateOrderActivity.this, ShippingAddressActivity.class));
                }
            });

            progressWheel.setBarColor(getResources().getColor(R.color.colorPrimary));
            progressWheel.setRimColor(Color.LTGRAY);

            confirmationOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (networkUtils.checkConnection()){
                        try {
                            ringProgressDialog = ProgressDialog.show(CreateOrderActivity.this, "Please wait ...", "Creating order...", true);
                            ringProgressDialog.setCancelable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        HashMap<String, String> params = new HashMap<>();
                        params.put("order_total",""+sharedPref.getStringValue("CART_TOTAL_AMOUNT"));
                        params.put("payment_method","COD");
                        if (loginDetailsModel!=null){
                            params.put("username", ""+loginDetailsModel.getUserEmail());
                            params.put("userid", ""+loginDetailsModel.getUserID());
                        }

                        try {
                            String cartList = createOrdersGroupInServer(cartListDataList).toString();
                            params.put("cart",""+cartList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception eee){
                            eee.printStackTrace();
                        }

                        params.put("billing_phone", ""+sharedPref.getStringValue( "MOBILE"));
                        params.put("billing_email", ""+sharedPref.getStringValue( "EMAIL_ID"));
                        params.put("billing_postcode", ""+sharedPref.getStringValue( "ZIP_CODE"));
                        params.put("billing_state", ""+sharedPref.getStringValue( "STATE"));
                        params.put("billing_city", ""+sharedPref.getStringValue( "CITY_NAME"));
                        params.put("billing_address_1", ""+sharedPref.getStringValue( "DELIVERY_ADDRESS_LINE1"));
                        params.put("billing_address_2", ""+""+sharedPref.getStringValue( "DELIVERY_ADDRESS_LINE2"));
                        params.put("billing_company", "");
                        params.put("billing_last_name", ""+sharedPref.getStringValue( "LAST_NAME"));
                        params.put("billing_first_name", ""+sharedPref.getStringValue( "FIRST_NAME"));
                        params.put("billing_country", "IND");


                        createOrderUrl(UrlUtility.ORDER_CREATE_URL, params);
                    }
                }
            });

            shopNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CreateOrderActivity.this, HomeActivity.class));
                    finish();
                }
            });

            loadCartList();
        }
    } //end of onCreate

    private JSONObject createOrdersGroupInServer(ArrayList<CartDataModel> cartList) throws JSONException {
        JSONObject jsonObjectResult = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < cartList.size(); i++) {

            JSONObject jsonObjectGroup = new JSONObject();
            jsonObjectGroup.put("product_id", cartList.get(i).getProduct_id());
            jsonObjectGroup.put("product_name", cartList.get(i).getProduct_name());
            jsonObjectGroup.put("quantity", cartList.get(i).getQuantity());
            jsonObjectGroup.put("price", cartList.get(i).getPrice());
            jsonObjectGroup.put("vendor_id", cartList.get(i).getVendor_id());

            jsonArray.put(jsonObjectGroup);
        }

        jsonObjectResult.put("cart", jsonArray);
        return jsonObjectResult;
    }


    private void createOrderUrl(String orderCreateUrl, final HashMap<String, String> params) {
       StringRequest stringRequest = new StringRequest(Request.Method.POST, orderCreateUrl, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
               if (response != null) {
                   System.out.println(response);
                   try {
                       JSONObject jsonObject = new JSONObject(response);
                       String ress = jsonObject.getString("response");
                       if (ress!= null && ress.contains("success")){
                           String order_id = jsonObject.getString("order_id");
                           Intent success = new Intent(CreateOrderActivity.this, OrderConfirmationActivity.class);
                           success.putExtra("ORDER_ID", ""+order_id);
                           success.putExtra("TOTAL_AMOUNT", ""+sharedPref.getStringValue("CART_TOTAL_AMOUNT"));
                           startActivity(success);
                           Toast.makeText(CreateOrderActivity.this, "Order Success", Toast.LENGTH_SHORT).show();
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               if (ringProgressDialog != null) {
                   ringProgressDialog.dismiss();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
                UrlUtility.showCustomToast("OOPS!!! Something went wrong", CreateOrderActivity.this);
               if (ringProgressDialog != null) {
                   ringProgressDialog.dismiss();
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
    } //end of createOrderUrl


    public class CartListAdapter extends BaseAdapter {

        ArrayList<CartDataModel> cartDataModelArrayList;
        private LayoutInflater layoutInflater = null;
        Context context;


        public CartListAdapter(ArrayList<CartDataModel> cartDataModelArrayList, Context context) {
            this.cartDataModelArrayList = cartDataModelArrayList;
            layoutInflater = LayoutInflater.from(context);
            this.context = context;

        }

        @Override
        public int getCount() {
            return cartDataModelArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
            CartViewHolder holder = null;
            if (convertView == null) {
                holder = new CartViewHolder();
                convertView = layoutInflater.inflate(R.layout.cart_list_item, null, false);

                holder.cardView =  convertView.findViewById(R.id.cartItemsCardRelativeLayout);

                holder.productNameTV = (TextView) convertView.findViewById(R.id.productNameTextView);

                holder.vendorNameTV = (TextView) convertView.findViewById(R.id.vendorNameTextView);

                holder.priceTV = (TextView) convertView.findViewById(R.id.cartItemsPriceTextView);

                holder.productImageIV = (ImageView) convertView.findViewById(R.id.productImageView);

                holder.quantityTV = (TextView) convertView.findViewById(R.id.cartItemQuantityTextView);

                holder.deleteCartItemBtn = (Button) convertView.findViewById(R.id.deleteCartItemButton);

                // This will now execute only for the first time of each row
                convertView.setTag(holder);
            } else {
                holder = (CartViewHolder) convertView.getTag();
            }

            holder.productNameTV.setText("" + Html.fromHtml(cartDataModelArrayList.get(position).getProduct_name()));
            holder.vendorNameTV.setText("By " + Html.fromHtml(cartDataModelArrayList.get(position).getVendroName()));
            holder.priceTV.setText("" + context.getResources().getString(R.string.rupees) + Html.fromHtml(cartDataModelArrayList.get(position).getPrice()));
            holder.quantityTV.setText("" + Html.fromHtml(cartDataModelArrayList.get(position).getQuantity()));

            if (cartDataModelArrayList.get(position).getImageUrl() != null && !cartDataModelArrayList.get(position).getImageUrl().isEmpty()) {
                Picasso.get().load(cartDataModelArrayList.get(position).getImageUrl()).placeholder(R.drawable.ic_menu_share).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(holder.productImageIV);
            } else {
                holder.productImageIV.setBackgroundResource(R.drawable.ic_menu_send);
            }

            switch (position % 2) {
                case 0:
                    //holder.cardView.setBackgroundResource(R.color.row1);
                    break;
                case 1:
                    // holder.cardView.setBackgroundResource(R.color.white);
                    break;
            }

            holder.deleteCartItemBtn.setTag(position);
            holder.deleteCartItemBtn.setFocusable(false);
            holder.deleteCartItemBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int pos = (Integer) v.getTag();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Are you sure, You wanted to delete this item from cart?");

                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (networkUtils.checkConnection()) {
                                HashMap<String, String> params = new HashMap<>();
                                LoginDetailsModel loginDetails = databaseHelper.getLoginDetails();
                                String udid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                                params.put("deviceid", "" + udid);
                                if (loginDetails != null) {
                                    params.put("userid", "" + loginDetails.getUserID());
                                }
                                params.put("product_id", "" + cartDataModelArrayList.get(pos).getProduct_id());

                                Log.d("CartURL", "onClick: "+UrlUtility.REMOVE_CART_LIST_ITEM_URL+","+params.toString());
                                String removeCartURL = UrlUtility.REMOVE_CART_LIST_ITEM_URL+"product_id="
                                        +cartDataModelArrayList.get(pos).getProduct_id()+"&deviceid="+udid+
                                        "&userid="+loginDetails.getUserID();
                                removeCartListItem(removeCartURL, params);
                            }
                        }
                    });

                    alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            return convertView;
        }

        private void removeCartListItem(final String cartClearUrl, final HashMap<String, String> params){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, cartClearUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        try {
                            Log.d("RemoveCart", "onResponse: "+response+","+cartClearUrl);
                            JSONObject jsonObject = new JSONObject(response);
                            String resp = jsonObject.getString("response");
                            String cartTotalAmount = jsonObject.getString("total");
                            String cartCount = jsonObject.getString("count");
                            sharedPref.setStringValue("CART_TOTAL_AMOUNT", "" + cartTotalAmount);
                            sharedPref.setStringValue("CART_ITEMS_COUNT", "" + cartCount);
                            totalAmountTextView.setText(context.getResources().getString(R.string.rupees) + "" +cartTotalAmount);
                            if (resp != null && resp.contains("success")){
                                loadCartList();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    progressWheel.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    UrlUtility.showCustomToast("OOPS!! Something went wrong", CreateOrderActivity.this);
                }
            });
            VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
            VolleySingleton.getmApplication().getmRequestQueue().add(stringRequest);
        }

        class CartViewHolder{
            RelativeLayout cardView;
            TextView productNameTV;
            TextView vendorNameTV;
            TextView priceTV, quantityTV;
            ImageView productImageIV;
            Button deleteCartItemBtn;
        }

    } //end of CartListAdapter

    private void loadCartList() {

        if (networkUtils.checkConnection()) {
            HashMap<String, String> params = new HashMap<>();
            LoginDetailsModel loginDetails = databaseHelper.getLoginDetails();
            String udid = Settings.Secure.getString(CreateOrderActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

            Log.d("CreateOrder", "loadCartList: "+udid+","+loginDetails.getUserID());
            params.put("deviceid", "" + udid);
            if (loginDetails != null) {
                params.put("userid", "" + loginDetails.getUserID());
            }

            getCartList(UrlUtility.SHOW_CART_LIST_URL, params);
        } else {
            UrlUtility.showCustomToast(getResources().getString(R.string.no_connection), CreateOrderActivity.this);
            finish();
        }
    } //end of loadCartList

    private void getCartList(final String cartListUrl, final HashMap<String, String> params){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, cartListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("CartList", "onResponse: "+response+","+cartListUrl);
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("no data found")) {
                        cartListView.setVisibility(View.GONE);
                        cartIsEmptyRealtiveLayout.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                            /*******  Returns null otherwise.  *******/
                            JSONObject jsonMainNode1 = new JSONObject(response);
                            String cartTotalAmount = jsonMainNode1.getString("cart_total");
                            String cartCount = jsonMainNode1.getString("count");
                            sharedPref.setStringValue("CART_TOTAL_AMOUNT", "" + cartTotalAmount);
                            sharedPref.setStringValue("CART_ITEMS_COUNT", "" + cartCount);
                            totalAmountTextView.setText(CreateOrderActivity.this.getResources().getString(R.string.rupees) + "" +cartTotalAmount);

                            JSONArray jsonMainNode = jsonMainNode1.getJSONArray("cart");

                            /*********** Process each JSON Node ************/
                            cartListDataList.clear();
                            cartListDataList = null;
                            cartListDataList = new ArrayList<CartDataModel>();
                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                /****** Get Object for each JSON node.***********/
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                /******* Fetch node values **********/
                                String product_name = jsonChildNode.optString("product_name");
                                String product_id = jsonChildNode.optString("product_id");
                                String price = jsonChildNode.optString("price");
                                String quantity = jsonChildNode.optString("quantity");
                                String vendor_id = jsonChildNode.optString("vendor_id");
                                String image = jsonChildNode.optString("image");
                                String vendor_name = jsonChildNode.optString("vendor_name");
                                //String total = jsonChildNode.optString("total");

                                cartListDataList.add(new CartDataModel(product_id, product_name, price, image, vendor_id, quantity, vendor_name, ""));
                            }

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (cartListDataList.size() > 0) {
                                        cartListAdapter = null;
                                        cartListAdapter = new CartListAdapter(cartListDataList,CreateOrderActivity.this );
                                        cartListView.setAdapter(cartListAdapter);
                                        cartListView.setVisibility(View.VISIBLE);
                                        cartIsEmptyRealtiveLayout.setVisibility(View.GONE);
                                    } else {
                                        cartListView.setVisibility(View.GONE);
                                        cartIsEmptyRealtiveLayout.setVisibility(View.VISIBLE);
                                    }

                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cartListView.setVisibility(View.GONE);
                            cartIsEmptyRealtiveLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                progressWheel.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UrlUtility.showCustomToast("OOPS!! Something went wrong", CreateOrderActivity.this);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                JSONObject jsonObject = new JSONObject(params);
                Log.d("CreateOrder", "getParams: "+jsonObject.toString());
                return params;
            }
        };
        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().getmRequestQueue().add(stringRequest);
    } //end of getCartList

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.left_pull_in, R.anim.right_push_out);
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
                startActivity(new Intent(CreateOrderActivity.this, CartActivity.class));
            }
        });
        cartButton.setBackgroundResource(R.drawable.search_black_icon);//shopping cart image 24X24
    }//end of setUpNavigation
}



