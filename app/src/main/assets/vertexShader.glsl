attribute vec3 positionAttribute;
attribute vec3 normalAttribute;
uniform mat4 mvpMatrixUniform;

varying vec3 normalVarying;

void main() {
    normalVarying = normalAttribute; // ???
    gl_Position = mvpMatrixUniform * vec4(positionAttribute, 1.0);
}
