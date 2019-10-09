package org.mtwashingtonsoaring.logger;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;


import java.util.Calendar;

/**
 * Created by Rickr on 12/9/2015.
 */
public class SetTimePicker implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

        private EditText editText;
        private Calendar myCalendar;
        private int hour, minute;
        private Context ctx;

        public SetTimePicker(EditText editText, Context ctx,int hour, int minute){
            this.editText = editText;
            this.editText.setOnFocusChangeListener(this);
            this.hour = hour;
            this.minute = minute;
            this.ctx = ctx;

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if(hasFocus){
                new TimePickerDialog(ctx, this, hour, minute, true).show();
            }
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            this.editText.setText( hourOfDay + ":" + minute);
        }

    }



