package appsnova.com.mybuydeals.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.HomeProductsModel;

public class HomeProductsAdapter extends RecyclerView.Adapter<HomeProductsAdapter.HomeProductsViewHolder> {
    Context mContext;
    OnItemClickListener mItemClickListener;
    private ArrayList<HomeProductsModel> homeProductsModelArrayList ;

    public HomeProductsAdapter(Context mContext, ArrayList<HomeProductsModel> homeProductsModelArrayList) {
        this.mContext = mContext;
        this.homeProductsModelArrayList = homeProductsModelArrayList;
    }

    @NonNull
    @Override
    public HomeProductsAdapter.HomeProductsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        try {
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_items_list, viewGroup, false);
        } catch (OutOfMemoryError oom){
            oom.printStackTrace();
        }
        return new HomeProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeProductsAdapter.HomeProductsViewHolder homeProductsViewHolder, int position) {
        HomeProductsModel homeProductsModel = homeProductsModelArrayList.get(position);
        homeProductsViewHolder.homeProductsKeyIdTextView.setText(homeProductsModel.getProductId());
        homeProductsViewHolder.productTileTextView.setText(homeProductsModel.getProductName());
        homeProductsViewHolder.productSoldByTextView.setText("" + homeProductsModel.getVendorName()+ "");
        homeProductsViewHolder.vendorDescTextView.setText(homeProductsModel.getVendorDescription());
        if (homeProductsModel.getRegularPrice()!= null && !homeProductsModel.getRegularPrice().isEmpty()
                && homeProductsModel.getRegularPrice().equalsIgnoreCase("null") && homeProductsModel.getRegularPrice().equalsIgnoreCase("0")) {
            homeProductsViewHolder.productAvailableTextView.setVisibility(View.GONE);
        } else {
            homeProductsViewHolder.productAvailableTextView.setVisibility(View.VISIBLE);
            homeProductsViewHolder.productAvailableTextView.setText(""+mContext.getResources().getString(R.string.rupees)+ " " + homeProductsModel.getRegularPrice());
        }

        if (homeProductsModel.getImageUrl() != null && !homeProductsModel.getImageUrl().isEmpty()) {
          //  Picasso.with(mContext).load(homeProductsModel.getImageUrl()).placeholder(R.drawable.ic_menu_share).into(homeProductsViewHolder.pic);
            Picasso.get().load(homeProductsModel.getImageUrl()).placeholder(R.drawable.ic_menu_share).into(homeProductsViewHolder.homeListImageView);
        } else {
            homeProductsViewHolder.homeListImageView.setBackgroundResource(R.drawable.ic_menu_share);
        }
    }

    @Override
    public int getItemCount() {
        return homeProductsModelArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class HomeProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView productTileTextView, productSoldByTextView, vendorDescTextView,
                productAvailableTextView, homeProductsKeyIdTextView;
        protected ImageView homeListImageView;
        protected RatingBar homeProductsRatingBar;
        protected RelativeLayout homeProductListItemRelativeLayout, homeListRelativeLayout;

        public HomeProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            homeProductListItemRelativeLayout = (RelativeLayout)itemView.findViewById(R.id.homeProductListItemRelativeLayout);
            productSoldByTextView = (TextView) itemView.findViewById(R.id.productSoldByTextView);
            vendorDescTextView = (TextView) itemView.findViewById(R.id.vendorDescTextView);
            homeProductsKeyIdTextView = (TextView) itemView.findViewById(R.id.homeProductsKeyIdTextView);
            productTileTextView = (TextView) itemView.findViewById(R.id.productTileTextView);
            homeListImageView = (ImageView) itemView.findViewById(R.id.homeListImageView);
            homeListRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.homeListRelativeLayout);
            productAvailableTextView = (TextView) itemView.findViewById(R.id.productAvailableTextView);
            homeProductsRatingBar = (RatingBar) itemView.findViewById(R.id.homeProductsRatingBar);

            homeProductListItemRelativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }


}
