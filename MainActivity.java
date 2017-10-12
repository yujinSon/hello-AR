package com.seniorproject.ibeaconnavigation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;

import com.seniorproject.ibeaconnavigation.model.Building;
import com.seniorproject.ibeaconnavigation.model.Room;

import java.util.ArrayList;
import java.util.Collection;


public class MainActivity extends ActionBarActivity {
    private ListView buildingListView;
    private ListView favoritesListView;
    private SearchView searchView;


    private ArrayList<Room> favRooms = new ArrayList<Room>(){{
            this.add(Building.getBuilding(5).getRoom(517).setFavName("E동 517호"));

    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildingListView = (ListView)findViewById(R.id.listBuilding);
        searchView = (SearchView)findViewById(R.id.searchBuilding);

        setupTabHost();
        populateSearchList();
        populateFavoritesList();
        setupSearchFiltering();
    }


    private void populateSearchList() {
        BuildingListAdapter listAdapter =
                new BuildingListAdapter(this, new ArrayList<Building>(Building.getBuildings()));
        buildingListView.setAdapter(listAdapter);
    }

    private void populateFavoritesList() {
        favoritesListView = (ListView)findViewById(R.id.listFavorites);
        RoomListAdapter listAdapter = new RoomListAdapter(this, favRooms, true);
        favoritesListView.setAdapter(listAdapter);
    }


    private void setupTabHost() {
        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("search");
        tabSpec.setContent(R.id.tabSearch);
        tabSpec.setIndicator("강의실검색");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("favorites");
        tabSpec.setContent(R.id.tabFavorites);
        tabSpec.setIndicator("즐겨찾기");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("recent");
        tabSpec.setContent(R.id.tabRecent);
        tabSpec.setIndicator("최근검색기록");
        tabHost.addTab(tabSpec);
    }

    private void setupSearchFiltering() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Building> temp = new ArrayList<Building>();
                int queryLen = newText.length();
                Collection<Building> buildings = Building.getBuildings();

                for (Building building : buildings) {
                    String bLabel = building.toString();
                    if (queryLen <= bLabel.length()
                            && bLabel.toLowerCase().contains(newText.toLowerCase())) {
                        temp.add(building);
                    }
                }
                BuildingListAdapter listAdapter = new BuildingListAdapter(MainActivity.this, temp);
                buildingListView.setAdapter(listAdapter);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
