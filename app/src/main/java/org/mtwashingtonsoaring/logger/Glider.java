package org.mtwashingtonsoaring.logger;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by Rickr on 11/21/2015.
 */
public class Glider {

    public String id;
    public String contestNumber;
    public boolean twoPlace;
    public String type;
    public String comments;
    public static final String gliderReturnValue = "GliderReturnValue";

    // constructor
    public Glider(){
        this.id = String.valueOf(System.currentTimeMillis());
        this.contestNumber = "";
        this.twoPlace = false;
        this.type = "";
        this.comments = "";
    }
    public Glider(String id, String contestNumber, boolean twoPlace, String type, String comments) {
        this.id = id;
        this.contestNumber = contestNumber;
        this.twoPlace = twoPlace;
        this.type = type;
        this.comments = comments;
   }

    public static boolean isGliderTwoplace(String contestID){


        Cursor c = MyApp.db.query( DBHelper.GLIDER_TABLE_NAME,
                new String[]{DBHelper.GLIDER_CONTEST_NUMBER,DBHelper.GLIDER_IS_TWO_PLACE},
                DBHelper.GLIDER_CONTEST_NUMBER + " = ? ",
                new String[]{contestID},
                null,
                null,
                null);
        c.moveToFirst();
        boolean temp;
        Log.e("RRR Glider","first return and cursor count = " + c.getCount());
        if( temp = (c.getCount()==0)) return false;
        String s = c.getString(c.getColumnIndex(DBHelper.GLIDER_IS_TWO_PLACE));
        temp = (s.equalsIgnoreCase("TRUE") || s.equalsIgnoreCase("Yes") || s.equalsIgnoreCase("Y"));
        c.close();
        Log.e("RRR","later return and isTwoPlace = " + temp);
        return temp;

    }
}
