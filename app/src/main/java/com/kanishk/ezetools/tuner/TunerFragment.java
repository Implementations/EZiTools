package com.kanishk.ezetools.tuner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kanishk.ezetools.R;

/**
 * A class for metronome.
 */
public class TunerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public TunerFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("TunerFrag", "Tuner View");
        return inflater.inflate(R.layout.fragment_tuner, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TunerFrag", "Tuner Attached");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("TunerFrag", "Tuner Deached");
        mListener = null;
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
