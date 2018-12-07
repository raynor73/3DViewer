attribute vec3 positionAttribute;
uniform mat4 mvpMatrixUniform;

void main() {
    gl_Position = mvpMatrixUniform * vec4(positionAttribute, 1.0);
}
