package mit.spbau.arptam;

import java.util.HashMap;

public class Map {
  private final long mHandle;
  private final HashMap<Long, MapPoint> pointsMap = new HashMap<>();

  public Map(long handle) {
    mHandle = handle;
  }

  public int getNumPoints() {
    return nGetNumPoints(mHandle);
  }

  public MapPoint getPoint(int i) {
    long handle = nGetMapPoint(mHandle, i);
    MapPoint point = pointsMap.get(handle);
    if (point == null) {
      point = new MapPoint(handle);
      pointsMap.put(handle, point);
    }
    return point;
  }

  private static native int nGetNumPoints(long handle);

  private static native long nGetMapPoint(long handle, int i);
}
