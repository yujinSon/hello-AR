package com.seniorproject.ibeaconnavigation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.seniorproject.ibeaconnavigation.model.Building;
import com.seniorproject.ibeaconnavigation.model.Room;

import java.util.ArrayList;
import java.util.Collection;


public class RoomSearchActivity extends ActionBarActivity {
    private ArrayList<Room> rooms;
    private ListView roomListView;
    private SearchView roomSearchView;
    private Building bldg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_search);
        roomListView = (ListView)findViewById(R.id.listRoom);
        roomSearchView = (SearchView)findViewById(R.id.searchRoom);
        int bldgNum = this.getIntent().getExtras().getInt(Building.TAG_BLDG_NUM);
        bldg = Building.getBuilding(bldgNum);
        rooms = new ArrayList<Room>(bldg.getRooms());

        setTitle(bldg.toString());
        populateRoomList();
        setupSearchFiltering();
    }

    private void populateRoomList() {
        RoomListAdapter listAdapter = new RoomListAdapter(this, rooms);
        roomListView.setAdapter(listAdapter);
    }

    private void setupSearchFiltering() {

        roomSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Room> matched = new ArrayList<Room>();
                int queryLen = newText.length();
                Collection<Room> rooms = bldg.getRooms();

                for (Room room : rooms) {
                    String rLabel = room.toString();
                    if (queryLen <= rLabel.length()
                            && rLabel.toLowerCase().contains(newText.toLowerCase())) {
                        matched.add(room);
                    }
                }
                RoomListAdapter listAdapter = new RoomListAdapter(RoomSearchActivity.this, matched);
                roomListView.setAdapter(listAdapter);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
