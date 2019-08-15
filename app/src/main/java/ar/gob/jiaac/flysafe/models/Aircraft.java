package ar.gob.jiaac.flysafe.models;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
@IgnoreExtraProperties
public class Aircraft implements Serializable {
    @Exclude
    private String id;
    private String registration;
    private AircraftType type;

    static public Aircraft fromSnapshot(Context ctx, DataSnapshot ds) {
        Aircraft a = new Aircraft();
        a.setRegistration(ds.getKey());
        DataSnapshot t = ds.child("type");
        if (t.getValue() != null) {
            a.setType(AircraftType.fromLong(ctx, ((Number) t.getValue()).longValue()));
        }
        return a;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public AircraftType getType() {
        return type;
    }

    public void setType(AircraftType type) {
        this.type = type;
    }

    @Exclude
    @NonNull
    public String toString() {
        return getType().getName() + " - " + getRegistration();
    }

    @Exclude
    public Map<String, Object> toMap() {
        return toMap(false);
    }

    @Exclude
    public Map<String, Object> toMap(boolean excludeRegistration) {
        HashMap<String, Object> m = new HashMap<>();
        m.put("type", getType().getType());
        if (!excludeRegistration) {
            m.put("registration", getRegistration());
        }
        return m;
    }

    @Override
    public int hashCode() {
        return getRegistration().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return  false;
        }
        if (o instanceof Aircraft) {
            return ((Aircraft) o).getRegistration().equals(this.getRegistration());
        }
        return false;
    }
}
