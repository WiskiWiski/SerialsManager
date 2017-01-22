package by.wiskiw.serialsmanager.objects;

import java.util.Arrays;

/**
 * Created by WiskiW on 25.12.2016.
 */

public class Serial {
    private String name;
    private int eps;
    private int season;
    private int episode;
    private String note;

    private int identityLevel;
    private long nextEpisodeDateMs;
    private int nextEpisode;
    private int nextSeason;
    private int notificationId;

    public Serial(String name) {
        this.name = name;
        defaultInit();
    }

    public Serial(String name, int episode, int season, int eps, String note) {
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

    public void addSeason(){
        season++;
        episode = 1;
    }

    public int getEpisode() {
        return episode < 1 ? 1 : episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public void addEpisode(){
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
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getName() {
        return name;
    }

    public void resetNotificationData() {
        this.identityLevel = -1;
        this.nextEpisodeDateMs = -1;
        this.notificationId = 0;
    }

    private void defaultInit() {
        identityLevel = -1;
        nextEpisodeDateMs = -1;
        season = 1;
        episode = 1;
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
}
