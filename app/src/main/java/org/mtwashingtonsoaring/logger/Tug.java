package org.mtwashingtonsoaring.logger;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Rickr on 11/21/2015.
 */
public class Tug {

    public String tug;
    public String club;
    public String disc;
    public static final String TugID = "tugID";

    // constructor

    public Tug() {
        this.tug = "Tug";
        this.club = "Club";
        this.disc = "Discription";
    }

    public Tug(String tug, String club, String disc) {
        this.tug = tug;
        this.club = club;
        this.disc = disc;
    }

    ArrayList<StringWithTag> getTaggedTugList(boolean isActive) {
        ArrayList<StringWithTag> st = new ArrayList<StringWithTag>();

        Cursor c = MyApp.db.query(DBHelper.TUG_TABLE_NAME,
                new String[]{DBHelper.TUG_ID, DBHelper.TUG_NAME},
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();
        int stringColumnIndex = c.getColumnIndex(DBHelper.TUG_NAME);
        int tagColumnIndex = c.getColumnIndex(DBHelper.TUG_ID);
        if(c.getCount()>0) {


            do {
                StringWithTag tmp = new StringWithTag("", 0);
                tmp.string = c.getString(stringColumnIndex);
                tmp.tag = c.getLong(tagColumnIndex);
                st.add(tmp); //add the item
            } while (c.moveToNext());
        }
        c.close();
        return st;
    }

}
