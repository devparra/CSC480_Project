package com.CS480.hoa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


public class ConvertImage {

    public static String convertImageToString(Bitmap image){

        return encodeToBase64(image, Bitmap.CompressFormat.PNG, 100);
    }


    //this converts a bitmap for an image into a base64 string
    private static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }


    //this converts a base64 into a drawable
    public static Bitmap convertBase64ToDrawable(String base64String){

        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bitmap;

    }


}
