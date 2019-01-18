package appsnova.com.mybuydeals.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import appsnova.com.mybuydeals.R;
import appsnova.com.mybuydeals.models.SliderImageModel;

public class SliderPageImageAdapter extends PagerAdapter {

    private ArrayList<SliderImageModel> sliderImageModelArrayList;
    private LayoutInflater inflater;
    private Context context;

    public SliderPageImageAdapter(ArrayList<SliderImageModel> sliderImageModelArrayList, Context context) {
        this.sliderImageModelArrayList = sliderImageModelArrayList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sliderImageModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_images_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.sliding_imageview);


        imageView.setImageResource(sliderImageModelArrayList.get(position).getImage_drawable());

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
