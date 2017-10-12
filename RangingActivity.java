package com.seniorproject.ibeaconnavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;
    private ListView beaconListView;
    private TextView noBeaconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconListView = (ListView) findViewById(R.id.listBeacons);
        noBeaconView = (TextView) findViewById(R.id.noBeaconView);
        beaconManager.bind(this);
        beaconManager
                .getBeaconParsers()
                .add(new BeaconParser()
                        .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (beacons.size() > 0) {
                            noBeaconView.setText("");
                            ArrayList beaconList = new ArrayList();

                            for (Beacon beacon : beacons) {
                                Log.d("starng", "비콘 (" + beacon.getBluetoothAddress() +
                                        ") : " + beacon.getDistance() + " m 떨어져 있습니다.");
                            }
                            Log.d("starng", "비콘의 수: " + beacons.size());

                            beaconList.addAll(beacons);
                            Collections.sort(beaconList, new Comparator<Beacon>() {
                                @Override
                                public int compare(Beacon o1, Beacon o2) {
                                    int result = 0;

                                    if (o1.getDistance() > o2.getDistance()) {
                                        result = 1;
                                    } else if (o1.getDistance() > o2.getDistance()) {
                                        result = -1;
                                    }
                                    return result;
                                }
                            });

                            BeaconAdapter listAdapter =
                                    new BeaconAdapter(RangingActivity.this, beaconList);
                            beaconListView.setAdapter(listAdapter);

                            Intent bpService = new Intent(getBaseContext(), BuildingProximityService.class);
                            RangingActivity.this.startService(bpService);
                        }
                        else {
                            noBeaconView.setText("비콘이 근처에 없습니다.");
                            beaconListView.setAdapter
                                    (new BeaconAdapter(RangingActivity.this, new ArrayList()));
                        }
                    }
                });
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }
}
