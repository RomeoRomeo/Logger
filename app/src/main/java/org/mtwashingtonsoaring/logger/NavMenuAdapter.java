package org.mtwashingtonsoaring.logger;


import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class NavMenuAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> menuItems;


    //FragmentManager fm = getActivity().getSupportFragmentManager();

    public NavMenuAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.navmenu_row, values);
        this.context = context;
        this.menuItems = values;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.navmenu_row, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.textViewNavMenuItem);
        textView.setText(menuItems.get(position));
        textView.setTextColor(Color.WHITE);

        return rowView;
    }

}