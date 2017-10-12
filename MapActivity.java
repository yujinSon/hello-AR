package com.seniorproject.ibeaconnavigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.seniorproject.ibeaconnavigation.model.Building;
import com.seniorproject.ibeaconnavigation.model.Room;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {
    private Room targetRoom;
    private Building bldg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final String beaconAddr = getIntent().getExtras().getString(Room.TAG_BEACON_ADDR);
        final Room room = (Room)getIntent().getExtras().getSerializable(Room.TAG_ROOM);
        targetRoom = room;
        setTitle("강의실 " + room.toString());
        bldg = Building.getBuilding(targetRoom.getBldgNum());


        Button navButton = (Button)findViewById(R.id.map_navButton);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bpService = new Intent(MapActivity.this, BuildingProximityService.class);
                bpService.putExtra(Room.TAG_BEACON_ADDR, beaconAddr);
                bpService.putExtra(Room.TAG_ROOM, room);
                MapActivity.this.startService(bpService);

                String queryDestination = bldg.getX() + ", " + bldg.getY();
                String queryMode = "&mode=w";
                Uri gmapBuildingUri = Uri.parse("google.navigation:q=" + queryDestination + queryMode);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmapBuildingUri);
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                MapActivity.this.startActivity(mapIntent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap map) {
        LatLng targetCoord = new LatLng(bldg.getX(), bldg.getY());

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(targetCoord, 13));

        Marker marker = map.addMarker(new MarkerOptions()
            .title(bldg.getName())
            .position(targetCoord));
        marker.showInfoWindow();
    }


}
