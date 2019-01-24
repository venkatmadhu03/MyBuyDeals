package appsnova.com.mybuydeals.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private String PREF_NAME = "MyBuyDeals";

    public SharedPref(Context context){

        this.mContext = context;
        preferences = mContext.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = preferences.edit();

    }

    public void setStringValue(String KeyName,String Value){
        editor.putString(KeyName,Value);
        editor.commit();
    }

    public String getStringValue(String KeyName){

        return preferences.getString(KeyName,"");
    }

    public boolean removeSession(String KeyName){
        editor.remove(KeyName);
//        editor.clear();
        editor.commit();
        return true;
    }

    public void SetBooleanValue(String keyName, boolean Value){
        editor.putBoolean(keyName, Value);
        editor.commit();
    }

    public boolean getBooleanValue(String keyName){
        return preferences.getBoolean(keyName, false);
    }


}
