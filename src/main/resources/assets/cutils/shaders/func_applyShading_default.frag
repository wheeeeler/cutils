#version 120

varying vec3 normal;



void applyShading(inout vec4 color) {

    float intensity0 = max(dot(normalize(vec3(gl_LightSource[0].position)), normal), 0.0f);
    float intensity1 = max(dot(normalize(vec3(gl_LightSource[1].position)), normal), 0.0f);
    float intensity = min(intensity0 + intensity1, 1.0f);

    color *= intensity * vec4(0.6f, 0.6f, 0.6f, 0.0f) + vec4(0.4f, 0.4f, 0.4f, 1.0f);
}