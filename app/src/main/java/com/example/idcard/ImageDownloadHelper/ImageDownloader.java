package com.example.idcard.ImageDownloadHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {

    public static void downloadImage(Context context, String imageUrl, String folderName) {
        new DownloadImageTask(context, folderName).execute(imageUrl);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private Context context;
        private String folderName;
        public DownloadImageTask(Context context, String folderName) {
            this.context = context;
            this.folderName = folderName;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                saveImageToExternalStorage(context, bitmap);
            } else {
                Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveImageToExternalStorage(Context context, Bitmap bitmap) {
            try {
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                File file = new File(directory, fileName);
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.flush();
                outputStream.close();

                // Refresh Media Scanner to make the image visible
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);

                Toast.makeText(context, "Images saved in Picture Folder", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
