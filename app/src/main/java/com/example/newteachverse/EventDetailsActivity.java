package com.example.newteachverse;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class EventDetailsActivity extends AppCompatActivity {

    TextView tvName, tvDate, tvType, tvLocation, tvDescription;
    ImageView ivEventImage;
    Button btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        tvName = findViewById(R.id.tvDetailEventName);
        tvDate = findViewById(R.id.tvDetailEventDate);
        tvType = findViewById(R.id.tvDetailEventType);
        tvLocation = findViewById(R.id.tvDetailEventLocation);
        tvDescription = findViewById(R.id.tvDetailEventDescription);
        ivEventImage = findViewById(R.id.ivDetailEventImage);
        btnJoin = findViewById(R.id.btnJoinEvent);

        Event event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            tvName.setText(event.getName());
            tvDate.setText(event.getDate());
            tvType.setText(event.getType());
            tvLocation.setText(event.getLocation());
            tvDescription.setText(event.getDescription());
            if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
                ivEventImage.setImageURI(Uri.parse(event.getImageUri()));
            } else {
                ivEventImage.setImageResource(R.drawable.bg_event_image_placeholder);
            }
        }

        if (event != null && event.isJoined()) {
            btnJoin.setText("Joined");
            btnJoin.setEnabled(false);
            btnJoin.setBackgroundColor(Color.GRAY);
        }

        btnJoin.setOnClickListener(v -> {
            if (event != null && !event.isJoined()) {
                // Get studentId from SharedPreferences
                SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                int studentId = prefs.getInt("studentId", -1);
                EventDatabaseHelper dbHelper = new EventDatabaseHelper(this);
                long result = dbHelper.joinEvent(studentId, event.getId());
                if (result != -1) {
                    event.setJoined(true);
                    btnJoin.setText("Joined");
                    btnJoin.setEnabled(false);
                    btnJoin.setBackgroundColor(Color.GRAY);
                    Toast.makeText(this, "Event Joined", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Already Joined", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
