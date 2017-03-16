package mit.spbau.arptam;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.*;
import android.widget.*;

public class ARActivity extends Activity {

  public static final Size PREVIEW_SIZE = new Size(640, 480);

  private GLSurfaceView mGlView;
  private ARRenderer mRenderer;
  private ARSystem mARSystem;
  private Handler mHandler;
  private TextView mMessageView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
    mARSystem = new ARSystem(PREVIEW_SIZE.getWidth(), PREVIEW_SIZE.getHeight());
    mRenderer = new ARRenderer(mGlView, mARSystem);
    mGlView.setEGLContextClientVersion(2);
    mGlView.setRenderer(mRenderer);
    mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    RelativeLayout contentLayout = new RelativeLayout(this);
    contentLayout.addView(mGlView);

    LinearLayout buttonsLayout = new LinearLayout(this);
    buttonsLayout.setOrientation(LinearLayout.VERTICAL);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    buttonsLayout.setGravity(Gravity.END);
    buttonsLayout.setLayoutParams(layoutParams);

    TextView fpsView = new TextView(this);
    fpsView.setTextColor(Color.GREEN);
    fpsView.setText("FPS: 0");
    buttonsLayout.addView(fpsView);

    mMessageView = new TextView(this);
    mMessageView.setTextColor(Color.GREEN);
    buttonsLayout.addView(mMessageView);

    final Button button = new Button(this);
    button.setText("Next");
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mARSystem.nextTrackingState();
      }
    });
    buttonsLayout.addView(button);

    contentLayout.addView(buttonsLayout);
    setContentView(contentLayout);

    mHandler = new Handler();
    mUpdater.run();
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
        String trackingInfo = mARSystem.getTrackingInfo();
        mMessageView.setText(trackingInfo);
      } finally {
        mHandler.postDelayed(mUpdater, 200);
      }
    }
  };

  static {
    System.loadLibrary("native-lib");
  }
}



