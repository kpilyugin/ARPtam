package mit.spbau.ptam.demo.graphics;

public class ShaderTexture extends Shader {

  private static final String VERTEX_SHADER = "" +
      "attribute vec2 vPosition;\n" +
      "attribute vec2 vTexCoord;\n" +
      "varying vec2 texCoord;\n" +
      "void main() {\n" +
      "  texCoord = vTexCoord;\n" +
      "  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n" +
      "}";

  private static final String FRAGMENT_SHADER = "" +
      "#extension GL_OES_EGL_image_external : require\n" +
      "precision mediump float;\n" +
      "uniform samplerExternalOES sTexture;\n" +
      "varying vec2 texCoord;\n" +
      "void main() {\n" +
//      "  gl_FragColor = vec4(texCoord.x, texCoord.y, 1.0, 1.0);\n" +
      "  gl_FragColor = texture2D(sTexture,texCoord);\n" +
      "}";

  public ShaderTexture() {
    super(VERTEX_SHADER, FRAGMENT_SHADER);
  }
}
