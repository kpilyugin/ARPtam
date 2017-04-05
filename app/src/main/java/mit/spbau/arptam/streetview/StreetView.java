package mit.spbau.arptam.streetview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.*;
import java.net.URL;

public class StreetView {
  public static void saveImageToDisk(String folder) {
    new MyAsyncTask().execute(folder);
  }

  private static class MyAsyncTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
      System.out.println("MyAsyncTask.doInBackground");
      try {
        String imageUrl = new ImageUrlBuilder().build();
        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
        try (FileOutputStream fos = new FileOutputStream(new File(params[0], "image.png"))) {
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
          System.out.println("Saved image to folder");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void v) {
      System.out.println("MyAsyncTask.onPostExecute");
    }
  }
}
