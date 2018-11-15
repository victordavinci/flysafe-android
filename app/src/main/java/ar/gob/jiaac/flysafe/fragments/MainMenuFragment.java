package ar.gob.jiaac.flysafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.models.Notification;

public class MainMenuFragment extends Fragment {
    public MainMenuFragment() {
    }

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("notifications");
        String u = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final View v = getView();
        final TextView notif = v.findViewById(R.id.textViewNotification);

        notifRef.orderByChild("user").equalTo(u).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        notif.setVisibility(View.VISIBLE);
                        notif.setText(Notification.fromSnapshot(getContext(), ds).getNotificationText());
                    }
                } else {
                    notif.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        notifRef.orderByChild("user").equalTo(u).limitToLast(1).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                v.findViewById(R.id.textViewNotification).setVisibility(View.VISIBLE);
                notif.setText(Notification.fromSnapshot(getContext(), dataSnapshot).getNotificationText());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                v.findViewById(R.id.textViewNotification).setVisibility(View.VISIBLE);
                notif.setText(Notification.fromSnapshot(getContext(), dataSnapshot).getNotificationText());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                v.findViewById(R.id.textViewNotification).setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);
        v.findViewById(R.id.textViewNotification).setVisibility(View.GONE);
        AppCompatImageButton buttonLatestReports = v.findViewById(R.id.buttonLatestReports);
        AppCompatImageButton buttonSafetyReports = v.findViewById(R.id.buttonSafetyRecommendations);
        AppCompatImageButton buttonAlerts = v.findViewById(R.id.buttonAlerts);
        buttonLatestReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment hf = new ReportsTabsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, hf);
                ft.addToBackStack(hf.getClass().getName());
                ft.commit();
            }
        });
        buttonSafetyReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment hf = SafetyRecommendationsFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, hf);
                ft.addToBackStack(hf.getClass().getName());
                ft.commit();
            }
        });
        buttonAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment hf = ContactFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, hf);
                ft.addToBackStack(hf.getClass().getName());
                ft.commit();
            }
        });
        return v;
    }
}
