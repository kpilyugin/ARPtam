package mit.spbau.arptam.streetview;

public class ImageUrlBuilder {

  private static final String TEMPLATE =
      "https://maps.googleapis.com/maps/api/streetview?size=%dx%d&location=%f,%f&fov=%f&heading=%f&pitch=%f";

  private int size = 600;
  private float latitude = 59.9258131f;
  private float longitude = 30.3142191f;
  private float fov = 90;
  private float heading = 90;
  private float pitch = 20;

  public ImageUrlBuilder setSize(int size) {
    this.size = size;
    return this;
  }

  public ImageUrlBuilder setLatitude(float latitude) {
    this.latitude = latitude;
    return this;
  }

  public ImageUrlBuilder setLongitude(float longitude) {
    this.longitude = longitude;
    return this;
  }

  public ImageUrlBuilder setFov(float fov) {
    this.fov = fov;
    return this;
  }

  public ImageUrlBuilder setHeading(float heading) {
    this.heading = heading;
    return this;
  }

  public ImageUrlBuilder setPitch(float pitch) {
    this.pitch = pitch;
    return this;
  }

  public String build() {
    return String.format(TEMPLATE, size, size, latitude, longitude, fov, heading, pitch);
  }
}
