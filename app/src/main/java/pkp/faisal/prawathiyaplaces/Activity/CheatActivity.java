package pkp.faisal.prawathiyaplaces.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import pkp.faisal.prawathiyaplaces.Function.FirebaseConstant;
import pkp.faisal.prawathiyaplaces.Function.HttpRequest;
import pkp.faisal.prawathiyaplaces.Function.MyItem;
import pkp.faisal.prawathiyaplaces.Model.PlacesModel;
import pkp.faisal.prawathiyaplaces.R;

public class CheatActivity extends AppCompatActivity implements
        OnMapReadyCallback {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker collMarker;
    private Boolean started = false;
    private Timer timer;
    private TimerTask timerTask;
    private EditText mRadius;
    private TextView mCategory;
    private String selectedCategoryValue;
    private String selectedCategoryId;
    private int count = 0;
    private static final int CATEGORY_REQUEST_CODE = 2;
    private DatabaseReference baseRef;
    private TextView mCount;
    private ClusterManager<MyItem> mClusterManager;
    private ArrayList<Marker> nearbyMarker = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        mRadius = (EditText) findViewById(R.id.radius);
        mCount = (TextView) findViewById(R.id.count);
        mCategory = (TextView) findViewById(R.id.pilih_category);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cheat");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        baseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void onCluster(View view) {
        if (selectedCategoryValue == null) {
            mCategory.setError("Pilih Kategory");
            return;
        }
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        final long[] loaded = {0};
        final long[] max = {0};
        mClusterManager.clearItems();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child(FirebaseConstant.PLACE_CATEGORY)
                .child(selectedCategoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        max[0] = dataSnapshot.getChildrenCount();
                        dialog.setMessage("Total Item " + max[0]);
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            loaded[0]++;
                            dialog.setMessage("Looping " + loaded[0]);
                            rootRef.child(FirebaseConstant.PLACES)
                                    .child(data.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            loaded[0]++;
                                            dialog.setMessage("Total data " + loaded[0] / 2 + "/" + max[0]);

                                            PlacesModel placesModel = dataSnapshot.getValue(PlacesModel.class);
                                            mClusterManager.addItem(new MyItem(Double.valueOf(placesModel.Lat), Double.valueOf(placesModel.Lon)));
                                            if (loaded[0] / 2 == max[0]) {
                                                dialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//
//                orderByChild("MasterCategoryId")
//                .equalTo(selectedCategoryId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        mClusterManager.clearItems();
//                        for(DataSnapshot data: dataSnapshot.getChildren()){
//                            PlacesModel placesModel = data.getValue(PlacesModel.class);
//                            mClusterManager.addItem(new MyItem(Double.valueOf(placesModel.Lat), Double.valueOf(placesModel.Lon)));
//                            dialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    private void setUpClusterer() {
        // Position the map.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng taskLocation = new LatLng(Double.valueOf("-6.382001"), Double.valueOf("106.925224"));
        MarkerOptions markerOptions = new MarkerOptions()
                .position(taskLocation)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_collector))
                .title("Title");

        collMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(taskLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                collMarker = marker;
            }
        });
        setUpClusterer();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CATEGORY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    selectedCategoryValue = data.getStringExtra("Value");
                    selectedCategoryId = data.getStringExtra("Uid");
                    String name = data.getStringExtra("Name");
                    mCategory.setText(name);
                } else {

                }

                break;


        }
    }

    public void onRandom(View view) {
        final int radius = Integer.parseInt(mRadius.getText().toString());
        if (radius < 0) {
            mRadius.setError("Angka Di atas 0");
            return;
        }
        if (selectedCategoryValue == null) {
            mCategory.setError("Pilih Kategory");
            return;
        }
        TextView btn = (TextView) findViewById(R.id.random);
        if (started) {
            stop();
            started = false;
            btn.setText("Random");
        } else {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            random(radius);
                        }
                    });

                }
            };
            start();
            started = true;
            btn.setText("Stop");
        }
    }

    public void onCategory(View view) {
        startActivityForResult(new Intent(this, CategoryActivity.class), CATEGORY_REQUEST_CODE);
    }

    private void random(int radius) {

        LatLng newPosition = getLocation(collMarker.getPosition().latitude, collMarker.getPosition().longitude, radius);
        collMarker.setPosition(newPosition);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(newPosition));

        final HttpRequest http = new HttpRequest(getApplicationContext());
        http.getTag(selectedCategoryValue, radius, newPosition, new HttpRequest.SuccessCallback() {
            @Override
            public void onHttpPostSuccess(String res) {
                JSONObject result = null;
                try {
                    result = new JSONObject(res);
//                    String status = result.getString("Status");
                    JSONArray results = result.getJSONArray("results");


                    for (int i = 0; i < results.length(); i++) {
                        final JSONObject places = (JSONObject) results.get(i);
                        JSONObject geometry = places.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        final String latitude = location.getString("lat");
                        final String longitude = location.getString("lng");
                        final String placeId = places.getString("place_id");
                        final String icon = places.getString("icon");
                        final String name = places.getString("name");
                        final String vicinity = places.getString("vicinity");

//                        try {
//                            JSONArray photos = places.getJSONArray("photos");
//                            for (int j = 0; j < photos.length(); j++) {
//                                JSONObject photo = (JSONObject) photos.get(j);
//                                String photoReference = photo.getString("photo_reference");
//                                http.getPhoto(photoReference, new HttpRequest.SuccessCallback() {
//                                    @Override
//                                    public void onHttpPostSuccess(String result) {
//                                        PlacesModel placesModel = new PlacesModel(
//                                                name,
//                                                vicinity,
//                                                result,
//                                                "-Kjf8dgaeCYlpnHRQ2sg",
//                                                latitude,
//                                                longitude
//                                        );
//                                        savePlcaes(placeId, placesModel);
//                                    }
//                                }, new HttpRequest.ErrorCallback() {
//                                    @Override
//                                    public void onHttpPostError(VolleyError error) {
//                                        PlacesModel placesModel = new PlacesModel(
//                                                name,
//                                                vicinity,
//                                                icon,
//                                                "-Kjf8dgaeCYlpnHRQ2sg",
//                                                latitude,
//                                                longitude
//                                        );
//                                        savePlcaes(placeId, placesModel);
//                                    }
//                                });
//
//                            }
//
//                        } catch (Exception e) {
                        PlacesModel placesModel = new PlacesModel(
                                name,
                                vicinity,
                                icon,
                                selectedCategoryId,
                                latitude,
                                longitude
                        );
                        savePlcaes(placeId, placesModel);
                        // }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpRequest.ErrorCallback() {
            @Override
            public void onHttpPostError(VolleyError error) {
                Log.e("", error.getMessage());
            }
        });
    }

    private void savePlcaes(final String placeId, final PlacesModel placesModel) {
        baseRef.child(FirebaseConstant.PLACES)
                .child(placeId)
                .setValue(placesModel, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(getApplicationContext(), "Data could not be saved " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            baseRef.child(FirebaseConstant.PLACE_CATEGORY)
                                    .child(placesModel.MasterCategoryId)
                                    .child(placeId)
                                    .setValue(placesModel.Name);
                        }
                    }
                });
        count++;
        mCount.setText(count + "");
    }


    public void start() {
        if (timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 500);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }

    public static LatLng getLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(Math.toRadians(y0));

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        return new LatLng(foundLongitude, foundLatitude);
    }

    public void onNearby(View view) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        for (Marker marker : nearbyMarker) {
            marker.remove();
        }
        nearbyMarker.clear();

        HttpRequest http = new HttpRequest(getApplicationContext());
        http.nearby(collMarker.getPosition(), 10, new HttpRequest.SuccessCallback() {
            @Override
            public void onHttpPostSuccess(String result) {
                try {
                    JSONArray res = new JSONArray(result);
                    for (int i = 0; i < res.length(); i++) {
                        JSONObject place = res.getJSONObject(i);
                        String lat = place.getString("lat");
                        String lon = place.getString("lon");
                        String name = place.getString("name");
                        LatLng pos = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(pos)
                                .draggable(true)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_collector))
                                .title(name);
                        nearbyMarker.add(mMap.addMarker(markerOptions));
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
                Log.d("", "");
            }
        }, new HttpRequest.ErrorCallback() {
            @Override
            public void onHttpPostError(VolleyError error) {
                Log.d("", "");
            }
        });

    }
}
