package mit.spbau.arptam;

public class PtamSystem {
  private final Map mMap;
  private final long mHandle;
  private final int mWidth;
  private final int mHeight;

  public PtamSystem(int width, int height) {
    mHandle = nCreate(width, height);
    mWidth = width;
    mHeight = height;
    mMap = new Map(nGetMap(mHandle));
  }

  public void processFrame(byte[] data) {
    nProcessFrame(mHandle, mWidth, mHeight, data);
  }

  public void nextTrackingState() {
    nNextTrackingState(mHandle);
  }

  public String getMessage() {
    return nGetMessage(mHandle);
  }

  public void reset() {
    nReset(mHandle);
  }

  public Map getMap() {
    return mMap;
  }

  private static native long nCreate(int width, int height);

  private static native void nProcessFrame(long handle, int width, int height, byte[] data);

  private static native void nNextTrackingState(long handle);

  private static native void nReset(long handle);

  private static native String nGetMessage(long handle);

  private static native long nGetMap(long handle);
}
