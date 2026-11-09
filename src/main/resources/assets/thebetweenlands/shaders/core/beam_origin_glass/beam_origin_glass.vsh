#version 150

#moj_import <fog.glsl>

in vec3 Position;
in float Visibility;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out float vertexColor;
out vec2 texCoord0;

void main() {
	vec3 pos = Position + ChunkOffset;
	gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

	vertexDistance = fog_distance(pos, FogShape);
	vertexColor = Visibility;
	texCoord0 = UV0;
}
