package ar.gob.jiaac.flysafe.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.models.Aircraft;
import ar.gob.jiaac.flysafe.models.AircraftType;

public class NewAircraftFragment extends Fragment {
    private EditText editTextRegistration;
    private Spinner spinnerType;

    private final ArrayList<AircraftType> aircraftTypes = new ArrayList<>(Arrays.asList(AircraftType.aircraftTypes));

    public NewAircraftFragment() {
    }

    public static NewAircraftFragment newInstance() {
        return new NewAircraftFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_aircraft, container, false);
        Context context = getContext();
        if (context == null) return v;
        editTextRegistration = v.findViewById(R.id.editTextRegistration);
        spinnerType = v.findViewById(R.id.spinnerAircraftType);
        spinnerType.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, aircraftTypes));
        Button add = v.findViewById(R.id.buttonAddAircraft);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String registration = editTextRegistration.getText().toString().trim();
                if (registration.length() == 0) {
                    editTextRegistration.setError(getString(R.string.FieldCantBeEmpty));
                    return;
                }
                Aircraft a = new Aircraft();
                a.setRegistration(editTextRegistration.getText().toString().trim());
                a.setType((AircraftType) spinnerType.getSelectedItem());
                Fragment fragment = getTargetFragment();
                if (fragment != null)
                    fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent().putExtra("ar.gob.jiaac.flysafe.fragments.NewAircraftFragment", a));
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null)
                    fragmentManager.popBackStack();
            }
        });
        return v;
    }
}
