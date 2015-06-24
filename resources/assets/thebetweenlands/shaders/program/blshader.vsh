#version 120

attribute vec4 a_position;

uniform mat4 u_projMat;
uniform vec2 u_inSize;
uniform vec2 u_outSize;

varying vec2 v_texCoord;
varying vec2 v_oneTexel;

void main(){
    vec4 outPos = u_projMat * vec4(a_position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);

    v_oneTexel = 1.0 / u_inSize;

    v_texCoord = a_position.xy / u_outSize;
    v_texCoord.y = 1.0 - v_texCoord.y;
}