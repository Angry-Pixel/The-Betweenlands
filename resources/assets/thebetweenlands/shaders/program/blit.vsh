#version 120

attribute vec4 Position;

uniform mat4 ProjMat;
uniform vec2 OutSize;

varying vec2 texCoord;

//The blit shader just blits two framebuffers, in other words, it copies the texture from one framebuffer to another
void main(){
    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);

    texCoord = Position.xy / OutSize;
    texCoord.y = 1.0 - texCoord.y;
}
