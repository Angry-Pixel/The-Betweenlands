#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TexMat;

out vec3 projUV;

// port of some FFP functonality
void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vec4 texCoord = TexMat * vec4(UV0, 0.0, 1.0);
    projUV = texCoord.xyz / texCoord.w;
}
