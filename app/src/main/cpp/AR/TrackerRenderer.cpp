#include "TrackerRenderer.h"
#include <iostream>

namespace AR {

TrackerRenderer::TrackerRenderer() {
}

TrackerRenderer::~TrackerRenderer() {
    if (m_program) {
        glDeleteProgram(m_program);
        glDeleteBuffers(1, &m_vBuffer);
        delete[] m_vertices;
    }
}

void TrackerRenderer::renderLine(Point2f a, Point2f b, TVector<float> color) {
    if (!m_program) {
        _initGl();
    }
    glUseProgram(m_program);
    glDepthFunc(GL_ALWAYS);
    GLuint ph = (GLuint) glGetAttribLocation(m_program, "vPosition");
    glBindBuffer(GL_ARRAY_BUFFER, m_vBuffer);
    m_vertices[0] = a.x;
    m_vertices[1] = a.y;
    m_vertices[2] = b.x;
    m_vertices[3] = b.y;
    glBufferData(GL_ARRAY_BUFFER, 4 * 4, m_vertices, GL_STATIC_DRAW);
    glVertexAttribPointer(ph, 2, GL_FLOAT, GL_FALSE, 4 * 2, 0);
    glEnableVertexAttribArray(ph);

    GLuint colorLocation = (GLuint) glGetUniformLocation(m_program, "uColor");
    glUniform3f(colorLocation, color[0], color[1], color[2]);
    glDrawArrays(GL_LINES, 0, 2);
}

void TrackerRenderer::_initGl() {
    m_program = _compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
    glGenBuffers(1, &m_vBuffer);
    m_vertices = new float[4];
}

GLuint TrackerRenderer::_compileShader(const char* vertexCode, const char* fragmentCode) {
    GLuint vshader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vshader, 1, &vertexCode, 0);
    glCompileShader(vshader);
    GLint compiled = 0;
    glGetShaderiv(vshader, GL_COMPILE_STATUS, &compiled);
    if (compiled == 0) {
        GLint maxLength = 0;
        glGetProgramiv(vshader, GL_INFO_LOG_LENGTH, &maxLength);
        std::vector<GLchar> errorLog((size_t) maxLength);
        glGetShaderInfoLog(vshader, maxLength, &maxLength, &errorLog[0]);
        std::cout << "Could not compile vertex shader: " << &errorLog[0] << std::endl;
        glDeleteShader(vshader);
        vshader = 0;
    }

    GLuint fshader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fshader, 1, &fragmentCode, 0);
    glCompileShader(fshader);
    glGetShaderiv(fshader, GL_COMPILE_STATUS, &compiled);
    if (compiled == 0) {
        GLint maxLength = 0;
        glGetProgramiv(fshader, GL_INFO_LOG_LENGTH, &maxLength);
        std::vector<GLchar> errorLog((size_t) maxLength);
        glGetShaderInfoLog(fshader, maxLength, &maxLength, &errorLog[0]);
        std::cout << "Could not compile fragment shader: " << &errorLog[0] << std::endl;
        glDeleteShader(fshader);
        fshader = 0;
    }

    GLuint program = glCreateProgram();
    glAttachShader(program, vshader);
    glAttachShader(program, fshader);
    glLinkProgram(program);
    return program;
}

}





