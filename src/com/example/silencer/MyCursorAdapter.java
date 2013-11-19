package com.example.silencer;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.silencer.DBAdapter;
import com.example.silencer.R;

/**
 * Created by Alex on 19.11.13.
 */
public class MyCursorAdapter extends SimpleCursorAdapter {

    private int layout;
    private final String[] from;
    private Context context;
    private Cursor cursor;


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
        int idfromTime = cursor.getColumnIndex( DBAdapter.KEY_FROM_TIME );
        int idtoTime = cursor.getColumnIndex( DBAdapter.KEY_TO_TIME );
        int idsound = cursor.getColumnIndex( DBAdapter.KEY_SOUND );
        int idsoundAfter = cursor.getColumnIndex( DBAdapter.KEY_SOUND_AFTER );
        String fromTime = cursor.getString(idfromTime);
        String toTime = cursor.getString(idtoTime);
        int sound = cursor.getInt(idsound);
        int soundAfter = cursor.getInt(idsoundAfter);
       /* ViewHolder  holder = new ViewHolder();
        holder.textView = (TextView) view.findViewById(R.id.textViewFrom);
        holder.textView.setText(fromTime);*/
        TextView textfrom = (TextView) view.findViewById(R.id.textViewFrom);
        TextView textto = (TextView) view.findViewById(R.id.textViewTo);
        TextView textsound = (TextView) view.findViewById(R.id.textViewSound);
        TextView textsoundAfter = (TextView) view.findViewById(R.id.textViewSoundAfter);
        ImageView im = (ImageView) view.findViewById(R.id.imageView);
        textfrom.setText(fromTime);
        textto.setText(toTime);
        textsound.setText(String.valueOf(sound));
        textsoundAfter.setText(String.valueOf(soundAfter));

        if (sound == 1) im.setVisibility(ImageView.VISIBLE);
        else im.setVisibility(ImageView.INVISIBLE);

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
