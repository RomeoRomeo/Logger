package org.mtwashingtonsoaring.logger;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;



public class FlightListHolder extends AppCompatActivity implements ItemPickerDF.OnItemSelectedListener {
    static private final String EDIT_PILOTS = "Edit Pilots";
    static private final String EDIT_TUGS = "Edit Tugs";
    static private final String EDIT_GLIDERS = "Edit Gliders";
    static private final String GET_ALL = "Get Sheets Data";
    static private final String WRITE_ALL = "Write Sheets Data";
    static private final String CLEAR_ALL_DATA = "Clear All Data";
    static private final String SETTINGS = "Settings";
    static final String FLIGHT_LIST_FRAGMENT_TAG = "flightListFragment";
    static final String TEST_SD_CARD = "Test SD Card";
    static final String BACKUP_TO_SDCARD = "Backup";
    static final String WRITE_CSV_FILE = "Write CSV File";
    static final String READ_CSV_FILE = "Read CSV File";
    static long lastFlightLaunched;

    private ArrayList <String> drawerMenuItems = new ArrayList<String>(Arrays.asList(new String[] {
            //GET_ALL,
            EDIT_PILOTS,EDIT_GLIDERS,EDIT_TUGS,
            //WRITE_ALL,
            CLEAR_ALL_DATA,
            //BACKUP_TO_SDCARD,
            WRITE_CSV_FILE,
            READ_CSV_FILE,
            SETTINGS}));
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProgressDialog mProgress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbflight_list_holder);

        Log.e("RRR","Startup 3");

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            FlightListFragment firstFragment = new FlightListFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment,FLIGHT_LIST_FRAGMENT_TAG).commit();
        }

        setActionbarTitle();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlightEditActivity.start(view.getContext(), Long.toString(Flight.NEW_FLIGHT));
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new NavMenuAdapter(this, drawerMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Retrieving Google Sheets Data");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            drawerItemActions(position);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        menu.findItem(R.id.filter_air_active).setVisible(false);
        menu.findItem(R.id.filter_grid_active).setVisible(false);
        menu.findItem(R.id.filter_ground_active).setVisible(false);

        String flightMode = MyApp.listFilter;
       switch (flightMode){
           case Flight.GRID :   menu.findItem(R.id.filter_grid).setVisible(false);
                                menu.findItem(R.id.filter_grid_active).setVisible(true);
                                break;
           case Flight.AIR :   menu.findItem(R.id.filter_air).setVisible(false);
                                menu.findItem(R.id.filter_air_active).setVisible(true);
                                break;
           case Flight.GROUND :   menu.findItem(R.id.filter_ground).setVisible(false);
                                menu.findItem(R.id.filter_ground_active).setVisible(true);
                                break;
            default: // make no change
       }
       return true;
    }





    DialogInterface.OnClickListener clearDataOK = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    new DBHelper(MyApp.getContext()).clearAllData();
                    FlightListFragment fList = (FlightListFragment) getSupportFragmentManager().findFragmentByTag(FLIGHT_LIST_FRAGMENT_TAG);
                    fList.updateList();

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    DialogInterface.OnClickListener readDataFromCSVConfirm = new DialogInterface.OnClickListener() {
        @Override

        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    new DBHelper(MyApp.getContext()).clearAllData();
                    FlightListFragment fList = (FlightListFragment) getSupportFragmentManager().findFragmentByTag(FLIGHT_LIST_FRAGMENT_TAG);
                    fList.updateList();
                    String msg = DBHelper.readDataFromCSVFile();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void drawerItemActions(int position) {
       String s;
       String msg;

        s = drawerMenuItems.get(position);

        switch (s){
            case GET_ALL        : SheetsComGetData.start(this, SheetsComGetData.READ_SHEET_DATA); break;
            case EDIT_GLIDERS   : ListCursorActivity.start(this,DBHelper.GLIDER_TABLE_NAME); break;
            case EDIT_PILOTS    : ListCursorActivity.start(this, DBHelper.PILOT_TABLE_NAME);break;
            case EDIT_TUGS      : ListCursorActivity.start(this, DBHelper.TUG_TABLE_NAME);break;
            case WRITE_ALL      : SheetsComGetData.start(this, SheetsComGetData.WRITE_SHEET_DATA); break;
            case CLEAR_ALL_DATA: AlertDialog.Builder ADbuilder = new AlertDialog.Builder(this);
                ADbuilder.setMessage("Are you sure?").setPositiveButton("Yes", clearDataOK )
                        .setNegativeButton("No", clearDataOK)
                        .setTitle("Clear all Data?").show();
                break;
            case SETTINGS       : getFragmentManager().beginTransaction()
                                  .replace(R.id.fragment_container, new SettingsFragment()).addToBackStack(null)
                                  .commit();
                                  break;
            case TEST_SD_CARD   :   TestSDCardWrite.start(this); break;
            case BACKUP_TO_SDCARD : DBHelper.backupDBToSDCard(this);break;
            case WRITE_CSV_FILE :   msg = DBHelper.writeDataToCSVFile();
                                    Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                                            .show();
                                    break;
            case READ_CSV_FILE  : AlertDialog.Builder confirmDeleteDB = new AlertDialog.Builder(this);
                confirmDeleteDB.setMessage("Are you sure?").setPositiveButton("Yes", readDataFromCSVConfirm )
                        .setNegativeButton("No", readDataFromCSVConfirm)
                        .setTitle("Read Will Clear All Old Data!").show();
                break;
        }

        mDrawerLayout.closeDrawer(mDrawerList);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionBar = getSupportActionBar();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.flightlist_menu, menu);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeActionContentDescription("test");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24);
        return true;
    }

    private void setActionbarTitle(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Log / " + MyApp.listFilter);
        // actionBar.setTitle(MyApp.listFilter);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.filter_grid:
                MyApp.listFilter = Flight.GRID;
                Toast.makeText(this, "Grid", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.filter_air:
                MyApp.listFilter = Flight.AIR;
                Toast.makeText(this, "Air", Toast.LENGTH_SHORT).show();
                break;
            case R.id.filter_ground:
                MyApp.listFilter = Flight.GROUND;
                Toast.makeText(this, "Ground", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }

                break;

            default:

                break;
        }

        // update list
        FlightListFragment fList = (FlightListFragment) getSupportFragmentManager().findFragmentByTag(FLIGHT_LIST_FRAGMENT_TAG);
        fList.updateList();
        setActionbarTitle();
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        FlightListFragment fList = (FlightListFragment) getSupportFragmentManager().findFragmentByTag(FLIGHT_LIST_FRAGMENT_TAG);
  //      Log.e("RRR","fList is Null " + (fList == null));
        fList.updateList();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void launchLandBtnPressed(Long flightId){

        int fIndex;

        Flight f = new Flight();
        GregorianCalendar now = new GregorianCalendar();
        now.setTime(new Date());
        SimpleDateFormat fmt = new SimpleDateFormat("kk:mm");
        String nowString = fmt.format(now.getTime());

        f = f.getFlightById(flightId);
        Log.e("RRR","flight = " + f._id);
        if (f == null)return;
        if(MyApp.listFilter.equals(Flight.GRID)){

            f.takeoffTime = nowString;
            f.mode = Flight.AIR;
            displayTugDB();
            lastFlightLaunched = flightId;
        }
        if(MyApp.listFilter == Flight.AIR){
            f.landingTime = nowString;
            f.mode = Flight.GROUND;
        }
        f.writeFlight(f);

    }



    public void displayTugDB() {


        Cursor c = MyApp.db.query(DBHelper.TUG_TABLE_NAME, new String[]{ DBHelper.TUG_NAME}, null, null, null, null, null);
        ArrayList<ItemPickerDF.Item> pickerItems = new ArrayList<>();
        if(c != null) {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                pickerItems.add(new ItemPickerDF.Item(c.getString(0), c.getPosition()));
            }
        }



        ItemPickerDF dialog = ItemPickerDF.newInstance("PickTug", pickerItems,-1);
        FragmentManager fm = getSupportFragmentManager();
        dialog.show(fm, "PickTug");
    }

    @Override

    public void onItemSelected(ItemPickerDF fragment, ItemPickerDF.Item item, int index) {
        Flight f = new Flight();
        f = f.getFlightById(lastFlightLaunched);
        String selectedValue = item.getTitle();
        if (selectedValue != null) {
            f.tug = selectedValue;
            f.writeFlight(f);
        }
        Log.e( "RRR"," Selected Value = " + selectedValue );

    }

}
