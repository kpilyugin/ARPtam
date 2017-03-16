package mit.spbau.arptam;

public class ARSystem {
  private final long mHandle;
  private final int mWidth;
  private final int mHeight;

  public ARSystem(int width, int height) {
    mHandle = nCreate();
    mWidth = width;
    mHeight = height;
  }

  public void processFrame(byte[] data) {
    nProcessFrame(mHandle, mWidth, mHeight, data);
  }

  public TrackingState getTrackingState() {
    int state = nGetTrackingState(mHandle);
    return TrackingState.values()[state];
  }

  public void nextTrackingState() {
    nNextTrackingState(mHandle);
  }

  public void renderTrackingInfo() {
    nRenderTrackingInfo(mHandle);
  }

  public String getTrackingInfo() {
    return nGetTrackingInfo(mHandle);
  }

  private static native long nCreate();

  private static native void nProcessFrame(long handle, int width, int height, byte[] data);

  private static native int nGetTrackingState(long handle);

  private static native void nNextTrackingState(long handle);

  private static native void nRenderTrackingInfo(long handle);

  private static native String nGetTrackingInfo(long handle);
}
