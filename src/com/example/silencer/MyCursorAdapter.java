package com.example.silencer;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.silencer.DBAdapter;
import com.example.silencer.R;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Alex on 19.11.13.
 */
public class MyCursorAdapter extends SimpleCursorAdapter {

    private int layout;
    private final String[] from;
    private final Context context;
    private Cursor cursor;
    Intent intentEdit;

    final String LOG_TAG = "myLogs";

    public MyCursorAdapter(Context _context, int _layout, Cursor _c, String[] _from, int[] _to) {
        super(_context, _layout, _c, _from, _to);
        layout = _layout;
        from = _from;
        context = _context;
        cursor = _c;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        protected CheckBox checkbox;
    }



    public void bindView(View view, Context _context, Cursor _cursor) {
        int id_id = cursor.getColumnIndex( DBAdapter.KEY_ROWID );
        int idfromTime = cursor.getColumnIndex( DBAdapter.KEY_FROM_TIME );
        int idtoTime = cursor.getColumnIndex( DBAdapter.KEY_TO_TIME );
        int idsound = cursor.getColumnIndex( DBAdapter.KEY_SOUND );
        int idsoundAfter = cursor.getColumnIndex( DBAdapter.KEY_SOUND_AFTER );
        final int id = cursor.getInt(id_id);
        final String fromTime = cursor.getString(idfromTime);
        final String toTime = cursor.getString(idtoTime);
        int sound = cursor.getInt(idsound);
        int soundAfter = cursor.getInt(idsoundAfter);

        TextView textfrom = (TextView) view.findViewById(R.id.textViewFrom);
        TextView textto = (TextView) view.findViewById(R.id.textViewTo);

        final TextView textMainto = (TextView) view.findViewById(R.id.textTimeMainTo);
        //TextView textsound = (TextView) view.findViewById(R.id.textViewSound);
        //TextView textsoundAfter = (TextView) view.findViewById(R.id.textViewSoundAfter);
        ImageView im = (ImageView) view.findViewById(R.id.imageZoom);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        textfrom.setText(fromTime);
        textto.setText(toTime);
        //textsound.setText(String.valueOf(sound));
        //textsoundAfter.setText(String.valueOf(soundAfter));

        /*if (sound == 1) im.setVisibility(ImageView.VISIBLE);
        else im.setVisibility(ImageView.INVISIBLE);*/
        if (sound == 1) im.setImageDrawable(context.getResources().getDrawable(R.drawable.sound));
        else im.setImageDrawable(context.getResources().getDrawable(R.drawable.nosound));

        //select rows on ListView
        view.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d(LOG_TAG, "id = " + id);
                intentEdit = new Intent(context, TaskPaneEdit.class);
                intentEdit.putExtra("id", id);
                context.startActivity(intentEdit);

            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(LOG_TAG, "time = ");
               MainActivity main = new MainActivity();
              //  textMainto.setText("asdasd");
                main.setTime(fromTime, toTime);
                if(checkBox.isChecked()) {
                   // itemChecked.set(pos, true);
                   // Log.d(LOG_TAG, "time = " + fromTime);

                }
                else if (!checkBox.isChecked()) {
                  //  itemChecked.set(pos, false);

                }
            }
        });

    }


  /* @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        View rowView = convertView;
        String s = from[position];
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.item_layout, null, true);
        holder = new ViewHolder();
        int b =  cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_SOUND));

     //   if (b == 1) holder.imageView.setImageResource(R.drawable.icon);
        return rowView;

    }*/

    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;
    }


    }
