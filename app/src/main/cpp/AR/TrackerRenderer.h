#ifndef AR_TRACKERRENDERER_H
#define AR_TRACKERRENDERER_H

#include <GLES2/gl2.h>
#include "Point2.h"
#include "TMath/TVector.h"

using namespace TMath;

namespace AR {

class TrackerRenderer {

public:
    TrackerRenderer();
    ~TrackerRenderer();
    void renderLine(Point2f a, Point2f b, TVector<float> color);

private:
    GLuint m_program;
    GLuint m_vBuffer;
    float* m_vertices;

    const char* VERTEX_SHADER =
            "attribute vec2 vPosition;\n "
            "void main() {\n"
            "  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n"
            "}";

    const char* FRAGMENT_SHADER =
            "precision mediump float;\n"
            "uniform vec3 uColor;\n"
            "void main() {\n"
            "  gl_FragColor = vec4(uColor.x, uColor.y, uColor.z, 1);\n"
            "}";

    void _initGl();
    GLuint _compileShader(const char* vertexCode, const char* fragmentCode);
};


}

#endif //AR_TRACKERRENDERER_H
