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
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class Gameplay extends AppCompatActivity {

    ImageView[] imageViewsArray;
    //ImageView imageView;
    Button backButton;
    String mCurrentPhotoPath;
    GridView grid;
    private Integer num_squares;
    Bitmap[] bitmapsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        setup();
        takePhoto();

        // instantiate the Grid of pictures and populate it
        populateGrid();
    }

    private void setup() {
        grid = (GridView) findViewById(R.id.gridView);
        //imageView = (ImageView) findViewById(R.id.imageView);
        backButton = (Button) findViewById(R.id.backButton);

        // Get the message for how many squares to produce
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            num_squares = parseInt(bundle.getString("num_squares"));
        }

        bitmapsArray = new Bitmap[num_squares + 1];
        imageViewsArray = new ImageView[num_squares];
        for (int i = 0; i < num_squares; i++) {
            imageViewsArray[i] = new ImageView(this);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        //int targetW = imageView.getWidth();
        //int targetH = imageView.getHeight();
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

        // This is from https://stackoverflow.com/questions/4754985/android-split-drawable

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int height_inc = bitmap.getHeight() / 3;
        int width_inc = bitmap.getWidth() / 3;

        bitmapsArray[0] = Bitmap.createBitmap(bitmap, 0, 0, width_inc, height_inc);
        bitmapsArray[1] = Bitmap.createBitmap(bitmap, width_inc, 0, width_inc, height_inc);
        bitmapsArray[2] = Bitmap.createBitmap(bitmap, 2*width_inc, 0, width_inc, height_inc);
        bitmapsArray[3] = Bitmap.createBitmap(bitmap, 0, height_inc, width_inc, height_inc);
        bitmapsArray[4] = Bitmap.createBitmap(bitmap, width_inc, height_inc, width_inc, height_inc);
        bitmapsArray[5] = Bitmap.createBitmap(bitmap, 2*width_inc, height_inc, width_inc, height_inc);
        bitmapsArray[6] = Bitmap.createBitmap(bitmap, 0, 2*height_inc, width_inc, height_inc);
        bitmapsArray[7] = Bitmap.createBitmap(bitmap, width_inc, 2*height_inc, width_inc, height_inc);
        bitmapsArray[8] = Bitmap.createBitmap(bitmap, 2*width_inc, 2*height_inc, width_inc, height_inc);


        grid.setAdapter(new
                GridAdapter(this, bitmapsArray)
        );
        grid.setNumColumns(3);


        //imageView.setImageBitmap(bitmapsArray[4]);
    }

    /*
        Methods for chopping up the picture and populating the grid
     */
    private void populateGrid() {
        for (int i=0; i < 9; i++) {
            //grid.addView(imageViewsArray[i]);
        }
    }
}
