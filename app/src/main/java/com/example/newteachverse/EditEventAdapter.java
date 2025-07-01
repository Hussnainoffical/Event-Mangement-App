package com.example.newteachverse;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EditEventAdapter extends RecyclerView.Adapter<EditEventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private final OnEventActionListener listener;

    // Interface for edit/delete actions
    public interface OnEventActionListener {
        void onEdit(Event event, int position);
        void onDelete(Event event, int position);
    }

    public EditEventAdapter(List<Event> events, OnEventActionListener listener) {
        this.eventList = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvName.setText(event.getName());
        holder.tvDate.setText(event.getDate());
        holder.tvType.setText(event.getType());

        // Load image using Glide
        try {
            Glide.with(holder.itemView.getContext())
                .load(event.getImageUri())
                .placeholder(R.drawable.bg_event_image_placeholder)
                .error(R.drawable.bg_event_image_placeholder)
                .into(holder.ivEventImage);
        } catch (Exception e) {
            Log.e("EditEventAdapter", "Image load failed", e);
            holder.ivEventImage.setImageResource(R.drawable.bg_event_image_placeholder);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(event, position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(event, position);
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public void updateList(List<Event> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvType;
        Button btnEdit, btnDelete;
        ImageView ivEventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvType = itemView.findViewById(R.id.tvEventType);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
