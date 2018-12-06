#version 120

precision mediump float;

uniform vec3 ambientColorUniform;
uniform vec4 diffuseColorUniform;

void main() {
    gl_FragColor = diffuseColorUniform * vec4(ambientColorUniform, 1.0);
}
