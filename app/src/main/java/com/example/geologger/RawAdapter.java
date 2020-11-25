package com.example.geologger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RawAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List text;

    public RawAdapter(Context context, List text){
        this.text = text;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public Object getItem(int position) {
        return text.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = layoutInflater.inflate(R.layout.gridview_raw,null);
        TextView tv = (TextView) v.findViewById(R.id.gridView_raw);
        tv.setText((CharSequence) text.get(position));
        return v;
    }
}
