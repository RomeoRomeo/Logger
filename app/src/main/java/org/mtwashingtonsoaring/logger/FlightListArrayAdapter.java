package org.mtwashingtonsoaring.logger;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class FlightListArrayAdapter extends ArrayAdapter<Flight>
{
    private final Context mContext;
    private final ArrayList<Flight> flights;
    
    public FlightListArrayAdapter(Context context, ArrayList<Flight>  values) {

        super(context, R.layout.flight_list_row, values);
        this.mContext = context;
        this.flights = values;
    }

    static class ViewHolderItem {
        Button launchBtn , landBtn;
        TextView durationTV, landingTV,contestNumTV,pilotOneNameTV,pilotTwoNameTV,centeredPilotOneNameTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem vh;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.flight_list_row, parent, false);
            vh = new ViewHolderItem();
            vh.launchBtn = (Button) convertView.findViewById(R.id.launchBtn);
            vh.landBtn = (Button) convertView.findViewById(R.id.landBtn);
            vh.landingTV = (TextView) convertView.findViewById(R.id.flightListLandingTime);
            vh.durationTV = (TextView) convertView.findViewById(R.id.durationTV);
            vh.contestNumTV = (TextView) convertView.findViewById(R.id.textViewContestNumber);
            vh.pilotOneNameTV = (TextView) convertView.findViewById(R.id.pilotOneName);
            vh.pilotTwoNameTV = (TextView) convertView.findViewById(R.id.pilotTwoName);
            vh.centeredPilotOneNameTV = (TextView) convertView.findViewById(R.id.centeredPilotOneName);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolderItem) convertView.getTag();
        }

        Flight flight = flights.get(position);

        if (flight != null) {
            vh.contestNumTV.setText(flight.glider);
            vh.launchBtn.setTag(flight._id);
            vh.landBtn.setTag(flight._id);

            switch (MyApp.listFilter) {

                case Flight.GRID:
                    vh.launchBtn.setVisibility(View.VISIBLE);
                    vh.landingTV.setVisibility(View.INVISIBLE);
                    vh.landBtn.setVisibility(View.INVISIBLE);
                    vh.durationTV.setVisibility(View.INVISIBLE);
                    break;

                case Flight.AIR:
                    vh.launchBtn.setVisibility(View.INVISIBLE);
                    vh.landingTV.setVisibility(View.INVISIBLE);
                    vh.landBtn.setVisibility(View.VISIBLE);
                    vh.durationTV.setVisibility(View.VISIBLE);
                    vh.durationTV.setText(mContext.getString(R.string.durationTimePrefix) + Flight.getFlightDurationString(flight, true) + "    ");
                    break;

                case Flight.GROUND:
                    vh.launchBtn.setVisibility(View.INVISIBLE);
                    vh.landingTV.setVisibility(View.VISIBLE);
                    vh.landBtn.setVisibility(View.INVISIBLE);
                    vh.durationTV.setVisibility(View.VISIBLE);
                    vh.durationTV.setText(mContext.getString(R.string.durationTimePrefix) + Flight.getFlightDurationString(flight, false));
                    vh.landingTV.setText(mContext.getString(R.string.landingTimePrefix) + flight.landingTime);
                    break;
            }

            if (Glider.isGliderTwoplace(flight.glider)) {

                vh.pilotOneNameTV.setVisibility(View.VISIBLE);
                vh.pilotTwoNameTV.setVisibility(View.VISIBLE);
                vh.pilotOneNameTV.setText(flight.pilotOne);
                vh.pilotTwoNameTV.setText(flight.pilotTwo);
                vh.centeredPilotOneNameTV.setVisibility(View.GONE);

            } else {

                vh.centeredPilotOneNameTV.setVisibility(View.VISIBLE);
                vh.centeredPilotOneNameTV.setText(flight.pilotOne);
                vh.pilotTwoNameTV.setVisibility(View.GONE);
                vh.pilotTwoNameTV.setVisibility(View.GONE);
            }

            vh.launchBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int fIndex;

                    Long tag = (Long) v.getTag();
                    ((FlightListHolder) mContext).launchLandBtnPressed(tag);

                    for (fIndex = 0; fIndex < flights.size(); fIndex++) {
                        if (flights.get(fIndex)._id == tag) {
                            break;
                        }
                    }

                    flights.remove(fIndex);
                    notifyDataSetChanged();
                }
            });

            vh.landBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int fIndex;

                    Long tag = (Long) v.getTag();
                    ((FlightListHolder) mContext).launchLandBtnPressed(tag);

                    for (fIndex = 0; fIndex < flights.size(); fIndex++) {
                        if (flights.get(fIndex)._id == tag) {
                            break;
                        }
                    }
                    flights.remove(fIndex);
                    notifyDataSetChanged();

                }
            });

        }

        return convertView;
    }


    @Override
    public long getItemId(int position){
       return flights.get(position)._id;

    }

}