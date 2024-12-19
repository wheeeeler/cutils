#version 120

uniform vec4 entitybrightness;


void applyEntityBrightness(inout vec4 color) {
    color = color * (1 - entitybrightness.a) + entitybrightness;
}