package ar.gob.jiaac.flysafe.models;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
@IgnoreExtraProperties
public class Report {
    @Exclude
    private String id;
    private Date date;
    private OccurrenceType occurrenceType;
    private String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String narrative;
    private ArrayList<Aircraft> aircrafts = new ArrayList<>();
    private Long validated = 0L;
    private String location;
    private Double lat = 0.0;
    private Double lng = 0.0;

    @Exclude
    private final DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
    @Exclude
    private DatabaseReference stats = FirebaseDatabase.getInstance().getReference("stats");

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Report) {
            if (getId() == null || ((Report) o).getId() == null) {
                return false;
            }
            return getId().equals(((Report) o).getId());
        }
        return false;
    }

    static public Report fromSnapshot(Context ctx, DataSnapshot ds) {
        Report r = new Report();
        r.setId(ds.getKey());
        r.setDateFromString((String) ds.child("date").getValue());
        Object type = ds.child("type").getValue();
        if (type != null) {
            r.setOccurrenceType(OccurrenceType.fromLong(ctx, (Long) type));
        }
        DataSnapshot aircrafts = ds.child("aircrafts");
        if (aircrafts.getValue() != null) {
            for(DataSnapshot a : aircrafts.getChildren()) {
                r.aircrafts.add(Aircraft.fromSnapshot(ctx, a));
            }
        }
        r.setUser((String) ds.child("user").getValue());
        r.setNarrative((String) ds.child("narrative").getValue());
        r.setValidated((Long) ds.child("validated").getValue());
        r.setLocation((String) ds.child("location").getValue());
        r.setLat((Double) ds.child("lat").getValue());
        r.setLng((Double) ds.child("lng").getValue());
        return r;
    }

    public void updateLanguage(Context ctx) {
        setOccurrenceType(OccurrenceType.fromLong(ctx, getOccurrenceType().getType()));
    }

    @Exclude
    public void save() {
        if (getId() == null) {
            final Map<String, Object> r = toMap();
            final ArrayList<Aircraft> as = getAircrafts();

            String key = reportsRef.push().getKey();
            HashMap<String, Object> data = new HashMap<>();
            data.put("/reports/" + key, r);
            for (Aircraft a : as) {
                data.put("/aircrafts/" + a.getRegistration() + "/" + key, true);
            }
            FirebaseDatabase.getInstance().getReference().updateChildren(data);
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> m = new HashMap<>();
        m.put("type", getOccurrenceType().getType());
        m.put("date", getDateAsString());
        HashMap<String, Object> as = new HashMap<>();
        for (Aircraft a : aircrafts) {
            as.put(a.getRegistration(), a.toMap(true));
        }
        m.put("aircrafts", as);
        m.put("narrative", getNarrative());
        m.put("user", getUser());
        m.put("location", getLocation());
        m.put("lat", getLat());
        m.put("lng", getLng());
        return m;
    }

    public boolean setDateFromString(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.date = df.parse(date);
        } catch (NullPointerException | ParseException e) {
            return false;
        }
        return true;
    }

    public String getDateAsString() {
        if (getDate() == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(getDate());
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

    public OccurrenceType getOccurrenceType() {
        return occurrenceType;
    }

    public void setOccurrenceType(OccurrenceType occurrenceType) {
        this.occurrenceType = occurrenceType;
    }

    public ArrayList<Aircraft> getAircrafts() {
        return aircrafts;
    }

    public void setAircrafts(ArrayList<Aircraft> aircrafts) {
        this.aircrafts = aircrafts;
    }

    public void addAircraft(Aircraft aircraft) { this.aircrafts.add(aircraft); }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public Long getValidated() {
        return validated;
    }

    public void setValidated(Long validated) {
        if (validated == null) {
            this.validated = 0L;
        } else {
            this.validated = validated;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setLocation(String location, Double lat, Double lng) {
        setLocation(location);
        setLat(lat);
        setLng(lng);
    }
}
