package eventail.eventail.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import eventail.eventail.R;
import eventail.eventail.adapter.FriendAdapter;
import eventail.eventail.models.User;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.users.FriendsTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class FriendsFragment extends Fragment {

    private View view;
    private ArrayList<User> arrayFriends = new ArrayList<User>();
    private ArrayAdapter<User> adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        ListView friends = (ListView)view.findViewById(R.id.friends);

        adapter = new FriendAdapter(getActivity(), arrayFriends);
        friends.setAdapter(adapter);

        new FriendsTask(getActivity(), new WebTaskHandler<List<User>>() {
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
        }).execute();

        return view;
    }
}
