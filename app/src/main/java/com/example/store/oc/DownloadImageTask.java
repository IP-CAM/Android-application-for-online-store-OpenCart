package com.example.store.oc;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Rost on 05.10.2018.
 */
/*Клас для асинхронного завантаження зображення з інтернет ресурсу*/
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage; // де буде відображено завантажене зображення

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    @SuppressLint("LongLogTag")
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0].replace("127.0.0.1","10.0.2.2"); // посилання на зображення
        Bitmap mIcon11 = null; // бітова мапа для зображення
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            Log.e("Bitmap", String.valueOf(mIcon11.getConfig()));
        } catch (Exception e) {

            Log.e("Ошибка передачи изображения", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result); // відображаємо завантажене зображення
    }
}
