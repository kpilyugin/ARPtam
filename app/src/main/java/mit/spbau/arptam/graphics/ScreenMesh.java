package mit.spbau.arptam.graphics;

import android.opengl.GLES11Ext;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

public class ScreenMesh {

  private final int[] vBuffer = new int[1];

  public ScreenMesh() {
    glGenBuffers(1, vBuffer, 0);
    glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
    float[] vertices = getVertices();
    glBufferData(GL_ARRAY_BUFFER, vertices.length * 4, FloatBuffer.wrap(vertices), GL_STATIC_DRAW);
  }

  public void render(Shader shader, int texture) {
    int hProgram = shader.getProgram();
    int ph = glGetAttribLocation(hProgram, "vPosition");
    int tch = glGetAttribLocation(hProgram, "vTexCoord");
    shader.set();

    glBindBuffer(GL_ARRAY_BUFFER, vBuffer[0]);
    glVertexAttribPointer(ph, 2, GL_FLOAT, false, 4 * 4, 0);
    glVertexAttribPointer(tch, 2, GL_FLOAT, false, 4 * 4, 4 * 2);

    glEnableVertexAttribArray(ph);
    glEnableVertexAttribArray(tch);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
    glUniform1i(glGetUniformLocation(hProgram, "sTexture"), 0);

    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
  }

  private static float[] getVertices() {
    return new float[] {
        1.0f, -1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f, 0.0f
    };
  }
}
