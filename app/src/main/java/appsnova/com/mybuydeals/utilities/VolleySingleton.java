package appsnova.com.mybuydeals.utilities;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VolleySingleton extends MultiDexApplication {

    private static VolleySingleton mApplication;
    public static final String TAG=VolleySingleton.class.getSimpleName();
    private RequestQueue mRequestQueue;

    private static Context getmAppContext;
  //  DatabaseHandler databaseHandler;
    public String address, baseUrl="";

    @Override
    public void onCreate() {
        super.onCreate();

        getmAppContext = getApplicationContext();
        mApplication=this;
    }
    public static Context getAppContext(){
        return getmAppContext;
    }

    public static synchronized VolleySingleton getmApplication(){
        return mApplication;
    }

    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag)? TAG:tag);
        getmRequestQueue().add(request);
    }
    public <T> void addToRequestQueue(Request<T>request){
        request.setTag(TAG);
        getmRequestQueue().add(request);
    }
    public void cancelPendingRequests(Object tag){
        if (mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(VolleySingleton.this);
    }
}
