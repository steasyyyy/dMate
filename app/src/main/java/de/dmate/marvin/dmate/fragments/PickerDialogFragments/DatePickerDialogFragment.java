package de.dmate.marvin.dmate.fragments.PickerDialogFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;

import de.dmate.marvin.dmate.R;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDatePickerFragmentInteractionListener mListener;
    private Calendar calendar;

    public DatePickerDialogFragment() {
        // Required empty public constructor
    }

    public static DatePickerDialogFragment newInstance(String param1, String param2) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        Bundle args = getArguments();
        calendar.setTimeInMillis(args.getLong("dateMillis"));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picker_date, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDatePickerFragmentInteractionListener) {
            mListener = (OnDatePickerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimePickerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (mListener != null) {
            mListener.updateDate(year, month, dayOfMonth);
        }
    }

    public interface OnDatePickerFragmentInteractionListener {
        void updateDate(int year, int month, int dayOfMonth);
    }
}
