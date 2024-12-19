#version 120
#define TAU 6.283185307

uniform sampler2D depth;
uniform vec2 depthdims;
uniform vec4 entitycolor;
uniform float visibletint, occludedtint, visibletintvariation, occludedtintvariation, animate;
uniform bool lightvisible, lightoccluded;

void applyColor(inout vec4 color);
void applyTexture(inout vec4 color);
void applyLighting(inout vec4 color);
void applyEntityBrightness(inout vec4 color);
void applyShading(inout vec4 color);



void main()
{
    vec4 color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
    applyColor(color);
    applyTexture(color);


    float tint = visibletint;
    float tintVar = visibletintvariation;
    bool doLighting = lightvisible;
    if (gl_FragCoord.z > texture2D(depth, gl_FragCoord.xy / depthdims).r) {
        tint = occludedtint;
        tintVar = occludedtintvariation;
        doLighting = lightoccluded;
    }
    tint = max(min(tint + sin(animate * TAU) * tintVar * 0.5, 1), 0);
    color.rgb = entitycolor.rgb * tint + color.rgb * (1 - tint);

    if (doLighting) {
        applyLighting(color);
    } else {
        applyEntityBrightness(color);
    }
    applyShading(color);

    gl_FragColor = color;


    gl_FragDepth = gl_FragCoord.z * 0.05;
}
