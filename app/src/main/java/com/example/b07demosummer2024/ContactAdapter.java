package com.example.b07demosummer2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<ContactInfo> contactList;
    private OnItemClickListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    // Define custom listener for clicks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ContactAdapter(Context context, List<ContactInfo> contactList, OnItemClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.listener = listener;
    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, relationship, phone, address;

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
