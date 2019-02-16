package appsnova.com.mybuydeals.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import appsnova.com.mybuydeals.ProductListActivity;
import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.HomeProductsModel;

public class HomeProductsAdapter extends RecyclerView.Adapter<HomeProductsAdapter.HomeProductsViewHolder> {
    Context mContext;
    OnItemClickListener mItemClickListener;
    private List<HomeProductsModel> homeProductsModelArrayList ;
    HomeProductsModel productsModel;

    public HomeProductsAdapter(Context mContext, List<HomeProductsModel> homeProductsModelArrayList) {
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
    public void onBindViewHolder(@NonNull HomeProductsAdapter.HomeProductsViewHolder homeProductsViewHolder, final int position) {
        HomeProductsModel homeProductsModel = homeProductsModelArrayList.get(position);
        HomeProductsChildAdpter homeProductsChildAdpter = new HomeProductsChildAdpter(homeProductsModel.getChildModelArrayList(),mContext);

        homeProductsViewHolder.product_title.setText(homeProductsModel.getCategory_name());
        homeProductsViewHolder.recyclerView.setHasFixedSize(true);
        homeProductsViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        homeProductsViewHolder.recyclerView.setAdapter(homeProductsChildAdpter);

        homeProductsViewHolder.view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sportIntent = new Intent(mContext, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", homeProductsModelArrayList.get(position).getCategory_slug());
                sportIntent.putExtra("CAT_NAME", homeProductsModelArrayList.get(position).getCategory_name());
                sportIntent.putExtra("FROM_HOME", "HOME");
                sportIntent.putExtra("CAT_ID", homeProductsModelArrayList.get(position).getCat_id());
                mContext.startActivity(sportIntent);
              //  Toast.makeText(v.getContext(), "click event on more, "+homeProductsModelArrayList.get(position).getCategory_name() , Toast.LENGTH_SHORT).show();
            }
        });
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
        RecyclerView recyclerView;
        TextView product_title, view_all;
        public HomeProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.main_recycler_view);
            product_title = itemView.findViewById(R.id.product_title);
            view_all = itemView.findViewById(R.id.view_all_btn);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }


}
