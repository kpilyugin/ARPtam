package mit.spbau.ptam.demo;

import android.os.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PtamStatistics {
  private static final String HEADER = "Frame, Number of points, Number of keyframes, Tracking time, Tracking quality";

  private int frame = 0;
  private final List<Stat> stats = new ArrayList<>();

  public void onNewFrame(int numPoints, int numKeyFrames, double trackingTime, float trackingQuality) {
    stats.add(new Stat(frame, numPoints, numKeyFrames, trackingTime, trackingQuality));
    frame++;
  }

  public void saveToFile() {
    File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "PtamStats");
    if (!folder.exists()) {
      //noinspection ResultOfMethodCallIgnored
      folder.mkdirs();
    }

    String name = "stats_" + new Date().toString() + ".txt";
    File file = new File(folder, name);
    try (PrintWriter writer = new PrintWriter(file)) {
      writer.println(HEADER);
      for (Stat stat : stats) {
        writer.println(stat);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Saved stats to file " + file.getAbsolutePath());
    stats.clear();
  }

  private static class Stat {
    private final int frame;
    private final int numPoints;
    private final int numKeyFrames;
    private final double trackingTime;
    private final float trackingQuality;

    public Stat(int frame, int numPoints, int numKeyFrames, double trackingTime, float trackingQuality) {
      this.trackingQuality = trackingQuality;
      this.frame = frame;
      this.numPoints = numPoints;
      this.numKeyFrames = numKeyFrames;
      this.trackingTime = trackingTime;
    }

    @Override
    public String toString() {
      return frame + ": " + numPoints + " " + numKeyFrames + " " + trackingTime + " " + trackingQuality;
    }
  }
}
