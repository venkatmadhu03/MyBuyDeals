package appsnova.com.mybuydeals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import appsnova.com.mybuydeals.adapters.FullScreenImageAdapter;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FullScreenViewActivity extends AppCompatActivity {

    ViewPager fullScreenViewPager;
    FullScreenImageAdapter screenImageAdapter;
    Bundle bundle;
    ArrayList<String> imagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if (bundle !=null){
            imagesList = bundle.getStringArrayList("imagesList");
            Log.d("ImagesList", "onCreate: sie "+imagesList.size());
        }
        setContentView(R.layout.activity_full_screen_view);
        fullScreenViewPager = findViewById(R.id.fullScreenViewPager);
        screenImageAdapter = new FullScreenImageAdapter(this, imagesList, "fullScree");
        fullScreenViewPager.setAdapter(screenImageAdapter);
    }
}
