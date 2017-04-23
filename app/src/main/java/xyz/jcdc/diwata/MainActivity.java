package xyz.jcdc.diwata;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
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

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private final int MAP_TYPE_NORMAL = 0;
    private final int MAP_TYPE_HYBRID = 1;
    private final int MAP_TYPE_SATELLITE = 2;
    private final int MAP_TYPE_TERRAIN = 3;

    private int map_type = MAP_TYPE_NORMAL;

    private Context context;

    private GoogleMap googleMap;
    private Marker marker_diwata;

    private MaterialDialog materialDialog_position;

    private Timer diwataTimer;

    private BitmapDescriptor diwataIcon;

    private LatLng prevLatLng_diwata;

    private Diwata diwatang_ina;
    private LatLng diwata_latlng;

    private List<Polyline> polyline_diwata = new ArrayList<>();

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

    @BindView(R.id.diwata_pst)
    TextView diwata_pst;

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

        materialDialog_position = new MaterialDialog.Builder(context)
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
                setPath(path);
            }
        }, 2).execute();
    }

    private void setPath(Path p) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(2)
                .color(ContextCompat.getColor(context, R.color.dilawan));

        for (Features features : p.getFeatures()) {
            Geometry geometry = features.getGeometry();
            LatLng latLng = new LatLng(geometry.getCoordinates().get(1), geometry.getCoordinates().get(0));
            polylineOptions.add(latLng);
        }

        googleMap.addPolyline(polylineOptions);
    }

    private void getDiwataPosition() {
        new GetDiwata(new Diwata.DiwataPositionListener() {
            @Override
            public void onStartTracking() {

            }

            @Override
            public void onDiwataPositionReceived(Diwata diwata) {
                if (materialDialog_position.isShowing()) {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            materialDialog_position.dismiss();
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

        long unixTime = Math.round(diwata.getProperties().getTimestamp());
        Date time = new Date(unixTime * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

        diwata_pst.setText(dateFormat.format(time));
        diwata_latitude.setText( NumberHelper.toDecimalPlaces(diwata.getGeometry().getCoordinates().get(1)) );
        diwata_longitude.setText( NumberHelper.toDecimalPlaces(diwata.getGeometry().getCoordinates().get(0)) );
        diwata_elevation.setText( NumberHelper.toDecimalPlaces( (diwata.getProperties().getElevation() / 1000) ) + " km" );
    }

    private void showMapTypeDialog() {
        new MaterialDialog.Builder(this)
                .title("Map type")
                .items(R.array.map_type)
                .itemsCallbackSingleChoice(map_type, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which != map_type) {
                            map_type = which;
                            setMapType(map_type);
                        }

                        return true;
                    }
                })
                .negativeText("Cancel")
                .positiveText("Apply")
                .show();
    }

    private void setMapType(int type) {
        switch (type) {

            case MAP_TYPE_NORMAL:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case MAP_TYPE_HYBRID:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            case MAP_TYPE_SATELLITE:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case MAP_TYPE_TERRAIN:
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map_type) {
            showMapTypeDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title("Exit")
                .content("Are you sure you want to exit?")
                .positiveText("Exit")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }
}
