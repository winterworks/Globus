package nl.bramwinter.globus.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.fragments.NotificationsFragment.OnListFragmentInteractionListener;
import nl.bramwinter.globus.models.Contact;

public class MyContactsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactsRecyclerViewAdapter.ViewHolder> {

    private final List<Contact> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyContactsRecyclerViewAdapter(List<Contact> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.nameView.setText(mValues.get(position).getContactor().getFullName());
        holder.buttonAccept.setOnClickListener(v -> AcceptContactRequest());
        holder.buttonReject.setOnClickListener(v -> RejectContactRequest());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onNotificationsFragmentInteraction(holder.mItem);
            }
        });
    }

    private void AcceptContactRequest() {
        // TODO implement
    }

    private void RejectContactRequest() {
        // TODO implement
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView nameView;
        final Button buttonAccept;
        final Button buttonReject;
        Contact mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            nameView = view.findViewById(R.id.textName);
            buttonAccept = mView.findViewById(R.id.buttonAccept);
            buttonReject = mView.findViewById(R.id.buttonReject);
        }
    }
}
