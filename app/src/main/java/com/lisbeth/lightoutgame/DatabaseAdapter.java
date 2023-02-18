package com.lisbeth.lightoutgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DatabaseAdapter extends BaseAdapter {

    private Context context;
    private List<Records> itemList;

    public DatabaseAdapter(Context context, List<Records> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.records_items, parent, false);
        }

        // Obtener el elemento de la lista
        Records item = itemList.get(position);

        // Configurar los campos del layout
        TextView dateView = convertView.findViewById(R.id.textViewDate);
        TextView secondsView = convertView.findViewById(R.id.textViewSeconds);

        dateView.setText( item.getDate());
        secondsView.setText(item.getSeconds());

        return convertView;
    }
}
