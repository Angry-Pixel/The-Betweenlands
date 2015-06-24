#version 120

uniform sampler2D s_diffuse;

varying vec2 v_texCoord;

//The blit shader just blits two framebuffers, in other words, it copies the texture from one framebuffer to another
void main(){
    gl_FragColor = texture2D(s_diffuse, v_texCoord);
}
