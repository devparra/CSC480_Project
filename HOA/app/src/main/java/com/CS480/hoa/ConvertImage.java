package com.CS480.hoa;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


public class ConvertImage {

    public static String convertImageToString(Drawable image){

        BitmapDrawable drawable = (BitmapDrawable) image;
        Bitmap bitmap = drawable.getBitmap();
        return encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
    }


    //this converts a bitmap for an image into a base64 string
    private static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }


}
