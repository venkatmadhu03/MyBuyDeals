package appsnova.com.mybuydeals.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import appsnova.com.mybuydeals.models.LoginDetailsModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String dbName ="MyBuyDeals";
    public static final int dbVersion = 2;

    public static final String loginTable = "users";
    public static final String userID ="userID";
    public static final String emailID = "emailID";
    public static final String user_login = "user_login";
    public static final String user_mobile = "user_mobile";
    public static final String date = "loginDate";

    public DatabaseHelper(Context context){
        super(context, dbName, null, dbVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + loginTable + " (" + userID + " TEXT, " + emailID + " TEXT, "
                + user_login + " TEXT, " + user_mobile + " TEXT, " + date + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + loginTable);
        onCreate(db);
    }

    public void addLoginDetails(LoginDetailsModel _loginData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + loginTable);
        ContentValues cv = new ContentValues();

        cv.put(userID, _loginData.getUserID());
        cv.put(emailID, _loginData.getUserEmail());
        cv.put(user_mobile, _loginData.getUserMobile());
        cv.put(user_login, _loginData.getUserName());
        cv.put(date, _loginData.getLoginDate());

        db.insert(loginTable, emailID, cv);
        db.close();
    }

    public void deleteLoginDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + loginTable);
    }

    public LoginDetailsModel getLoginDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + loginTable, new String[] {});
        LoginDetailsModel loginDetails = null;
        if (cur.moveToFirst()) {
            do {
                loginDetails = new LoginDetailsModel();
                loginDetails.setUserID(cur.getString(cur.getColumnIndex(userID)));
                loginDetails.setUserEmail(cur.getString(cur.getColumnIndex(emailID)));
                loginDetails.setUserName(cur.getString(cur.getColumnIndex(user_login)));
                loginDetails.setUserMobile(cur.getString(cur.getColumnIndex(user_mobile)));
                loginDetails.setLoginDate(cur.getString(cur.getColumnIndex(date)));
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        if (loginDetails != null) {
            return loginDetails;
        } else {
            return null;
        }
    }
}
