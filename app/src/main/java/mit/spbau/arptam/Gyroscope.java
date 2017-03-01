package mit.spbau.arptam;

import android.content.Context;
import android.hardware.*;

import java.util.Arrays;


public class Gyroscope implements SensorEventListener {
  private final SensorManager mSensorManager;
  private final float[] mRotation = new float[9];
  private final float[] mRotationVector = new float[4];

  public Gyroscope(Context context) {
    mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor = getRotationSensor();
    if (sensor != null) {
      mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
  }

  private Sensor getRotationSensor() {
    Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    if (sensor != null) {
      return sensor;
    }
    return mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
  }

  public void stop() {
    mSensorManager.unregisterListener(this);
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    switch (event.sensor.getType()) {
      case Sensor.TYPE_ROTATION_VECTOR:
      case Sensor.TYPE_GAME_ROTATION_VECTOR:
        System.arraycopy(event.values, 0, mRotationVector, 0, 4);
    }
  }

  public float[] getRotation() {
    SensorManager.getRotationMatrixFromVector(mRotation, mRotationVector);
    System.out.println("mRotationVector = " + Arrays.toString(mRotationVector));
    return mRotation;
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }
}
