package xyz.jcdc.diwata;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.jcdc.diwata.helper.NumberHelper;
import xyz.jcdc.diwata.model.Diwata;
import xyz.jcdc.diwata.model.Features;
import xyz.jcdc.diwata.model.Geometry;
import xyz.jcdc.diwata.model.Path;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Context context;

    private GoogleMap googleMap;
    private Marker marker_diwata;

    private MaterialDialog materialDialog;

    private Timer diwataTimer;

    private BitmapDescriptor diwataIcon;

    private LatLng prevLatLng_diwata;
    private LatLng prevLatLng_diwata_path;
    private LatLng prevLatLng_diwata_path_2;

    private Diwata diwatang_ina;
    private LatLng diwata_latlng;

    private List<Polyline> polyline_diwata = new ArrayList<>();
    private List<Polyline> polyline_diwata_2 = new ArrayList<>();

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.diwata_latitude)
    TextView diwata_latitude;

    @BindView(R.id.diwata_longitude)
    TextView diwata_longitude;

    @BindView(R.id.diwata_elevation)
    TextView diwata_elevation;

    @BindView(R.id.info)
    CardView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (diwata_latlng != null)
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(diwata_latlng).build()));
            }
        });

        diwataIcon = BitmapDescriptorFactory.fromResource(R.mipmap.marker_diwata);

        materialDialog = new MaterialDialog.Builder(context)
                .title("Please wait")
                .content("Getting Diwata-I position")
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.lunar_landscape));

        getDiwataPosition();
        getPath();
        getPath_2();
    }

    private void getPath() {
        new GetPath(new Path.PathListener() {
            @Override
            public void onStartTracking() {

            }

            @Override
            public void onPathReceived(Path path) {
                setPath(path);
            }
        }).execute();
    }

    private void getPath_2() {
        new GetPath(new Path.PathListener() {
            @Override
            public void onStartTracking() {

            }

            @Override
            public void onPathReceived(Path path) {
                setPath_2(path);
            }
        }, 2).execute();
    }

    private void setPath(Path p) {
        for (Features features : p.getFeatures()) {
            LatLng latLng = new LatLng(features.getGeometry().getCoordinates().get(1), features.getGeometry().getCoordinates().get(0));

            if (prevLatLng_diwata_path != null) {
                Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                        .add(prevLatLng_diwata_path, latLng)
                        .width(2)
                        .color(ContextCompat.getColor(context, R.color.dilawan)));

                polyline_diwata.add(polyline);
            }

            prevLatLng_diwata_path = latLng;
        }
    }

    private void setPath_2(Path p) {
        for (Features features : p.getFeatures()) {
            LatLng latLng = new LatLng(features.getGeometry().getCoordinates().get(1), features.getGeometry().getCoordinates().get(0));

            if (prevLatLng_diwata_path_2 != null) {
                Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                        .add(prevLatLng_diwata_path_2, latLng)
                        .width(2)
                        .color(ContextCompat.getColor(context, R.color.dilawan)));

                polyline_diwata_2.add(polyline);
            }

            prevLatLng_diwata_path_2 = latLng;
        }
    }

    private void getDiwataPosition() {
        new GetDiwata(new Diwata.DiwataPositionListener() {
            @Override
            public void onStartTracking() {

            }

            @Override
            public void onDiwataPositionReceived(Diwata diwata) {
                if (materialDialog.isShowing()) {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            materialDialog.dismiss();
                        }
                    });
                }

                diwatang_ina = diwata;
                setDiwataPosition(diwatang_ina);
            }
        }).execute();
    }

    private void setDiwataPosition(Diwata diwata) {

        if (marker_diwata != null)
            marker_diwata.remove();

        diwata_latlng = new LatLng(diwata.getGeometry().getCoordinates().get(1), diwata.getGeometry().getCoordinates().get(0));

        marker_diwata = googleMap.addMarker(new MarkerOptions()
                .position(diwata_latlng)
                .title("Diwata - I")
                .icon(diwataIcon)
                .anchor(0.5f, 0.5f));

        if (prevLatLng_diwata != null) {
            //    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(diwata_latlng).build()));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(diwata_latlng).build()));
            fab.setVisibility(View.VISIBLE);
        }

        prevLatLng_diwata = diwata_latlng;

        setDiwataInfo(diwata);

        if (diwataTimer != null)
            diwataTimer.cancel();

        diwataTimer = new Timer();
        diwataTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getDiwataPosition();
            }
        }, 1000);

    }

    private void setDiwataInfo(Diwata diwata) {
        info.setVisibility(View.VISIBLE);
        diwata_latitude.setText( NumberHelper.toDecimalPlaces(diwata.getGeometry().getCoordinates().get(1)) );
        diwata_longitude.setText( NumberHelper.toDecimalPlaces(diwata.getGeometry().getCoordinates().get(0)) );
        diwata_elevation.setText( NumberHelper.toDecimalPlaces( (diwata.getProperties().getElevation() / 1000) ) + " km" );
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
