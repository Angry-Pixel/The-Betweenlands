#version 150

uniform sampler2D beforeDepth;
uniform sampler2D afterDepth;
uniform sampler2D base;

//Input coords
in vec2 v_texCoord;

out vec4 o_FragColor;

void main(){
    float before    = texture2D(beforeDepth, v_texCoord).r;
    float after     = texture2D(afterDepth, v_texCoord).r;
    float base      = texture2D(base, v_texCoord).r;
    float epsilon   = 1e-5;
    float changed   = step(epsilon, abs(after - before));
    gl_FragDepth    = mix(base, after, changed);
    o_FragColor     = vec4(mix(base, after, changed),mix(base, after, changed),mix(base, after, changed),changed);
}