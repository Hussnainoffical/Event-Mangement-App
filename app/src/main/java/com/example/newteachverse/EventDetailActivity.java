package com.example.newteachverse;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EventDetailActivity extends AppCompatActivity {

    TextView tvName, tvDate, tvLocation, tvType, tvDescription;
    ListView lvJoinedStudents;
    ImageView ivEventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        tvName = findViewById(R.id.tvEventName);
        tvDate = findViewById(R.id.tvEventDate);
        tvLocation = findViewById(R.id.tvEventLocation);
        tvType = findViewById(R.id.tvEventType);
        tvDescription = findViewById(R.id.tvEventDescription);
        lvJoinedStudents = findViewById(R.id.lvJoinedStudents);
        ivEventImage = findViewById(R.id.ivEventImage);

        Event event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            tvName.setText(event.getName());
            tvDate.setText(event.getDate());
            tvLocation.setText(event.getLocation());
            tvType.setText(event.getType());
            tvDescription.setText(event.getDescription());

            if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
                ivEventImage.setImageURI(Uri.parse(event.getImageUri()));
            } else {
                ivEventImage.setImageResource(R.drawable.bg_event_image_placeholder);
            }

            EventDatabaseHelper dbHelper = new EventDatabaseHelper(this);
            List<String> studentNames = dbHelper.getStudentNamesByEvent(event.getId());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, studentNames
            );
            lvJoinedStudents.setAdapter(adapter);
        }
    }
} 