package org.mtwashingtonsoaring.logger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FlightEditActivity extends AppCompatActivity {

    private static final String EXTRA_GUID = "org.mtwashingtonsoaring.logger.flightGUID";
    public static final String NEW_PILOT = "NewPilot";

    private Long flightId;
    private Flight flight;

    private boolean  addToDroplist = false;
    private static final int PILOT_ONE_TVA = R.id.tvaPilotOne, PILOT_TWO_TVA = R.id.tvaPilotTwo,
                             BILL_TO_TVA = R.id.tvaBillTo, TOW_PILOT_TVA = R.id.tvaTowPilot,
                             TUG_SPINNER = R.id.spinnerTowPlane,CID_SPINNER = R.id.spinnerCID, SPINNER_TYPE = R.id.spinnerFlightType ;
    private static final int P1_TAG = 1, P2_TAG = 2, P_TOW_TAG = 3, P_BILL_TO_TAG = 4,GLIDER_TAG = 5,TUG_TAG=6, TYPE_TAG = 7;  // nead this as you can only pass 16bits to activity for result
    private ArrayAdapter<String> pilotDataAdapter;  //   private ArrayAdapter<StringWithTag> pilotDataAdapter;
    private boolean userIsInteracting = false;
    private boolean itemDeleted = false;
    private static final String GLIDER_PROMPT = "??";
    private static final String NEW_ITEM_PROMPT = " + ";
    private static final String PICK_PILOT_PROMPT = "Pick Pilot";



    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }


    public static void start(Context context, String extraGUID) {
        Intent intent = new Intent(context, FlightEditActivity.class);
        intent.putExtra(EXTRA_GUID, extraGUID);
        context.startActivity(intent);
    }

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain", "Belltime"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        flightId = Long.parseLong(intent.getStringExtra(EXTRA_GUID));
        setContentView(R.layout.activity_flight_edit);

        flight = new Flight();
        if (!flightId.equals(Flight.NEW_FLIGHT)) {

            flight = flight.getFlightById(flightId);  //  retrieve flight by id
            if (flight == null){
                Log.e ("RR Error", "flight index not found, guid not found");
                return;
            }
        }

        populateSpinnerList(DBHelper.TYPE_TABLE_NAME,DBHelper.TYPE_NAME,flight.type,(Spinner) findViewById(R.id.spinnerFlightType));

        //  fill Tow Height
        EditText editText = (EditText) findViewById(R.id.editTextTowHeight);
        editText.setText(String.valueOf(flight.towHeight));

        // fill flight mode;
        RadioButton tempRB;
        tempRB = (RadioButton) findViewById(R.id.radioAir); tempRB.setChecked(false);
        tempRB = (RadioButton) findViewById(R.id.radioGrid); tempRB.setChecked(false);
        tempRB = (RadioButton) findViewById(R.id.radioGround); tempRB.setChecked(false);

        switch (flight.mode){
            case Flight.AIR : tempRB = (RadioButton) findViewById(R.id.radioAir); tempRB.setChecked(true); break;
            case Flight.GRID : tempRB = (RadioButton) findViewById(R.id.radioGrid); tempRB.setChecked(true); break;
            case Flight.GROUND : tempRB = (RadioButton) findViewById(R.id.radioGround); tempRB.setChecked(true); break;
        }

        //  fill Comments
        editText = (EditText) findViewById(R.id.editTextComments);
        editText.setText(String.valueOf(flight.comments));

        //  fill takeoff and landing time
        EditText time = (EditText) findViewById(R.id.editTextTakeOffTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        if(flight.takeoffTime == Flight.EMPTY_TIME){
            time.setText("");
        }else{
            time.setText(flight.takeoffTime);
            // SetTimePicker returnTime = new SetTimePicker(time, this,flight.takeoffTime.get(GregorianCalendar.HOUR_OF_DAY),flight.takeoffTime.get(GregorianCalendar.MINUTE));
        }

        time = (EditText) findViewById(R.id.editTextLandingTime);
        if(flight.landingTime == Flight.EMPTY_TIME){
            time.setText("");
        }else{
   //     time.setText(timeFormat.format(flight));
          time.setText(flight.landingTime);
        }

        populateSpinnerList(DBHelper.TUG_TABLE_NAME,DBHelper.TUG_NAME,flight.tug,(Spinner) findViewById(R.id.spinnerTowPlane));
        populateSpinnerList(DBHelper.GLIDER_TABLE_NAME,DBHelper.GLIDER_CONTEST_NUMBER,flight.glider,(Spinner) findViewById(R.id.spinnerCID));
        if(flightId.equals(Flight.NEW_FLIGHT)){
            Spinner s = (Spinner) findViewById(R.id.spinnerCID);

            s.setFocusable(true);
            s.setFocusableInTouchMode(true);
            s.requestFocus();
        }

        if (!flightId.equals(Flight.NEW_FLIGHT)) {
            populatePilotDropList(PILOT_ONE_TVA, flight.pilotOne);
            populatePilotDropList(PILOT_TWO_TVA, flight.pilotTwo);
            populatePilotDropList(BILL_TO_TVA,flight.billTo);
            populatePilotDropList(TOW_PILOT_TVA,flight.towPilot);
        } else {
            populatePilotDropList(PILOT_ONE_TVA, NEW_PILOT);
            populatePilotDropList(PILOT_TWO_TVA, NEW_PILOT);
            populatePilotDropList(BILL_TO_TVA, NEW_PILOT);
            populatePilotDropList(TOW_PILOT_TVA, NEW_PILOT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String oldPilot1, oldPilot2;
        InstantAutoComplete s;


        addToDroplist = false;
        setPilotSpinnersForTwoPlace(Glider.isGliderTwoplace(flight.glider));
        Log.i("RR> ", "Came back to onActivityResult");
        Log.i("RR", "requestCode = " + requestCode);
        if (resultCode == RESULT_OK) {
            String newItem = data.getStringExtra(EditCursorActivity.EXTRA_RETURN_VALUE_TAG);
            switch (requestCode) {

                case P1_TAG         :   populatePilotDropList(PILOT_ONE_TVA, newItem); break;
                case P2_TAG         :   populatePilotDropList(PILOT_TWO_TVA, newItem); break;
                case P_BILL_TO_TAG  :   populatePilotDropList(BILL_TO_TVA, newItem); break;
                case P_TOW_TAG      :   populatePilotDropList(TOW_PILOT_TVA, newItem); break;

                case GLIDER_TAG    :  // flight.glider = newItem;
                                        populateSpinnerList(DBHelper.GLIDER_TABLE_NAME,DBHelper.GLIDER_CONTEST_NUMBER,newItem,(Spinner) findViewById(R.id.spinnerCID));
                                        break;

                case TUG_TAG        :   flight.tug = newItem;
                                        populateSpinnerList(DBHelper.TUG_TABLE_NAME,DBHelper.TUG_NAME,newItem,(Spinner) findViewById(R.id.spinnerTowPlane));
                                        break;
                case TYPE_TAG       :   populateSpinnerList(DBHelper.TYPE_TABLE_NAME,DBHelper.TYPE_NAME,newItem,(Spinner) findViewById(R.id.spinnerFlightType));
                                        break;
                default:
            }
            if(requestCode == P1_TAG || requestCode == P2_TAG || requestCode == P_BILL_TO_TAG || requestCode == P_TOW_TAG){
                s = (InstantAutoComplete) findViewById(PILOT_ONE_TVA); String tempString = s.getText().toString(); populatePilotDropList(PILOT_ONE_TVA,tempString);
                s = (InstantAutoComplete) findViewById(PILOT_TWO_TVA);  tempString = s.getText().toString(); populatePilotDropList(PILOT_TWO_TVA,tempString);
                s = (InstantAutoComplete) findViewById(TOW_PILOT_TVA);  tempString = s.getText().toString(); populatePilotDropList(TOW_PILOT_TVA,tempString);
                s = (InstantAutoComplete) findViewById(BILL_TO_TVA);  tempString = s.getText().toString(); populatePilotDropList(BILL_TO_TVA,tempString);


            }
        } else {
            if (resultCode == RESULT_CANCELED) {
                Log.i("RR> resultCode = ", "RESULT_CANCELED");

            }
        }
    }



    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFlightEdit();
    }


  private void saveFlightEdit () {
      int idx;


      Spinner s = (Spinner) findViewById(R.id.spinnerCID);
      if (s.getSelectedItem().equals(NEW_ITEM_PROMPT)) {
          flight.glider = "";
      } else {
          flight.glider = (s.getSelectedItem().toString());
      }

      InstantAutoComplete t = (InstantAutoComplete) findViewById(R.id.tvaPilotOne);
      if (t.getText().toString().equals(NEW_ITEM_PROMPT)||t.getText().toString().equals(PICK_PILOT_PROMPT)) {
          flight.pilotOne = "";
      } else {
          flight.pilotOne = (String) t.getText().toString();
      }

      t = (InstantAutoComplete) findViewById(R.id.tvaPilotTwo);
      if (t.getText().toString().equals(NEW_ITEM_PROMPT)||t.getText().toString().equals(PICK_PILOT_PROMPT)) {
          flight.pilotTwo = "";
      } else {
          flight.pilotTwo =  (String) t.getText().toString();
      }
      t = (InstantAutoComplete) findViewById(R.id.tvaBillTo);
      if (t.getText().toString().equals(NEW_ITEM_PROMPT)||t.getText().toString().equals(PICK_PILOT_PROMPT)) {
          flight.billTo = flight.pilotOne;
      } else {
          flight.billTo = (String) t.getText().toString();
      }
      t = (InstantAutoComplete) findViewById(R.id.tvaTowPilot);
      if (t.getText().toString().equals(NEW_ITEM_PROMPT)||t.getText().toString().equals(PICK_PILOT_PROMPT)) {
          flight.towPilot = "";
      } else {
          flight.towPilot = (String) t.getText().toString();
      }

        // save flight mode
      if(((RadioButton) findViewById(R.id.radioGround)).isChecked()) { flight.mode = Flight.GROUND;}
      if(((RadioButton) findViewById(R.id.radioAir)).isChecked()) { flight.mode = Flight.AIR;}
      if(((RadioButton) findViewById(R.id.radioGrid)).isChecked()) { flight.mode = Flight.GRID;}

      s = (Spinner) findViewById(R.id.spinnerTowPlane);
      if (s.getSelectedItem().equals(NEW_ITEM_PROMPT)) {
          flight.tug = "";
      } else {
          flight.tug = (s.getSelectedItem().toString());
      }

      EditText et = (EditText) findViewById(R.id.editTextTowHeight);
      flight.towHeight = Double.parseDouble(et.getText().toString());
      et = (EditText) findViewById(R.id.editTextTakeOffTime);
      flight.takeoffTime = et.getText().toString();
      et = (EditText) findViewById(R.id.editTextLandingTime);
      flight.landingTime = et.getText().toString();
      et = (EditText) findViewById(R.id.editTextComments);
      flight.comments = et.getText().toString();

      if(!(itemDeleted || flight.glider.equals(GLIDER_PROMPT) )){

          long id = flight.writeFlight(flight);
          if (id>=0) flight._id = id;
      }
  }



    private void populatePilotDropList(int id, String pilot){

        final InstantAutoComplete textViewAutoComplete;
        ArrayList<String> list;

        Pilot p = new Pilot();

        String currentGlider = ((Spinner) findViewById(R.id.spinnerCID)).getSelectedItem().toString();
        if (id == R.id.tvaTowPilot){
            list = p.getPilotList("TowPilot");
        } else {
            list = p.getPilotList(currentGlider);
        }

        textViewAutoComplete = (InstantAutoComplete) findViewById(id);
        textViewAutoComplete.setOnFocusChangeListener(new PilotTVAFocusChangeListener());
        pilotDataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);   //ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerList);
        pilotDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        textViewAutoComplete.setAdapter(pilotDataAdapter);
        if(!pilot.equals(NEW_PILOT)) textViewAutoComplete.setText(pilot);

        setPilotSpinnersForTwoPlace(Glider.isGliderTwoplace(flight.glider));
    }

    public class PilotTVAFocusChangeListener implements TextView.OnFocusChangeListener{


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                if(v.getId() == PILOT_ONE_TVA){
                    InstantAutoComplete tva = (InstantAutoComplete) findViewById(BILL_TO_TVA);
                    if(tva.getText().toString().length()==0) {
                        tva.setText(((TextView) v).getText());
                    }
                }
            }
        }
    }


    public class PilotStringSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
            if(userIsInteracting) {  //userIsInteracting



                String s =  (String) parent.getItemAtPosition(position);
                int spinnerSource =  parent.getId();
                if (s.equals(NEW_ITEM_PROMPT)){
                    addToDroplist = true;
                    Intent intent = new Intent(getApplicationContext(), EditCursorActivity.class);
                    intent.putExtra(EditCursorActivity.EXTRA_ID, EditCursorActivity.NEW_ITEM);
                    String tableName = DBHelper.PILOT_TABLE_NAME;
                    int resultTag = 0;
                    switch (spinnerSource){
                        case PILOT_ONE_TVA: resultTag = P1_TAG; break;
                        case PILOT_TWO_TVA: resultTag = P2_TAG; break;
                        case TOW_PILOT_TVA: resultTag = P_TOW_TAG; break;
                        case BILL_TO_TVA: resultTag = P_BILL_TO_TAG; break;
                        case TUG_SPINNER  : resultTag = TUG_TAG; tableName = DBHelper.TUG_TABLE_NAME; break;
                        case CID_SPINNER  : resultTag = GLIDER_TAG; tableName = DBHelper.GLIDER_TABLE_NAME; break;
                        case SPINNER_TYPE : resultTag = TYPE_TAG; tableName = DBHelper.TYPE_TABLE_NAME;break;
                    }
                    intent.putExtra(EditCursorActivity.EXTRA_TABLE_NAME_TAG, tableName);
                    startActivityForResult(intent, resultTag);
                }
                if(spinnerSource == CID_SPINNER) {
                    flight.glider = s;
                    populatePilotDropList(R.id.tvaPilotOne, flight.pilotOne);
                    populatePilotDropList(R.id.tvaPilotTwo,flight.pilotTwo);
                }
               InstantAutoComplete billTo = (InstantAutoComplete) findViewById(BILL_TO_TVA);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

            // sometimes you need nothing here
        }

    }


    private void setPilotSpinnersForTwoPlace(Boolean twoPlace) {

        InstantAutoComplete pilotSpinner1 = (InstantAutoComplete) findViewById(R.id.tvaPilotOne);
        InstantAutoComplete pilotSpinner2 = (InstantAutoComplete) findViewById(R.id.tvaPilotTwo);
        TextView spinnerPrompt = (TextView) findViewById(R.id.textViewP2Prompt);

        if (twoPlace) {
            pilotSpinner1.setEnabled(true);
            pilotSpinner2.setEnabled(true);
            pilotSpinner2.setVisibility(View.VISIBLE);
            spinnerPrompt.setVisibility(View.VISIBLE);
        } else {
            pilotSpinner1.setEnabled(true);
            pilotSpinner2.setEnabled(false);
            pilotSpinner2.setVisibility(View.INVISIBLE);
            spinnerPrompt.setVisibility(View.INVISIBLE);
        }
    }



    private Integer findPilotIndexByName(ArrayList<String> spinnerList ,String name){
        int i;

        for (i=0;i<spinnerList.size() ;i++){

            if(spinnerList.get(i).equals(name)){
                return i;
            }
        }
        return -1; // not found
    };



    public void populateSpinnerList(String tableName, String nameCol, String initalValue, Spinner spinner) {

        List <String> spinnerList = new ArrayList<String>();
        boolean found = false; Integer i;
        ArrayAdapter<String> dataAdapter;

        spinnerList = DBHelper.getNameList(tableName,nameCol);

        spinnerList.add(0,GLIDER_PROMPT);
        spinnerList.add(spinnerList.size(), NEW_ITEM_PROMPT);
        spinner.setOnItemSelectedListener(new PilotStringSpinnerListener());
        if(spinner.getId() == R.id.spinnerCID) {
             dataAdapter = new ArrayAdapter<String>(this, R.layout.cid_spinner_layout, spinnerList);
        } else {
             dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item , spinnerList);
        }
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);

        for(i=0;i<spinnerList.size();i++){
            if(spinnerList.get(i).equals(initalValue)){
                found = true;
                spinner.setSelection(i);
                break;
            }
        }

        if(initalValue.length()>0 && !found){
            spinnerList.add(1, initalValue);
            spinner.setSelection(1);   // insert old value found in flight that is not currently in database;
        }

    }








//


    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionBar = getSupportActionBar();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cursor_edit_menu, menu);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        String editOrNew = (flightId.equals(Flight.NEW_FLIGHT))? "New " : "Edit ";
        actionBar.setTitle(editOrNew + "Flight");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.delete_item:
                if(flightId.equals(Flight.NEW_FLIGHT)){
                    itemDeleted = true;
                    finish();
                    break;
                }
                AlertDialog.Builder ADbuilder = new AlertDialog.Builder(this);
                ADbuilder.setMessage("Delete Flight? Are you sure?").setPositiveButton("Yes", yesNoDialogClickListener)
                        .setNegativeButton("No", yesNoDialogClickListener).show();
                break;

            case R.id.done :
                finish();

            default:

                break;
        }
        return true;
    }



    DialogInterface.OnClickListener yesNoDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    MyApp.db.delete(DBHelper.FLIGHT_TABLE_NAME,"_id = " + flightId,null);
                    Toast.makeText(getBaseContext(), "Deleted Item " + flightId, Toast.LENGTH_SHORT).show();
                    itemDeleted = true;
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

}

