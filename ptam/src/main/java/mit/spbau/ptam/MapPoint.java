package mit.spbau.ptam;

public class MapPoint {
  private final long mHandle;
  private final float[] imagePos = new float[2];
  private final float[] worldPos = new float[3];
  private final float[] localPos = new float[3];

  public MapPoint(long handle) {
    this.mHandle = handle;
  }

  public float[] getLastImagePos() {
    nGetImagePos(mHandle, imagePos);
    return imagePos;
  }

  public float[] getWorldPos() {
    nGetWorldPos(mHandle, worldPos);
    return worldPos;
  }

  public float[] getLocalPos() {
    nGetLocalPos(mHandle, localPos);
    return localPos;
  }

  public boolean isTracked() {
    return nIsTracked(mHandle);
  }

  private static native void nGetImagePos(long handle, float[] imagePos);

  private static native void nGetWorldPos(long handle, float[] worldPos);

  private static native void nGetLocalPos(long handle, float[] localPos);

  private static native boolean nIsTracked(long handle);
}
