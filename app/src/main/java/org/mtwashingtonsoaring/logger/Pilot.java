package org.mtwashingtonsoaring.logger;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Rickr on 11/21/2015.
 */
public class Pilot {

    public static final String PILOT_ID = "pilotId";
    public static final String DBC_ID = "id";

    public static final String DBC_TABLE_NAME = "pilots";

    public String pid;
    public String name;
    public String club;
    public String towPilot;
    public String cellNum;
    public String eMail;
    public String eDevices;
    public String eContact;
    public String ePhoneNum;
    public String comments;


    // constructor

    public Pilot() {
        this.pid = "Pilot ID";
        this.name = "PilotName";
        this.club = "Club";
        this.towPilot = "towPilot";
        this.cellNum = "CellPhone";
        this.eMail = "eMail";
        this.eDevices = "Enter eDevices";
        this.eContact = "eContact";
        this.ePhoneNum = "eNumber";
        this.comments = "Comment";
    }

    public Pilot(String pid, String name, String club, String towPilot, String cellNum, String eMail, String eDevices, String eContact, String ePhoneNum, String comments) {
        this.pid = pid;
        this.name = name;
        this.club = club;
        this.towPilot = towPilot;
        this.cellNum = cellNum;
        this.eMail = eMail;
        this.eDevices = eDevices;
        this.eContact = eContact;
        this.ePhoneNum = ePhoneNum;
        this.comments = comments;

    }

    public Pilot getEmptyPilot() {

        return new Pilot("Pilot ID","Pilot Name", "Club", "towPilot", "CellPhone", "eMail", "Enter eDevices", "eNumber", "eContact", "Comment");

    }


    ArrayList<String> getPilotList(String match) {
        ArrayList<String> s = new ArrayList<String>();
        Cursor c;
        String notInList = "";

        if (match.equals("TowPilot")){
            c = MyApp.db.rawQuery("Select " + DBHelper.PILOT_NAME + " , " + DBHelper.PILOT_IS_TOWPILOT + " from " + DBHelper.PILOT_TABLE_NAME + " Where " + DBHelper.PILOT_IS_TOWPILOT + " like 'true'", null);
        } else {
 //           c = MyApp.db.query(DBHelper.PILOT_TABLE_NAME, new String[]{DBHelper.PILOT_NAME},null, null, null, null, null);

            c= MyApp.db.rawQuery("Select pilot, count ( " + DBHelper.FLIGHT_GLIDER + " ) as pilotcount from ( Select " +
                    DBHelper.FLIGHT_PILOT_ONE + " As pilot, " + DBHelper.FLIGHT_GLIDER + " From " + DBHelper.FLIGHT_TABLE_NAME +
                    " Where " + DBHelper.FLIGHT_GLIDER + " = '" + match + "' Union All Select " +
                    DBHelper.FLIGHT_PILOT_TWO + " As pilot, " + DBHelper.FLIGHT_GLIDER + " From " + DBHelper.FLIGHT_TABLE_NAME +
                    " Where " + DBHelper.FLIGHT_GLIDER + " = '" + match  + "') Group by pilot Order by pilotcount Desc", null);
            if(c.getCount()>0){
                c.moveToFirst();
                do {
                    String tmp = new String();
                    tmp = c.getString(0);
                    if(!tmp.equals("")) {
                        s.add(tmp); //add the item
                        notInList = notInList + "'" + tmp + "' ,";
                    }
                } while (c.moveToNext());
                c.close();
                if (notInList.length()>0) notInList = notInList.substring(0,notInList.length() - 1);  // trim trailing comma
                s.add("- - - - - - - ");
            }
            if (notInList != "") {

                c = MyApp.db.rawQuery("Select " + DBHelper.PILOT_NAME + " From " + DBHelper.PILOT_TABLE_NAME + " where " + DBHelper.PILOT_NAME +
                        " Not In ( " + notInList + " )" + " Order By " + DBHelper.PILOT_NAME, null);
            } else {
                c = MyApp.db.rawQuery("Select " + DBHelper.PILOT_NAME + " From " + DBHelper.PILOT_TABLE_NAME +
                         " Order By " + DBHelper.PILOT_NAME, null);
            }

        }



//sqlite> select pilot, count(glider) as pilotcount from (select pilot, glider from t2 where glider = "PL" union all
// select pilot2 as pilot, glider from t2 where glider = "PL") group by pilot order by pilotcount desc;

//        c = MyApp.db.rawQuery(" SELECT " + DBHelper.PILOT_NAME + " , " + " COUNT ( " + DBHelper.GLIDER_TABLE_NAME + " ) " +
//                                    " AS gliderCount FROM ( SELECT " + DBHelper.PILOT_NAME + " , " + DBHelper.PILOT_ID + " , "  as pilotcount from (select pilot, glider from tp where glider = "PL") group by pilot order by pilotcount desc

        //     select pilot, count(glider) as pilotcount from (select pilot, glider from tp where glider = "PL") group by pilot order by pilotcount desc


        //           MyApp.db.query(DBHelper.PILOT_TABLE_NAME, new String[]{DBHelper.PILOT_ID, DBHelper.PILOT_NAME},null, null, null, null, null);

        if(c.getCount()>0) {
            c.moveToFirst();
            int stringColumnIndex = c.getColumnIndex(DBHelper.PILOT_NAME);
            do {
                String tmp = new String();
                tmp = c.getString(stringColumnIndex);
                s.add(tmp); //add the item
            } while (c.moveToNext());
        }
        c.close();
        return s;
    }










    ArrayList<StringWithTag> getTaggedPilotList() {
        ArrayList<StringWithTag> st = new ArrayList<StringWithTag>();


//        Cursor c = MyApp.db.rawQuery(" SELECT " + DBHelper.F + " , " + PILOT_ID + " , " + " COUNT ( " + DBHelper.GLIDER_TABLE_NAME + " ) " +
//                                    " AS gliderCount FROM ( SELECT " + DBHelper.PILOT_NAME + " , " + DBHelper.PILOT_ID + " , "  as pilotcount from (select pilot, glider from tp where glider = "PL") group by pilot order by pilotcount desc

           //     select pilot, count(glider) as pilotcount from (select pilot, glider from tp where glider = "PL") group by pilot order by pilotcount desc


     //           MyApp.db.query(DBHelper.PILOT_TABLE_NAME, new String[]{DBHelper.PILOT_ID, DBHelper.PILOT_NAME},null, null, null, null, null);

        Cursor c = MyApp.db.query(DBHelper.PILOT_TABLE_NAME, new String[]{DBHelper.PILOT_ID, DBHelper.PILOT_NAME},null, null, null, null, null);
        c.moveToFirst();
        int stringColumnIndex = c.getColumnIndex(DBHelper.PILOT_NAME);
        int tagColumnIndex = c.getColumnIndex(DBHelper.PILOT_ID);
        do {
            StringWithTag tmp = new StringWithTag("", 0);
            tmp.string = c.getString(stringColumnIndex);
            tmp.tag = c.getLong(tagColumnIndex);
            st.add(tmp); //add the item
        } while (c.moveToNext());

        return st;
    }
}