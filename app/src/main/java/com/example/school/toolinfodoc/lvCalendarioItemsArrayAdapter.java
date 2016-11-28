package com.example.school.toolinfodoc;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class lvCalendarioItemsArrayAdapter extends ArrayAdapter<lvCalendarioItems> {

    private Context context;
    private int layoutResourceId;
    private lvCalendarioItems[] data = null;

    public lvCalendarioItemsArrayAdapter(Context context, int layoutResourceId, lvCalendarioItems[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WeatherHolder();
            holder.iconoItem = (ImageView)row.findViewById(R.id.iconoItem);
            holder.textoItem = (TextView)row.findViewById(R.id.textoItem);

            row.setTag(holder);
        }
        else
        {
            holder = (WeatherHolder)row.getTag();
        }

        lvCalendarioItems weather = data[position];

        holder.textoItem.setText(weather.texto);
        holder.iconoItem.setImageResource(weather.icono);

        return row;
    }

    private static class WeatherHolder
    {
        ImageView iconoItem;
        TextView textoItem;
    }
}
