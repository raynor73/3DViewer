precision mediump float;

struct DirectionalLight {
    vec3 color;
    vec3 direction;
};

uniform DirectionalLight directionalLightUniform;
uniform vec4 diffuseColorUniform;

varying vec3 normalVarying;

void main() {
    gl_FragColor =
        diffuseColorUniform *
        vec4(directionalLightUniform.color, 1.0) * dot(normalVarying, directionalLightUniform.direction);
}