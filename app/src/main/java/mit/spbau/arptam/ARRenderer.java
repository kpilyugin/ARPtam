package mit.spbau.arptam;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import delightex.graphics.*;
import delightex.graphics.gles.Graphics;
import delightex.graphics.gles.GraphicsAndroid;
import delightex.math.*;
import delightex.nodes.BinaryObjectLoaderMobile;
import delightex.nodes.ObjectFactory;
import delightex.scene.ObjectRenderer;
import delightex.scene.ObjectRoot;
import delightex.scene.camera.CameraMatrix;
import jetbrains.jetpad.base.function.Consumer;
import mit.spbau.arptam.graphics.ScreenMesh;
import mit.spbau.arptam.graphics.Shader;
import mit.spbau.arptam.graphics.ShaderTexture;

import static android.opengl.GLES20.*;

public class ARRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

  private int textureId;
  private ScreenMesh mesh;
  private Shader shader;

  private final CameraMatrix cameraMatrix = new CameraMatrix(new Transform(), CameraParameters.getDefault(1.07, true));
  private final Transform ptamTm = new Transform();
  private Graphics graphics;
  private boolean renderModel = false;
  private ObjectRenderer modelRenderer;
  private ObjectRoot object;
  private final Transform modelWorldTm = new Transform();

  private SurfaceTexture mSTexture;
  private volatile boolean mUpdateST = false;

  private final GLSurfaceView mView;
  private final PtamSystem mPtam;

  private CameraManager mCamera;

  ARRenderer(GLSurfaceView view, PtamSystem ptam) {
    mView = view;
    mPtam = ptam;
  }

  public void onResume() {
    mUpdateST = true;
    if (mCamera != null) {
      mCamera.onResume();
    }
  }

  public void onPause() {
    mUpdateST = false;
    if (mCamera != null) {
      mCamera.onPause();
    }
  }

  public void setRenderModel(boolean renderModel) {
    this.renderModel = renderModel;
    if (renderModel) {
      Vec3f average = new Vec3f();
      Map map = mPtam.getMap();
      int goodPoints = 0;
      for (int i = 0; i < map.getNumPoints(); i++) {
        MapPoint point = map.getPoint(i);
        if (point.isTracked()) {
          average.add(point.getWorldPos());
          goodPoints++;
        }
      }
      if (goodPoints > 0) {
        Vec3f worldPos = new Vec3f(0, 0, 0);
//        Vec3f worldPos = Vec3f.mul(average, 1. / goodPoints);
        modelWorldTm.m0.setNegate(ptamTm.m0);
        modelWorldTm.m1.setNegate(ptamTm.m2);
        modelWorldTm.m2.setNegate(ptamTm.m1);
        modelWorldTm.pos.copyFrom(worldPos);
        modelWorldTm.scale(0.2 * Vec3f.distance(worldPos, ptamTm.pos));
      }
    }
  }

  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    graphics = new GraphicsAndroid();
    textureId = initTexture();
    mSTexture = new SurfaceTexture(textureId);
    try {
      mCamera = new CameraManager(mSTexture) {
        @Override
        protected void processFrame(final byte[] data) {
          mView.queueEvent(new Runnable() {
            @Override
            public void run() {
              mPtam.processFrame(data);
            }
          });
          mView.requestRender();
        }
      };
    } catch (IOException e) {
      e.printStackTrace();
    }
    mSTexture.setOnFrameAvailableListener(this);
    mesh = new ScreenMesh();
    shader = new ShaderTexture();
    loadModel();
  }

  public void onDrawFrame(GL10 unused) {
    graphics.resetState();
    graphics.clear(Color.GRAY, 1);
    graphics.setCullFaceEnable(false);
    graphics.setZWritable(false);
    graphics.setZFunction(IGraphics.Z_ALWAYS);

    if (mUpdateST) {
      mSTexture.updateTexImage();
      mUpdateST = false;
    }
    mesh.render(shader, textureId);
    graphics.setCullFaceEnable(true);
    graphics.setZWritable(true);

    graphics.setZFunction(IGraphics.Z_LESS);
    updateCameraPtam(ptamTm);
    graphics.setCamera(cameraMatrix.tm, cameraMatrix.getParameters());
    if (renderModel) {
      Transform.mulInverse(modelWorldTm, ptamTm, object.rootNode.nodeTm);
      M3x3.mul3x3(ptamTm, modelWorldTm.pos, object.rootNode.nodeTm.pos).add(ptamTm.pos);
      object.rootNode.evaluateWorldTm();
      modelRenderer.render(graphics);
    } else {
      int numPoints = mPtam.getMap().getNumPoints();
      if (numPoints == 0) {
        mPtam.renderTrackingInfo();
        return;
      }
      for (int i = 0; i < numPoints; i++) {
        MapPoint point = mPtam.getMap().getPoint(i);
        if (point.isTracked()) {
          Vec3f pos = point.getWorldPos();
          Vec3f local = M3x3.mul3x3(ptamTm, pos, new Vec3f()).add(ptamTm.pos);
          graphics.renderSphere(Color.BLUE, local.x, local.y, local.z, 0.005f * local.norm());
        }
      }
    }
  }

  private void updateCameraPtam(Transform tm) {
    float[] position = mPtam.getPosition();
    float[] rotation = mPtam.getRotation();
    tm.pos.set(position[0], position[1], position[2]);
    tm.m0.set(rotation[0], rotation[1], rotation[2]);
    tm.m1.set(rotation[3], rotation[4], rotation[5]);
    tm.m2.set(rotation[6], rotation[7], rotation[8]);
  }

  public void onSurfaceChanged(GL10 unused, int width, int height) {
    width = width * ARActivity.PREVIEW_SIZE.getHeight() / ARActivity.PREVIEW_SIZE.getWidth();
    graphics.setDefaultRtSize(width, height);
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

  public void onDestroy() {
  }

  private void loadModel() {
    BinaryObjectLoaderMobile loader = new BinaryObjectLoaderMobile();
    loader.load("lp_people.jet", "LP_Man").onSuccess(new Consumer<ObjectFactory>() {
      @Override
      public void accept(ObjectFactory objectFactory) {
        object = objectFactory.createObject();
        modelRenderer = createRenderer(object.meshCT(), object.meshesVC);
      }
    });
  }

  private static ObjectRenderer createRenderer(final ObjectRoot.GeomLink[] list,
                                               final ObjectRoot.GeomLink[] listVC) {
    final PhongParameters params = new PhongParameters();
    return new ObjectRenderer() {
      @Override
      public void render(IGraphics g) {
        for (ObjectRoot.GeomLink ln : list) {
          g.setTransform(ln.node.worldTm);
          g.renderPhong(ln.geom.getMesh(g), Color.GREEN, params);
        }
        for (ObjectRoot.GeomLink ln : listVC) {
          g.setTransform(ln.node.worldTm);
          g.renderPhongVc(ln.geom.getMesh(g), params);
        }
      }
    };
  }

  static {
    System.loadLibrary("scene-graph-android");
  }
}