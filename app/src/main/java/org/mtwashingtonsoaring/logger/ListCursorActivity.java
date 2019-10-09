package org.mtwashingtonsoaring.logger;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class ListCursorActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "org.mtwashingtonsoaring.logger.EDIT_ID";
    public static final String EXTRA_TABLE_NAME_TAG = "org.mtwashingtonsoaring.logger.TABLE_NAME";


    String tableName;
    ListView lv;



    public static void start(Context context, String tableName) {
        Intent intent = new Intent(context, ListCursorActivity.class);
        intent.putExtra(EXTRA_TABLE_NAME_TAG, tableName);
        context.startActivity(intent);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        final Context context = this;
        String[] where = new String[]{""};

        Intent intent = this.getIntent();
        tableName = intent.getStringExtra(EXTRA_TABLE_NAME_TAG);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit "+ tableName);
//        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);







        setContentView(R.layout.activity_cursor_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditCursorActivity.start(view.getContext(), EditCursorActivity.NEW_ITEM,tableName);
            }
        });


        lv = (ListView)findViewById(R.id.cursorListView);
        lv.setAdapter(getDataListAdapter());


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e ("RRR","position = " + position + "  id = " + id);
                EditCursorActivity.start(context,String.valueOf(id), tableName);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onResume(){
        super.onResume();


        lv.setAdapter(getDataListAdapter());


    }

    SimpleCursorAdapter getDataListAdapter() {

        Cursor dbCursor = MyApp.db.query(tableName, null, null, null, null, null, null);
        String[] columns = new String[]{dbCursor.getColumnName(1)};
        Log.e("RRR", "first column = " + columns[0]);
        int tv = R.id.text1;
        int[] textViews = new int[1];
        textViews[0] = tv;
        Log.e("RRR", "column count = " + dbCursor.getColumnCount());
        Log.e("RRR", "row count = " + dbCursor.getCount());
        return  new SimpleCursorAdapter(this,R.layout.cursor_list_row_layout, dbCursor, columns, textViews,0);
    }
}

