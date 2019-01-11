package edu.tamu.howdyhacklocal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class Gameplay extends AppCompatActivity {

    ImageView[] imageViewsArray;
    //ImageView imageView;
    Button backButton;
    String mCurrentPhotoPath;
    GridView grid;
    private Integer num_squares, dimension, blankSpot;
    Bitmap[] bitmapArray, solution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        setup();
        takePhoto();
    }

    private void setup() {
        //imageView = (ImageView) findViewById(R.id.imageView);

        // Get the grid and button:
        grid = (GridView) findViewById(R.id.gridView);
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        backButton.setVisibility(View.GONE);

        // Get the message for how many squares to produce
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            num_squares = parseInt(bundle.getString("num_squares"));
            dimension = (int) Math.sqrt(num_squares+1);
        }

        bitmapArray = new Bitmap[num_squares + 1];
        solution = new Bitmap[num_squares + 1];

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                swap(i);
            }
        });
    }

    // Our new methods:
    public void swap(int i) {
        // Test for valid moves:
        Boolean isValid = true;
        isValid = i == (blankSpot - dimension);
        isValid |= i == (blankSpot + dimension);
        isValid |= i == (blankSpot - 1) && (blankSpot % dimension) != 0;
        isValid |= i == (blankSpot + 1) && (blankSpot % dimension) != (dimension - 1);
        if (!isValid) return;

        // Swap the pictures and reset the adapter:
        Bitmap temp = bitmapArray[i];
        bitmapArray[i] = bitmapArray[blankSpot];
        bitmapArray[blankSpot] = temp;
        blankSpot = i;
        grid.setAdapter(new
                GridAdapter(this, bitmapArray)
        );

        // Show the exit button when it is solved
        Boolean isSolved = true;
        for (int j = 0; j < bitmapArray.length; j++) {
            if (solution[j] != bitmapArray[j]) {
                isSolved = false;
                break;
            }
        }
        if (isSolved) {
            backButton.setVisibility(View.VISIBLE);
        }
    }

    /*
        Methods for taking the photo and storing it as a file...
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                System.out.print("Getting photoURI");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "edu.tamu.howdyhacklocal.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 0);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the dimensions of the View
        int targetW = grid.getWidth();
        int targetH = grid.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        // Get the amount to increment for each picture
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int height_inc = bitmap.getHeight() / dimension;
        int width_inc = bitmap.getWidth() / dimension;

        // Randomly pick the blank spot
        blankSpot = new Random().nextInt(num_squares + 2);

        // Create the pieces of the bitmap
        int bmpCount = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                // For each picture, use these bounds to cut the pictures
                // This is from https://stackoverflow.com/questions/4754985/android-split-drawable
                if (bmpCount == blankSpot)
                    bitmapArray[bmpCount] = Bitmap.createBitmap(width_inc, height_inc, Bitmap.Config.ARGB_8888);
                else
                    bitmapArray[bmpCount] = Bitmap.createBitmap(bitmap,
                        j*width_inc, i*height_inc, width_inc, height_inc);
                bmpCount++;
            }
        }

        // Copy the solution before shuffling
        for (int i = 0; i < bitmapArray.length; i++) {
            solution[i] = bitmapArray[i];
        }

        // Shuffle
        Collections.shuffle(Arrays.asList(bitmapArray));

        // Find the blank tile in the new shuffled mess
        for (int i = 0; i < bitmapArray.length; i++) {
            if (bitmapArray[i] == solution[blankSpot]) {
                blankSpot = i;
                break;
            }
        }

        // Set the scrambled order as the new adapter
        grid.setAdapter(new
                GridAdapter(this, bitmapArray)
        );
        grid.setNumColumns(dimension);

        //imageView.setImageBitmap(bitmapArray[4]);
    }
}
