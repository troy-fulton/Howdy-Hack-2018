package edu.tamu.howdyhacklocal;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button cameraButton;
    Button twentyfour_squares, fifteen_squares, eight_squares;
    Integer num_squares = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = (Button) findViewById(R.id.cameraButton);
        fifteen_squares = (Button) findViewById(R.id.fifteen_squares);
        twentyfour_squares = (Button) findViewById(R.id.twentyfour_squares);
        eight_squares = (Button) findViewById(R.id.eight_squares);

        cameraButton.setVisibility(View.GONE);
        setListeners();
    }

    private void setListeners() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        eight_squares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_selection(8);
            }
        });

        fifteen_squares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_selection(15);
            }
        });

        twentyfour_squares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_selection(24);
            }
        });
    }

    private void change_selection(int selection) {
        if (num_squares == 0) cameraButton.setVisibility(View.VISIBLE);
        num_squares = selection;
        if (selection == 8) {
            eight_squares.setTextColor(Color.BLUE);
            fifteen_squares.setTextColor(Color.WHITE);
            twentyfour_squares.setTextColor(Color.WHITE);
        }
        else if (selection == 15) {
            eight_squares.setTextColor(Color.WHITE);
            fifteen_squares.setTextColor(Color.BLUE);
            twentyfour_squares.setTextColor(Color.WHITE);
        }
        else if (selection == 24) {
            eight_squares.setTextColor(Color.WHITE);
            fifteen_squares.setTextColor(Color.WHITE);
            twentyfour_squares.setTextColor(Color.BLUE);
        }
    }

    private Boolean checkSelection() {
        return num_squares == 8 || num_squares == 15 || num_squares == 24;
    }

    private void dispatchTakePictureIntent() {
        // Don't let the user continue if they haven't made a selection yet
        if (!checkSelection()) return;

        Intent game_intent = new Intent(this, Gameplay.class);
        game_intent.putExtra("num_squares", num_squares.toString());
        startActivity(game_intent);
    }
}


