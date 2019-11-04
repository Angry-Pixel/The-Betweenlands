#version 120

varying vec2 v_texCoord;

void main() {
	gl_TexCoord[0] = gl_MultiTexCoord0;
	v_texCoord = gl_MultiTexCoord0.st * vec2(1, -1) + vec2(0, 1);
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}