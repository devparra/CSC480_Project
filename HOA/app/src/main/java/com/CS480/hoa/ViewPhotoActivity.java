package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileInputStream;

public class ViewPhotoActivity extends AppCompatActivity {

    public static final String photoCode = "com.CS480.hoa.viewPhoto";

    private Bitmap[] photoList;
    private ImageView photoImageView;
    private int index;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        photoImageView = findViewById(R.id.viewPhotoImageView);

        int numberOfPhotos = getIntent().getIntExtra("totalPhotos", 0);

        photoList = new Bitmap[numberOfPhotos];

        String[] filenames = new String[numberOfPhotos];

        for (int i = 0; i < numberOfPhotos; i++) {

            //get filename from the intent
            filenames[i] = getIntent().getStringExtra("filename" + i);

            Bitmap bmp = null;

            try {
                FileInputStream is = getBaseContext().openFileInput(filenames[i]);
                bmp = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            photoList[i] = bmp;

        }

        index = 0;

        if (photoList[index] != null) {
            photoImageView.setImageBitmap(photoList[index]);
        }


        //add on touch listener to allowing scrolling of images
        photoImageView.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            @Override
            public void onSwipeLeft() {

                //test for multiple photos
                if (photoList.length > 1) {

                    //if we are at the end, go to the beginning
                    if (index == photoList.length - 1) {
                        index = 0;
                        photoImageView.setImageBitmap(photoList[index]);
                    } else {
                        //we are not at the end, go to next photo
                        index++;
                        photoImageView.setImageBitmap(photoList[index]);
                    }
                }

            }//end of onSwipeLeft

            @Override
            public void onSwipeRight() {

                //test for multiple photos
                if (photoList.length > 1) {

                    //if we are at the beginning, go to the end
                    if (index == 0) {
                        index = photoList.length - 1;
                        photoImageView.setImageBitmap(photoList[index]);
                    } else {
                        //we are not at the beginning, go to previous photo
                        index--;
                        photoImageView.setImageBitmap(photoList[index]);
                    }
                }

            }//end of onSwipeRight
        });

    }//end of onCreate

}