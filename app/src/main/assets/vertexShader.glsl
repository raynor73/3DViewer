attribute vec3 positionAttribute;
attribute vec3 normalAttribute;

uniform mat4 mvpMatrixUniform;
uniform mat4 modelMatrixUniform;

varying vec3 normalVarying;

void main() {
    normalVarying = (modelMatrixUniform * vec4(normalAttribute, 1.0)).xyz;
    gl_Position = mvpMatrixUniform * vec4(positionAttribute, 1.0);
}
