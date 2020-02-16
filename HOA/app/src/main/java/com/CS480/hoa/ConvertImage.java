package com.CS480.hoa;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


public class ConvertImage {


    public static String convertImageToString(Bitmap image){

        return encodeToBase64(image, Bitmap.CompressFormat.JPEG, 75);
    }


    //this converts a bitmap for an image into a base64 string
    private static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }

}
