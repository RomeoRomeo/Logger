package org.mtwashingtonsoaring.logger;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

// Note this Class requires that a field _id is the first column of the record and it is unique and a int

public class EditCursorActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "org.mtwashingtonsoaring.logger.EDIT_ID";
    public static final String NEW_ITEM = "NEW";
    public static final String EXTRA_TABLE_NAME_TAG = "org.mtwashingtonsoaring.logger.TABLE_NAME";
    public static final String EXTRA_RETURN_VALUE_TAG = "org.mtwashingtonsoaring.logger.RETURN_VALUE";

    static private final String EDIT_PILOTS = "Edit Pilots";
    static private final String EDIT_TUGS = "Edit Tugs";
    static private final String EDIT_GLIDERS = "Edit Gliders";
    static private final String GET_ALL = "Get Sheets Data";


    TextView promptTV[];
    EditText valuesET[];
    String tableName;
    String itemID;
    Cursor vCursor;
    String[] prompts;
    boolean itemDeleted = false;



    public static void start(Context context, String itemID, String tableName) {
        Intent intent = new Intent(context, EditCursorActivity.class);
        intent.putExtra(EXTRA_ID, itemID);
        intent.putExtra(EXTRA_TABLE_NAME_TAG, tableName);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionBar = getSupportActionBar();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cursor_edit_menu, menu);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    //    actionBar.setDisplayShowHomeEnabled(true);
   //     actionBar.setDisplayHomeAsUpEnabled(true);
   //     actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24);
        String editOrNew = (itemID.equals(NEW_ITEM))? "New " : "Edit ";
        switch (tableName){
            case DBHelper.GLIDER_TABLE_NAME : actionBar.setTitle(editOrNew + "Glider"); break;
            case DBHelper.PILOT_TABLE_NAME : actionBar.setTitle(editOrNew + "Pilot"); break;
            case DBHelper.TUG_TABLE_NAME : actionBar.setTitle(editOrNew + "Tow Plane"); break;
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.delete_item:
                if(itemID.equals(NEW_ITEM)){
                    itemDeleted = true;
                    finish();
                }
                AlertDialog.Builder ADbuilder = new AlertDialog.Builder(this);
                String singularItem = tableName.substring(0,tableName.length()-1);
                ADbuilder.setMessage("Delete " + singularItem + "? Are you sure?").setPositiveButton("Yes", yesNoDialogClickListener)
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
                    MyApp.db.delete(tableName,"_id = " + itemID,null);
                    itemDeleted = true;
                    finish();

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] where = new String[]{""};

        Intent intent = this.getIntent();
        itemID = intent.getStringExtra(EXTRA_ID);
        tableName = intent.getStringExtra(EXTRA_TABLE_NAME_TAG);

        Cursor dbCursor = MyApp.db.query(tableName, null, null, null, null, null, null);
        prompts = dbCursor.getColumnNames();

        where[0] = itemID;
        if(!itemID.equals(NEW_ITEM)){
            vCursor = MyApp.db.query(tableName, null, "_id = ?", where, null, null, null);
            vCursor.moveToFirst();
        }


        ScrollView scrollview = new ScrollView(this);
        TableRow.LayoutParams itemParams = new TableRow.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f);

        TableLayout tableLayout = new TableLayout(this);
        scrollview.addView(tableLayout);

        promptTV = new TextView[prompts.length];
        valuesET = new EditText[prompts.length];

        for (int i = 0; i < prompts.length; i++) { // i = 1 as we dont want to show the id field
            promptTV[i] = new TextView(this);
            promptTV[i].setGravity(Gravity.RIGHT);
            promptTV[i].setText(prompts[i] + ": ");
            valuesET[i] = new EditText(this);
            valuesET[i].setText("");
            if(!itemID.equals(NEW_ITEM) ){
                valuesET[i].setText(vCursor.getString(i));
            }
            valuesET[i].setLayoutParams(itemParams);
            TableRow tableRow = new TableRow(this);
            if(i>0) {                               // dont add the id to the view
                tableRow.addView(promptTV[i]);
                tableRow.addView(valuesET[i]);
                tableLayout.addView(tableRow);
            }
        }

        setContentView(scrollview);
        dbCursor.close();
        if(!itemID.equals(NEW_ITEM)) vCursor.close();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(!(itemDeleted || valuesET[1].getText().toString().equals(""))) saveEdit();
    }

    @Override
    public void onBackPressed() {

        if(!(itemDeleted || valuesET[1].getText().toString().equals(""))) saveEdit();


        Intent resultData = new Intent();
        resultData.putExtra(EXTRA_RETURN_VALUE_TAG, valuesET[1].getText().toString());
        setResult(Activity.RESULT_OK, resultData);
    //    Log.i("RR> ", "set result = " + glider.contestNumber);
        super.onBackPressed();
    }



    private void saveEdit() {
        int i;

        ContentValues values = new ContentValues();

        if (valuesET[0].getText() != null || valuesET[0].getText().toString() != "") {

            if (!itemID.equals(NEW_ITEM))
                values.put(prompts[0], Long.parseLong(itemID));  // add id for insert

            for (i = 1; i < valuesET.length; i++) {
                values.put(prompts[i], valuesET[i].getText().toString());
            }
            for (i = 0; i < values.size(); i++) {
                Log.e("RRR", "table write vals = " + values.valueSet());
            }
            long longInt = MyApp.db.replace(tableName, null, values);
            itemID = String.valueOf(longInt);
        }
    }
}
