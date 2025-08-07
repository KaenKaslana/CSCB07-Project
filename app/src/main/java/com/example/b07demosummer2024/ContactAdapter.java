package com.example.b07demosummer2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * {ContactAdapter} binds a list of {ContactInfo} objects to a RecyclerView,
 * displaying each contact’s details and supporting item selection highlighting
 * and click events via a custom listener.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    /** The Context in which this adapter is operating. */
    private Context context;

    /** The list of contacts to display. */
    private List<ContactInfo> contactList;

    /** Listener for item click callbacks. */
    private OnItemClickListener listener;

    /** Tracks the currently selected item position; NO_POSITION if none. */
    private int selectedPosition = RecyclerView.NO_POSITION;

    /**
     * Updates which item is highlighted as selected.  Notifies the adapter to
     * redraw the previously selected and newly selected items.
     *
     * @param position adapter position to mark as selected
     */
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    /**
     * Defines a callback interface to notify when a contact item is clicked.
     */
    public interface OnItemClickListener {
       /**
         * Called when a contact item at the given position is clicked.
         *
         * @param position the adapter position of the clicked item
         */
        void onItemClick(int position);
    }

    /**
     * Constructs a new {@link ContactAdapter}.
     *
     * @param context      the Activity or fragment context
     * @param contactList  the list of contacts to display
     * @param listener     callback to invoke on item clicks
     */
    public ContactAdapter(Context context, List<ContactInfo> contactList, OnItemClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.listener = listener;
    }

    /**
     * Binds data from a {@link ContactInfo} into the item view.
     * Also applies selection highlighting and installs a click listener.
     *
     * @param holder   the ViewHolder containing item views
     * @param position adapter position of the item being bound
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactInfo contact = contactList.get(position);
        holder.name.setText("Name: " + contact.getName());
        holder.relationship.setText("Relationship: " + contact.getRelationship());
        holder.phone.setText("Phone: " + contact.getPhone());
        holder.address.setText("Address: " + contact.getAddress());

        // Highlight selected contact
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.contact_selected_border);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.contact_normal_border);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    /**
     * Inflates the item view and creates a {@link ViewHolder}.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return item count
     */
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    /**
     * {ViewHolder} holds and caches the item view’s subviews,
     * and wires up the click listener to notify the adapter’s callback.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, relationship, phone, address;

        /**
         * Initializes references to item subviews and installs click handling.
         *
         * @param itemView the root view of this item
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            relationship = itemView.findViewById(R.id.contactRelationship);
            phone = itemView.findViewById(R.id.contactPhone);
            address = itemView.findViewById(R.id.contactAddress);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
