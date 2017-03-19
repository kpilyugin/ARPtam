#include "TrackerRenderer.h"
#include <iostream>
#include <vector>

TrackerRenderer::TrackerRenderer() {
    m_program = _compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
    glGenBuffers(1, &m_vBuffer);
    m_vertices = new float[4];
}

TrackerRenderer::~TrackerRenderer() {
    if (m_program) {
        glDeleteProgram(m_program);
        glDeleteBuffers(1, &m_vBuffer);
        delete[] m_vertices;
    }
}

void TrackerRenderer::setProgram() {
    glUseProgram(m_program);
}

void TrackerRenderer::setColor(float r, float g, float b) {
    GLuint colorLocation = (GLuint) glGetUniformLocation(m_program, "uColor");
    glUniform3f(colorLocation, r, g, b);
}

void TrackerRenderer::renderLine(double ax, double ay, double bx, double by) {
    glDepthFunc(GL_ALWAYS);
    GLuint ph = (GLuint) glGetAttribLocation(m_program, "vPosition");
    glBindBuffer(GL_ARRAY_BUFFER, m_vBuffer);
    m_vertices[0] = (float) ax;
    m_vertices[1] = (float) ay;
    m_vertices[2] = (float) bx;
    m_vertices[3] = (float) by;
    glBufferData(GL_ARRAY_BUFFER, 4 * 4, m_vertices, GL_STATIC_DRAW);
    glVertexAttribPointer(ph, 2, GL_FLOAT, GL_FALSE, 4 * 2, 0);
    glEnableVertexAttribArray(ph);
    glDrawArrays(GL_LINES, 0, 2);
}

void TrackerRenderer::renderPoint(double x, double y) {
    if (true) {
        renderLine(x - 0.01, y - 0.01, x + 0.01, y + 0.01);
        renderLine(x + 0.01, y - 0.01, x - 0.01, y + 0.01);
    }
    glUseProgram(m_program);
    glDepthFunc(GL_ALWAYS);
    GLuint ph = (GLuint) glGetAttribLocation(m_program, "vPosition");
    glBindBuffer(GL_ARRAY_BUFFER, m_vBuffer);
    m_vertices[0] = (float) x - 0.05f;
    m_vertices[1] = (float) y - 0.05f;
    m_vertices[2] = (float) x + 0.05f;
    m_vertices[3] = (float) y + 0.05f;
    glBufferData(GL_ARRAY_BUFFER, 4 * 4, m_vertices, GL_STATIC_DRAW);
    glVertexAttribPointer(ph, 2, GL_FLOAT, GL_FALSE, 4 * 2, 0);
    glEnableVertexAttribArray(ph);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 1);
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






