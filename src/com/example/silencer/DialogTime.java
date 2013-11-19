package com.example.silencer;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class DialogTime extends DialogFragment implements OnClickListener{
	
	TimePicker tp;
	Button button;
	
	int myHour = 14;
	int myMinute = 35;
	final String LOG_TAG = "myLogs";
	
	 TimeDialogListener mListener;
	
	
	public static interface TimeDialogListener {
        public void onDialogSetClick(int Hour, int Minut);
    }
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {

		    View v = inflater.inflate(R.layout.dialog_time, null);
            getDialog().setTitle(getResources().getString(R.string.time));
		    tp = (TimePicker) v.findViewById (R.id.timePicker1);
		    tp.setOnTimeChangedListener(new TimeListener());
		    tp.setIs24HourView(true);
		    
		    button = (Button) v.findViewById(R.id.button_time_set);
		    button.setOnClickListener(this);

		    return v;
		  }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (TimeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	private class TimeListener implements OnTimeChangedListener{

		@Override
		public void onTimeChanged(TimePicker tp, int selectedHour, int selectedMinute) {
			// TODO Auto-generated method stub
			myHour = selectedHour;
			myMinute = selectedMinute;

		}
		
	}
	
	@Override
	public void onClick(View v) {
		mListener.onDialogSetClick(myHour, myMinute);
	    dismiss();
	}
	

}
