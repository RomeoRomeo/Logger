package org.mtwashingtonsoaring.logger;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    AlertDialog alertDialogStores;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

    }

    public void editPilot(View view)
    {
      //  DataStore ps = new DataStore();
      //  ps.currentIndex = 2;
      //  ListCursorActivity.start(this, DBHelper.GLIDER_TABLE_NAME);
        Intent intent = new Intent(this, FlightListHolder.class);
        startActivity(intent);

    }

    public void editFlight(View view)
    {
        //DataStore ps = new DataStore();
       // FlightEditActivity.start(this, (DataStore.flightList.get(1)).id);
        SheetsComGetData.start(this, "placeholder");

    }





 //   public void showSheetsCom (View view){
 //       SheetsCom.start(this, "placeholder");
//
 //   }


    public void testCursorEdit(View view)
    {
        EditCursorActivity.start(this, "Rick Roelke", Pilot.DBC_TABLE_NAME);

    }



    public void showTime(View view){
        GregorianCalendar cal = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        Button showTimeBtn = (Button) findViewById(R.id.timeBtn);
        showTimeBtn.setText("Time : " + format.format(cal.getTime()));

    }

}



