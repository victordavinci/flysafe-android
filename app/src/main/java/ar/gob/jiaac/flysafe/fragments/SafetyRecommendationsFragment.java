package ar.gob.jiaac.flysafe.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.adapters.RecommendationAdapter;
import ar.gob.jiaac.flysafe.models.Recommendation;

public class SafetyRecommendationsFragment extends ListFragment {
    private final ArrayList<Recommendation> recommendations = new ArrayList<>();
    private RecommendationAdapter recommendationAdapter;

    public SafetyRecommendationsFragment() {
    }

    public static SafetyRecommendationsFragment newInstance() {
        return new SafetyRecommendationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_safety_recommendations, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (recommendationAdapter == null) {
            recommendationAdapter = new RecommendationAdapter(getActivity(), recommendations);
            setListAdapter(recommendationAdapter);

            DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("recommendations");

            reportsRef.orderByChild("date").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recommendations.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Recommendation r = Recommendation.fromSnapshot(ds);
                        if (!recommendations.contains(r)) {
                            recommendations.add(0, r);
                        }
                    }
                    recommendationAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            reportsRef.orderByChild("date").limitToLast(20).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Recommendation r = Recommendation.fromSnapshot(dataSnapshot);
                    if (!recommendations.contains(r)) {
                        recommendations.add(0, r);
                        recommendationAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    Recommendation r = Recommendation.fromSnapshot(dataSnapshot);
                    int i = recommendations.indexOf(r);
                    if (i >= 0) {
                        recommendations.set(i, r);
                        recommendationAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Recommendation r = Recommendation.fromSnapshot(dataSnapshot);
                    if (recommendations.remove(r)) {
                        recommendationAdapter.notifyDataSetChanged();
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
