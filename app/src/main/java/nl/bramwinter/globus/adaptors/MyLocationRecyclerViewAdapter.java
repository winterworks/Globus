package nl.bramwinter.globus.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.fragments.MyLocationsFragment;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.util.MyProperties;

public class MyLocationRecyclerViewAdapter extends RecyclerView.Adapter<MyLocationRecyclerViewAdapter.ViewHolder> {

    private final List<Location> locations;
    private final MyLocationsFragment.MyLocationsFragmentListener mListener;

    public MyLocationRecyclerViewAdapter(List<Location> items, MyLocationsFragment.MyLocationsFragmentListener listener) {
        locations = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_locations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.location = locations.get(position);
        String titleText = locations.get(position).getName();
        holder.textTitle.setText(titleText);

        Integer iconResource = locations.get(position).getIcon();
        if (iconResource != null) {
            holder.imageViewIcon.setImageResource(MyProperties.iconMap.get(iconResource));
        }

        Format formatter = new SimpleDateFormat("dd-MM-yy");
        String readableDate = formatter.format(locations.get(position).getAddedAt());
        holder.textUserMovedDate.setText(String.format(holder.mView.getResources().getString(R.string.added_add), readableDate));

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.MyLocationsClickListener(holder.location);
            }
        });

        holder.mView.setOnLongClickListener(view -> {
            if (null != mListener) {
                mListener.MyLocationsPressListener(holder.location);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView textTitle;
        final TextView textUserMovedDate;
        final ImageView imageViewIcon;
        Location location;

        ViewHolder(View view) {
            super(view);
            mView = view;
            textTitle = view.findViewById(R.id.textTitle);
            textUserMovedDate = view.findViewById(R.id.textAddedAtDate);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
        }
    }

}
