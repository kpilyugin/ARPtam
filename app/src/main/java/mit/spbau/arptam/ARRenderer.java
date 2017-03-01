package mit.spbau.arptam;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import mit.spbau.arptam.graphics.ScreenMesh;
import mit.spbau.arptam.graphics.Shader;
import mit.spbau.arptam.graphics.ShaderTexture;

import static android.opengl.GLES20.*;

public class ARRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

  private int textureId;
  private ScreenMesh mesh;
  private Shader shader;

  private SurfaceTexture mSTexture;

  private volatile boolean mUpdateST = false;

  private final GLSurfaceView mView;
  private final ARSystem mARSystem;
  private final Gyroscope mGyroscope;
  private final Geolocation mGeolocation;

  private CameraManager mCamera;

  ARRenderer(Context context, GLSurfaceView view, ARSystem arSystem) {
    mView = view;
    mARSystem = arSystem;
    mGyroscope = new Gyroscope(context);
    mGeolocation = new Geolocation(context);
  }

  public void onResume() {
    mUpdateST = true;
    mGeolocation.onResume();
    if (mCamera != null) {
      mCamera.onResume();
    }
  }

  public void onPause() {
    mUpdateST = false;
    mGeolocation.onPause();
    if (mCamera != null) {
      mCamera.onPause();
    }
  }

  public void onDestroy() {
    mGyroscope.stop();
  }

  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    textureId = initTexture();
    mSTexture = new SurfaceTexture(textureId);
    try {
      mCamera = new CameraManager(mSTexture) {
        @Override
        protected void processFrame(byte[] data) {
          mARSystem.processFrame(data);
          mView.requestRender();
        }
      };
    } catch (IOException e) {
      e.printStackTrace();
    }
    mSTexture.setOnFrameAvailableListener(this);

    Point ss = new Point();
    mView.getDisplay().getRealSize(ss);
    float ratio = (float) (Math.min(ss.x, ss.y) * ARActivity.PREVIEW_SIZE.getWidth()) /
        (Math.max(ss.x, ss.y) * ARActivity.PREVIEW_SIZE.getHeight());
    System.out.println("ratio = " + ratio);
    mesh = new ScreenMesh(ratio);
    shader = new ShaderTexture();
  }

  public void onDrawFrame(GL10 unused) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

    if (mUpdateST) {
      mSTexture.updateTexImage();
      mUpdateST = false;
    }
    mesh.render(shader, textureId);
    mARSystem.renderTrackingInfo();
//    mGyroscope.getRotation();
    mGeolocation.getLocation();
  }

  public void onSurfaceChanged(GL10 unused, int width, int height) {
    glViewport(0, 0, width, height);
  }

  private int initTexture() {
    int[] hTex = new int[1];
    glGenTextures(1, hTex, 0);
    glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, hTex[0]);
    glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    return hTex[0];
  }

  public void onFrameAvailable(SurfaceTexture st) {
    mView.requestRender();
    mView.queueEvent(new Runnable() {
      @Override
      public void run() {
        mUpdateST = true;
      }
    });
  }

}