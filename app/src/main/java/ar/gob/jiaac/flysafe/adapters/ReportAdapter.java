package ar.gob.jiaac.flysafe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.models.Aircraft;
import ar.gob.jiaac.flysafe.models.Report;

public class ReportAdapter extends ArrayAdapter<Report> {
    private final Context context;
    private final List<Report> objects;

    public ReportAdapter(@NonNull Context context, @NonNull List<Report> objects) {
        super(context, R.layout.row_report, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_report, parent, false);
        TextView date = rowView.findViewById(R.id.textViewDateValue);
        TextView eventType = rowView.findViewById(R.id.textViewTypeValue);
        TextView narrative = rowView.findViewById(R.id.textViewNarrative);
        TextView location = rowView.findViewById(R.id.textViewLocationValue);
        TextView aircraftRegistrations = rowView.findViewById(R.id.textViewRegistrations);
        TextView validated = rowView.findViewById(R.id.textViewValidated);
        Report r = objects.get(position);
        if (r.getValidated() == 1) {
            validated.setText(R.string.ReportAdapterYesInvalid);
        } else if (r.getValidated() == 2) {
            validated.setText(R.string.ReportAdapterYesValid);
        } else if (r.getValidated() == 3) {
            validated.setText(R.string.ReportAdapterYesResolved);
        } else {
            validated.setText(R.string.No);
        }
        date.setText(r.getDateAsString());
        eventType.setText(r.getOccurrenceType().getName());
        if (r.getNarrative() != null) {
            narrative.setText(r.getNarrative());
        } else {
            TextView narrativeLabel = rowView.findViewById(R.id.textViewNarrativeLabel);
            narrativeLabel.setVisibility(View.GONE);
            narrative.setVisibility(View.GONE);
        }
        if (r.getLocation() != null) {
            location.setText(r.getLocation());
        } else {
            TextView locationLabel = rowView.findViewById(R.id.textViewLocationLabel);
            location.setVisibility(View.GONE);
            locationLabel.setVisibility(View.GONE);
        }
        StringBuilder registrations = new StringBuilder();
        String comma = "";
        for (Aircraft a : r.getAircrafts()) {
            registrations.append(comma);
            registrations.append(a.getRegistration());
            comma = ", ";
        }
        aircraftRegistrations.setText(registrations.toString());
        return rowView;
    }
}
