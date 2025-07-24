#version 150

in vec4 Position;
in vec2 UV0;

uniform vec2 OutSize;

out vec2 v_texCoord;

void main() {
    vec2 screenPos = Position.xy * 2.0 - 1.0;
    gl_Position = vec4(screenPos.x, screenPos.y, 1.0, 1.0);
    v_texCoord = Position.xy;
}