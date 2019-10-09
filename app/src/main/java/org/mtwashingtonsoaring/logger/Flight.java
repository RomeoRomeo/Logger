package org.mtwashingtonsoaring.logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.ParseException;
import android.util.Log;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Rickr on 11/24/2015.
 */
public class Flight {

    public long _id;
    public String date;
    public String mode;
    public String glider;
    public String pilotOne;
    public String pilotTwo;
    public String billTo;
    public String type;
    public double towHeight;
    public String tug;
    public String towPilot;
    public String takeoffTime;
    public String landingTime;
    public String comments;
    public static final String GROUND = "Ground";
    public static final String GRID = "Grid";
    public static final String AIR = "Air";
    public static final long NEW_FLIGHT = -1L;
    public static final String EMPTY_TIME = "";


    // constructor

    public Flight(){
    SimpleDateFormat Fmt = new SimpleDateFormat("yyyy/MM/dd");
    Date date = new Date();
    String dateString = Fmt.format(date);
        this._id = NEW_FLIGHT;
        this.date = dateString;
        this.mode = GRID;
        this.type = "Normal";
        this.glider = "";
        this.pilotOne = "";
        this.pilotTwo = "";
        this.billTo = "";
        this.tug = "";
        this.towPilot = "";
        this.towHeight = 3;
        this.takeoffTime = EMPTY_TIME;
        this.landingTime = EMPTY_TIME;
        this.comments = "";
    }



    public Flight getFlightById (Long id){

        MyApp App = new MyApp();
        Log.e("RRRDB", "looking up flight id = " + id);
        Cursor c = App.db.query(DBHelper.FLIGHT_TABLE_NAME,null,"_id = ? ",new String[]{Long.toString(id)},null,null,null);
        c.moveToFirst();
        if(c.isNull(0))
            return null;

        Flight flight = new Flight();



        flight._id        = c.getLong(0);
        flight.date       = c.getString(1);
        flight.mode       = c.getString(2);
        flight.type       = c.getString(3);
        flight.glider     = c.getString(4);
        flight.pilotOne   = c.getString(5);
        flight.pilotTwo   = c.getString(6);
        flight.billTo     = c.getString(7);
        flight.tug        = c.getString(8);
        flight.towPilot   = c.getString(9);
        flight.towHeight  = c.getDouble(10);
        flight.takeoffTime= c.getString(11);
        flight.landingTime= c.getString(12);
        flight.comments   = c.getString(13);
        c.close();
        return flight;

    }

    public static ArrayList<Flight> getFilteredFlights (String modeFilter){

//        MyApp App = new MyApp();
//        Log.e ("RR","db is null" + (App.db.equals(null)));
        Cursor c = MyApp.db.query(DBHelper.FLIGHT_TABLE_NAME,null,null,null,null,null,null);
        c.moveToFirst();
        if(c.getCount()==0)
            return new ArrayList<Flight>();


        if (c.getCount() == 0) return null;
        Log.e("RRR","columns in row = " + c.getColumnCount());
        ArrayList<Flight> flightList = new ArrayList<Flight>();
        do {
            Flight flight = new Flight();
            flight._id = c.getLong(0);           //Log.i ("RRR_DB","id = " + flight._id);
            flight.date = c.getString(1);
            flight.mode = c.getString(2);         //  Log.i ("RRR_DB","mode = " + flight.mode);
            flight.type = c.getString(3);         //  Log.i ("RRR_DB","type = " + flight.type);
            flight.glider = c.getString(4);       //  Log.i ("RRR_DB","glider = " + flight.glider);
            flight.pilotOne = c.getString(5);     //  Log.i ("RRR_DB","pilotOne = " + flight.pilotOne);
            flight.pilotTwo = c.getString(6);     //  Log.i ("RRR_DB","pilotTwo = " + flight.pilotTwo);
            flight.billTo = c.getString(7);       // Log.i ("RRR_DB","billTo = " + flight.billTo);
            flight.tug = c.getString(8);           // Log.i ("RRR_DB","tug = " + flight.tug);
            flight.towPilot = c.getString(9);      // Log.i ("RRR_DB","towPilot = " + flight.towPilot);
            flight.towHeight = c.getDouble(10);    // Log.i ("RRR_DB","towHeight = " + flight.towHeight);
            flight.takeoffTime = c.getString(11);  // Log.i ("RRR_DB","takeoffTime = " + flight.takeoffTime);
            flight.landingTime = c.getString(12); //  Log.i ("RRR_DB","landingTime = " + flight.landingTime);
            flight.comments = c.getString(13);    //  Log.i ("RRR_DB","comments = " + flight.comments);
            if(flight.mode.equals(modeFilter)){
                flightList.add(flight);
                                                   // Log.i("RRRDB","Flight glider = " + flight.glider);
            }

        }while(c.moveToNext());
        c.close();

        for(int i = 0; i<flightList.size();i++){
            Log.e("RRR", "the FlightList has these gliders -- " + flightList.get(i).glider) ;
        }
        return flightList;

    }

    public Long writeFlight (Flight flight){



        ContentValues cv =  new ContentValues(14);


         if (flight._id != NEW_FLIGHT) cv.put(DBHelper.FLIGHT_ID, flight._id);
         cv.put(DBHelper.FLIGHT_DATE,       flight.date) ;
         cv.put(DBHelper.FLIGHT_MODE,       flight.mode)       ;
         cv.put(DBHelper.FLIGHT_TYPE,       flight.type )      ;
         cv.put(DBHelper.FLIGHT_GLIDER,     flight.glider )    ;
         cv.put(DBHelper.FLIGHT_PILOT_ONE,  flight.pilotOne )  ;
         cv.put(DBHelper.FLIGHT_PILOT_TWO,  flight.pilotTwo)   ;
         cv.put(DBHelper.FLIGHT_BILL_TO,    flight.billTo)     ;
         cv.put(DBHelper.FLIGHT_TUG,        flight.tug)        ;
         cv.put(DBHelper.FLIGHT_TOW_PILOT,  flight.towPilot)   ;
         cv.put(DBHelper.FLIGHT_TOW_HEIGHT, flight.towHeight)  ;
         cv.put(DBHelper.FLIGHT_TAKE_OFF,   flight.takeoffTime);
         cv.put(DBHelper.FLIGHT_LANDING,    flight.landingTime);
         cv.put(DBHelper.FLIGHT_COMMENTS,   flight.comments)   ;

         return MyApp.db.replace(DBHelper.FLIGHT_TABLE_NAME,null,cv);

    }

    static String getFlightDurationString(Flight flight, boolean flying){
        Date toTime, nowTime, landTime = new Date();
        Long dur = 0L;
        String durString,mins;
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        try {
            toTime = fmt.parse(flight.takeoffTime);
            nowTime = fmt.parse(fmt.format(new Date()));
            if(!flying) {
                landTime = fmt.parse(flight.landingTime);
            }
        } catch (java.text.ParseException e) {

            Log.e ("RRR flight", " Bad time conversion to string");
            return "";
        }

        if (flying) {
             dur = nowTime.getTime() - toTime.getTime();
        }else {
            dur = landTime.getTime() - toTime.getTime();
        }
        durString = Long.toString(dur / (1000 * 60*60));

        if (durString.length()<2) durString = "0" + durString;
        mins = Long.toString((dur % (1000*60*60)) / (1000*60));
        if (mins.length()<2) mins = mins + "0";
        durString = durString + ":" + mins;

        return durString;
    }

}
