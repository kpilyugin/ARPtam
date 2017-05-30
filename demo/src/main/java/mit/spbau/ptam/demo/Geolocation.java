package mit.spbau.ptam.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

@SuppressWarnings("MissingPermission")
public class Geolocation implements LocationListener {

  private final Context mContext;
  private final LocationManager mLocationManager;

  public Geolocation(Context context) {
    mContext = context;
    mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    if (!enabled) {
      Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      context.startActivity(intent);
    }
    if (checkPermissions()) {
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
      Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      onLocationChanged(location);
    }
  }

  public void onResume() {
    if (checkPermissions()) {
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
  }

  public void onPause() {
    mLocationManager.removeUpdates(this);
  }

  public void getLocation() {
    Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    onLocationChanged(location);
  }

  private boolean checkPermissions() {
    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      System.out.println("No permission!");
      return false;
    }
    return true;
  }

  @Override
  public void onLocationChanged(Location location) {
    if (location != null) {
      int latitude = (int) (location.getLatitude());
      System.out.println("latitude = " + latitude);
      int longitude = (int) (location.getLongitude());
      System.out.println("longitude = " + longitude);
    }
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }
}