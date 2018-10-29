package ar.gob.jiaac.flysafe.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.gob.jiaac.flysafe.R;

public class HelpFragment extends Fragment {


    public HelpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        ((TextView) v.findViewById(R.id.userManualLabel)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) v.findViewById(R.id.installationManualLabel)).setMovementMethod(LinkMovementMethod.getInstance());
        return v;
    }

}
