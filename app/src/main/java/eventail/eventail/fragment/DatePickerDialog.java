package eventail.eventail.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import eventail.eventail.R;

public class DatePickerDialog extends DialogFragment implements DatePicker.OnDateChangedListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "date";

    public interface OnDateChangedListener {
        void onDateChanged(Calendar date);
    }

    private DatePicker datePicker;
    private Calendar calendar;
    private OnDateChangedListener listener;

    public DatePickerDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date Parameter 1.
     * @return A new instance of fragment DatePickerDialog.
     */
    public static DatePickerDialog newInstance(Calendar date) {
        DatePickerDialog fragment = new DatePickerDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, date);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDateChangeListener(OnDateChangedListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            calendar = (Calendar) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_date_picker_dialog, container);

        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        getDialog().setTitle("Choose date");

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        //TODO: handle context change
        super.onDismiss(dialogInterface);
        if(listener != null){
            listener.onDateChanged(calendar);
        }
        else {
            OnDateChangedListener listener = (OnDateChangedListener) getActivity();
            listener.onDateChanged(calendar);
        }
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        calendar.set(year, month, day);
    }
}
