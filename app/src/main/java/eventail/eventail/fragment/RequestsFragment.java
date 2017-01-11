package eventail.eventail.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eventail.eventail.R;
import eventail.eventail.adapter.RequestAdapter;
import eventail.eventail.models.Request;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.users.RequestsTask;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class RequestsFragment extends Fragment {

    private View view;
    private ArrayList<Request> arrayRequests = new ArrayList<Request>();
    private ArrayAdapter<Request> adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requests, container, false);

        ListView requests = (ListView)view.findViewById(R.id.requests);

        adapter = new RequestAdapter(getActivity(), arrayRequests);
        requests.setAdapter(adapter);

        new RequestsTask(getActivity(), new WebTaskHandler<List<Request>>() {
            @Override
            public void handleSuccess(List<Request> response) {
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
