#ifndef TRACKERRENDERER_H
#define TRACKERRENDERER_H

#include <GLES2/gl2.h>
#include "Point2.h"

class TrackerRenderer {

public:
    TrackerRenderer();
    ~TrackerRenderer();
    void setColor(float r, float g, float b);
    void renderLine(float ax, float ay, float bx, float by);
    void renderPoint(float x, float y);

private:
    GLuint m_program;
    GLuint m_vBuffer;
    float* m_vertices;

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
