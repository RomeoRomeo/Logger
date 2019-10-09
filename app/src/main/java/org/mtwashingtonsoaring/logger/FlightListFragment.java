package org.mtwashingtonsoaring.logger;


import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Rick on 12/15/2015.
 */
public class FlightListFragment extends ListFragment  {
    BaseAdapter adapter;
    ArrayList <Flight> list = new ArrayList<Flight>();

    public FlightListFragment(){

    // empty constructor required for fragment subclasses

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_flight_list);
        list = Flight.getFilteredFlights(MyApp.listFilter);

//        for(int i = 0; i<list.size();i++){
//            Log.e("RRR", "the list has these gliders -- " + list.get(i).glider) ;
//        }
       // list = DataStore.flightList;
        Log.i("RR>", "Activity Created");
        adapter = new FlightListArrayAdapter(getActivity(), list);
        this.setListAdapter(adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_dbflight_list, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        String selectedId = String.valueOf(id);
        Log.e("RRR","one item click id that was passed in = " + id);
        Toast.makeText(getActivity(), selectedId, Toast.LENGTH_SHORT).show();
        FlightEditActivity.start(getActivity(), selectedId);


    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
        this.getListView().refreshDrawableState();
    }

    public void updateList(){

        list = Flight.getFilteredFlights(MyApp.listFilter);
        adapter = new FlightListArrayAdapter(getActivity(), list);
        this.setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        this.getListView().refreshDrawableState();
    }



//        private ArrayList <Flight> flightListFliter( String filter){
//
//        ArrayList<Flight> outputList = new ArrayList<Flight>();
//
//
//        for (Flight flight : source){
//            if(flight.mode == filter){
//                outputList.add(flight);
//                Log.i("RR> ", "mode = " + flight.mode + " filter = " + filter);
//            } else {
//                Log.i("RR> ", "mode = " + flight.mode + " filter = " + filter);
//            }
//
//        }
//
//        return outputList;
//
//    }













//    private ArrayList <Flight> flightListFliter(ArrayList<Flight> source, String filter){
//
//        ArrayList<Flight> outputList = new ArrayList<Flight>();
//
//
//        for (Flight flight : source){
//            if(flight.mode == filter){
//                outputList.add(flight);
//                Log.i("RR> ", "mode = " + flight.mode + " filter = " + filter);
//            } else {
//                Log.i("RR> ", "mode = " + flight.mode + " filter = " + filter);
//            }
//
//        }
//
//        return outputList;
//
//    }





}
