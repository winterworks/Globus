package nl.bramwinter.globus.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;
import nl.bramwinter.globus.util.MyProperties;

public class LocationsUpdateRecyclerViewAdapter extends RecyclerView.Adapter<LocationsUpdateRecyclerViewAdapter.ViewHolder> {

    private final List<User> users;
    private final LocationUpdatesFragment.locationsFragmentListener mListener;

    public LocationsUpdateRecyclerViewAdapter(List<User> users, LocationUpdatesFragment.locationsFragmentListener listener) {
        this.users = users;
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
        holder.user = users.get(position);
        String title = holder.mView.getResources().getString(R.string.locations_of) + " " + holder.user.getName();
        holder.textTitle.setText(title);

        TableLayout tableLayout = holder.locationsTable;
        for (Location location : holder.user.getLocations().values()) {
            TableRow tableRow = new TableRow(holder.mView.getContext());
            Integer iconResource = location.getIcon();
            if (iconResource != null) {
                ImageView imageView = new ImageView(holder.mView.getContext());
                imageView.setImageResource(MyProperties.ICON_MAP.get(iconResource));
                tableRow.addView(imageView);
            }

            TextView textViewLocationName = new TextView(holder.mView.getContext());
            textViewLocationName.setText(location.getName());
            tableRow.addView(textViewLocationName);

            Format formatter = new SimpleDateFormat("dd-MM-yy");
            String readableDate = formatter.format(location.getAddedAt());
            TextView textViewAddedAt = new TextView(holder.mView.getContext());
            textViewAddedAt.setText(String.format(holder.mView.getResources().getString(R.string.added_at), readableDate));
            tableRow.addView(textViewAddedAt);

            tableLayout.addView(tableRow);
        }

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.locationsClickListener(holder.user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView textTitle;
        final TableLayout locationsTable;
        User user;

        ViewHolder(View view) {
            super(view);
            mView = view;
            textTitle = view.findViewById(R.id.textTitle);
            locationsTable = view.findViewById(R.id.locationsTable);
        }
    }
}
