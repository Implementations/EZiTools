package com.kanishk.ezetools.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class maintians the state of metronome like beat count, timing, notes per beat,
 * volume and whether the metronome is currently playing or not.
 */
public class MetronomeState implements Parcelable {

    private static final String CURRENT_BEAT = "currentBeat";

    private static final String CURENT_TIMING = "currentTiming";

    private static final String CUURENT_PLAYING = "currentPlaying";

    private static final String CURENT_NOTES_PER_BEAT = "notesPerBeat";

    private static final String CURENT_VOLUME = "currentVolume";

    public static final int DEFAULT_TIMING = 4;

    public static final int DEFAULT_BEAT = 90;

    public static final int DEFAULT_NOTESPER_BEAT = 1;

    public static final int DEFAULT_VOLUME = 25;

    private int currentBeat;

    private int currentTiming;

    private int notesPerBeat;

    private int volume;

    private long lastTapTime;

    private boolean isPlaying;

    private boolean isTapped;
    
    public MetronomeState() {
        this.currentBeat = DEFAULT_BEAT;
        this.currentTiming = DEFAULT_TIMING;
        this.notesPerBeat = DEFAULT_NOTESPER_BEAT;
        this.isPlaying = false;
        this.volume = DEFAULT_VOLUME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_BEAT, currentBeat);
        bundle.putInt(CURENT_TIMING, currentTiming);
        bundle.putInt(CURENT_NOTES_PER_BEAT, notesPerBeat);
        bundle.putInt(CURENT_VOLUME, volume);
        bundle.putBoolean(CUURENT_PLAYING, isPlaying);
        dest.writeBundle(bundle);
    }

    public int getCurrentBeat() {
        return currentBeat;
    }

    public void setCurrentBeat(int currentBeat) {
        this.currentBeat = currentBeat;
    }

    public int getCurrentTiming() {
        return currentTiming;
    }

    public void setCurrentTiming(int currentTiming) {
        this.currentTiming = currentTiming;
    }

    public int getNotesPerBeat() {
        return notesPerBeat;
    }

    public void setNotesPerBeat(int notesPerBeat) {
        this.notesPerBeat = notesPerBeat;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void incrementBeat() {
        currentBeat++;
    }

    public void decrementBeat() {
        currentBeat--;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isTapped() {
        return isTapped;
    }

    public void setTapped(boolean tapped) {
        isTapped = tapped;
    }

    public long getLastTapTime() {
        return lastTapTime;
    }

    public void setLastTapTime(long lastTapTime) {
        this.lastTapTime = lastTapTime;
    }

    private static final Parcelable.Creator<MetronomeState> CREATOR = new Creator<MetronomeState>() {

        @Override
        public MetronomeState createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            MetronomeState state = new MetronomeState();
            state.setCurrentBeat(bundle.getInt(CURRENT_BEAT, DEFAULT_BEAT));
            state.setCurrentTiming(bundle.getInt(CURENT_TIMING, DEFAULT_TIMING));
            state.setNotesPerBeat(bundle.getInt(CURENT_NOTES_PER_BEAT, DEFAULT_NOTESPER_BEAT));
            state.setVolume(bundle.getInt(CURENT_VOLUME, DEFAULT_VOLUME));
            state.setPlaying(bundle.getBoolean(CUURENT_PLAYING, false));
            return state ;
        }

        @Override
        public MetronomeState[] newArray(int size) {
            return new MetronomeState[size];
        }
    };
}
