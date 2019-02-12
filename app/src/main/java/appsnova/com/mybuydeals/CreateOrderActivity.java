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
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CreateOrderActivity extends AppCompatActivity {

    //utils object creation
    NetworkUtils networkUtils;
    SharedPref sharedPref;
    ArrayList<CartDataModel> cartListDataList = new ArrayList<CartDataModel>();
    DatabaseHelper databaseHelper = new DatabaseHelper(CreateOrderActivity.this);


    public CartListAdapter cartListAdapter;
    RelativeLayout cartIsEmptyRL;
    ListView cartListviewLV;
    private MaterialProgressWheel progressWheel;
    TextView totalAmountTV;

    LoginDetailsModel loginDetailsModel;
    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkUtils = new NetworkUtils(this);
        sharedPref = new SharedPref(this);
        setContentView(R.layout.activity_create_order);
    }


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

                                removeCartListItem(UrlUtility.REMOVE_CART_LIST_ITEM_URL, params);
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

        private void removeCartListItem(String cartClearUrl, final HashMap<String, String> params){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, cartClearUrl, new Response.Listener<String>() {
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
                            totalAmountTV.setText(context.getResources().getString(R.string.rupees) + "" +cartTotalAmount);
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

    private void getCartList(String cartListUrl, final HashMap<String, String> params){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, cartListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("no data found")) {
                        cartListviewLV.setVisibility(View.GONE);
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
                            totalAmountTV.setText(CreateOrderActivity.this.getResources().getString(R.string.rupees) + "" +cartTotalAmount);

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
                                        cartListviewLV.setAdapter(cartListAdapter);
                                        cartListviewLV.setVisibility(View.VISIBLE);
                                        cartIsEmptyRL.setVisibility(View.GONE);
                                    } else {
                                        cartListviewLV.setVisibility(View.GONE);
                                        cartIsEmptyRL.setVisibility(View.VISIBLE);
                                    }

                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cartListviewLV.setVisibility(View.GONE);
                            cartIsEmptyRL.setVisibility(View.VISIBLE);
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
                return params;
            }
        };
        VolleySingleton.getmApplication().getmRequestQueue().getCache().clear();
        VolleySingleton.getmApplication().getmRequestQueue().add(stringRequest);
    }
}



