package eventail.eventail.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import eventail.eventail.R;
import eventail.eventail.models.Request;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.users.AcceptRequestTask;
import eventail.eventail.service.webService.task.users.DenyRequestTask;


public class RequestAdapter extends ArrayAdapter<Request> {
    public RequestAdapter(Context context, ArrayList<Request> requests) {
        super(context, 0, requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Request request = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        Button accept = (Button) convertView.findViewById(R.id.accept);
        Button delete = (Button) convertView.findViewById(R.id.delete);


        // Populate the data into the template view using the data object
        name.setText(request.getUser().getFirstname() + " " + request.getUser().getLastname());
        username.setText(request.getUser().getUsername());
        if(request.getDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            date.setText(df.format(request.getDate().getTime()));
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AcceptRequestTask(getContext(), new WebTaskHandler<Void>() {
                    @Override
                    public void handleSuccess(Void response) {
                        remove(request);
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), getContext().getString(R.string.msg_request_accepted), Toast.LENGTH_LONG);
                    }
                }).execute(request.getUser().getId());
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.leave_dialog_title)
                        .setMessage(R.string.msg_request_delete_confirm)
                        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new DenyRequestTask(getContext(), new WebTaskHandler<Void>() {
                                    @Override
                                    public void handleSuccess(Void response) {
                                        remove(request);
                                        notifyDataSetChanged();
                                        Toast.makeText(getContext(), getContext().getString(R.string.msg_request_denied), Toast.LENGTH_LONG);
                                    }
                                }).execute(request.getUser().getId());
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}