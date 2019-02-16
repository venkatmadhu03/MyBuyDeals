package appsnova.com.mybuydeals.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import appsnova.com.mybuydeals.ProductListActivity;
import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.HomeChildModel;

public class HomeProductsChildAdpter extends RecyclerView.Adapter<HomeProductsChildAdpter.HomeChildViewHolder> {

    ArrayList<HomeChildModel> homeChildModelArrayList;
    Context context;

    public HomeProductsChildAdpter(ArrayList<HomeChildModel> homeChildModelArrayList, Context context) {
        this.homeChildModelArrayList = homeChildModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_child_row, null);
        HomeChildViewHolder homeChildViewHolder = new HomeChildViewHolder(v);
        return homeChildViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull HomeChildViewHolder holder, final int position) {
        HomeChildModel homeChildModel = homeChildModelArrayList.get(position);
        Log.d("ChildAdapter", "onBindViewHolder: "+homeChildModel.getPrice());
        holder.product_name.setText(homeChildModel.getSlug());
        Picasso.get().load(homeChildModel.getImage()).into(holder.product_image);
        holder.product_price.setText(homeChildModel.getPrice());

        holder.product_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(context, ProductListActivity.class);
                sportIntent.putExtra("HOME_KEY", homeChildModelArrayList.get(position).getCategory_slug());
                sportIntent.putExtra("CAT_NAME", homeChildModelArrayList.get(position).getCategory_name());
                sportIntent.putExtra("FROM_HOME", "HOME");
                sportIntent.putExtra("CAT_ID", homeChildModelArrayList.get(position).getCategory_id());
                context.startActivity(sportIntent);
             //   Toast.makeText(v.getContext(), "click event on more, "+homeChildModelArrayList.get(position).getCategory_name() , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return homeChildModelArrayList.size();
    }

    public class HomeChildViewHolder extends RecyclerView.ViewHolder {
        TextView product_name,product_price;
        ImageView product_image;
        LinearLayout product_layout;

        public HomeChildViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_image = itemView.findViewById(R.id.product_image);
            product_price  = itemView.findViewById(R.id.product_price);
            product_layout = itemView.findViewById(R.id.product_layout);
        }
    }
}
