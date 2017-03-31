package by.wiskiw.serialsmanager.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by WiskiW on 25.12.2016.
 */

public class Serial implements Parcelable {

    private String name;
    private int eps;
    private int season;
    private int episode;
    private String note;

    private int identityLevel;
    private long nextEpisodeDateMs;
    private int nextEpisode;
    private int nextSeason;
    private boolean notificationsEnable;

    public Serial(String name) {
        defaultInit();
        this.name = name;
    }

    public Serial(String name, int episode, int season, int eps, String note) {
        defaultInit();
        this.name = name;
        this.episode = episode;
        this.season = season;
        this.eps = eps;
        this.note = note;
    }

    public Serial(Serial serial) {
        this.name = serial.name;
        this.episode = serial.episode;
        this.season = serial.season;
        this.eps = serial.eps;
        this.note = serial.note;
        this.nextSeason = serial.nextSeason;
        this.nextEpisode = serial.nextEpisode;
        this.nextEpisodeDateMs = serial.nextEpisodeDateMs;
        this.identityLevel = serial.identityLevel;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public boolean isNotificationsEnable() {
        return notificationsEnable;
    }

    public void enableNotifications(boolean bool) {
        this.notificationsEnable = bool;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIdentityLevel() {
        return identityLevel;
    }

    public void setIdentityLevel(int identityLevel) {
        this.identityLevel = identityLevel;
    }

    public long getNextEpisodeDateMs() {
        return nextEpisodeDateMs;
    }

    public void setNextEpisodeDateMs(long nextEpisodeDateMs) {
        this.nextEpisodeDateMs = nextEpisodeDateMs;
    }

    public int getEps() {
        return eps;
    }

    public void setEps(int eps) {
        this.eps = eps;
    }

    public int getSeason() {
        return season < 1 ? 1 : season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void addSeason() {
        season++;
        episode = 1;
    }

    public int getEpisode() {
        return episode < 1 ? 1 : episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public void addEpisode() {
        if (episode >= eps && eps != 0) {
            episode = 0;
            addSeason();
        }
        episode++;
    }

    public int getNextEpisode() {
        return nextEpisode;
    }

    public void setNextEpisode(int nextEpisode) {
        this.nextEpisode = nextEpisode;
    }

    public int getNextSeason() {
        return nextSeason;
    }

    public void setNextSeason(int nextSeason) {
        this.nextSeason = nextSeason;
    }

    public int getNotificationId() {
        return (int) (nextEpisodeDateMs / 100000);
    }

    public String getName() {
        return name;
    }

    public void resetNotificationData() {
        this.identityLevel = -1;
        this.nextEpisodeDateMs = -1;
    }

    private void defaultInit() {
        identityLevel = -1;
        nextEpisodeDateMs = -1;
        season = 1;
        episode = 1;
        notificationsEnable = true;
        resetNotificationData();
    }

    @Override
    public String toString() {
        String[] toString = new String[]{
                "name:", name,
                "season:", String.valueOf(season),
                "episode:", String.valueOf(episode),
                "eps:", String.valueOf(eps),
                "identityLevel:" + identityLevel
        };
        return Arrays.toString(toString);
    }

    private int mData;

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    public static final Parcelable.Creator<Serial> CREATOR = new Parcelable.Creator<Serial>() {
        public Serial createFromParcel(Parcel in) {
            return new Serial(in);
        }

        public Serial[] newArray(int size) {
            return new Serial[size];
        }
    };

    private Serial(Parcel in) {
        mData = in.readInt();
    }
}
