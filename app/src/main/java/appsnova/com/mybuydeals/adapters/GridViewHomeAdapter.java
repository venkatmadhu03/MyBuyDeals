package appsnova.com.mybuydeals.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.HomeProductsModel;
import appsnova.com.mybuydeals.models.ProductListModel;

public class GridViewHomeAdapter extends BaseAdapter {

    private List<ProductListModel> homeProductsModelArrayList;
    private LayoutInflater layoutInflater = null;
    Context context;

    public GridViewHomeAdapter(List<ProductListModel> homeProductsModelArrayList, Context mContext) {
        this.homeProductsModelArrayList = homeProductsModelArrayList;
        this.context = mContext;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return homeProductsModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View itemView, ViewGroup paramViewGroup) {
        ViewHolder holder = null;
        if (itemView == null) {
            holder = new ViewHolder();
            itemView = layoutInflater.inflate(R.layout.list_item_row_products_view_all, null, false);

            holder.listItemClickCardView = itemView.findViewById(R.id.listItemClickCardView);
            holder.viewAllSoldbyTextView =  itemView.findViewById(R.id.viewAllSoldbyTextView);
            holder.viewAllVendorDescTextView = itemView.findViewById(R.id.viewAllVendorDescTextView);
            holder.viewAllKeyid = itemView.findViewById(R.id.viewAllKeyid);
            holder.viewAllTitleTextView = itemView.findViewById(R.id.viewAllTitleTextView);
            holder.viewAllThumbnail =  itemView.findViewById(R.id.viewAllThumbnail);
            holder.viewAllRelativeLayout = itemView.findViewById(R.id.viewAllRelativeLayout);
            holder.viewAllAvailableTextView = itemView.findViewById(R.id.viewAllAvailableTextView);
            holder.viewAllRatingBar = itemView.findViewById(R.id.viewAllRatingBar);

            // This will now execute only for the first time of each row
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        ProductListModel homeProductsModel = homeProductsModelArrayList.get(position);
        holder.viewAllKeyid.setText(homeProductsModel.getProduct_id());
        holder.viewAllTitleTextView.setText(homeProductsModel.getProduct_name());
        holder.viewAllSoldbyTextView.setText("" + homeProductsModel.getVendor_name() + "");
     //   holder.viewAllVendorDescTextView.setText(homeProductsModel.getVendorDescription());
        if (homeProductsModel.getPrice().equalsIgnoreCase("0") || homeProductsModel.getPrice().isEmpty()) {
            holder.viewAllAvailableTextView.setText("");
        } else {
            holder.viewAllAvailableTextView.setText(""+context.getResources().getString(R.string.rupees)+ " " + homeProductsModel.getPrice());
        }

        if (homeProductsModel.getImage() != null && !homeProductsModel.getImage().isEmpty()) {
            Picasso.get().load(homeProductsModel.getImage()).placeholder(R.drawable.ic_menu_share).into(holder.viewAllThumbnail);
        } else {
            holder.viewAllThumbnail.setBackgroundResource(R.drawable.ic_menu_share);
        }

        return itemView;
    }

    static class ViewHolder {
        protected TextView viewAllKeyid;
        protected TextView viewAllSoldbyTextView;
        protected TextView viewAllVendorDescTextView;
        protected TextView viewAllTitleTextView;
        protected TextView viewAllAvailableTextView;
        protected ImageView viewAllThumbnail;
        protected RelativeLayout viewAllRelativeLayout;
        protected RatingBar viewAllRatingBar;
        CardView listItemClickCardView;
    }
}
