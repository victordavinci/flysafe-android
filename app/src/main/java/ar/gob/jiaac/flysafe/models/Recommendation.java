package ar.gob.jiaac.flysafe.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.gob.jiaac.flysafe.utils.DateUtils;

@IgnoreExtraProperties
public class Recommendation {
    @Exclude
    private String id;
    private Date date;
    private String category;
    private String text;

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Recommendation) {
            if (getId() == null || ((Recommendation) o).getId() == null) {
                return false;
            }
            return getId().equals(((Recommendation) o).getId());
        }
        return false;
    }

    static public Recommendation fromSnapshot(DataSnapshot ds) {
        Recommendation r = new Recommendation();
        r.setId(ds.getKey());
        r.setDateFromString((String) ds.child("date").getValue());
        r.setCategory((String) ds.child("category").getValue());
        r.setText((String) ds.child("text").getValue());
        return r;
    }

    public void setDateFromString(String date) {
        this.date = DateUtils.dateFromString(date);
    }

    public String getDateAsString() {
        return DateUtils.dateToString(getDate());
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
