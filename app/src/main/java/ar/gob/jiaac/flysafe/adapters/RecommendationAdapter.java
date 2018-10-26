package ar.gob.jiaac.flysafe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.models.Recommendation;

public class RecommendationAdapter extends ArrayAdapter<Recommendation> {
    private final Context context;
    private final List<Recommendation> objects;

    public RecommendationAdapter(@NonNull Context context, @NonNull List<Recommendation> objects) {
        super(context, R.layout.row_recommendation, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_recommendation, parent, false);
        TextView date = rowView.findViewById(R.id.textViewDateValue);
        TextView category = rowView.findViewById(R.id.textViewCategoryValue);
        TextView text = rowView.findViewById(R.id.textViewText);
        Recommendation r = objects.get(position);
        date.setText(r.getDateAsString());
        category.setText(r.getCategory());
        text.setText(r.getText());
        return rowView;
    }
}
