package com.example.b07demosummer2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.DocumentInfo;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private Context context;
    private List<DocumentInfo> documentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DocumentAdapter(Context context, List<DocumentInfo> documentList, OnItemClickListener listener) {
        this.context = context;
        this.documentList = documentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentInfo doc = documentList.get(position);
        holder.fileName.setText(doc.getName());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.documentName);
        }
    }
}


