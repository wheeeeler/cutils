#version 120

uniform sampler2D texture;


void applyTexture(inout vec4 color) {
    color *= texture2D(texture, gl_TexCoord[0].st);
}