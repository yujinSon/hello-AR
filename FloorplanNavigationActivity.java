package com.seniorproject.ibeaconnavigation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.matabii.dev.scaleimageview.ScaleImageView;
import com.seniorproject.ibeaconnavigation.model.Room;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class FloorplanNavigationActivity extends ActionBarActivity implements BeaconConsumer {
    private BeaconManager beaconManager;
    private ScaleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Room targetRoom = (Room)getIntent().getSerializableExtra(Room.TAG_ROOM);
        setTitle("Room " + targetRoom.toString());
        setContentView(R.layout.activity_floorplan_nav);
        imageView = (ScaleImageView) findViewById(R.id.imageView);
        beaconManager = BeaconManager.getInstanceForApplication(this);
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
                            String address = beacons.iterator().next().getBluetoothAddress();
                            if (address.equals("E7:BC:E6:E9:66:EB")) {
                                setBeacon(1);
                            }
                            else if (address.equals("F3:01:4B:FE:CF:BE")) {
                                setBeacon(2);
                            }
                        }
                        else {
                            setBeacon(0);
                        }
                    }
                });
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    private void setBeacon(int beacon) {
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.e5f);

        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(Color.RED);
        paint.setAlpha(125);

        tempCanvas.drawBitmap(myBitmap, 0, 0, null);
        tempCanvas.drawCircle(500, 600, 100 * imageView.scale(), paint);

        Log.d("starng", "Beacon: " + beacon);
        if (beacon > 0) {
            paint.setColor(Color.BLUE);
            paint.setAlpha(125);
            if (beacon == 1) {
                tempCanvas.drawCircle(1350, 3025, 100 * imageView.scale(), paint);
            }
            else if (beacon == 2) {
                tempCanvas.drawCircle(1600, 3025, 100 * imageView.scale(), paint);
            }
        }

        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }
}
