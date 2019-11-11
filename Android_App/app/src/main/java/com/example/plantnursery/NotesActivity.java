package com.example.plantnursery;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotesActivity extends AppCompatActivity {

    ImageButton sendNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotes);

        sendNotes = findViewById(R.id.imageButton2);

        sendNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            //when clicked everything in this will get executed
            public void onClick(View v) {
                Toast.makeText(NotesActivity.this, "Clicked!!", Toast.LENGTH_SHORT).show();//mini b_notification
                //startActivity(new Intent(Notes.this, Notifications.class));
            }
        });

    }
}