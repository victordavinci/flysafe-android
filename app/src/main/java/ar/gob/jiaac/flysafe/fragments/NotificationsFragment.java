package ar.gob.jiaac.flysafe.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.models.Notification;

public class NotificationsFragment extends ListFragment {
    private final ArrayList<Notification> notifications = new ArrayList<>();
    private ArrayAdapter<Notification> listAdapter = null;

    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance() {
        Bundle args = new Bundle();
        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity fragmentActivity = getActivity();
        if (listAdapter == null && fragmentActivity != null) {
            listAdapter = new ArrayAdapter<>(fragmentActivity, android.R.layout.simple_list_item_1, notifications);
            setListAdapter(listAdapter);

            DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

            String u = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            notificationsRef.orderByChild("user").equalTo(u).limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notifications.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Notification r = Notification.fromSnapshot(getContext(), ds);
                        if (!notifications.contains(r)) {
                            notifications.add(0, r);
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            notificationsRef.orderByChild("user").equalTo(u).limitToLast(20).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Notification r = Notification.fromSnapshot(getContext(), dataSnapshot);
                    if (!notifications.contains(r)) {
                        notifications.add(0, r);
                        listAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    Notification r = Notification.fromSnapshot(getContext(), dataSnapshot);
                    int i = notifications.indexOf(r);
                    if (i >= 0) {
                        notifications.set(i, r);
                        listAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Notification r = Notification.fromSnapshot(getContext(), dataSnapshot);
                    if (notifications.remove(r)) {
                        listAdapter.notifyDataSetChanged();
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

}
