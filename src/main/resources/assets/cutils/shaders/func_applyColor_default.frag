#version 120


void applyColor(inout vec4 color) {
    color *= gl_Color;
}