package nl.bramwinter.globus.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment.OnListFragmentInteractionListener;
import nl.bramwinter.globus.models.Location;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link nl.bramwinter.globus.models.Location} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyLocationRecyclerViewAdapter extends RecyclerView.Adapter<MyLocationRecyclerViewAdapter.ViewHolder> {

    private final List<Location> locations;
    private final OnListFragmentInteractionListener mListener;

    public MyLocationRecyclerViewAdapter(List<Location> items, OnListFragmentInteractionListener listener) {
        locations = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.location = locations.get(position);
        String titleText = locations.get(position).getName()+" → "+locations.get(position).getName();
        holder.textTitle.setText(titleText);
        // TODO get the username from the user
        holder.textUsername.setText("user");

        Format formatter = new SimpleDateFormat("dd-MM-yy");
        String readableDate = formatter.format(locations.get(position).getAddedAt());
        holder.textUserMovedDate.setText(String.format(holder.mView.getResources().getString(R.string.user_moved_on_date), readableDate));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onLocationUpdatesFragmentInteraction(holder.location);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView textTitle;
        public final TextView textUserMovedDate;
        public final TextView textUsername;
        public Location location;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textTitle = (TextView) view.findViewById(R.id.textTitle);
            textUserMovedDate = (TextView) view.findViewById(R.id.textUserMovedDate);
            textUsername = (TextView) view.findViewById(R.id.textUsername);
        }
    }
}
