package org.mtwashingtonsoaring.logger;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyApp extends Application {

    public static SQLiteDatabase db;
    public static String listFilter = Flight.GRID;
    private static Context context;

        public static Context getContext()
        {
            return MyApp.context;
        }

        @Override
        public void onCreate (){
            super.onCreate();
            DBHelper dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            MyApp.context = getApplicationContext();

        }

}