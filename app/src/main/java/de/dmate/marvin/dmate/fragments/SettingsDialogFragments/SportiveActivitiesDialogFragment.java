package de.dmate.marvin.dmate.fragments.SettingsDialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dmate.marvin.dmate.R;

public class SportiveActivitiesDialogFragment extends DialogFragment {

    private OnSportiveActivitiesDialogFragmentListener mListener;

    public SportiveActivitiesDialogFragment() {
        // Required empty public constructor
    }

    public static SportiveActivitiesDialogFragment newInstance() {
        SportiveActivitiesDialogFragment fragment = new SportiveActivitiesDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.fragment_dialog_sportive_activities);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_sportive_activities, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSportiveActivitiesDialogFragmentListener) {
            mListener = (OnSportiveActivitiesDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBasalInsulineDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSportiveActivitiesDialogFragmentListener {

    }
}