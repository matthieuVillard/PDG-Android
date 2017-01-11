package eventail.eventail.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eventail.eventail.R;
import eventail.eventail.adapter.UserAdapter;
import eventail.eventail.models.User;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.users.SearchUserTask;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class UsersFragment extends Fragment {

    private View view;
    private ArrayList<User> arrayUsers = new ArrayList<User>();
    private ArrayAdapter<User> adapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users, container, false);

        final EditText username = (EditText)view.findViewById(R.id.username);
        Button search = (Button)view.findViewById(R.id.search);

        ListView users = (ListView)view.findViewById(R.id.users);

        adapter = new UserAdapter(getActivity(), arrayUsers);
        users.setAdapter(adapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchUserTask(getActivity(), new WebTaskHandler<List<User>>() {
                    @Override
                    public void handleSuccess(List<User> response) {
                        if(response.isEmpty()){
                            Toast.makeText(getActivity(), getString(R.string.msg_no_data), Toast.LENGTH_LONG);
                        }
                        else {
                            adapter.clear();
                            adapter.addAll(response);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }).execute(username.getText().toString());
            }
        });

        return view;
    }
}
