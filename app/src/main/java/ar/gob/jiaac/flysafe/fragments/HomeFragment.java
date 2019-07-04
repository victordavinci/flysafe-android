package ar.gob.jiaac.flysafe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;

import com.evernote.android.state.State;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.adapters.ReportAdapter;
import ar.gob.jiaac.flysafe.models.Report;

@SuppressWarnings("WeakerAccess")
public class HomeFragment extends ListFragment {
    @State public ArrayList<Report> reports = new ArrayList<>();
    public ReportAdapter reportAdapter;
    @State public boolean mine;
    @State public boolean search;

    private OnFragmentInteractionListener mListener;

    private EditText searchText;

    private final DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
    private final DatabaseReference aircraftsRef = FirebaseDatabase.getInstance().getReference("aircrafts");
    private Query q;

    private final ValueEventListener initialListListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            reports.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Report r = Report.fromSnapshot(getContext(), ds);
                if (!reports.contains(r)) {
                    reports.add(0, r);
                }
            }
            reportAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    private final ChildEventListener updateListListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Report r = Report.fromSnapshot(getContext(), dataSnapshot);
            if (!reports.contains(r)) {
                reports.add(0, r);
                reportAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            Report r = Report.fromSnapshot(getContext(), dataSnapshot);
            int i = reports.indexOf(r);
            if (i >= 0) {
                reports.set(i, r);
                reportAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Report r = Report.fromSnapshot(getContext(), dataSnapshot);
            if (reports.remove(r)) {
                reportAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    public HomeFragment() {
        setRetainInstance(true);
    }

    public static HomeFragment newInstance() {
        return newInstance(false, false);
    }

    public static HomeFragment newInstance(boolean mine, boolean search) {
        Bundle b = new Bundle();
        b.putBoolean("mine", mine);
        b.putBoolean("search", search);
        HomeFragment h = new HomeFragment();
        h.setArguments(b);
        return h;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mine = arguments.getBoolean("mine", false);
            search = arguments.getBoolean("search", false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_report_item) {
            if (mListener != null) {
                mListener.onNewReport();
            }
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        if (search && v != null) {
            TableRow tr1 = v.findViewById(R.id.searchRow1);
            TableRow tr2 = v.findViewById(R.id.searchRow2);
            tr1.setVisibility(View.VISIBLE);
            tr2.setVisibility(View.VISIBLE);
            searchText = v.findViewById(R.id.editTextSearch);
            Button searchButton = v.findViewById(R.id.buttonSearch);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateReportsList(true);
                }
            });
        }

        if (reportAdapter == null) {
            FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity != null) {
                reportAdapter = new ReportAdapter(getActivity(), reports);
                setListAdapter(reportAdapter);

                updateReportsList();
            }
        } else {
            for (Report r : reports) {
                r.updateLanguage(getContext());
            }
            reportAdapter.notifyDataSetChanged();
        }
    }

    private void updateReportsList() {
        updateReportsList(false);
    }

    private void updateReportsList(boolean searchClicked) {
        if (q != null && !search) {
            q.removeEventListener(initialListListener);
            q.removeEventListener(updateListListener);
        }

        if (searchClicked && search) {
            q = null;
            aircraftsRef.orderByKey().equalTo(searchText.getText().toString().trim().toUpperCase()).limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final ArrayList<String> reportList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot dsc : ds.getChildren()) {
                            if (!reportList.contains(dsc.getKey())) {
                                reportList.add(dsc.getKey());
                            }
                        }
                    }
                    if (reportList.size() == 0) {
                        Toast.makeText(getContext(), R.string.NoReportsFound, Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        reports.clear();
                        for (String r : reportList) {
                            db.getReference("reports/" + r).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    reports.add(Report.fromSnapshot(getContext(), dataSnapshot));
                                    reportAdapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else if(!search) {
            if (mine) {
                q = reportsRef.orderByChild("user").equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).limitToLast(20);
            } else {
                q = reportsRef.orderByChild("date").limitToLast(20);
            }
        }

        if (q != null && !search) {
            q.addListenerForSingleValueEvent(initialListListener);
            q.addChildEventListener(updateListListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onNewReport();
    }
}
