package com.kanishk.ezetools.utils;

import com.kanishk.ezetools.R;

/**
 * Created by kanishk on 1/9/16.
 */
public class Constants {

    public static int[] TAB_ICON = {R.drawable.metro, R.drawable.tuner};

    public static int[] CLICK_FILES = {R.raw.metro_beat1, R.raw.metro_beat2, R.raw.marimba1,
            R.raw.marimba2, R.raw.click_1, R.raw.click_2, R.raw.pt_click1,
            R.raw.pt_click2};

    public static final String TICK = "tick.wav";

    public static final String TOCK = "tock.wav";

    public static final String METRONOME_PATCH = "metro1.pd";

    public static final String TUNER_PATCH = "pitch.pd";

    public static final String TICK_FILE = "tickFile";

    public static final int MAX_BEATS = 400;

    public static final int MIN_BEATS = 20;

    public static final int DEFAULT_TIMING = 4;

    public static final int DEFAULT_BEAT = 90;

    public static final String SEND_BEAT = "bpm";

    public static final String SEND_BANG = "bang";

    public static final String[] LOAD_PATCH = {METRONOME_PATCH, };
}
