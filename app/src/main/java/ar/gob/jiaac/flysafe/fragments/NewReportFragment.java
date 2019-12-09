package ar.gob.jiaac.flysafe.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.evernote.android.state.State;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.models.Aircraft;
import ar.gob.jiaac.flysafe.models.OccurrenceType;
import ar.gob.jiaac.flysafe.models.Report;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("WeakerAccess")
public class NewReportFragment extends Fragment {
    private static final int NEW_AIRCRAFT_REQUEST_CODE = 1;
    private static final int PLACE_PICKER_REQUEST = 2;

    private DatePickerDialog picker;
    private EditText editDate;
    private EditText editNarrative;
    private Spinner spinnerType;
    private ListView aircraftList;
    private TableRow clearLocationRow;
    private Button location;

    @State public String placeName;
    public Place place;
    @State public ArrayList<Aircraft> aircrafts = new ArrayList<>();
    @State public ArrayList<OccurrenceType> occurrenceTypes = new ArrayList<>(Arrays.asList(OccurrenceType.occurrenceTypes));

    public NewReportFragment() {
        setRetainInstance(true);
    }

    public static NewReportFragment newInstance() {
        return new NewReportFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_AIRCRAFT_REQUEST_CODE && resultCode == RESULT_OK) {
            Aircraft a = (Aircraft) data.getSerializableExtra("ar.gob.jiaac.flysafe.fragments.NewAircraftFragment");
            if (a != null) {
                if (aircrafts.contains(a)) {
                    Toast.makeText(getContext(), R.string.AircraftAlreadyAdded, Toast.LENGTH_LONG).show();
                    return;
                }
                aircrafts.add(a);
                ((ArrayAdapter) aircraftList.getAdapter()).notifyDataSetChanged();
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(Objects.requireNonNull(getActivity()), data);
                if (place != null) {
                    placeName = getLocationName();
                    location.setText(getString(R.string.SetLocation) + " (" + placeName + ")");
                    clearLocationRow.setVisibility(View.VISIBLE);
                } else {
                    placeName = null;
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_report, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity a = getActivity();
        View v = getView();
        clearLocationRow = v != null ? (TableRow) v.findViewById(R.id.clearLocation) : null;
        editDate = v != null ? (EditText) v.findViewById(R.id.editDate) : null;
        editNarrative = v.findViewById(R.id.editNarrative);
        spinnerType = v.findViewById(R.id.spinnerType);
        aircraftList = v.findViewById(R.id.aircraftList);
        Context context = getContext();
        if (context != null) {
            spinnerType.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, occurrenceTypes));
            aircraftList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, aircrafts));
        }
        if (editDate != null) {
            editDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar cldr = Calendar.getInstance();
                    picker = new DatePickerDialog(NewReportFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            editDate.setText(String.format("%04d", year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));
                        }
                    }, cldr.get(Calendar.YEAR), cldr.get(Calendar.MONTH), cldr.get(Calendar.DAY_OF_MONTH));
                    picker.show();
                }
            });
        }
        aircraftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext()).setTitle(aircrafts.get(position).toString()).setMessage(R.string.RemoveAircraftQ).setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                aircrafts.remove(position);
                                ((ArrayAdapter) aircraftList.getAdapter()).notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        Button report = v.findViewById(R.id.buttonReport);
        Button addAircraft = v.findViewById(R.id.buttonAddAircraft);
        location = v.findViewById(R.id.buttonLocation);
        Button clearLocation = v.findViewById(R.id.buttonClearLocation);
        if (placeName != null) {
            location.setText(getString(R.string.SetLocation) + " (" + placeName + ")");
            if (clearLocationRow != null)
                clearLocationRow.setVisibility(View.VISIBLE);
        } else if (clearLocationRow != null) {
            clearLocationRow.setVisibility(View.GONE);
        }
        addAircraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment na = NewAircraftFragment.newInstance();
                na.setTargetFragment(NewReportFragment.this, NEW_AIRCRAFT_REQUEST_CODE);
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.fragment_container, na);
                    ft.addToBackStack(na.getClass().getName());
                    ft.commit();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(a), PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                    Toast.makeText(a, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        clearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(getString(R.string.SetLocation));
                clearLocationRow.setVisibility(View.GONE);
                place = null;
                placeName = null;
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report r = new Report();
                if (!r.setDateFromString(editDate.getText().toString().trim())) {
                    Toast.makeText(getContext(), R.string.InvalidDateFormat, Toast.LENGTH_LONG).show();
                    return;
                }
                r.setNarrative(editNarrative.getText().toString().trim());
                r.setOccurrenceType((OccurrenceType) spinnerType.getSelectedItem());
                if (place != null) {
                    String location = getLocationName();
                    Double lat = place.getLatLng().latitude;
                    Double lng = place.getLatLng().longitude;
                    r.setLocation(location, lat, lng);
                }
                r.setAircrafts(aircrafts);
                if (r.getAircrafts().size() == 0) {
                    Toast.makeText(getContext(), R.string.AddAtLeastOneAircraft, Toast.LENGTH_LONG).show();
                } else {
                    r.save();
                }
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStack();
                    if (fragmentManager.getBackStackEntryCount() == 1) {
                        AppCompatActivity activity = (AppCompatActivity) getActivity();
                        if (activity != null) {
                            androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
                            if (actionBar != null)
                                actionBar.setDisplayHomeAsUpEnabled(false);
                        }
                    }
                }
            }
        });
    }

    public String getLocationName() {
        if (place == null) {
            return null;
        }
        String location = place.getName().toString().trim();
        if (location.length() == 0) {
            double lat = place.getLatLng().latitude;
            double lng = place.getLatLng().longitude;
            location = Location.convert(lat, Location.FORMAT_SECONDS) + " - " + Location.convert(lng, Location.FORMAT_SECONDS);
        }
        return location;
    }
}
