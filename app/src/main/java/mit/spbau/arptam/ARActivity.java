package mit.spbau.arptam;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Size;
import android.view.*;
import android.widget.*;

public class ARActivity extends Activity {

  public static final Size PREVIEW_SIZE = new Size(640, 480);

  private GLSurfaceView mGlView;
  private ARRenderer mRenderer;
  private ARSystem mARSystem;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    int ui = getWindow().getDecorView().getSystemUiVisibility();
    ui = ui | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    getWindow().getDecorView().setSystemUiVisibility(ui);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    mGlView = new GLSurfaceView(this);
    mARSystem = new ARSystem(PREVIEW_SIZE.getWidth(), PREVIEW_SIZE.getHeight());
    mRenderer = new ARRenderer(this, mGlView, mARSystem);
    mGlView.setEGLContextClientVersion(2);
    mGlView.setRenderer(mRenderer);
    mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    RelativeLayout contentLayout = new RelativeLayout(this);
    contentLayout.addView(mGlView);

    LinearLayout buttonsLayout = new LinearLayout(this);
    buttonsLayout.setOrientation(LinearLayout.VERTICAL);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.addRule(RelativeLayout.ALIGN_RIGHT);
    buttonsLayout.setGravity(Gravity.END);
    buttonsLayout.setLayoutParams(layoutParams);

    TextView fpsView = new TextView(this);
    fpsView.setText("FPS: 0");
    buttonsLayout.addView(fpsView);

    final Button button = new Button(this);
    button.setText("Start");
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mARSystem.nextTrackingState();
        button.setText(mARSystem.getTrackingState().toString());
      }
    });
    buttonsLayout.addView(button);

    contentLayout.addView(buttonsLayout);
    setContentView(contentLayout);
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
    super.onDestroy();
  }

  static {
    System.loadLibrary("native-lib");
  }
}



