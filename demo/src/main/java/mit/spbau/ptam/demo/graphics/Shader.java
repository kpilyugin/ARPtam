package mit.spbau.ptam.demo.graphics;

import android.util.Log;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glLinkProgram;

public abstract class Shader {

  protected int hProgram;

  protected Shader(String vss, String fss) {
    int vshader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vshader, vss);
    glCompileShader(vshader);
    int[] compiled = new int[1];
    glGetShaderiv(vshader, GL_COMPILE_STATUS, compiled, 0);
    if (compiled[0] == 0) {
      Log.e("Shader", "Could not compile vshader");
      Log.v("Shader", "Could not compile vshader:" + glGetShaderInfoLog(vshader));
      glDeleteShader(vshader);
      vshader = 0;
    }

    int fshader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fshader, fss);
    glCompileShader(fshader);
    glGetShaderiv(fshader, GL_COMPILE_STATUS, compiled, 0);
    if (compiled[0] == 0) {
      Log.e("Shader", "Could not compile fshader");
      Log.v("Shader", "Could not compile fshader:" + glGetShaderInfoLog(fshader));
      glDeleteShader(fshader);
      fshader = 0;
    }

    int program = glCreateProgram();
    glAttachShader(program, vshader);
    glAttachShader(program, fshader);
    glLinkProgram(program);

    hProgram = program;
  }

  public int getProgram() {
    return hProgram;
  }

  protected void bindAttributes() {

  }

  public void set() {
    bindAttributes();
    glUseProgram(hProgram);
  }
}
