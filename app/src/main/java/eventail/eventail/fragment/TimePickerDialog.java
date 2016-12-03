package eventail.eventail.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import eventail.eventail.R;

public class TimePickerDialog extends DialogFragment implements TimePicker.OnTimeChangedListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "time";



    public interface TimePickerDialogListener {
        void onTimeChange(Calendar time);
    }

    private TimePicker timePicker;
    private Calendar time;

    public TimePickerDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date Parameter 1.
     * @return A new instance of fragment DatePickerDialog.
     */
    public static TimePickerDialog newInstance(Date date) {
        TimePickerDialog fragment = new TimePickerDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, date.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        time = Calendar.getInstance();
        if (getArguments() != null) {
            time.setTimeInMillis(getArguments().getLong(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_time_picker_dialog, container);

        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        getDialog().setTitle("Choose date");

        timePicker.setCurrentHour(time.get(Calendar.HOUR));
        timePicker.setCurrentMinute(time.get(Calendar.MINUTE));
        timePicker.setOnTimeChangedListener(this);

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        //TODO: handle context change
        super.onDismiss(dialogInterface);
        TimePickerDialogListener listener = (TimePickerDialogListener) getActivity();
        listener.onTimeChange(time);
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        time.set(Calendar.HOUR, hour);
        time.set(Calendar.MINUTE, minute);
    }
}
