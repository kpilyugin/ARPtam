package mit.spbau.arptam;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.*;
import android.util.Size;
import android.view.*;
import android.widget.*;

import java.io.*;

import delightex.graphics.ResourceLoader;
import delightex.tools.LoggerJava;
import mit.spbau.ptam.PtamSystem;

public class ARActivity extends Activity {

  public static final Size PREVIEW_SIZE = new Size(640, 480);

  private GLSurfaceView mGlView;
  private DemoRenderer mRenderer;
  private PtamSystem mPtam;
  private Handler mHandler;
  private TextView mMessageView;
  private RelativeLayout contentLayout;
  private LinearLayout ptamButtons;
  private LinearLayout modelButtons;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LoggerJava.init();
    ResourceLoader.init(this);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    int ui = getWindow().getDecorView().getSystemUiVisibility();
    ui = ui | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    getWindow().getDecorView().setSystemUiVisibility(ui);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, 1);
      }
    }

    mGlView = new GLSurfaceView(this);
    mPtam = new PtamSystem(PREVIEW_SIZE.getWidth(), PREVIEW_SIZE.getHeight());
    mRenderer = new DemoRenderer(mGlView, mPtam);
    mGlView.setEGLContextClientVersion(2);
    mGlView.setRenderer(mRenderer);
    mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    contentLayout = new RelativeLayout(this);
    contentLayout.addView(mGlView);
    initPtamButtons();
    initModelButtons();
    contentLayout.addView(ptamButtons);
    setContentView(contentLayout);
    mHandler = new Handler();
    mUpdater.run();
  }

  private void initPtamButtons() {
    ptamButtons = new LinearLayout(this);
    ptamButtons.setOrientation(LinearLayout.VERTICAL);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    ptamButtons.setGravity(Gravity.END);
    ptamButtons.setLayoutParams(layoutParams);

    mMessageView = new TextView(this);
    mMessageView.setTextColor(Color.GREEN);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        1920 / 4, ViewGroup.LayoutParams.WRAP_CONTENT);
    ptamButtons.addView(mMessageView, params);

    final Button next = new Button(this);
    next.setText("Next");
    next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPtam.nextTrackingState();
      }
    });
    params = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    ptamButtons.addView(next, params);

    final Button reset = new Button(this);
    reset.setText("Reset");
    reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPtam.reset();
      }
    });
    ptamButtons.addView(reset, params);

    final Button saveMap = new Button(this);
    final File folder = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "PTAM");
    if (!folder.exists()) {
      //noinspection ResultOfMethodCallIgnored
      folder.mkdirs();
    }
    saveMap.setText("Save map");
    saveMap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPtam.saveMap(folder.getAbsolutePath());
      }
    });
    ptamButtons.addView(saveMap, params);

    final Button loadMap = new Button(this);
    loadMap.setText("Load map");
    loadMap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPtam.loadMap(folder.getAbsolutePath());
      }
    });
    ptamButtons.addView(loadMap, params);

    final Button showModel = new Button(this);
    showModel.setText("Show 3D object");
    showModel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mGlView.queueEvent(new Runnable() {
          @Override
          public void run() {
            mRenderer.setRenderModel(true);
          }
        });
        contentLayout.removeView(ptamButtons);
        contentLayout.addView(modelButtons);
      }
    });
    ptamButtons.addView(showModel, params);
  }

  private void initModelButtons() {
    modelButtons = new LinearLayout(this);
    modelButtons.setOrientation(LinearLayout.VERTICAL);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    modelButtons.setGravity(Gravity.END);
    modelButtons.setLayoutParams(layoutParams);

    final Button showPtam = new Button(this);
    showPtam.setText("Show tracking");
    showPtam.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mRenderer.setRenderModel(false);
        contentLayout.removeView(modelButtons);
        contentLayout.addView(ptamButtons);
      }
    });
    modelButtons.addView(showPtam);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mRenderer.onResume();
  }

  @Override
  protected void onPause() {
    mRenderer.onPause();
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    mRenderer.onDestroy();
    mHandler.removeCallbacks(mUpdater);
    super.onDestroy();
  }

  Runnable mUpdater = new Runnable() {
    @Override
    public void run() {
      try {
        mGlView.queueEvent(new Runnable() {
          @Override
          public void run() {
            final String trackingInfo = mPtam.getMessage();
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                mMessageView.setText(trackingInfo);
              }
            });
          }
        });
      } finally {
        mHandler.postDelayed(mUpdater, 200);
      }
    }
  };
}



