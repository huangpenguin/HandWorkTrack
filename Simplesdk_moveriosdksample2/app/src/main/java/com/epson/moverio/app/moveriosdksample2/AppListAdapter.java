package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppData>{

    private List<AppData> appList = null;

    public AppListAdapter(Context context, int resourceId, ArrayList<AppData> appList) {
        super(context, resourceId, appList);

        this.appList = appList;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
        RadioButton radioButton;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public AppData getItem(int position) {
        return appList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.app_list_view, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageview);
            viewHolder.textView = (TextView)convertView.findViewById(R.id.textview);
            viewHolder.radioButton = (RadioButton)convertView.findViewById(R.id.radiobutton);

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final AppData appData = (AppData)getItem(position);

        viewHolder.imageView.setImageBitmap(appData.getIcon());
        viewHolder.textView.setText(appData.getLabel());
        viewHolder.radioButton.setChecked(appData.isChecked());

        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                for(int i = 0; i < appList.size(); i++){
                    appList.get(i).setChecked(false);
                }

                appData.setChecked(true);
                notifyDataSetChanged();
            }

        });

        return convertView;
    }

}