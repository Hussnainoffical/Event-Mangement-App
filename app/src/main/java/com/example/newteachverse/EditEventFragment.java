package com.example.newteachverse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EditEventFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditEventAdapter adapter;
    private List<Event> eventList;
    private FloatingActionButton fab;
    private EventDatabaseHelper dbHelper;
    private static final int REQUEST_IMAGE_PICK = 1001;
    private Uri selectedImageUri;
    private ImageView previewImageView;
    private String copiedImagePath;

    public EditEventFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        fab = view.findViewById(R.id.fab);

        dbHelper = new EventDatabaseHelper(requireContext());
        List<Event> events = dbHelper.getAllEvents();

        adapter = new EditEventAdapter(events, new EditEventAdapter.OnEventActionListener() {
            @Override
            public void onEdit(Event event, int position) {
                showEventDialog(event, position);
            }

            @Override
            public void onDelete(Event event, int position) {
                dbHelper.deleteEvent(event.getId());
                loadEvents();
                Toast.makeText(getContext(), "Deleted: " + event.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Floating Action Button click
        fab.setOnClickListener(v -> showEventDialog(null, -1));
    }

    private void showEventDialog(@Nullable Event eventToEdit, int position) {
        boolean isEditMode = eventToEdit != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_event, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText etName = dialogView.findViewById(R.id.etEventName);
        EditText etDate = dialogView.findViewById(R.id.etEventDate);
        EditText etType = dialogView.findViewById(R.id.etEventType);
        EditText etDesc = dialogView.findViewById(R.id.etEventDescription);
        EditText etLoc = dialogView.findViewById(R.id.etEventLocation);
        Button btnSave = dialogView.findViewById(R.id.btnSaveEvent);
        ImageView ivPreview = dialogView.findViewById(R.id.ivPreview);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);

        this.previewImageView = ivPreview;
        copiedImagePath = null; 

        if (isEditMode) {
            etName.setText(eventToEdit.getName());
            etDate.setText(eventToEdit.getDate());
            etType.setText(eventToEdit.getType());
            etDesc.setText(eventToEdit.getDescription());
            etLoc.setText(eventToEdit.getLocation());
            copiedImagePath = eventToEdit.getImageUri(); 
            if (copiedImagePath != null && !copiedImagePath.isEmpty()) {
                Glide.with(this).load(copiedImagePath).into(ivPreview);
            }
        }

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String loc = etLoc.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || type.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Event event = isEditMode ? eventToEdit : new Event();
            event.setName(name);
            event.setDate(date);
            event.setType(type);
            event.setDescription(desc);
            event.setLocation(loc);

            if (copiedImagePath != null) {
                event.setImageUri(copiedImagePath);
            }

            if (isEditMode) {
                dbHelper.updateEvent(event);
                Toast.makeText(getContext(), "Event Updated", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addEvent(event);
                Toast.makeText(getContext(), "Event Added", Toast.LENGTH_SHORT).show();
            }
            
            dialog.dismiss();
            loadEvents();
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Copy the image to app cache and get the local file path
            copiedImagePath = copyImageToCache(selectedImageUri);
            if (previewImageView != null && copiedImagePath != null) {
                previewImageView.setImageURI(Uri.fromFile(new File(copiedImagePath)));
            }
        }
    }

    private String copyImageToCache(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File tempFile = new File(requireContext().getCacheDir(), "event_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            inputStream.close();
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadEvents() {
        List<Event> events = dbHelper.getAllEvents();
        adapter = new EditEventAdapter(events, new EditEventAdapter.OnEventActionListener() {
            @Override
            public void onEdit(Event event, int position) {
                showEventDialog(event, position);
            }

            @Override
            public void onDelete(Event event, int position) {
                dbHelper.deleteEvent(event.getId());
                loadEvents();
                Toast.makeText(getContext(), "Deleted: " + event.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
