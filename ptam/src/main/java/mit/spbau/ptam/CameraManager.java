package mit.spbau.ptam;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraManager implements Camera.PreviewCallback {
  private Camera mCamera;
  private byte[][] mPreviewBuffers;

  public CameraManager(SurfaceTexture surfaceTexture) throws IOException {
    startCamera(surfaceTexture);
  }

  public void onPause() {
    mCamera.stopPreview();
  }

  private int getBackFacingCamera() {
    int numberOfCameras = Camera.getNumberOfCameras();

    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    for (int i = 0; i < numberOfCameras; i++) {
      Camera.getCameraInfo(i, cameraInfo);
      if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
        return i;
      }
    }
    Log.w("Camera", "No back facing mCamera.");
    return 0;
  }

  private void startCamera(SurfaceTexture surfaceTexture) throws IOException {
    int camId = getBackFacingCamera();

    mCamera = Camera.open(camId);

    if (mCamera == null) {
      throw new RuntimeException("Could not open camera.");
    }

    //debug
    Camera.Parameters camparams = mCamera.getParameters();
    Log.i("Camera", "Camera params:" + camparams.flatten());

    List<Integer> supportedPreviewFormats = camparams.getSupportedPreviewFormats();
    System.out.println("supportedPreviewFormats = " + supportedPreviewFormats);
    int imageFormat = ImageFormat.NV21; //should be supported on almost any device

    //find fastest supported framerate
    List<int[]> supportedFPSRanges = camparams.getSupportedPreviewFpsRange();
    int[] fpsRange = supportedFPSRanges.get(supportedFPSRanges.size() - 1);

    int pwidth = 640;
    int pheight = 480;

    Log.i("Camera", "Best FPS range: " + fpsRange[0] + "-" + fpsRange[1]);
    Log.i("Camera", "Size: " + pwidth + "x" + pheight);

    camparams.setPreviewSize(pwidth, pheight);
    camparams.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
    camparams.setPreviewFormat(imageFormat);

    if (camparams.isVideoStabilizationSupported())
      camparams.setVideoStabilization(false);

    if (camparams.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
      camparams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    } else {
      Log.w("Camera", "FOCUS_MODE_CONTINUOUS_VIDEO not supported");
    }
    camparams.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);

    if (!camparams.isAutoExposureLockSupported())
      Log.w("Camera", "No auto exposure lock!");

    if (camparams.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_OFF))
      camparams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

    //set parameters
    mCamera.setDisplayOrientation(90);
    mCamera.setParameters(camparams);

    //create buffers for mCamera frames
    Camera.Size previewFrameSize = mCamera.getParameters().getPreviewSize();
    Log.i("actual size: ", "" + previewFrameSize.width + "x" + previewFrameSize.height);

    int bPP = ImageFormat.getBitsPerPixel(imageFormat);
    int bufferSize = (int) (previewFrameSize.height * previewFrameSize.width * ((double) bPP / 8.0) * 1.5);

    mPreviewBuffers = new byte[2][bufferSize];
    resume(surfaceTexture);
  }

  public void resume(SurfaceTexture surfaceTexture) throws IOException {
    mCamera.addCallbackBuffer(mPreviewBuffers[0]);
    mCamera.setPreviewCallbackWithBuffer(this);
    mCamera.setPreviewTexture(surfaceTexture);
    mCamera.startPreview();
  }

  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
    camera.addCallbackBuffer(data == mPreviewBuffers[0] ? mPreviewBuffers[1] : mPreviewBuffers[0]);
    processFrame(data);
  }

  protected void processFrame(byte[] data) {
  }
}
