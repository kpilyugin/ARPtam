package mit.spbau.ptam;

public class PtamSystem {
  private final Map mMap;
  private final long mHandle;
  private final int mWidth;
  private final int mHeight;
  private final float[] mRotation = new float[9];
  private final float[] mPosition = new float[3];
  private long mRenderer;

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

  public void saveMap(String fileName) {
    nSaveMap(mHandle, fileName);
  }

  public void loadMap(String fileName) {
    nLoadMap(mHandle, fileName);
  }

  public Map getMap() {
    return mMap;
  }

  public void renderTrackingInfo() {
    if (mRenderer == 0) {
      mRenderer = nInitRenderer();
    }
    nRenderTrackingInfo(mHandle, mRenderer);
  }

  public float[] getRotation() {
    nGetRotation(mHandle, mRotation);
    return mRotation;
  }

  public float[] getPosition() {
    nGetPosition(mHandle, mPosition);
    return mPosition;
  }

  private static native long nCreate(int width, int height);

  private static native void nProcessFrame(long handle, int width, int height, byte[] data);

  private static native void nNextTrackingState(long handle);

  private static native void nReset(long handle);

  private static native void nSaveMap(long handle, String fileName);

  private static native void nLoadMap(long handle, String fileName);

  private static native String nGetMessage(long handle);

  private static native long nGetMap(long handle);

  private static native long nInitRenderer();

  private static native void nRenderTrackingInfo(long system, long renderer);

  private static native void nGetRotation(long handle, float[] rotation);

  private static native void nGetPosition(long handle, float[] translation);

  static {
    System.loadLibrary("PTAM");
  }
}
