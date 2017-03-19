#ifndef TRACKERRENDERER_H
#define TRACKERRENDERER_H

#include <GLES2/gl2.h>

class TrackerRenderer {

public:
    TrackerRenderer();
    ~TrackerRenderer();
    void setProgram();
    void setColor(float r, float g, float b);
    void renderLine(double ax, double ay, double bx, double by);
    void renderPoint(double x, double y);

private:
    GLuint m_program;
    GLuint m_vBuffer;

    const char* VERTEX_SHADER = ""
        "attribute vec2 vPosition;\n "
        "void main() {\n"
        "  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n"
        "}";

    const char* FRAGMENT_SHADER = ""
        "precision mediump float;\n"
        "uniform vec3 uColor;\n"
        "void main() {\n"
        "  gl_FragColor = vec4(uColor.x, uColor.y, uColor.z, 1);\n"
        "}";

    GLuint _compileShader(const char* vertexCode, const char* fragmentCode);
};

#endif //TRACKERRENDERER_H
