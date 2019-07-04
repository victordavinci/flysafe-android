package ar.gob.jiaac.flysafe.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ar.gob.jiaac.flysafe.R;

public class ReportsMapFragment extends Fragment implements OnMapReadyCallback {


    public ReportsMapFragment() {
    }

    public static ReportsMapFragment newInstance() {
        Bundle args = new Bundle();
        ReportsMapFragment fragment = new ReportsMapFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reports_map, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(map != null) map.getMapAsync(this);
        return v;
    }

    private void updateMap(GoogleMap googleMap, ArrayList<LatLng> locations) {
        googleMap.clear();
        for (LatLng latLng : locations) {
            googleMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng arg= new LatLng(-40, -63);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arg, 4));
        DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference("locations");
        final ArrayList<LatLng> locations = new ArrayList<>();
        locationsRef.orderByChild("date").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locations.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    LatLng latLng = new LatLng((Double) ds.child("lat").getValue(), (Double) ds.child("lng").getValue());
                    if (!locations.contains(latLng)) {
                        locations.add(0, latLng);
                    }
                }
                updateMap(googleMap, locations);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        locationsRef.orderByChild("date").limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, String s) {
                LatLng latLng = new LatLng((Double) ds.child("lat").getValue(), (Double) ds.child("lng").getValue());
                for (LatLng latLng2 : locations) {
                    if (latLng.latitude == latLng2.latitude && latLng.longitude == latLng2.longitude) {
                        return;
                    }
                }
                locations.add(0, latLng);
                updateMap(googleMap, locations);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot ds, String s) {
                LatLng latLng = new LatLng((Double) ds.child("lat").getValue(), (Double) ds.child("lng").getValue());
                int i = -1;
                for (LatLng latLng2 : locations) {
                    if (latLng.latitude == latLng2.latitude && latLng.longitude == latLng2.longitude) {
                        break;
                    } else {
                        i++;
                    }
                }
                if (i >= 0) {
                    locations.set(i, latLng);
                    updateMap(googleMap, locations);
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot ds) {
                LatLng latLng = new LatLng((Double) ds.child("lat").getValue(), (Double) ds.child("lng").getValue());
                for (LatLng latLng2 : locations) {
                    if (latLng.latitude == latLng2.latitude && latLng.longitude == latLng2.longitude) {
                        locations.remove(latLng2);
                        updateMap(googleMap, locations);
                        break;
                    }
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
