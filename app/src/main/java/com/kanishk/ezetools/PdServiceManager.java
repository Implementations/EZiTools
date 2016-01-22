package com.kanishk.ezetools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.kanishk.ezetools.utils.Constants;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdDispatcher;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by kanishk on 1/11/16. A singleton to manage PdService.
 */
public class PdServiceManager {

    private static PdServiceManager sMANAGER = new PdServiceManager();

    private Context mContext;

    private PdService mPdService;

    private ServiceConnection mPdConnection;

    private Map<String, Integer> mPatches = new ArrayMap<>(2);

    private static final String TAG = PdServiceManager.class.toString();

    private PdServiceManager() {
        mPdConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPdService = ((PdService.PdBinder) service).getService();
                try {
                    initPd();
                    loadPatch(Constants.LOAD_PATCH);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "Service disconnected:" + name);
            }
        };
    }

    public static PdServiceManager get() {
        return sMANAGER;
    }

    /**
     * Initializes the manager. Use it to initialize before any activity starts.
     */
    public static void init(Context context) {
        sMANAGER.mContext = context.getApplicationContext();
    }

    public void stopService() {
        mContext.unbindService(mPdConnection);
    }

    public void startService() {
        mContext.bindService(new Intent(mContext, PdService.class), mPdConnection, Context.BIND_AUTO_CREATE);
    }

    private void initPd() throws IOException {
        // Configure the audio glue
        AudioParameters.init(mContext);
        int sampleRate = AudioParameters.suggestSampleRate();
        mPdService.initAudio(sampleRate, 1, 2, 10.0f);
        startAudio();
        // Create and install the dispatcher
//        PdServiceManager.EziPdDispatcher dispatcher = new PdServiceManager.EziPdDispatcher();
//        PdBase.setReceiver(dispatcher);
//        dispatcher.addListener("beat", new PdListener.Adapter() {
//            @Override
//            public void receiveBang(String source) {
//                Log.d(TAG, "Beatsssssssssssssssssss:::::::::::::");
////                    mPlay.setVisibility(View.GONE);
//            }
//        });
    }

    public void loadPatch(String... patches) throws IOException {
        File dir = mContext.getFilesDir();
        IoUtils.extractZipResource(mContext.getResources().openRawResource(R.raw.patch), dir, true);
        for (String patch : patches) {
            File patchFile = new File(dir, patch);
            int handler = PdBase.openPatch(patchFile.getAbsolutePath());
            mPatches.put(patch, handler);
        }
    }

    public void closePatch(String patchName) {
        Integer patchHandler = mPatches.get(patchName);
        if (patchHandler != null) {
            mPatches.remove(patchName);
            PdBase.closePatch(patchHandler);
        }
    }

    public void startAudio() {
        if (mPdService != null && !mPdService.isRunning()) {
            mPdService.startAudio();
        }
    }

    public void stopAudio() {
        if (mPdService != null) {
            mPdService.stopAudio();
        }
    }

    public boolean isServiceRunning() {
        return mPdService != null && mPdService.isRunning();
    }

    public static class EziPdDispatcher extends PdDispatcher {

        @Override
        public void print(String s) {

        }
    }
}
