package appsnova.com.mybuydeals.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import appsnova.com.mybuydeals.ProductListActivity;
import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.NavMenuModel;

public class NavMenuAdapter extends RecyclerView.Adapter<NavMenuAdapter.NavMenuViewHolder> {
    List<NavMenuModel> navMenuModelList;
    Context context;

    public NavMenuAdapter(List<NavMenuModel> navMenuModelList, Context context) {
        this.navMenuModelList = navMenuModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public NavMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_nav_item, null);
        return new NavMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavMenuViewHolder holder, final int position) {
        NavMenuModel navMenuModel= navMenuModelList.get(position);
        holder.navTextView.setText(navMenuModel.getCategoryName());
        Picasso.get()
                .load(navMenuModel.getImageUrl())
                .placeholder(R.drawable.ic_menu_share)
                .into(holder.menuImageView);

        holder.navMenuLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent catIntent = new Intent(context, ProductListActivity.class);
                catIntent.putExtra("HOME_KEY", navMenuModelList.get(position).getCategoryName());
                catIntent.putExtra("CAT_NAME", navMenuModelList.get(position).getCategoryName());
                catIntent.putExtra("FROM_HOME", "HOME");
                catIntent.putExtra("CAT_ID", navMenuModelList.get(position).getCatId());
                context.startActivity(catIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return navMenuModelList.size();
    }

    public class NavMenuViewHolder extends RecyclerView.ViewHolder {
        TextView navTextView;
        ImageView menuImageView;
        LinearLayout navMenuLinearLayout;

        public NavMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            navTextView = itemView.findViewById(R.id.navTextView);
            menuImageView = itemView.findViewById(R.id.menuImageView);
            navMenuLinearLayout = itemView.findViewById(R.id.navMenuLinearLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent catIntent = new Intent(context, ProductListActivity.class);
                    catIntent.putExtra("HOME_KEY", navMenuModelList.get(getAdapterPosition()).getCategoryName());
                    catIntent.putExtra("CAT_NAME", navMenuModelList.get(getAdapterPosition()).getCategoryName());
                    catIntent.putExtra("FROM_HOME", "HOME");
                    catIntent.putExtra("CAT_ID", navMenuModelList.get(getAdapterPosition()).getCatId());
                    context.startActivity(catIntent);
                }
            });
        }
    }
}
