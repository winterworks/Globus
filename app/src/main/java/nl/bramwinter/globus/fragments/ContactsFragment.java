package nl.bramwinter.globus.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.adaptors.MyUserRecyclerViewAdapter;
import nl.bramwinter.globus.models.User;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ContactFragmentListener}
 * interface.
 */
public class ContactsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ContactFragmentListener mListener;
    private Button buttonAddContact;
    private EditText editTextEmail;

    private MutableLiveData<List<User>> usersLiveData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactsFragment newInstance(int columnCount) {
        ContactsFragment fragment = new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view.findViewById(R.id.contact_list) instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = view.findViewById(R.id.contact_list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            Observer<List<User>> userObserver = users -> recyclerView.setAdapter(new MyUserRecyclerViewAdapter(users, mListener));
            usersLiveData.observe(this, userObserver);
        }

        return view;
    }

    //Code to select and View in a fragment from: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonAddContact = view.findViewById(R.id.buttonAddContact);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.ContactAddListener(editTextEmail.getText().toString());
                Toast.makeText(getContext(), R.string.request_sent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUsersLiveData(MutableLiveData<List<User>> userLiveData) {
        this.usersLiveData = userLiveData;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ContactFragmentListener) {
            mListener = (ContactFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ContactFragmentListener");
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
    public interface ContactFragmentListener {
        void ContactClickListener(User user);

        void ContactAddListener(String email);

        void ContactPressListener(User user);
    }
}
