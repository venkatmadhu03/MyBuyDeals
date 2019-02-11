package appsnova.com.mybuydeals.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.CartDataModel;

public class CartListAdapter extends BaseAdapter {

    ArrayList<CartDataModel> cartDataModelArrayList;
    private LayoutInflater layoutInflater = null;
    Context context;

    public CartListAdapter(ArrayList<CartDataModel> cartDataModelArrayList, LayoutInflater layoutInflater, Context context) {
        this.cartDataModelArrayList = cartDataModelArrayList;
        this.layoutInflater = layoutInflater;
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

            holder.cardView = (RelativeLayout) convertView.findViewById(R.id.cartItemsCardRLID);

            holder.productNameTV = (TextView) convertView.findViewById(R.id.productNameTVID);

            holder.vendorNameTV = (TextView) convertView.findViewById(R.id.vendorNameTVID);

            holder.priceTV = (TextView) convertView.findViewById(R.id.priceTVID);

            holder.productImageIV = (ImageView) convertView.findViewById(R.id.productImageIVID);

            holder.quantityTV = (TextView) convertView.findViewById(R.id.quantityTVID);

            holder.deleteCartItemBtn = (Button) convertView.findViewById(R.id.deleteCartItemBtnID);

            // This will now execute only for the first time of each row
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.productNameTV.setText("" + Html.fromHtml(mCartListData.get(position).getproduct_name()));
        holder.vendorNameTV.setText("By " + Html.fromHtml(mCartListData.get(position).getvendroName()));
        holder.priceTV.setText("" + mContext.getResources().getString(R.string.rs) + Html.fromHtml(mCartListData.get(position).getprice()));
        holder.quantityTV.setText("" + Html.fromHtml(mCartListData.get(position).getquantity()));

        if (mCartListData.get(position).getimageUrl() != null && !mCartListData.get(position).getimageUrl().isEmpty()) {
            Picasso.with(mContext).load(mCartListData.get(position).getimageUrl()).placeholder(R.drawable.placeholder_pro).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(holder.productImageIV);
        } else {
            holder.productImageIV.setBackgroundResource(R.drawable.placeholder_pro);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateOrderActivity.this);
                alertDialogBuilder.setMessage("Are you sure, You wanted to delete this item from cart?");

                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (Utility.isOnline(CreateOrderActivity.this)) {
                            RequestParams params = new RequestParams();
                            LoginDetails loginDetails = dataBaseHelper.getLoginDetails();
                            String udid = Settings.Secure.getString(CreateOrderActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

                            params.put("deviceid", "" + udid);
                            if (loginDetails != null) {
                                params.put("userid", "" + loginDetails.getUserID());
                            }
                            params.put("product_id", "" + mCartListData.get(pos).getproduct_id());

                            removeCartListItem(AppConstants.REMOVE_CART_LIST_ITEM_URL, params);
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
}

class CartViewHolder{
        RelativeLayout cardView;
        TextView productNameTV;
        TextView vendorNameTV;
        TextView priceTV, quantityTV;
        ImageView productImageIV;
        Button deleteCartItemBtn;
}
