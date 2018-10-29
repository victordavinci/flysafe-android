package ar.gob.jiaac.flysafe.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.gob.jiaac.flysafe.R;

public class ContactFragment extends Fragment {
    public ContactFragment() {
    }

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        ((TextView) v.findViewById(R.id.webLabel)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) v.findViewById(R.id.emailLabel)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) v.findViewById(R.id.phoneLabel)).setMovementMethod(LinkMovementMethod.getInstance());
        return v;
    }

}
