#version 120

void applyColor(inout vec4 color);
void applyTexture(inout vec4 color);
void applyLighting(inout vec4 color);
void applyShading(inout vec4 color);


void main()
{
    vec4 color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
    applyColor(color);
    applyTexture(color);
    applyLighting(color);
    applyShading(color);
    gl_FragColor = color;
}
