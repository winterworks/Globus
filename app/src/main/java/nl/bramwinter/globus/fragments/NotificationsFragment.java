package nl.bramwinter.globus.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.adaptors.MyNotificationsRecyclerViewAdapter;
import nl.bramwinter.globus.models.Contact;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link NotificationFragmentListener}
 * interface.
 */
public class NotificationsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private NotificationFragmentListener mListener;

    private MutableLiveData<List<Contact>> contactsLiveData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NotificationsFragment newInstance(int columnCount) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            Observer<List<Contact>> contactsObserver = contacts -> recyclerView.setAdapter(new MyNotificationsRecyclerViewAdapter(contacts, mListener));
            contactsLiveData.observe(this, contactsObserver);
        }
        return view;
    }

    public void setContactsLiveData(MutableLiveData<List<Contact>> contactsLiveData) {
        this.contactsLiveData = contactsLiveData;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotificationFragmentListener) {
            mListener = (NotificationFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NotificationFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
    public interface NotificationFragmentListener {
        void NotificationClickListener(Contact contact);

        void NotificationAcceptListener(Contact contact);

        void NotificationDeclineListener(Contact contact);
    }
}
