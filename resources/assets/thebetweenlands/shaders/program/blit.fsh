#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

//The blit shader just blits two framebuffers, in other words, it copies the texture from one framebuffer to another
void main(){
    gl_FragColor = texture2D(DiffuseSampler, texCoord);
}
