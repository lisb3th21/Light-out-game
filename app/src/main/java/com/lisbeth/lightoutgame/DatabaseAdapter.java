package com.lisbeth.lightoutgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * It's a class that extends BaseAdapter and is used to populate a ListView with
 * data from a database
 */
public class DatabaseAdapter extends BaseAdapter {
    // ! Attributes
    private Context context;
    private List<Records> itemList;

    public DatabaseAdapter(Context context, List<Records> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    /**
     * It returns the size of the itemList.
     * 
     * @return The size of the itemList.
     */
     @Override
    public int getCount() {
        return itemList.size();
    }

    /**
     * It returns the item at the specified position in the list.
     * 
     * @param position The position of the item within the adapter's data set of the
     *                 item whose view we
     *                 want.
     * @return The itemList.get(position) is being returned.
     */
    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    /**
     * It returns the id of the item at the given position.
     * 
     * @param position The position of the item within the adapter's data set of the
     *                 item whose view we
     *                 want.
     * @return The id of the item in the list.
     */
    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    /**
     * If the convertView is null, inflate the layout, otherwise, get the view from
     * the convertView
     * 
     * @param position    The position of the item within the adapter's data set of
     *                    the item whose view we
     *                    want.
     * @param convertView The old view to reuse, if possible. Note: You should check
     *                    that this view is
     *                    non-null and of an appropriate type before using. If it is
     *                    not possible to convert this view to
     *                    display the correct data, this method can create a new
     *                    view.
     * @param parent      The parent to which the new view is attached to
     * @return The view of the item in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.records_items, parent, false);
        }

        Records item = itemList.get(position);

        TextView dateView = convertView.findViewById(R.id.textViewDate);
        TextView secondsView = convertView.findViewById(R.id.textViewSeconds);

        dateView.setText(item.getDate());
        secondsView.setText(item.getSeconds());

        return convertView;
    }
}
