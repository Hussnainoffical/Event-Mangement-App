package com.example.newteachverse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.example.newteachverse.Event;

public class StudentEventAdapter extends RecyclerView.Adapter<StudentEventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private final Context context;
    private int studentId;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
    private final OnEventClickListener eventClickListener;

    public StudentEventAdapter(List<Event> events, Context context, int studentId, OnEventClickListener eventClickListener) {
        this.context = context;
        this.eventList = events;
        this.studentId = studentId;
        this.eventClickListener = eventClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvName.setText(event.getName());
        holder.tvDate.setText(event.getDate());
        holder.tvType.setText(event.getType());

        // Load event image using Glide
        try {
            Glide.with(context)
                .load(event.getImageUri())
                .placeholder(R.drawable.bg_event_image_placeholder) // use your placeholder drawable
                .error(R.drawable.bg_event_image_placeholder) // use your error drawable or a different one if you have
                .into(holder.ivEventImage);
        } catch (Exception e) {
            Log.e("StudentEventAdapter", "Image load failed", e);
            holder.ivEventImage.setImageResource(R.drawable.bg_event_image_placeholder);
        }

        // Handle click to open event details
        holder.itemView.setOnClickListener(v -> {
            if (eventClickListener != null) eventClickListener.onEventClick(event);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateList(List<Event> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
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
