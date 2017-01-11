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

import java.util.ArrayList;

import eventail.eventail.models.User;

import eventail.eventail.R;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.users.InviteUserTask;


public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView mail = (TextView) convertView.findViewById(R.id.mail);
        Button invite = (Button) convertView.findViewById(R.id.invite);


        // Populate the data into the template view using the data object
        name.setText(user.getFirstname() + " " + user.getLastname());
        username.setText(user.getUsername());
        mail.setText(user.getEmail());

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.leave_dialog_title)
                        .setMessage(R.string.msg_invitation_confirm)
                        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new InviteUserTask(getContext(), new WebTaskHandler<Void>() {
                                    @Override
                                    public void handleSuccess(Void response) {
                                        remove(user);
                                        notifyDataSetChanged();
                                        Toast.makeText(getContext(), getContext().getString(R.string.msg_invitation_sendt), Toast.LENGTH_LONG);
                                    }
                                }).execute(user.getId());
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