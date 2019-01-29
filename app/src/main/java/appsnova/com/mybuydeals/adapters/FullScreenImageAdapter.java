package appsnova.com.mybuydeals.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import appsnova.com.mybuydeals.FullScreenViewActivity;
import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.ownlibraries.TouchImageView;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    private String intentFrom="";

    // constructor
    public FullScreenImageAdapter(Activity activity,
                                  ArrayList<String> imagePaths, String intentFrom) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.intentFrom = intentFrom;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        TouchImageView productImages;
        Button btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_fullscreen_imageview, container,
                false);

        productImages = view.findViewById(R.id.productImages);

        Picasso.get()
                .load(_imagePaths.get(position))
                .placeholder(R.drawable.ic_menu_share)
                .into(productImages);

        if (intentFrom.equalsIgnoreCase("productDetails")){
            productImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_activity, FullScreenViewActivity.class);
                    intent.putExtra("imagesList", _imagePaths);
                    _activity.startActivity(intent);
                    Toast.makeText(_activity, "you clicked "+position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        (container).addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);

    }

}