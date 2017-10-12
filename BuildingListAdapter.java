package com.seniorproject.ibeaconnavigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.seniorproject.ibeaconnavigation.model.Building;

import java.util.List;

public class BuildingListAdapter extends ArrayAdapter {
    protected Context mContext;
    protected List mBuildings;


    public BuildingListAdapter(Context context, List<Building> buildings) {
        super(context, R.layout.simplerow, buildings);
        mContext = context;
        mBuildings = buildings;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.simplerow, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.rowTextView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }


        final Building building = (Building)mBuildings.get(position);

        holder.name.setText(building.toString());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),  building.toString()+ "이 선택되었습니다.",
                               Toast.LENGTH_SHORT).show();
                final TextView bldgTxtVw = (TextView)v;
                final int oldTextColor = bldgTxtVw.getCurrentTextColor();
                bldgTxtVw.setTextColor(Color.GREEN);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        bldgTxtVw.setTextColor(oldTextColor);
                    }
                }, 200);

                Intent roomSearchIntent =
                        new Intent(getContext(), RoomSearchActivity.class);
                roomSearchIntent.putExtra(Building.TAG_BLDG_NUM, building.getNum());
                getContext().startActivity(roomSearchIntent);
            }
        });

        return convertView;
    }


    public static class ViewHolder {
        TextView name;
    }
}
