package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import appsnova.com.mybuydeals.models.CartDataModel;
import appsnova.com.mybuydeals.models.LoginDetailsModel;
import appsnova.com.mybuydeals.ownlibraries.MaterialProgressWheel;
import appsnova.com.mybuydeals.utilities.DatabaseHelper;
import appsnova.com.mybuydeals.CreateOrderActivity.CartListAdapter;
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

public class CartActivity extends AppCompatActivity {

    //create Util Objects
    NetworkUtils networkUtils;
    SharedPref sharedPref;

    //create List Utils
    ArrayList<CartDataModel> cartListDataList = new ArrayList<CartDataModel>();
    DatabaseHelper dbHelper = new DatabaseHelper(CartActivity.this);
    public CartPlaceOrdersListAdapter mAdapter;

    //Create View Objects
    RelativeLayout cartIsEmptyRL, cartItemsListRL;
    ListView cartListviewLV;
    private MaterialProgressWheel progressWheel_CENTER;
    TextView totalAmountTV;

    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize Utils
        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.cart_screen);
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
        UrlUtility.setDimensions(this);
        setupNavigation();

        loadCartList();
    }

    private void loadCartList() {
        if (networkUtils.checkConnection()) {
            progressWheel_CENTER = findViewById(R.id.progress_wheel1);
            progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
            progressWheel_CENTER.setRimColor(Color.LTGRAY);

            cartItemsListRL =  findViewById(R.id.cartItemsListRLID);
            cartItemsListRL.setVisibility(View.GONE);
            cartIsEmptyRL =  findViewById(R.id.cartIsEmptyRLID);
            cartIsEmptyRL.setVisibility(View.GONE);
            cartListviewLV = findViewById(R.id.cartListViewLVID);

            totalAmountTV =  findViewById(R.id.totalAmountTVID);

            Button checkOutBtn = findViewById(R.id.checkOutBtnID);
            checkOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPref.setStringValue("FROM_SCREEN_USER", "CART");
                    LoginDetailsModel loginDetails = dbHelper.getLoginDetails();
                    if (loginDetails != null) {
                        startActivity(new Intent(CartActivity.this, ShippingAddressActivity.class));
                    } else {
                        startActivity(new Intent(CartActivity.this, LoginActivity.class));
                    }
                }
            });

            Button shopNow = (Button)findViewById(R.id.shopNowBtnID);
            shopNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CartActivity.this, HomeActivity.class));
                    finish();
                }
            });

            try {
                ringProgressDialog = ProgressDialog.show(CartActivity.this, "Please wait ...", "Loading cart list...", true);
                ringProgressDialog.setCancelable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            HashMap<String, String> params = new HashMap<>();
            LoginDetailsModel loginDetails = dbHelper.getLoginDetails();
            String udid = Settings.Secure.getString(CartActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

            params.put("deviceid", "" + udid);
            if (loginDetails != null) {
                params.put("userid", "" + loginDetails.getUserID());
            }

            getCartList(UrlUtility.SHOW_CART_LIST_URL, params);
        } else {
            UrlUtility.showCustomToast("Please connect your Internet!", CartActivity.this);
            finish();
        }
    }

    private void getCartList(String cartListUrl, final HashMap<String, String> params) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, cartListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("no data found")) {
                        cartItemsListRL.setVisibility(View.GONE);
                        cartIsEmptyRL.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                            /*******  Returns null otherwise.  *******/
                            JSONObject jsonMainNode1 = new JSONObject(response);
                            String cartTotalAmount = jsonMainNode1.getString("cart_total");
                            String cartCount = jsonMainNode1.getString("count");

                           sharedPref.setStringValue("CART_TOTAL_AMOUNT", "" + cartTotalAmount);
                            sharedPref.setStringValue("CART_ITEMS_COUNT", "" + cartCount);
                            totalAmountTV.setText(getResources().getString(R.string.rupees) + "" +cartTotalAmount);

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
                                        mAdapter = null;
                                        mAdapter = new CartPlaceOrdersListAdapter(CartActivity.this, cartListDataList);
                                        cartListviewLV.setAdapter(mAdapter);
                                        cartItemsListRL.setVisibility(View.VISIBLE);
                                        cartIsEmptyRL.setVisibility(View.GONE);
                                    } else {
                                        cartItemsListRL.setVisibility(View.GONE);
                                        cartIsEmptyRL.setVisibility(View.VISIBLE);
                                    }

                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cartItemsListRL.setVisibility(View.GONE);
                            cartIsEmptyRL.setVisibility(View.VISIBLE);
                        }
                    }
                }
                progressWheel_CENTER.setVisibility(View.GONE);
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    cartItemsListRL.setVisibility(View.GONE);
                    cartIsEmptyRL.setVisibility(View.VISIBLE);
                    Log.w("CartListError", "onFailure" + error.getMessage());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }

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
        VolleySingleton.getmApplication().addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.right_pull_in, R.anim.left_push_out);
    }

    public void setupNavigation() {
        RelativeLayout headerImage = findViewById(R.id.headerRelativeLayout);
        headerImage.getLayoutParams().height = (int) (UrlUtility.screenHeight / 10.2);

        RelativeLayout backAllRL = (RelativeLayout) findViewById(R.id.backallRelativeLayout);
        backAllRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });

        TextView titleTV = (TextView) findViewById(R.id.headerTitleTextView);
        // titleTV.setTypeface(Utility.font_bold);
        titleTV.setText("Your Cart");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);
        //subTitleTV.setText("Sub Categories");

        Button backBtn = (Button) findViewById(R.id.backButton);
        backBtn.getLayoutParams().width = (int) (UrlUtility.screenHeight / 28.0);
        backBtn.getLayoutParams().height = (int) (UrlUtility.screenHeight / 28.0);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });

        Button menuBtn = (Button) findViewById(R.id.cartButton);
        menuBtn.getLayoutParams().width = (int) (UrlUtility.screenHeight / 24.0);
        menuBtn.getLayoutParams().height = (int) (UrlUtility.screenHeight / 24.0);
        menuBtn.setVisibility(View.VISIBLE);
        menuBtn.setBackgroundResource(R.drawable.ic_shopping_cart_white_24dp);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.left_pull_in, R.anim.right_push_out);
    }

    public class CartPlaceOrdersListAdapter extends BaseAdapter {
        ArrayList<CartDataModel> mCartListData;
        private LayoutInflater layoutInflater = null;
        Context mContext;

        public CartPlaceOrdersListAdapter(Context context, ArrayList<CartDataModel> _cartListData) {
            this.mContext = context;
            this.mCartListData = _cartListData;
            layoutInflater = LayoutInflater.from(context);//7331167946
        }

        @Override
        public int getCount() {
            return mCartListData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
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
                holder = (ViewHolder) convertView.getTag();
            }

            holder.productNameTV.setText("" + Html.fromHtml(cartListDataList.get(position).getProduct_name()));
            holder.vendorNameTV.setText("By " + Html.fromHtml(cartListDataList.get(position).getVendroName()));
            holder.priceTV.setText("" + getResources().getString(R.string.rupees) + Html.fromHtml(cartListDataList.get(position).getPrice()));
            holder.quantityTV.setText("" + Html.fromHtml(cartListDataList.get(position).getQuantity()));

            if (cartListDataList.get(position).getImageUrl() != null && !cartListDataList.get(position).getImageUrl().isEmpty()) {
                Picasso.get().load(cartListDataList.get(position).getImageUrl()).placeholder(R.drawable.ic_menu_share).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(holder.productImageIV);
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
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                    alertDialogBuilder.setMessage("Are you sure, You wanted to delete this item from cart?");

                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (networkUtils.checkConnection()) {
                                HashMap<String, String> params = new HashMap<>();
                                LoginDetailsModel loginDetails = dbHelper.getLoginDetails();
                                String udid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                                params.put("deviceid", "" + udid);
                                if (loginDetails != null) {
                                    params.put("userid", "" + loginDetails.getUserID());
                                }
                                params.put("product_id", "" + cartListDataList.get(pos).getProduct_id());

                                Log.d("CartURL", "onClick: "+UrlUtility.REMOVE_CART_LIST_ITEM_URL+","+params.toString());
                                String removeCartURL = UrlUtility.REMOVE_CART_LIST_ITEM_URL+"product_id="
                                        +cartListDataList.get(pos).getProduct_id()+"&deviceid="+udid+
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


        private void removeCartListItem(String removeCartListItemUrl, final HashMap<String, String> params) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, removeCartListItemUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String resp = jsonObject.getString("response");
                            String cartTotalAmount = jsonObject.getString("total");
                            String cartCount = jsonObject.getString("count");
                            sharedPref.setStringValue("CART_TOTAL_AMOUNT", "" + cartTotalAmount);
                            sharedPref.setStringValue("CART_ITEMS_COUNT", "" + cartCount);
                            totalAmountTV.setText(CartActivity.this.getResources().getString(R.string.rupees) + "" +cartTotalAmount);
                            if (resp != null && resp.contains("success")){
                                loadCartList();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    progressWheel_CENTER.setVisibility(View.GONE);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        //cartItemsListRL.setVisibility(View.GONE);
                        //cartIsEmptyRL.setVisibility(View.VISIBLE);
                        Log.w("RemoveCartError", "onFailure" + error.getMessage());
                    } catch (Exception ee) {
                        ee.printStackTrace();
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
            VolleySingleton.getmApplication().addToRequestQueue(stringRequest);
        }

        class ViewHolder {
            RelativeLayout cardView;
            TextView productNameTV;
            TextView vendorNameTV;
            TextView priceTV, quantityTV;
            ImageView productImageIV;
            Button deleteCartItemBtn;
        }
    }
}
