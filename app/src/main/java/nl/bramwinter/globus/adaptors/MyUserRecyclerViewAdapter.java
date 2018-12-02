package nl.bramwinter.globus.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.fragments.ContactsFragment.OnContactInteractionListener;
import nl.bramwinter.globus.models.User;

public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private final OnContactInteractionListener mListener;

    public MyUserRecyclerViewAdapter(List<User> items, OnContactInteractionListener listener) {
        mValues = items;
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
        holder.mItem = mValues.get(position);
        holder.textName.setText(mValues.get(position).getFullName());
        holder.textEmail.setText(mValues.get(position).getEmail());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onContactFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView textName;
        final TextView textEmail;
        User mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            textName = view.findViewById(R.id.textName);
            textEmail = view.findViewById(R.id.textEmail);
        }
    }
}
