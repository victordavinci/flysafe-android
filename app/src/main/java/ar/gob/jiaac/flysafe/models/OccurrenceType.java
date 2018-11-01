package ar.gob.jiaac.flysafe.models;

import android.content.Context;
import android.content.res.Resources;

import ar.gob.jiaac.flysafe.R;

public class OccurrenceType {
    static public OccurrenceType UNKNOWN = new OccurrenceType(0, "Unknown");
    static public OccurrenceType ACCIDENT = new OccurrenceType(1, "Accident");
    static public OccurrenceType INCIDENT = new OccurrenceType(2, "Incident");

    static public final OccurrenceType[] occurrenceTypes = { UNKNOWN, ACCIDENT, INCIDENT };

    private long type;
    private String name;

    OccurrenceType(int type, String name) {
        setType(type);
        setName(name);
    }

    static public void updateNames(Context ctx) {
        if (ctx == null) {
            return;
        }
        UNKNOWN.setName(ctx.getString(R.string.Unknown));
        ACCIDENT.setName(ctx.getString(R.string.Accident));
        INCIDENT.setName(ctx.getString(R.string.Incident));
    }

    static public OccurrenceType fromLong(Context ctx, long type) {
        for (OccurrenceType occurrenceType : occurrenceTypes) {
            if (occurrenceType.getType() == type) {
                updateNames(ctx);
                return occurrenceType;
            }
        }
        return null;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() { return name; }
}
