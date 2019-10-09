package org.mtwashingtonsoaring.logger;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;


public class AddItemTaggedAdaptor extends ArrayAdapter<StringWithTag> {
    Context context;


    public AddItemTaggedAdaptor(Context context, int resorce, List<StringWithTag> values) {
        super (context,resorce,values);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int i = 1;

        //Log.e ("RRRAddItemTaggedAdaptor", " getCount = " + getCount() + "  position = " + position + " " + );
        View v = super.getView(position, convertView, parent);
//        if (position == getCount()) {
//            ((TextView)v.findViewById(android.R.id.text1)).setText("");
//            ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()).string); //"Hint to be displayed"
//        }

        return v;
    }

//    @Override
//    public int getCount() {
//        return super.getCount()-1; // you dont display last item. It is used as hint.
//    }

}

