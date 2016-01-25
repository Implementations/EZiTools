package com.kanishk.ezetools.metronome;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kanishk.ezetools.R;
import com.kanishk.ezetools.model.MetronomeState;
import com.kanishk.ezetools.utils.Constants;
import com.kanishk.ezetools.utils.Util;

import org.puredata.core.PdBase;

import java.io.File;
import java.io.IOException;

/**
 * Fragment for metronome. This fragment plays the metronome. It maintains the complete state of
 * the metronome.The beat count can be change by either signle press, long press of + and - buttons.
 * It can also be changed by seekbar. There are controls for adjusting the staff notation timings
 * as well as notes per beat.
 */
public class MetronomeFragment extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, View.OnLongClickListener {
    private OnFragmentInteractionListener mListener;

    private static final String TAG = MetronomeFragment.class.toString();

    private static final String CURRENT_STATE = "current_state";

    private Handler mHandler = new Handler();

    private MetronomeState mCurrentState;

    private BeatCountUpdater mbeatCountLongPressUpdater;

    private ImageButton mPlay;

    private ImageButton mNoteBeat;

    private TextView mBeatCountText;

    private SeekBar mBpmSeek;

    private SeekBar mVolumeSeek;

    private Button mTiming;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_metronome, container, false);
        mPlay = (ImageButton) root.findViewById(R.id.play);
        mNoteBeat = (ImageButton) root.findViewById(R.id.notesperBeat);
        mBeatCountText = (TextView) root.findViewById(R.id.bpm);
        mBpmSeek = (SeekBar) root.findViewById(R.id.bpmSeek);
        mVolumeSeek = (SeekBar) root.findViewById(R.id.volume);
        mBpmSeek.setMax(Constants.MAX_BEATS - Constants.MIN_BEATS);
        mVolumeSeek.setMax(Constants.MAX_VOLUME);
        mTiming = (Button) root.findViewById(R.id.timing);
        Button increment = (Button) root.findViewById(R.id.increment);
        Button decrement = (Button) root.findViewById(R.id.decrement);
        initState();
        mPlay.setOnClickListener(this);
        mBpmSeek.setOnSeekBarChangeListener(this);
        mVolumeSeek.setOnSeekBarChangeListener(this);
        mNoteBeat.setOnClickListener(this);
        mTiming.setOnClickListener(this);
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mbeatCountLongPressUpdater.isRunning
                        && event.getAction() == MotionEvent.ACTION_UP) {
                    mbeatCountLongPressUpdater.isRunning = false;
                    PdBase.sendFloat(Constants.SEND_BEAT, mCurrentState.getCurrentBeat());
                }
                return false;
            }
        };
        decrement.setOnClickListener(this);
        increment.setOnClickListener(this);
        increment.setOnLongClickListener(this);
        decrement.setOnLongClickListener(this);
        decrement.setOnTouchListener(onTouchListener);
        increment.setOnTouchListener(onTouchListener);
        return root;
    }

    private void initState() {
        mBeatCountText.setText(Integer.toString(mCurrentState.getCurrentBeat()));
        setSeekBarValue(mCurrentState.getCurrentBeat());
        mVolumeSeek.setProgress(mCurrentState.getVolume());
        mPlay.setImageResource(mCurrentState.isPlaying() ? R.drawable.stop : R.drawable.play);
        setTimingText(mCurrentState.getCurrentTiming());
        mNoteBeat.setImageResource(Constants.NOTES_PER_BEAT[mCurrentState.getNotesPerBeat() - 1]);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyClick(Constants.CLICK_FILES[0], Constants.CLICK_FILES[1]);
        if (savedInstanceState == null) {
            mCurrentState = new MetronomeState();
        } else {
            mCurrentState = savedInstanceState.getParcelable(CURRENT_STATE);
        }
        mbeatCountLongPressUpdater = new BeatCountUpdater();
    }

    /**
     * Copies the current click sounds to be played at the applications directory.
     * These sounds will be picked up automatically by the pure data patch.
     * */
    public void copyClick(int tickFile, int tockFile) {
        try {
            File dir = getActivity().getFilesDir();
            File tick = new File(dir, Constants.TICK);
            File tock = new File(dir, Constants.TOCK);
            Util.copyAsset(getActivity().getApplicationContext(), tickFile, tick);
            Util.copyAsset(getActivity().getApplicationContext(), tockFile, tock);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
//                PdBase.sendBang(Constants.TICK_FILE);
                mCurrentState.setPlaying(!mCurrentState.isPlaying());
                mPlay.setImageResource(mCurrentState.isPlaying() ? R.drawable.stop : R.drawable.play);
                PdBase.sendBang(Constants.SEND_BANG);
                break;
            case R.id.increment:
                changeBeatCount(true);
                PdBase.sendFloat(Constants.SEND_BEAT, mCurrentState.getCurrentBeat());
                break;
            case R.id.decrement:
                changeBeatCount(false);
                PdBase.sendFloat(Constants.SEND_BEAT, mCurrentState.getCurrentBeat());
                break;
            case R.id.notesperBeat:
                int imageIndex = changeNotesPerBeat() - 1;
                mNoteBeat.setImageResource(Constants.NOTES_PER_BEAT[imageIndex]);
                break;
            case R.id.timing:
                int timing = mCurrentState.getCurrentTiming();
                timing = (timing == 2 ? 4 : --timing);
                Log.d(TAG, "Index timing: " + timing);
                PdBase.sendFloat(Constants.SEND_TIMING, timing);
                setTimingText(timing);
                mCurrentState.setCurrentTiming(timing);
                break;
        }
    }

    /**
     * Changes the notes per beat in the current state  and return the value.
     * Currently the options are quarter note, eighth note and triplets.
     */
    public int changeNotesPerBeat() {
        int val = mCurrentState.getNotesPerBeat();
        val = (val == Constants.NOTES_PER_BEAT.length ? 1 : ++val);
        PdBase.sendFloat(Constants.SEND_NOTES_PERBEAT, val);
        PdBase.sendFloat(Constants.SEND_TIMING, mCurrentState.getCurrentTiming());
        PdBase.sendFloat(Constants.SEND_BEAT, mCurrentState.getCurrentBeat());
        mCurrentState.setNotesPerBeat(val);
        return val;
    }

    /**
     * Changes the beat count of the metronome. The beat count either increments or decrements.
     * based on the booleab flag. It updates the UI components (text bar and seek bar)
     * */
    public void changeBeatCount(boolean increment) {
        if (increment && mCurrentState.getCurrentBeat() < Constants.MAX_BEATS) {
            mCurrentState.incrementBeat();
            mBeatCountText.setText(Integer.toString(mCurrentState.getCurrentBeat()));
        } else if (!increment && mCurrentState.getCurrentBeat() > Constants.MIN_BEATS) {
            mCurrentState.decrementBeat();
            mBeatCountText.setText(Integer.toString(mCurrentState.getCurrentBeat()));
        }
        setSeekBarValue(mCurrentState.getCurrentBeat());
    }

    /**
     * Sets the beats per minute  seek bar value programatically.
     */
    private void setSeekBarValue(int beatCount) {
        mBpmSeek.setProgress(beatCount - Constants.MIN_BEATS);
    }

    /**
     * Sets the current interval timing(4/4, 3/4 etc.)  on the timing button
     */
    private void setTimingText(int timing) {
        mTiming.setText(timing + "/" + 4);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            switch (seekBar.getId()) {
                case R.id.bpmSeek:
                    mCurrentState.setCurrentBeat(progress + Constants.MIN_BEATS);
                    mBeatCountText.setText(Integer.toString(mCurrentState.getCurrentBeat()));
                    break;
                case R.id.volume:
                    mCurrentState.setVolume(progress);
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.bpmSeek:
                PdBase.sendFloat(Constants.SEND_BEAT, mCurrentState.getCurrentBeat());
                break;
            case R.id.volume:
                PdBase.sendFloat(Constants.SEND_VOLUME, (float) mCurrentState.getVolume() / 100);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.increment:
                mbeatCountLongPressUpdater.isRunning = true;
                mbeatCountLongPressUpdater.increment = true;
                mbeatCountLongPressUpdater.run();
                break;
            case R.id.decrement:
                mbeatCountLongPressUpdater.isRunning = true;
                mbeatCountLongPressUpdater.increment = false;
                mbeatCountLongPressUpdater.run();
                break;
        }
        return false;
    }


    /**
     * Runnable class to update the beat count on long press of increment and decrement button.
     * Using a single instance of this class. So keeping the status flag inside this class.
     */
    private class BeatCountUpdater implements Runnable {

        private static final int CHANGE_DELAY = 100;

        boolean increment;

        boolean isRunning;

        @Override
        public void run() {
            if(isRunning) {
                changeBeatCount(increment);
                mHandler.postDelayed(this, CHANGE_DELAY);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
