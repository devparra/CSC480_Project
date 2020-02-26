package com.CS480.hoa;

import android.graphics.Bitmap;
import android.os.FileUtils;
import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ConvertImage {


    public static String convertImageToString(Bitmap image){

       return encodeToBase64(image, Bitmap.CompressFormat.JPEG, 70);

    }


    public static String convertFileToString(File file){

        return encodeToBase64(file);
    }



    private static String encodeToBase64(File file){

        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();

            int nextByte = fileInputStream.read();
            while(nextByte != -1){
                byteArrayOS.write(nextByte);
                nextByte = fileInputStream.read();
            }

            String base64String = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);


            return base64String;

        }catch(IOException error) {
            System.out.println("Error in converting file*************************************************");
            System.out.println(error.getMessage());
        }

        return null;
    }


    //this converts a bitmap for an image into a base64 string
    private static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {


        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);


    }

}
