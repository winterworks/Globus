package nl.bramwinter.globus.adaptors;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.fragments.ContactsFragment.ContactFragmentListener;
import nl.bramwinter.globus.models.User;

public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private final List<User> users;
    private final ContactFragmentListener mListener;

    public MyUserRecyclerViewAdapter(List<User> users, ContactFragmentListener listener) {
        this.users = users;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.user = users.get(position);
        holder.textName.setText(users.get(position).getName());
        holder.textEmail.setText(users.get(position).getEmail());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.ContactClickListener(holder.user);
            }
        });

        holder.mView.setOnLongClickListener(view -> {
            if (null != mListener) {
                // Source: https://developer.android.com/guide/topics/ui/dialogs#java
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle(R.string.remove_contact);
                alertDialogBuilder.setMessage(R.string.are_you_sure);
                alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                    mListener.ContactPressListener(holder.user);
                });

                alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {
                });
                alertDialogBuilder.show();
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView textName;
        final TextView textEmail;
        User user;

        ViewHolder(View view) {
            super(view);
            mView = view;
            textName = view.findViewById(R.id.textName);
            textEmail = view.findViewById(R.id.textEmail);
        }
    }
}
