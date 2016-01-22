package com.kanishk.ezetools.metronome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.kanishk.ezetools.R;
import com.kanishk.ezetools.utils.Constants;
import com.kanishk.ezetools.utils.Util;

import org.puredata.core.PdBase;

import java.io.File;
import java.io.IOException;

/**
 * A class for metronome.
 */
public class MetronomeFragment extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private OnFragmentInteractionListener mListener;

    private static final String TAG = MetronomeFragment.class.toString();

    private static final String CURRENT_BEAT = "currentBeat";

    private static final String CUURENT_TIMING = "currentTiming";

    private static final String CUURENT_PLAYING = "currentPlaying";

    private ImageButton mPlay;

    private EditText mBeatCountText;

    private SeekBar mBpmSeek;

    private int mCurrentBeat;

    private int mCurrentTiming;

    private boolean mIsPlaying;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Metro view");
        View root = inflater.inflate(R.layout.fragment_metronome, container, false);
        mPlay = (ImageButton) root.findViewById(R.id.play);
        mBeatCountText = (EditText) root.findViewById(R.id.bpm);
        mBpmSeek = (SeekBar) root.findViewById(R.id.bpmSeek);
        mBpmSeek.setMax(Constants.MAX_BEATS - Constants.MIN_BEATS);
        Button increment = (Button) root.findViewById(R.id.increment);
        Button decrement = (Button) root.findViewById(R.id.decrement);
        initState(savedInstanceState);
        mPlay.setOnClickListener(this);
        mBpmSeek.setOnSeekBarChangeListener(this);
        decrement.setOnClickListener(this);
        increment.setOnClickListener(this);
        return root;
    }

    private void initState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mCurrentBeat = Constants.DEFAULT_BEAT;
            mCurrentTiming = Constants.DEFAULT_TIMING;
            mIsPlaying = false;
        } else {
            mCurrentBeat = savedInstanceState.getInt(CURRENT_BEAT, Constants.DEFAULT_BEAT);
            mCurrentTiming = savedInstanceState.getInt(CUURENT_TIMING, Constants.DEFAULT_TIMING);
            mIsPlaying = savedInstanceState.getBoolean(CUURENT_PLAYING, false);
        }
        mBeatCountText.setText(Integer.toString(mCurrentBeat));
        setSeekBarValue(mBpmSeek, mCurrentBeat);
        mPlay.setImageResource(mIsPlaying ? R.drawable.stop : R.drawable.play);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyClick(Constants.CLICK_FILES[0], Constants.CLICK_FILES[1]);
    }

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
                mIsPlaying = !mIsPlaying;
                mPlay.setImageResource(mIsPlaying ? R.drawable.stop : R.drawable.play);
                PdBase.sendBang(Constants.SEND_BANG);
                break;
            case R.id.increment:
                changeBeatCount(true);
                break;
            case R.id.decrement:
                changeBeatCount(false);
                break;
        }
    }

    private void changeBeatCount(boolean increment) {
        if (increment && mCurrentBeat < Constants.MAX_BEATS) {
            mCurrentBeat++;
            mBeatCountText.setText(Integer.toString(mCurrentBeat));
            setSeekBarValue(mBpmSeek, mCurrentBeat);
            PdBase.sendFloat(Constants.SEND_BEAT, mCurrentBeat);
        } else if (!increment && mCurrentBeat > Constants.MIN_BEATS) {
            mCurrentBeat--;
            mBeatCountText.setText(Integer.toString(mCurrentBeat));
            PdBase.sendFloat(Constants.SEND_BEAT, mCurrentBeat);
        }
        setSeekBarValue(mBpmSeek, mCurrentBeat);
    }

    private void setSeekBarValue(SeekBar seekBar, int beatCount) {
        seekBar.setProgress(beatCount - Constants.MIN_BEATS);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            mCurrentBeat = progress + Constants.MIN_BEATS;
            mBeatCountText.setText(Integer.toString(mCurrentBeat));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        PdBase.sendFloat(Constants.SEND_BEAT, mCurrentBeat);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
