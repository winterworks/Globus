package nl.bramwinter.globus.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.fragments.NotificationsFragment;
import nl.bramwinter.globus.fragments.NotificationsFragment.NotificationFragmentListener;
import nl.bramwinter.globus.models.Contact;

public class MyNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationsRecyclerViewAdapter.ViewHolder> {

    private final List<Contact> contacts;
    private final NotificationsFragment.NotificationFragmentListener mListener;

    public MyNotificationsRecyclerViewAdapter(List<Contact> contacts, NotificationFragmentListener listener) {
        for (Contact contact : contacts) {
            // Don't show the contact if it's already accepted or you are the one who requested the contact
//            if (contact.isAccepted() || contact.isInitiated()) {
//                contacts.remove(contact);
//            }
        }
        this.contacts = contacts;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.contact = contacts.get(position);
//        holder.nameView.setText(contacts.get(position).getContactor().getFullName());
        holder.buttonAccept.setOnClickListener(v -> AcceptContactRequest(holder.contact));
        holder.buttonReject.setOnClickListener(v -> RejectContactRequest(holder.contact));

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.NotificationClickListener(holder.contact);
            }
        });
    }

    private void AcceptContactRequest(Contact contact) {
        mListener.NotificationAcceptListener(contact);
    }

    private void RejectContactRequest(Contact contact) {
        mListener.NotificationDeclineListener(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView nameView;
        final Button buttonAccept;
        final Button buttonReject;
        Contact contact;

        ViewHolder(View view) {
            super(view);
            mView = view;
            nameView = view.findViewById(R.id.textName);
            buttonAccept = mView.findViewById(R.id.buttonAccept);
            buttonReject = mView.findViewById(R.id.buttonReject);
        }
    }
}
