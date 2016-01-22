package com.kanishk.ezetools;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.kanishk.ezetools.metronome.MetronomeFragment;
import com.kanishk.ezetools.tuner.TunerFragment;
import com.kanishk.ezetools.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private static final String TAG = MainActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);;
        initSystemServices();
//        MetronomePlayer.get().init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setTabIcon();
        PdServiceManager.get().startService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PdServiceManager.get().stopService();
    }

    private void setTabIcon() {
        mTabLayout.getTabAt(0).setIcon(Constants.TAB_ICON[0]);
        mTabLayout.getTabAt(1).setIcon(Constants.TAB_ICON[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new MetronomeFragment(), getString(R.string.metronome));
        pagerAdapter.addFragment(new TunerFragment(), getString(R.string.tuner));
        viewPager.setAdapter(pagerAdapter);
    }

    private void initSystemServices() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    PdServiceManager.get().startAudio();
                } else {
                    PdServiceManager.get().stopAudio();
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
