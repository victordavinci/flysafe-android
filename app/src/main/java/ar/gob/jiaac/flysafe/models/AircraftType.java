package ar.gob.jiaac.flysafe.models;

import android.content.Context;

import ar.gob.jiaac.flysafe.R;

public class AircraftType {
    static public final AircraftType UNKNOWN = new AircraftType(0, "Unknown");
    static public final AircraftType AIRPLANE = new AircraftType(1, "Airplane");
    static public final AircraftType HELICOPTER = new AircraftType(2, "Helicopter");
    static public final AircraftType EXPERIMENTAL = new AircraftType(3, "Experimental");

    static public final AircraftType[] aircraftTypes = {UNKNOWN, AIRPLANE, HELICOPTER, EXPERIMENTAL};

    private long type;
    private String name;

    AircraftType(int type, String name) {
        setType(type);
        setName(name);
    }

    static public void updateNames(Context ctx) {
        UNKNOWN.setName(ctx.getString(R.string.Unknown));
        AIRPLANE.setName(ctx.getString(R.string.Airplane));
        HELICOPTER.setName(ctx.getString(R.string.Helicopter));
        HELICOPTER.setName(ctx.getString(R.string.Experimental));
    }

    static public AircraftType fromLong(Context ctx, long type) {
        for (AircraftType aircraftType : aircraftTypes) {
            if (aircraftType.getType() == type) {
                updateNames(ctx);
                return aircraftType;
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
