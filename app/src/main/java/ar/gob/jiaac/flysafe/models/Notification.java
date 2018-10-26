package ar.gob.jiaac.flysafe.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.Date;
import java.util.Objects;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.utils.DateUtils;

public class Notification {
    private String id;
    private String aircrafts;
    private Date date;
    private Long newValue;
    private Long oldValue;
    private String report;
    private Date reportDate;
    private String user;
    private Context ctx;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(getAircrafts(), that.getAircrafts()) &&
                Objects.equals(getDate(), that.getDate()) &&
                Objects.equals(getNewValue(), that.getNewValue()) &&
                Objects.equals(getOldValue(), that.getOldValue()) &&
                Objects.equals(getReport(), that.getReport()) &&
                Objects.equals(getReportDate(), that.getReportDate()) &&
                Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAircrafts(), getDate(), getNewValue(), getOldValue(), getReport(), getReportDate(), getUser());
    }

    private Notification(Context ctx) {
        this.ctx = ctx;
    }

    static public Notification fromSnapshot(Context ctx, DataSnapshot ds) {
        Notification r = new Notification(ctx);
        r.setId(ds.getKey());
        r.setAircrafts((String) ds.child("aircrafts").getValue());
        if (ds.child("date").getValue() != null) {
            r.setDate(new Date((Long) ds.child("date").getValue()));
        }
        r.setNewValue((Long) ds.child("newValue").getValue());
        r.setOldValue((Long) ds.child("oldValue").getValue());
        r.setReport((String) ds.child("report").getValue());
        r.setReportDate(DateUtils.dateFromString((String) ds.child("reportDate").getValue()));
        r.setUser((String) ds.child("user").getValue());
        return r;
    }

    public String getNotificationText() {
        if (getAircrafts() == null || getNewValue() == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.getString(R.string.YourReportFrom)).append(DateUtils.dateToString(getDate()));
        if (getAircrafts().length() > 0) {
            sb.append(" (").append(getAircrafts()).append(")");
        }
        if (getNewValue() == 2) {
            sb.append(ctx.getString(R.string.HasBeenAccepted));
        } else if (getNewValue() == 3) {
            sb.append(ctx.getString(R.string.HasBeenInvestigated));
        } else if (getNewValue() == 1) {
            sb.append(ctx.getString(R.string.HasBeenRejected));
        } else {
            sb.append(ctx.getString(R.string.IsAwaitingApproval));
        }
        return sb.toString();
    }

    public String toString() {
        return getNotificationText();
    }

    public String getAircrafts() {
        return aircrafts;
    }

    public void setAircrafts(String aircrafts) {
        this.aircrafts = aircrafts;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getNewValue() {
        return newValue;
    }

    public void setNewValue(Long newValue) {
        this.newValue = newValue;
    }

    public Long getOldValue() {
        return oldValue;
    }

    public void setOldValue(Long oldValue) {
        this.oldValue = oldValue;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
