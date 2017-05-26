package mit.spbau.arptam;

import delightex.math.*;

public class MapPoint {
  private final long mHandle;
  private final float[] imagePos = new float[2];
  private final float[] worldPos = new float[3];
  private final float[] localPos = new float[3];
  private final Vec2f mImagePos = new Vec2f();
  private final Vec3f mWorldPos = new Vec3f();
  private final Vec3f mLocalPos = new Vec3f();
  private final Vec4f color = Color.randColor();

  public MapPoint(long handle) {
    this.mHandle = handle;
  }

  public Vec2f getLastImagePos() {
    nGetImagePos(mHandle, imagePos);
    return mImagePos.set(imagePos[0], imagePos[1]);
  }

  public Vec3f getWorldPos() {
    nGetWorldPos(mHandle, worldPos);
    return mWorldPos.set(worldPos[0], worldPos[1], worldPos[2]);
  }

  public Vec3f getLocalPos() {
    nGetLocalPos(mHandle, localPos);
    return mLocalPos.set(localPos[0], localPos[1], localPos[2]);
  }

  public Vec4f getColor() {
    return color;
  }

  public boolean isTracked() {
    return nIsTracked(mHandle);
  }

  private static native void nGetImagePos(long handle, float[] imagePos);

  private static native void nGetWorldPos(long handle, float[] worldPos);

  private static native void nGetLocalPos(long handle, float[] localPos);

  private static native boolean nIsTracked(long handle);
}
