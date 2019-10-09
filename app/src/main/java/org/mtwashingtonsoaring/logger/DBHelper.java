package org.mtwashingtonsoaring.logger;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

//import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "logger.db";
    private static final int DATABASE_VERSION = 33;

    //============ Flight =====================//
    public static final String FLIGHT_SOURCE_SHEET = "Flights!A:N";
    public static final String FLIGHT_WRITE_RANGE = "Flights!A2:N";
    public static final String FLIGHT_TABLE_NAME = "Flights";
    public static final String FLIGHT_ID = "_id";
    public static final String FLIGHT_DATE = "Date";
    public static final String FLIGHT_TYPE = "Type";
    public static final String FLIGHT_PILOT_ONE = "PilotOne";
    public static final String FLIGHT_PILOT_TWO = "PilotTwo";
    public static final String FLIGHT_BILL_TO = "BillTo";
    public static final String FLIGHT_GLIDER = "Glider";
    public static final String FLIGHT_TUG = "Tug";
    public static final String FLIGHT_TOW_PILOT = "TowPilot";
    public static final String FLIGHT_TOW_HEIGHT = "TowHeight";
    public static final String FLIGHT_MODE = "Mode";
    public static final String FLIGHT_TAKE_OFF = "TakeOff";
    public static final String FLIGHT_LANDING = "Landing";
    public static final String FLIGHT_COMMENTS = "Comments";
    public static final String FLIGHT_BACKUP_TABLE_NAME = "FlightsBackup";
    private static final String CREATE_TABLE_FLIGHTS = "create table Flights"
            + "( "
            + FLIGHT_ID + " INTEGER PRIMARY KEY , "
            + FLIGHT_DATE + " TEXT, "
            + FLIGHT_MODE + " TEXT, "
            + FLIGHT_TYPE + " TEXT, "
            + FLIGHT_GLIDER + " TEXT, "
            + FLIGHT_PILOT_ONE + " TEXT, "
            + FLIGHT_PILOT_TWO + " TEXT, "
            + FLIGHT_BILL_TO + " TEXT, "
            + FLIGHT_TUG + " TEXT, "
            + FLIGHT_TOW_PILOT + " TEXT, "
            + FLIGHT_TOW_HEIGHT + " TEXT, "
            + FLIGHT_TAKE_OFF + " TEXT, "
            + FLIGHT_LANDING + " TEXT, "
            + FLIGHT_COMMENTS + " TEXT "
            + " )";

    //============ PILOT =====================//

    public static final String PILOT_TABLE_NAME = "Pilots";
    public static final String PILOT_PID = "Pid";
    public static final String PILOT_ID = "_id";
    public static final String PILOT_NAME = "Name";
    public static final String PILOT_CLUB = "Club";
    public static final String PILOT_IS_TOWPILOT = "IsTowPilot";
    public static final String PILOT_CELL_NUM = "CellNum";
    public static final String PILOT_EMAIL = "eMail";
    public static final String PILOT_EDEVICES = "eDevices";
    public static final String PILOT_ECONTACT = "eContact";
    public static final String PILOT_EPHONE_NUM = "ePhone";
    public static final String PILOT_COMMENTS = "Comments";
    public static final String PILOT_SOURCE_SHEET = "Pilots!A:J";
    private static final String CREATE_TABLE_PILOTS = "create table Pilots"

            + "( "
            + PILOT_ID + " INTEGER PRIMARY KEY , "
            + PILOT_NAME + " text not null, "
            + PILOT_PID + " TEXT, "
            + PILOT_CLUB + " TEXT, "
            + PILOT_IS_TOWPILOT + " TEXT, "
            + PILOT_CELL_NUM + " TEXT, "
            + PILOT_EMAIL + " TEXT, "
            + PILOT_EDEVICES + " TEXT, "
            + PILOT_ECONTACT + " TEXT, "
            + PILOT_EPHONE_NUM + " TEXT, "
            + PILOT_COMMENTS + " TEXT "
            + " )";

    //============= Tow Planes ================//

    public static final String TUG_TABLE_NAME = "TowPlanes";
    public static final String TUG_ID = "_id";
    public static final String TUG_NAME = "Tug";
    public static final String TUG_IS_ACTIVE = "TugIsActive";
    public static final String TUG_CLUB = "Club";
    public static final String TUG_TOW_PILOT = "TowPilot";
    public static final String TUG_DESCRIPTION = "Description";
    public static final String TUG_COMMENTS = "Comments";
    public static final String TUG_SOURCE_SHEET = "TowPlanes!A:F";
    private static final String CREATE_TABLE_TUGS = "create table TowPlanes"
            + "( "
            + TUG_ID + " INTEGER PRIMARY KEY , "
            + TUG_NAME + " text not null, "
            + TUG_CLUB + " TEXT, "
            + TUG_TOW_PILOT + " TEXT, "
            + TUG_DESCRIPTION + " TEXT, "
            + TUG_COMMENTS + " TEXT "
            + " )";

//================ Gliders =======================//

    public static final String GLIDER_TABLE_NAME = "Gliders";
    public static final String GLIDER_ID = "_id";
    public static final String GLIDER_CONTEST_NUMBER = "ContestNumber";
    public static final String GLIDER_IS_TWO_PLACE = "IsTwoPlace";
    public static final String GLIDER_TYPE = "TYPE";
    public static final String GLIDER_OWNER = "OWNER";
    public static final String GLIDER_COMMENTS = "Comments";
    public static final String GLIDER_SOURCE_SHEET = "Gliders!A:F";
    private static final String CREATE_TABLE_GLIDERS = "create table Gliders"
            + "( "
            + GLIDER_ID + " INTEGER PRIMARY KEY , "
            + GLIDER_CONTEST_NUMBER + " text not null, "
            + GLIDER_IS_TWO_PLACE + " TEXT, "
            + GLIDER_TYPE + " TEXT, "
            + GLIDER_OWNER + " TEXT, "
            + GLIDER_COMMENTS + " TEXT "
            + " )";

//===================== FlightType ======================//

    public static final String TYPE_TABLE_NAME = "FlightType";
    public static final String TYPE_ID = "_id";
    public static final String TYPE_NAME = "FlightType";
    public static final String TYPE_SOURCE_SHEET = "FlightType!A:B";
    private static final String CREATE_TABLE_TYPE = "create table FlightType"
            + "( "
            + TYPE_ID + " INTEGER PRIMARY KEY , "
            + TYPE_NAME + " text not null "
            + " )";

//=======================================================//


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PILOTS);
        db.execSQL(CREATE_TABLE_TUGS);
        db.execSQL(CREATE_TABLE_GLIDERS);
        db.execSQL(CREATE_TABLE_FLIGHTS);
        db.execSQL(CREATE_TABLE_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + PILOT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TUG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GLIDER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FLIGHT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TYPE_TABLE_NAME);
        onCreate(db);

    }

    void clearAllData() {
        SQLiteDatabase db = MyApp.db;
        db.execSQL("DROP TABLE IF EXISTS " + PILOT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TUG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GLIDER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FLIGHT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TYPE_TABLE_NAME);
        onCreate(db);
    }

    public static ArrayList<String> getNameList(String tableName, String nameField) {

        Cursor c = MyApp.db.query(tableName, new String[]{nameField}, null, null, null, null, null);
        ArrayList<String> list = new ArrayList<String>();
        if (c != null) {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                list.add(c.getString(0)); //add the item
            }
        }
        return list;
    }

    public static boolean backupDBToSDCard(Context context) {
        Application m = new MyApp();
        //  Context context = MyApp.getContext();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        String path = sharedPreferences.getString("prefs_backup_path", "");
        File sd = new File(path);
        File extDir = new File(context.getExternalFilesDir("myfilepath"), "myfile");

        Log.i("RRR backupDBToSDCard", " file path" + sd.getAbsolutePath());


        boolean success = true;
        if (!sd.exists()) {
            success = sd.mkdir();
        }
        if (success) {

            File data = Environment.getDataDirectory();
            FileChannel source = null;
            FileChannel destination = null;
            String currentDBPath = "/data/" + context.getPackageName() + "/databases/" + "logger.db";
            String backupDBPath = "loggerbak.db";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);
            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
                Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String writeDataToCSVFile() {
        Application m = new MyApp();

        Log.i("RRR", "Trying Sqlite exporter for CSV");

        SqliteExporter sqliteExporter = new SqliteExporter();
        try {
            sqliteExporter.export(MyApp.db);
        } catch (Exception e) {
            Log.e("RRR>", "Error in Export CSV");
            Log.e("RRR>", e.getMessage());
            return e.getMessage();
        }
        return "Successful CSV File Write";
    }

    public static int numberOfColumns(String tableName) {


        Cursor cursor = MyApp.db.query(tableName, null, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.getColumnCount() > 0) {
                return cursor.getColumnCount();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }




    public static String readDataFromCSVFile() {
        SQLiteDatabase db = MyApp.db;
        FileReader importFileReader;
        String currentTable = "none";
        String fileName = "data.csv";



        String fullPath = FileUtils.getAppDir() + "/backup/" + fileName;

        File file = new File (fullPath);
        try {
            importFileReader = new FileReader(file);
        } catch (IOException e){
            Log.e("RRR>", e.getMessage());
            //todo add file not found toast
            return("File Not Found");
        }
        BufferedReader buffer = new BufferedReader(importFileReader);
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] colums = line.split(",");


                //               if (colums[0] == "dbVersion=" + DATABASE_VERSION){
                //todo add db version check
                //              }
                int t = colums.length;

                if (colums.length == 1) {
                    //Log.d("CSVParser", "Skipping Bad CSV Row");
                    //continue;
                    switch (colums[0]){
                        case "table=Pilots"     :
                        case "table=TowPlanes"  :
                        case "table=Flights"    :
                        case "table=Gliders"    :
                        case "table=FlightType" : currentTable = colums[0];
                                                  currentTable = currentTable.substring(currentTable.indexOf('=')+1); // returns the table
                                                  Log.i("RRR>","changing to table " + currentTable);
                                                  continue;
                        default                 : Log.d("CSVParser", "Skipping Bad CSV Row");
                                                  continue;
                    }
                }
                if(currentTable == "none") continue;

                Cursor cursor = db.rawQuery("SELECT * FROM " + currentTable, null);
                String[] colNames = cursor.getColumnNames();

                if(colums[0].equals("_id")) continue; // check for header row

                ContentValues cv = new ContentValues(numberOfColumns(currentTable));
                for (int i=0; i<colums.length;i++) {
                    cv.put(colNames[i], colums[i].trim());
                }
                db.insert(currentTable, null, cv);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return ("File Successfully Imported");
    }

    public String readDataFromCSVFileReader() {
        SQLiteDatabase db = MyApp.db;
        FileReader importFileReader;
        String currentTable = "none";
        String fileName = "data.csv";

        clearAllData();
        String fullPath = FileUtils.getAppDir() + "/backup/" + fileName;

        File file = new File (fullPath);
        try {
            importFileReader = new FileReader(file);

        } catch (IOException e){
            Log.e("RRR>", e.getMessage());
            //todo add file not found toast
            return("File Not Found");
        }
        CSVReader reader = new CSVReader(importFileReader);

        String line = "";
        db.beginTransaction();
        String[] colums;
        try {
            while ((colums = reader.readNext()) != null) {
                //               if (colums[0] == "dbVersion=" + DATABASE_VERSION){
                //todo add db version check
                //              }
                int t = colums.length;

                if (colums.length == 1) {
                    //Log.d("CSVParser", "Skipping Bad CSV Row");
                    //continue;
                    switch (colums[0]){
                        case "table=Pilots"    :
                        case "table=TowPlanes" :
                        case "table=Flights"   :
                        case "table=FlightType": currentTable = colums[0];
                            currentTable = currentTable.substring(currentTable.indexOf('=')+1); // returns the table
                            Log.i("RRR>","changing to table " + currentTable);
                            continue;
                        default             : Log.d("CSVParser", "Skipping Bad CSV Row");
                            continue;
                    }
                }
                if(currentTable == "none") continue;

                Cursor cursor = db.rawQuery("SELECT * FROM " + currentTable, null);
                String[] colNames = cursor.getColumnNames();

                if(colums[0].equals("_id")) continue; // check for header row

                ContentValues cv = new ContentValues(numberOfColumns(currentTable));
                for (int i=0; i<colums.length;i++) {
                    cv.put(colNames[i], colums[i].trim());
                }
                db.insert(currentTable, null, cv);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return ("File Successfully Imported");
    }


}

