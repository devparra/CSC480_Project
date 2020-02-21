package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ViewPhotoActivity extends AppCompatActivity {

    public static final String photoCode = "com.CS480.hoa.viewPhoto";

    private Bitmap[] photoList;
    private String[] urlList;
    private ImageView photoImageView;
    private TextView loadingImage;
    private int index = 0;

    private AsyncTask<Void, Void, Bitmap> getPhoto;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        photoImageView = findViewById(R.id.viewPhotoImageView);
        loadingImage = findViewById(R.id.viewPhotoLoadingImage);

        urlList = getIntent().getStringArrayExtra(photoCode);

        photoList = new Bitmap[urlList.length];

        for (int i = 0; i < urlList.length; i++) {

           getPhoto = new GetPhoto(urlList[i]);
           getPhoto.execute();

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






    class GetPhoto extends AsyncTask<Void, Void, Bitmap> {

        private String url;

        public GetPhoto(String url){
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            Bitmap image = null;

            try {
                InputStream input = (InputStream) new URL(url).getContent();
                //Drawable temp = Drawable.createFromStream(input,"testPhoto");

                image = BitmapFactory.decodeStream(input);

                return image;

            }catch(MalformedURLException error){
                System.out.println("MalformedURLException");
                System.out.println(error.getMessage());
            }catch(IOException error){
                System.out.println("IOException");
                System.out.println(error.getMessage());
            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);

            for(int i = 0; i < photoList.length; i++){
                if(photoList[i] == null){
                    if(i == 0){
                        //this is the first image, display it
                        loadingImage.setVisibility(View.GONE);
                         photoImageView.setImageBitmap(image);
                    }

                    //add image to the list
                    photoList[i] = image;
                    break;
                }
            }

        }//end onPostExecute

    }//end class GetPhoto

}