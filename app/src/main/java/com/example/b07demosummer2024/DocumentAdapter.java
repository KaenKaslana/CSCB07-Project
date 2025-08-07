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
 * Adapter for displaying a list of documents in a RecyclerView.
 * 
 * Highlights the selected document and notifies a listener when an
 * item is clicked.
 */
public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    /** Application context for inflating views. */
    private Context context;

    /** List of DocumentInfo objects to display. */
    private List<DocumentInfo> documentList;

    /** Listener for click events on document items. */
    private OnItemClickListener listener;

    /** Track which item is selected. */
    private int selectedPosition = RecyclerView.NO_POSITION;

    /**
     * Interface definition for a callback to be invoked when a document
     * item is clicked.
     */
    public interface OnItemClickListener {
        /**
         * Called when a document item has been clicked.
         *
         * @param position The position of the clicked item in the list.
         */
        void onItemClick(int position);
    }

    /**
     * Constructs a new DocumentAdapter.
     *
     * @param context      the context in which the adapter is running
     * @param documentList the list of DocumentInfo objects to display
     * @param listener     the listener to notify on item clicks
     */
    public DocumentAdapter(Context context, List<DocumentInfo> documentList, OnItemClickListener listener) {
        this.context = context;
        this.documentList = documentList;
        this.listener = listener;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type
     * to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new ViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * Binds the document name and applies highlight if selected.
     *
     * @param holder   The ViewHolder which should be updated
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentInfo document = documentList.get(position);
        holder.fileName.setText(document.name);

        // Apply highlighting based on selection
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.contact_selected_border);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.contact_normal_border);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged(); // Refresh to update highlight
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of items
     */
    @Override
    public int getItemCount() {
        return documentList.size();
    }

    /**
     * Updates the selected position and refreshes the view to reflect highlight.
     *
     * @param position The new selected position
     */
    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for document items.
     * Holds references to the UI components for a single item view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /** TextView displaying the document's name. */
        TextView fileName;

        /**
         * Creates a new ViewHolder and finds its subviews.
         *
         * @param itemView The root view of the document item layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.documentName);
        }
    }
}



