package appsnova.com.mybuydeals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import appsnova.com.mybuydeals.HomeActivity;
import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.SliderImageModel;

public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<SliderImageModel> sliderImageModelArrayList;
    Context mcontext;

    public ViewPagerAdapter(ArrayList<SliderImageModel> sliderImageModelArrayList, Context mcontext) {
        this.sliderImageModelArrayList = sliderImageModelArrayList;
        this.mcontext = mcontext;
    }

    @Override
    public int getCount() {
        return sliderImageModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View imageLayout = layoutInflater.inflate(R.layout.sliding_images_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.sliding_imageview);
        imageView.setImageResource(sliderImageModelArrayList.get(position).getImage_drawable());
        view.addView(imageLayout, 0);
        return imageLayout;
    }
}
