package com.kanishk.ezitools.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.kanishk.ezitools.PdServiceManager;
import com.kanishk.ezitools.utils.Constants;

import org.puredata.core.PdBase;
import org.puredata.core.PdListener;

import static com.kanishk.ezitools.utils.Constants.CLICK_FILES;

/**
 * Metronome player. A singleton to manage the play state of metronome.
 * Created by kanishk on 1/12/16.
 */
public class MetronomePlayer {

    private static final String TAG = MetronomePlayer.class.toString();

    private static MetronomePlayer sPLAYER = new MetronomePlayer();

    private Context mContext;

    private SoundPool mSoundPool;

    private int[] mSoundIds = new int[CLICK_FILES.length];

    private PlayerState mPlayerState;

    PdServiceManager.EziPdDispatcher mDispatcher = new PdServiceManager.EziPdDispatcher();

    private MetronomePlayer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        } else {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            mSoundPool = builder.build();
        }
        mPlayerState = new PlayerState();
    }

    public static MetronomePlayer get() {
        return sPLAYER;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mDispatcher = new PdServiceManager.EziPdDispatcher();
        loadSound();
    }


    public void loadSound() {
        int i = 0;
        for (int s : CLICK_FILES) {
            int out = mSoundPool.load(mContext, s, 1);
            mSoundIds[i] = out;
            i++;
        }
        mPlayerState.mTick = mSoundIds[0];
        mPlayerState.mTock = mSoundIds[1];
        PdBase.setReceiver(mDispatcher);
        mDispatcher.addListener(Constants.SEND_BEAT, new PdListener.Adapter() {
            @Override
            public void receiveBang(String source) {
                mPlayerState.play(mSoundPool);
            }
        });
    }

    /**
     * A helper class to maintain the current state of the metronome
     */
    private static class PlayerState {

        int mTick;
        int mTock;
        int mCurrent;
        int mTimeLimit;
        float mVolume;

        public PlayerState() {
            mTimeLimit = 4;
            mVolume = 0.5f;
        }

        public void reset() {
            mCurrent = 1;
        }

        public void play(SoundPool soundPool) {
            if(mCurrent == mTimeLimit) {
                soundPool.play(mTick, mVolume, mVolume, 1, 0, 1.0f);
                mCurrent = 1;
            } else {
                soundPool.play(mTock, mVolume, mVolume, 1, 0, 1.0f);
                mCurrent++;
            }
        }
    }
}
